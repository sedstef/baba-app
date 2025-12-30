package at.fhj.softsec.baba;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MainTest {
    private PrintStream stdout;
    private InputStream stdin;
    @TempDir
    private Path tmpDir;

    @BeforeEach
    public void setUp() throws Exception {
        stdout = System.out;
        stdin = System.in;
        Main.DATA_DIR = tmpDir;
    }

    @AfterEach
    public void tearDown() throws Exception {
        System.setOut(stdout);
        System.setIn(stdin);
    }

    @Test
    public void main() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos, true, StandardCharsets.UTF_8));
        System.setIn(new ByteArrayInputStream("secret\nexit\n".getBytes(StandardCharsets.UTF_8)));

        // act
        Main.main(null);

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        InputStream is = Main.class.getResourceAsStream("/banner.txt");
        String bannerText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        String printPrompt = "Enter master password to unlock BaBa:BaBa> ";
        assertThat(output, is(bannerText + System.lineSeparator() + printPrompt));
    }

}