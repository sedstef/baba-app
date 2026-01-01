package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import static java.lang.Long.parseLong;

public class AccountDeleteCommand extends AuthenticatedCommand {
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
        return "<account number>";
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) {
        Long accountNumber = parseLong(args[0]);

        app.account().deleteAccount(user, accountNumber);
        context.out.printf("Account %s deleted.\n", accountNumber);
    }
}
