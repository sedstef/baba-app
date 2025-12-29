package at.fhj.softsec.baba.cli;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry() {
    }

    public CommandRegistry register(Command command) {
        commands.put(command.name(), command);
        return this;
    }

    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    public void execute(String[] tokens, CliContext ctx) throws IOException {
        if (tokens.length == 0) return;

        Command command = commands.get(tokens[0]);
        if (command != null) {
            command.execute(tokens, ctx);
        } else {
            ctx.out.println("Unknown command: " + tokens[0]);
        }
    }

}
