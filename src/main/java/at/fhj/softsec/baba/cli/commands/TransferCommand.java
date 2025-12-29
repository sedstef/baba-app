package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;

import java.io.IOException;

public class TransferCommand implements Command {
    @Override
    public String name() {
        return "transfer";
    }

    @Override
    public String description() {
        return "Transfer money from an account into another account";
    }

    @Override
    public void execute(String[] args, CliContext context) throws IOException {
        //AccountService.getInstance().createAccount(context);
        Number sourceAccount = Integer.parseInt(args[0]);
        Number targetAccount = Integer.parseInt(args[1]);
        Double amount = Double.parseDouble(args[2]);
        context.out.printf("Account %d balance € %,.2f.\n" , sourceAccount, amount);
        context.out.printf("Account %d balance € %,.2f.\n" , targetAccount, amount);
    }
}
