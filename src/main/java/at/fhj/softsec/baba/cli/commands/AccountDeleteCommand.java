package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.model.Account;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;

public class AccountDeleteCommand extends AuthenticatedCommand {
    @Override
    public String name() {
        return "delete";
    }

    @Override
    public String description() {
        return "Create a new account";
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws IOException {
        Long accountNumber = Long.valueOf(args[0]);

        app.accounts().deleteAccount(user, accountNumber);
        context.out.printf("Account %s deleted.\n", accountNumber);
    }
}
