package at.fhj.softsec.baba;

import org.junit.jupiter.api.Test;

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
    public void promptLoopParsesRegister() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("register", "user", "password", "password", "exit");
        Main main = new Main(out, reader);

        // act
        main.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "BaBa> Username: Password: Verify password: BaBa> ";
        assertThat(output, is(printPrompt));
    }
}