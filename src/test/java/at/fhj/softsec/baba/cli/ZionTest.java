package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.ApplicationBootstrap;
import at.fhj.softsec.baba.service.AuthenticatedUser;
import at.fhj.softsec.baba.security.CryptoUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZionTest {
    @TempDir
    private Path tmpDir;

    private Application app;

    @BeforeEach
    public void setUp() throws Exception {
        SecretKey secretKey = CryptoUtils.loadMasterKey(tmpDir, "defaultmaster".toCharArray());
        app = ApplicationBootstrap.create(tmpDir, secretKey);
    }

    @AfterEach
    public void tearDown() throws Exception {
        app = null;
        deleteDirectory(tmpDir.toFile());
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    @Test
    public void promptLoopParsesHelp() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("help", "exit");
        Zion zion = new Zion(app, new CliContext(reader, out));

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        assertThat(output, startsWith("BaBa> Available commands:"));
        assertThat(output, endsWith("\nBaBa> "));
    }

    @ParameterizedTest
    @MethodSource("provideInputLines")
    public void promptLoopParsesInput(Consumer<Application> setup, CommandReader commandReader, String expectedPrompt, @TempDir File tmpDir) throws Exception {
        // arrange
        if (setup != null) setup.accept(app);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        Zion zion = new Zion(app, new CliContext(commandReader, out));

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
                                .withExpectedPrompt("BaBa> Username: Password: Verify password: alice@BaBa> "),
                        ArgumentsBuilder.of("login alice", "secret", "exit")
                                .withExpectedPrompt("BaBa> Password: Login successful.\nalice@BaBa> ")
                                .withSetup(app -> app.auth().register("alice", "secret".toCharArray())),
                        ArgumentsBuilder.of("logout", "exit")
                                .withExpectedPrompt("alice@BaBa> Logout successful.\nBaBa> ")
                                .withSetup(app -> login(app, "alice", "secret")),
                        ArgumentsBuilder.of("account list", "exit")
                                .withExpectedPrompt("alice@BaBa> Account listing:\nalice@BaBa> ")
                                .withSetup(app -> login(app, "alice", "secret")),
                        ArgumentsBuilder.of("account create", "exit")
                                .withExpectedPrompt("alice@BaBa> Account 1 created.\nalice@BaBa> ")
                                .withSetup(app -> login(app, "alice", "secret")),
                        ArgumentsBuilder.of("account show 1", "exit")
                                .withExpectedPrompt("alice@BaBa> Account 1 balance 0.\nalice@BaBa> ")
                                .withSetup(app -> login(app, "alice", "secret")),
                        ArgumentsBuilder.of("account delete 1", "exit")
                                .withExpectedPrompt("alice@BaBa> Account 1 deleted.\nalice@BaBa> ")
                                .withSetup(app -> login(app, "alice", "secret")),
                        ArgumentsBuilder.of("deposit 1 20.00", "exit")
                                .withExpectedPrompt("alice@BaBa> Account 1 balance € 20,00.\nalice@BaBa> ")
                                .withSetup(app -> login(app, "alice", "secret")),
                        ArgumentsBuilder.of("withdrawal 1 20.00", "exit")
                                .withExpectedPrompt("alice@BaBa> Account 1 balance € 20,00.\nalice@BaBa> ")
                                .withSetup(app -> login(app, "alice", "secret")),
                        ArgumentsBuilder.of("transfer 1 2 20.00", "exit")
                                .withExpectedPrompt("alice@BaBa> Account 1 balance € 20,00.\nAccount 2 balance € 20,00.\nalice@BaBa> ")
                                .withSetup(app -> login(app, "alice", "secret"))
                )
                .map(ArgumentsBuilder::build);
    }

    private static void login(Application app, String userId, String password) {
        app.auth().register(userId, password.toCharArray());
        AuthenticatedUser authenticatedUser = app.auth().login(userId, password.toCharArray());
        app.session().login(authenticatedUser);
    }

    private static class ArgumentsBuilder {
        public static ArgumentsBuilder of(String command, String... commands) {
            return new ArgumentsBuilder().withCommands(command, commands);
        }

        private Consumer<Application> setup;
        private CommandReader commandReader;
        private String expectedPrompt;

        public ArgumentsBuilder withCommands(String command, String... commands) {
            this.commandReader = CommandReader.of(command, commands);
            return this;
        }

        public ArgumentsBuilder withSetup(Consumer<Application> setup) {
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