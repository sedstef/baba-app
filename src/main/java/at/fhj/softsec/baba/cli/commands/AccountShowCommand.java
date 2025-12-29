package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;

import java.io.IOException;

public class AccountShowCommand implements Command {
    @Override
    public String name() {
        return "show";
    }

    @Override
    public String description() {
        return "Show a new account";
    }

    @Override
    public void execute(String[] args, CliContext context) throws IOException {
        //AccountService.getInstance().createAccount(context);
        context.out.println("Account 1 balance 0.");
    }
}
