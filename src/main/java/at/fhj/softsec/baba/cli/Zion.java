package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.commands.*;
import at.fhj.softsec.baba.exception.ApplicationException;

public class Zion {
    private final Application app;
    private final CliContext context;
    private final CommandRegistry registry;

    public Zion(Application app, CliContext cliContext) {
        this.app = app;
        this.context = cliContext;

        registry = new CommandRegistry();
        registry.register(new Command() {
            @Override
            public String[] name() {
                return new String[]{"help"};
            }

            @Override
            public String description() {
                return "Show help information";
            }

            @Override
            public void execute(String[] args, Application app, CliContext context) {
                if (args.length > 0) {
                    registry.findCommand(args).ifPresentOrElse(
                            command -> printUsage(command),
                            Zion.this::printGlobalHelp
                    );
                } else {
                    printGlobalHelp();
                }
            }
        });
        registry.register(new RegisterCommand());
        registry.register(new LoginCommand());
        registry.register(new LogoutCommand());
        registry.register(new AccountListCommand());
        registry.register(new AccountCreateCommand());
        registry.register(new AccountShowCommand());
        registry.register(new AccountDeleteCommand());
        registry.register(new DepositCommand());
        registry.register(new WithdrawalCommand());
        registry.register(new TransferCommand());
    }


    public void promptLoop() {
        do {
            String prompt = app.session().getCurrentUser()
                    .map(user -> user.getUserId() + "@BaBa> ")
                    .orElse("BaBa> ");
            String line = context.prompt(prompt);
            if (line == null || line.equals("exit")) break;

            String[] args = line.trim().split("\\s+");
            if (args.length == 0) continue;

            registry.findCommand(args)
                    .ifPresentOrElse(command -> {
                                String[] remainingArgs = new String[args.length - command.name().length];
                                System.arraycopy(args, command.name().length, remainingArgs, 0, remainingArgs.length);
                                try {
                                    command.execute(remainingArgs, app, context);
                                } catch (ApplicationException e) {
                                    context.out.println(e.getMessage());
                                    printUsage(command);
                                }
                            },
                            this::printGlobalHelp
                    );
        } while (true);
    }

    private void printGlobalHelp() {
        context.out.println("Available commands:");
        registry.getAllCommands().forEach(cmd ->
                context.out.printf("  %-15s %s%n", cmd.toName(), cmd.description())
        );
    }

    private void printUsage(Command command) {
        context.out.printf("Usage: \n%s\n%n", command.getUsage());
    }

}
