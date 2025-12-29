package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;

import java.io.IOException;

public class DepositCommand implements Command {
    @Override
    public String name() {
        return "deposit";
    }

    @Override
    public String description() {
        return "Deposit money into an account";
    }

    @Override
    public void execute(String[] args, CliContext context) throws IOException {
        //AccountService.getInstance().createAccount(context);
        Number accountNumber = Integer.parseInt(args[0]);
        Double amount = Double.parseDouble(args[1]);
        context.out.printf("Account %d balance € %,.2f.\n" , accountNumber, amount);
    }
}
