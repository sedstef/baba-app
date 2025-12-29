package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.cli.commands.*;
import at.fhj.softsec.baba.service.AuthService;
import at.fhj.softsec.baba.service.InvalidPasswordException;
import at.fhj.softsec.baba.storage.Storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

public class Zion {
    private final CliContext context;
    private final CommandRegistry root;

    public Zion(PrintWriter out, BufferedReader in) {
        context = new CliContext(in, out);

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
            public void execute(String[] args, CliContext context) throws IOException {
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
        unlockStorage();

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

    private void unlockStorage() {
        while (Storage.getInstance().isLocked()) {
            char[] masterPassword = context.promptPassword("Enter master password to unlock BaBa:");
            try {
                Storage.getInstance().unlock(masterPassword);
            } catch (InvalidPasswordException e) {
                context.out.println("Invalid master password, try again.");
            } finally {
                Arrays.fill(masterPassword, '\0');
            }
        }
    }

}
