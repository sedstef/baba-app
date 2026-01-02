package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.ApplicationBootstrap;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.exception.ApplicationException;
import at.fhj.softsec.baba.exception.AuthenticationException;
import at.fhj.softsec.baba.security.MasterKeyLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZionTest {
    @TempDir
    private Path tmpDir;

    private Application app;

    @BeforeEach
    public void setUp() throws Exception {
        SecretKey secretKey = MasterKeyLoader.loadMasterKey(tmpDir, "defaultmaster".toCharArray());
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

        BufferedReader reader = StringLineReader.of("help", "exit");
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
    public void promptLoopParsesInput(SetUp setup, StringLineReader stringLineReader, String expectedPrompt, @TempDir File tmpDir) throws Exception {
        // arrange
        if (setup != null) setup.call(app);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        Zion zion = new Zion(app, new CliContext(stringLineReader, out));

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        assertThat(output, is(expectedPrompt));
    }

    private static Stream<Arguments> provideInputLines() {
        return Stream.of(
                        ArgumentsBuilder.of("help help", "exit")
                                .withExpectedPrompt("BaBa> Usage: \nhelp \n\nBaBa> "),
                        ArgumentsBuilder.of("register alice", "secret", "secret", "exit")
                                .withExpectedPrompt("BaBa> Password: Verify password: alice@BaBa> "),
                        ArgumentsBuilder.of("login alice", "secret", "exit")
                                .withSetup(app -> app.auth().register("alice", "secret".toCharArray()))
                                .withExpectedPrompt("BaBa> Password: Login successful.\nalice@BaBa> "),
                        ArgumentsBuilder.of("logout", "exit")
                                .withSetup(app -> login(app, "alice", "secret"))
                                .withExpectedPrompt("alice@BaBa> Logout successful.\nBaBa> "),
                        ArgumentsBuilder.of("account list", "exit")
                                .withSetup(app -> createAccount(app, "alice", "secret"))
                                .withExpectedPrompt("alice@BaBa> Account listing:\n1 balance € 0,00\nalice@BaBa> "),
                        ArgumentsBuilder.of("account create", "exit")
                                .withSetup(app -> login(app, "alice", "secret"))
                                .withExpectedPrompt("alice@BaBa> Account 1 created.\nalice@BaBa> "),
                        ArgumentsBuilder.of("account show 1", "exit")
                                .withSetup(app -> createAccount(app, "alice", "secret"))
                                .withExpectedPrompt("alice@BaBa> Account 1 balance € 0,00\nalice@BaBa> "),
                        ArgumentsBuilder.of("account delete 1", "exit")
                                .withSetup(app -> createAccount(app, "alice", "secret"))
                                .withExpectedPrompt("alice@BaBa> Account 1 deleted.\nalice@BaBa> "),
                        ArgumentsBuilder.of("deposit 1 20.00", "exit")
                                .withSetup(app -> createAccount(app, "alice", "secret"))
                                .withExpectedPrompt("alice@BaBa> Account 1 balance € 20,00.\nalice@BaBa> "),
                        ArgumentsBuilder.of("withdrawal 1 20.00", "exit")
                                .withSetup(app -> {
                                    AuthenticatedUser user = login(app, "alice", "secret");
                                    OwnedAccount account = app.account().create(user);
                                    app.transfer().deposit(user, account.getNumber(), BigDecimal.valueOf(50));
                                })
                                .withExpectedPrompt("alice@BaBa> Account 1 balance € 30,00.\nalice@BaBa> "),
                        ArgumentsBuilder.of("transfer 1 2 20.00", "exit")
                                .withSetup(app -> {
                                    AuthenticatedUser user = login(app, "alice", "secret");
                                    OwnedAccount account = app.account().create(user);
                                    app.transfer().deposit(user, account.getNumber(), BigDecimal.valueOf(50));

                                    User bob = app.auth().register("bob", "secret".toCharArray());
                                    app.account().create(bob);
                                })
                                .withExpectedPrompt("alice@BaBa> Account 1 balance € 30,00.\nalice@BaBa> ")
                )
                .map(ArgumentsBuilder::build);
    }

    private static OwnedAccount createAccount(Application app, String userId, String password) throws AuthenticationException {
        AuthenticatedUser user = login(app, userId, password);
        return app.account().create(user);
    }

    private static AuthenticatedUser login(Application app, String userId, String password) throws AuthenticationException {
        app.auth().register(userId, password.toCharArray());
        AuthenticatedUser authenticatedUser = app.auth().login(userId, password.toCharArray());
        app.session().login(authenticatedUser);
        return authenticatedUser;
    }

    private static class ArgumentsBuilder {
        public static ArgumentsBuilder of(String command, String... commands) {
            return new ArgumentsBuilder().withCommands(command, commands);
        }

        private SetUp setup;
        private StringLineReader stringLineReader;
        private String expectedPrompt;

        public ArgumentsBuilder withCommands(String command, String... commands) {
            this.stringLineReader = StringLineReader.of(command, commands);
            return this;
        }

        public ArgumentsBuilder withSetup(SetUp setup) {
            this.setup = setup;
            return this;
        }

        public ArgumentsBuilder withExpectedPrompt(String expectedPrompt) {
            this.expectedPrompt = expectedPrompt;
            return this;
        }

        public Arguments build() {
            return Arguments.of(setup, stringLineReader, expectedPrompt);
        }
    }

    public interface SetUp {
        void call(Application app) throws ApplicationException;
    }
}