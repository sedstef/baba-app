package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.model.Account;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;
import java.math.BigDecimal;

public class WithdrawalCommand extends AuthenticatedCommand {
    @Override
    public String name() {
        return "withdrawal";
    }

    @Override
    public String description() {
        return "Withdrawal money from an account";
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws IOException {
        Long accountNumber = Long.parseLong(args[0]);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[1]));

        Account account = app.accounts().withdrawal(user, accountNumber, amount);
        context.out.printf("Account %d balance € %,.2f.\n", account.getNumber(), account.getBalance());
    }
}
