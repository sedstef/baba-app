package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;

import static java.lang.String.format;

public class AccountListCommand extends AuthenticatedCommand {
    @Override
    public String name() {
        return "list";
    }

    @Override
    public String description() {
        return "List all accounts of the user";
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user)  {

        context.out.println("Account listing:");
        app.account().retrieveAccounts(user).stream()
                .map(account -> format("%s balance € %,.2f", account.getNumber(), account.getBalance()))
                .forEach(context.out::println);
    }
}
