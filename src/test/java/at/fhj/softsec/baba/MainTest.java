package at.fhj.softsec.baba;

import at.fhj.softsec.baba.storage.Storage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MainTest {
    private PrintStream stdout;
    private InputStream stdin;

    @BeforeEach
    public void setUp() throws Exception{
        stdout = System.out;
        stdin = System.in;
        Storage.getInstance().unlock(new char[]{});
    }

    @AfterEach
    public void tearDown() {
        System.setOut(stdout);
        System.setIn(stdin);
    }

    @Test
    public void main() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos, true, StandardCharsets.UTF_8));
        System.setIn(new ByteArrayInputStream("exit\n".getBytes(StandardCharsets.UTF_8)));

        // act
        Main.main(null);

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        InputStream is = Main.class.getResourceAsStream("/banner.txt");
        String bannerText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        String printPrompt = "BaBa> ";
        assertThat(output, is(bannerText + System.lineSeparator() + printPrompt));
    }

}