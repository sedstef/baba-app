package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;

import java.io.IOException;
import java.util.*;

public class CommandRegistry implements Command {

    private final String name;
    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return "Registry of commands";
    }

    public void register(Command command) {
        commands.put(command.name(), command);
    }

    public CommandRegistry sub(String main) {
        return (CommandRegistry) commands.computeIfAbsent(main, CommandRegistry::new);
    }

    public void visitCommands(CommandVisitor visitor) {
        for (Command command : commands.values()) {
            visitor.visit(command);
            if (command instanceof CommandRegistry registry) {
                registry.visitCommands(visitor);
            }
        }
    }

    public void execute(String[] tokens, Application app, CliContext ctx)  {
        Command command = commands.get(tokens[0]);
        if (command != null) {
            command.execute(Arrays.copyOfRange(tokens, 1, tokens.length), app, ctx);
        } else {
            ctx.out.println("Unknown command: " + tokens[0]);
        }
    }

}
