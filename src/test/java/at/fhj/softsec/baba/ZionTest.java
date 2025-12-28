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

public class ZionTest {

    @Test
    public void promptLoopParsesHelp() throws Exception {
        // arrange
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos, true, StandardCharsets.UTF_8);
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
        Zion zion = new Zion(out, reader);

        // act
        zion.promptLoop();

        //assert
        String output = baos.toString(StandardCharsets.UTF_8);
        String printPrompt = "Baba$: Available commands: help, exit\nBaba$: ";
        assertThat(output, is(printPrompt));
    }
}