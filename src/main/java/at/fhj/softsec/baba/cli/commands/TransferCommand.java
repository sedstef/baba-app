package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Options;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.exception.InputParseException;

import java.math.BigDecimal;

import static at.fhj.softsec.baba.cli.InputParser.parseBigDecimal;
import static at.fhj.softsec.baba.cli.InputParser.parseLong;

public class TransferCommand extends AuthenticatedCommand {
    private final Options options = Options.builder()
            .withLong("source account number")
            .withLong("target account number")
            .withBigDecimal("amount")
            .build();

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
        return options.getUsage();
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws InputParseException {
        options.parse(args);
        Long sourceAccount = options.getLong("source account number");
        Long targetAccount = options.getLong("target account number");
        BigDecimal amount = options.getBigDecimal("amount");

        OwnedAccount ownedAccount = app.transfer().transfer(user, sourceAccount, targetAccount, amount);

        context.out.printf("Account %d balance € %,.2f.\n", ownedAccount.getNumber(), ownedAccount.getBalance());
    }
}
