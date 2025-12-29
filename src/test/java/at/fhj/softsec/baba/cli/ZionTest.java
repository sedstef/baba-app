package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZionTest {

    @AfterEach
    public void tearDown() {
        AuthService.getInstance().logout();
    }

    @Test
    public void promptLoopParsesHelp() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("help", "exit");
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        assertThat(output, startsWith("BaBa> Available commands:"));
        assertThat(output, endsWith("\nBaBa> "));
    }

    @ParameterizedTest
    @MethodSource("provideInputLines")
    public void promptLoopParsesInput(Consumer<File> setup, CommandReader commandReader, String expectedPrompt, @TempDir File tmpDir) throws Exception {
        // arrange
        if (setup != null) setup.accept(tmpDir);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        Zion zion = new Zion(out, commandReader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        assertThat(output, is(expectedPrompt));
    }

    private static Stream<Arguments> provideInputLines() {
        return Stream.of(
                        ArgumentsBuilder.of("help help", "exit")
                                .withExpectedPrompt("BaBa> help - Show help information\nBaBa> "),
                        ArgumentsBuilder.of("register", "alice", "secret", "secret", "exit")
                                .withExpectedPrompt("BaBa> Username: Password: Verify password: alice@BaBa> ")
                                .withSetup(tmpDir -> AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"))),
                        ArgumentsBuilder.of("login alice", "secret", "exit")
                                .withExpectedPrompt("BaBa> Password: Login successful.\nalice@BaBa> ")
                                .withSetup(tmpDir -> {
                                    AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
                                    AuthService.getInstance().createUser("alice", "secret".toCharArray());
                                    AuthService.getInstance().logout();
                                }),
                        ArgumentsBuilder.of("logout", "exit")
                                .withExpectedPrompt("alice@BaBa> Logout successful.\nBaBa> ")
                                .withSetup(tmpDir -> {
                                    AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
                                    AuthService.getInstance().createUser("alice", "secret".toCharArray());
                                }),
                        ArgumentsBuilder.of("account list", "exit")
                                .withExpectedPrompt("alice@BaBa> Account listing:\nalice@BaBa> ")
                                .withSetup(tmpDir -> {
                                    AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
                                    AuthService.getInstance().createUser("alice", "secret".toCharArray());
                                }),
                        ArgumentsBuilder.of("account create", "exit")
                                .withExpectedPrompt("alice@BaBa> Account 1 created.\nalice@BaBa> ")
                                .withSetup(tmpDir -> {
                                    AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
                                    AuthService.getInstance().createUser("alice", "secret".toCharArray());
                                }),
                        ArgumentsBuilder.of("account show 1", "exit")
                                .withExpectedPrompt("alice@BaBa> Account 1 balance 0.\nalice@BaBa> ")
                                .withSetup(tmpDir -> {
                                    AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
                                    AuthService.getInstance().createUser("alice", "secret".toCharArray());
                                }),
                        ArgumentsBuilder.of("account delete 1", "exit")
                                .withExpectedPrompt("alice@BaBa> Account 1 deleted.\nalice@BaBa> ")
                                .withSetup(tmpDir -> {
                                    AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
                                    AuthService.getInstance().createUser("alice", "secret".toCharArray());
                                })
                )
                .map(ArgumentsBuilder::build);
    }

    private static class ArgumentsBuilder {
        public static ArgumentsBuilder of(String command, String... commands) {
            return new ArgumentsBuilder().withCommands(command, commands);
        }

        private Consumer<File> setup;
        private CommandReader commandReader;
        private String expectedPrompt;

        public ArgumentsBuilder withCommands(String command, String... commands) {
            this.commandReader = CommandReader.of(command, commands);
            return this;
        }

        public ArgumentsBuilder withSetup(Consumer<File> setup) {
            this.setup = setup;
            return this;
        }

        public ArgumentsBuilder withExpectedPrompt(String expectedPrompt) {
            this.expectedPrompt = expectedPrompt;
            return this;
        }

        public Arguments build() {
            return Arguments.of(setup, commandReader, expectedPrompt);
        }
    }

}