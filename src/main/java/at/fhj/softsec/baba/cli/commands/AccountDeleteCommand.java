package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Options;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.exception.InputParseException;

import static java.lang.Long.parseLong;

public class AccountDeleteCommand extends AuthenticatedCommand {
    private final Options options = Options.builder()
            .withLong("account number")
            .build();

    @Override
    public String[] name() {
        return new String[]{"account", "delete"};
    }

    @Override
    public String description() {
        return "Delete a existing account";
    }

    @Override
    public String usage() {
        return options.getUsage();
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws InputParseException {
        options.parse(args);
        Long accountNumber = options.getLong("account number");

        app.account().deleteAccount(user, accountNumber);
        context.out.printf("Account %s deleted.\n", accountNumber);
    }
}
