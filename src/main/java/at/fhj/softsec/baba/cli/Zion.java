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
                        out.println("Available commands: help, exit");
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
