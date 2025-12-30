package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;

import java.io.IOException;

public class WithdrawalCommand implements Command {
    @Override
    public String name() {
        return "withdrawal";
    }

    @Override
    public String description() {
        return "Withdrawal money from an account";
    }

    @Override
    public void execute(String[] args, Application app, CliContext context) throws IOException {
        //AccountService.getInstance().createAccount(context);
        Number accountNumber = Integer.parseInt(args[0]);
        Double amount = Double.parseDouble(args[1]);
        context.out.printf("Account %d balance € %,.2f.\n" , accountNumber, amount);
    }
}
