package at.fhj.softsec.baba.cli;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class StringLineReader extends BufferedReader {
    public static StringLineReader of(String command, String... commands) {
        List<String> inputs = new ArrayList<>(List.of(command));
        if (null != commands) {
            inputs.addAll(List.of(commands));
        }
        return new StringLineReader(inputs);
    }

    private StringLineReader(Collection<String> commands) {
        super(new StringReader(String.join("\n", commands)));
    }
}
