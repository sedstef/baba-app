package at.fhj.softsec.baba;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

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

    @Test
    public void promptLoopParsesHelp() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos, true, StandardCharsets.UTF_8));
        Iterator<String> inputs = List.of("help", "exit").iterator();
        BufferedReader reader = new BufferedReader (new StringReader("")){
            @Override
            public String readLine() throws IOException {
                if(!inputs.hasNext()) {
                    throw new IOException("No more input");
                }
                return inputs.next();
            }
        };

        // act
        Main.promptLoop(reader);

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "Baba$: Available commands: help, exit\nBaba$: ";
        assertThat(output, is(printPrompt));
    }
}