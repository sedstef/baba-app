package at.fhj.softsec.baba;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MainTest {
    private PrintStream stdout;
    private InputStream stdin;

    @Before
    public void setUp() {
        stdout = System.out;
        stdin = System.in;
    }

    @After
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
        String printPrompt = "Baba$: ";
        assertThat(output, is(bannerText + System.lineSeparator() + printPrompt));
    }
}