package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.service.AuthService;
import at.fhj.softsec.baba.cli.commands.LoginCommand;
import at.fhj.softsec.baba.cli.commands.LogoutCommand;
import at.fhj.softsec.baba.cli.commands.RegisterCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class Zion {
    private final CliContext context;
    private final CommandRegistry root;

    public Zion(PrintWriter out, BufferedReader in) {
        context = new CliContext(in, out);

        root = new CommandRegistry()
                .register(new Command() {
                    @Override
                    public String name() {
                        return "help";
                    }

                    @Override
                    public String description() {
                        return "Show help information";
                    }

                    @Override
                    public void execute(String[] args, CliContext context) throws IOException {
                        if(args.length > 1) {
                            String cmdName = args[1];
                            Optional<Command> cmdOpt = root.getCommands().stream()
                                    .filter(cmd -> cmd.name().equals(cmdName))
                                    .findFirst();
                            if (cmdOpt.isPresent()) {
                                Command cmd = cmdOpt.get();
                                context.out.printf("%s - %s%n", cmd.name(), cmd.description());
                            } else {
                                context.out.println("Unknown command: " + cmdName);
                            }
                        }else {
                            context.out.println("Available commands: ");
                            root.getCommands().forEach(cmd -> {
                                context.out.printf("%-10s - %s%n", cmd.name(), cmd.description());
                            });
                        }
                    }
                })
                .register(new RegisterCommand())
                .register(new LoginCommand())
                .register(new LogoutCommand());
    }

    public void promptLoop() {
        do {
            try {
                String prompt = Optional.ofNullable(AuthService.getInstance().getCurrentUsername())
                        .map(username -> username + "@BaBa> ")
                        .orElse("BaBa> ");
                String line = context.prompt(prompt);
                if (line == null || line.equals("exit")) break;

                String[] tokens = line.trim().split("\\s+");
                if (tokens.length == 0) continue;
                root.execute(tokens, context);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (true);
    }


}
