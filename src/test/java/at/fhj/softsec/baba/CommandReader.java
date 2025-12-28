package at.fhj.softsec.baba;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class CommandReader extends BufferedReader {
    public static CommandReader of(String command, String... commands) {
        List<String> inputs = new ArrayList<>(List.of(command));
        if (null != commands) {
            inputs.addAll(List.of(commands));
        }
        return new CommandReader(inputs);
    }

    private CommandReader(Collection<String> commands) {
        super(new StringReader(String.join("\n", commands)));
    }
}
