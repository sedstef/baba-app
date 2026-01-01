package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.exception.InputParseException;

import java.math.BigDecimal;

import static at.fhj.softsec.baba.cli.InputParser.parseBigDecimal;
import static at.fhj.softsec.baba.cli.InputParser.parseLong;

public class TransferCommand extends AuthenticatedCommand {
    @Override
    public String[] name() {
        return new String[]{"transfer"};
    }

    @Override
    public String description() {
        return "Transfer money from an account into another account";
    }

    @Override
    public String usage() {
        return "<source account number> <target account number> <amount>";
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws InputParseException {
        Long sourceAccount = parseLong(args[0]);
        Long targetAccount = parseLong(args[1]);
        BigDecimal amount = parseBigDecimal(args[2]);

        OwnedAccount ownedAccount = app.transfer().transfer(user, sourceAccount, targetAccount, amount);

        context.out.printf("Account %d balance € %,.2f.\n", ownedAccount.getNumber(), ownedAccount.getBalance());
    }
}
