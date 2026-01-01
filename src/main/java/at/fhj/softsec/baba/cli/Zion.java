package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.commands.*;
import at.fhj.softsec.baba.exception.InputParseException;

import java.io.IOException;
import java.util.Optional;

public class Zion {
    private final Application app;
    private final CliContext context;
    private final CommandRegistry root;

    public Zion(Application app, CliContext cliContext) {
        this.app = app;
        this.context = cliContext;

        root = new CommandRegistry("root");
        root.register(new Command() {
            @Override
            public String name() {
                return "help";
            }

            @Override
            public String description() {
                return "Show help information";
            }

            @Override
            public void execute(String[] args, Application app, CliContext context) {
                if (args.length > 0) {
                    String cmdName = args[0];
                    root.visitCommands(cmd -> {
                                if (cmd.name().equals(cmdName)) {
                                    context.out.printf("%s - %s%n", cmd.name(), cmd.description());
                                }
                            }
                    );
                } else {
                    context.out.println("Available commands: ");
                    root.visitCommands(cmd ->
                            context.out.printf("%-10s - %s%n", cmd.name(), cmd.description())
                    );
                }
            }
        });
        root.register(new RegisterCommand());
        root.register(new LoginCommand());
        root.register(new LogoutCommand());
        root.sub("account").register(new AccountListCommand());
        root.sub("account").register(new AccountCreateCommand());
        root.sub("account").register(new AccountShowCommand());
        root.sub("account").register(new AccountDeleteCommand());
        root.register(new DepositCommand());
        root.register(new WithdrawalCommand());
        root.register(new TransferCommand());
    }

    public void promptLoop() {
        do {
            try {
                String prompt = app.session().getCurrentUser()
                        .map(user -> user.getUserId() + "@BaBa> ")
                        .orElse("BaBa> ");
                String line = context.prompt(prompt);
                if (line == null || line.equals("exit")) break;

                String[] tokens = line.trim().split("\\s+");
                if (tokens.length == 0) continue;
                root.execute(tokens, app, context);
            } catch (InputParseException e) {
                context.out.println(e.getMessage());
            }
        } while (true);
    }


}
