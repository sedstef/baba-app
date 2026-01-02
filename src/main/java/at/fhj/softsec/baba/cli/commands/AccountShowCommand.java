package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Options;
import at.fhj.softsec.baba.domain.model.Movement;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.exception.InputParseException;

import java.util.Comparator;

import static java.lang.Long.parseLong;
import static java.lang.String.format;

public class AccountShowCommand extends AuthenticatedCommand {
    private final Options options = Options.builder()
            .withLong("account number")
            .build();

    @Override
    public String[] name() {
        return new String[]{"account", "show"};
    }

    @Override
    public String description() {
        return "Show details about a account";
    }

    @Override
    public String usage() {
        return options.getUsage();
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws InputParseException {
        options.parse(args);
        Long accountNumber = options.getLong("account number");

        OwnedAccount ownedAccount = app.account().retrieveAccount(user, accountNumber);
        context.out.printf("Account %d balance € %,.2f\n", ownedAccount.getNumber(), ownedAccount.getBalance());
        ownedAccount.getMovements().stream()
                .map(movement -> format("%s: %s € %,.2f", movement.timestamp(), movement.description(), movement.amount()))
                .forEach(context.out::println);
    }
}
