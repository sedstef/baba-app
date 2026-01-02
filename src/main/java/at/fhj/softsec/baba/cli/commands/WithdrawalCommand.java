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

public class WithdrawalCommand extends AuthenticatedCommand {
    private final Options options = Options.builder()
            .withLong("account number")
            .withBigDecimal("amount")
            .build();

    @Override
    public String[] name() {
        return new String[]{"withdrawal"};
    }

    @Override
    public String description() {
        return "Withdrawal money from an account";
    }

    @Override
    public String usage() {
        return options.getUsage();
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws InputParseException {
        options.parse(args);
        Long accountNumber = options.getLong("account number");
        BigDecimal amount = options.getBigDecimal("amount");

        OwnedAccount ownedAccount = app.transfer().withdrawal(user, accountNumber, amount);
        context.out.printf("Account %d balance € %,.2f.\n", ownedAccount.getNumber(), ownedAccount.getBalance());
    }
}
