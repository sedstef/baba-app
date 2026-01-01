package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.exception.InputParseException;

import java.math.BigDecimal;

import static at.fhj.softsec.baba.cli.InputParser.parseBigDecimal;
import static java.lang.Long.parseLong;

public class DepositCommand extends AuthenticatedCommand {
    @Override
    public String[] name() {
        return new String[]{"deposit"};
    }

    @Override
    public String description() {
        return "Deposit money into an account";
    }

    @Override
    public String usage() {
        return "<account number> <amount>";
    }
    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws InputParseException {
        Long accountNumber = parseLong(args[0]);
        BigDecimal amount = parseBigDecimal(args[1]);

        OwnedAccount ownedAccount = app.transfer().deposit(user, accountNumber, amount);
        context.out.printf("Account %d balance € %,.2f.\n", ownedAccount.getNumber(), ownedAccount.getBalance());
    }
}
