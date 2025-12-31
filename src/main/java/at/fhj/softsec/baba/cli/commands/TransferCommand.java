package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;
import java.math.BigDecimal;

public class TransferCommand extends AuthenticatedCommand {
    @Override
    public String name() {
        return "transfer";
    }

    @Override
    public String description() {
        return "Transfer money from an account into another account";
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws IOException {
        Long sourceAccount = Long.parseLong(args[0]);
        Long targetAccount = Long.parseLong(args[1]);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[2]));

        OwnedAccount ownedAccount = app.transfer().transfer(user, sourceAccount, targetAccount, amount);

        context.out.printf("Account %d balance € %,.2f.\n" , ownedAccount.getNumber(), ownedAccount.getBalance());
    }
}
