package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;

import java.io.IOException;

public class AccountListCommand implements Command {
    @Override
    public String name() {
        return "list";
    }

    @Override
    public String description() {
        return "List all existing accounts";
    }

    @Override
    public void execute(String[] args, Application app, CliContext context) throws IOException {
        //AccountService.getInstance().createAccount(context);
        context.out.println("Account listing:");
    }
}
