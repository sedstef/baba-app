package at.fhj.softsec.baba;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MainPromptTest {

    @Test
    public void promptLoopParsesHelp() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("help", "exit");
        Main main = new Main(out, reader);

        // act
        main.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "BaBa> Available commands: help, exit\nBaBa> ";
        assertThat(output, is(printPrompt));
    }

    @Test
    public void promptLoopParsesRegister(@TempDir File tmpDir) throws Exception {
        // arrange
        AuthService.getInstance().setStorageDir(new File(tmpDir, "storage"));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("register", "user", "password", "password", "exit");
        Main main = new Main(out, reader);

        // act
        main.promptLoop();

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

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("login "+ username, new String(password), "exit");
        Main main = new Main(out, reader);

        // act
        main.promptLoop();

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

        BufferedReader reader = CommandReader.of("logout",  "exit");
        Main main = new Main(out, reader);

        // act
        main.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "user@BaBa> Logout successful.\nBaBa> ";
        assertThat(output, is(printPrompt));
    }
}