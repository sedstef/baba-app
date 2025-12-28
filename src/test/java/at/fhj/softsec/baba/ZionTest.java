package at.fhj.softsec.baba;

import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZionTest {

    @Test
    public void promptLoopParsesHelp() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("help", "exit");
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "Baba$: Available commands: help, exit\nBaba$: ";
        assertThat(output, is(printPrompt));
    }

    @Test
    public void promptLoopParsesRegister() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos, true, StandardCharsets.UTF_8);

        BufferedReader reader = CommandReader.of("register", "user", "password", "password", "exit");
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "Baba$: username: password: verify password: Baba$: ";
        assertThat(output, is(printPrompt));
    }
}