package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;

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
        //AccountService.getInstance().createAccount(context);
        Number accountNumber = Integer.parseInt(args[0]);
        Double amount = Double.parseDouble(args[1]);
        context.out.printf("Account %d balance € %,.2f.\n", accountNumber, amount);
    }
}
