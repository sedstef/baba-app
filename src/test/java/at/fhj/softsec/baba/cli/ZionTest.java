package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;

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

    @Test
    public void promptLoopParsesHelpHelp() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("help help", "exit");
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "BaBa> help - Show help information\nBaBa> ";
        assertThat(output, is(printPrompt));
    }

    @Test
    public void promptLoopParsesRegister(@TempDir File tmpDir) throws Exception {
        // arrange
        AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("register", "user", "password", "password", "exit");
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "BaBa> Username: Password: Verify password: user@BaBa> ";
        assertThat(output, is(printPrompt));
    }

    @Test
    public void promptLoopParsesLogin(@TempDir File tmpDir) throws Exception {
        // arrange
        AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
        String username = "user";
        char[] password = "password".toCharArray();
        AuthService.getInstance().createUser(username, password);
        AuthService.getInstance().logout();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("login " + username, new String(password), "exit");
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "BaBa> Password: Login successful.\nuser@BaBa> ";
        assertThat(output, is(printPrompt));
    }

    @Test
    public void promptLoopParsesLogout(@TempDir File tmpDir) throws Exception {
        // arrange
        AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
        String username = "user";
        char[] password = "password".toCharArray();
        AuthService.getInstance().createUser(username, password);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("logout", "exit");
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "user@BaBa> Logout successful.\nBaBa> ";
        assertThat(output, is(printPrompt));
    }

    @Test
    public void promptLoopParsesAccountList(@TempDir File tmpDir) throws Exception {
        // arrange
        AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
        String username = "user";
        char[] password = "password".toCharArray();
        AuthService.getInstance().createUser(username, password);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("account list", "exit");
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "user@BaBa> Account listing:\nuser@BaBa> ";
        assertThat(output, is(printPrompt));
    }

    @Test
    public void promptLoopParsesAccountCreate(@TempDir File tmpDir) throws Exception {
        // arrange
        AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));
        String username = "user";
        char[] password = "password".toCharArray();
        AuthService.getInstance().createUser(username, password);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("account create", "exit");
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "user@BaBa> Account 1 created.\nuser@BaBa> ";
        assertThat(output, is(printPrompt));
    }
}