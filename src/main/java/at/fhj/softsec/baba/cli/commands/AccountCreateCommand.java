package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;

import java.io.IOException;

public class AccountCreateCommand implements Command {
    @Override
    public String name() {
        return "create";
    }

    @Override
    public String description() {
        return "Create a new account";
    }

    @Override
    public void execute(String[] args, Application app, CliContext context) throws IOException {
        //AccountService.getInstance().createAccount(context);
        context.out.println("Account 1 created.");
    }
}
