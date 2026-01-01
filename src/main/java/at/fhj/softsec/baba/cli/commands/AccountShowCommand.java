package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;

import static java.lang.Long.parseLong;

public class AccountShowCommand extends AuthenticatedCommand {
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
        return "<account number>";
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) {
        Long accountNumber = parseLong(args[0]);

        OwnedAccount ownedAccount = app.account().retrieveAccount(user, accountNumber);
        context.out.printf("Account %d balance € %,.2f\n", ownedAccount.getNumber(), ownedAccount.getBalance());
    }
}
