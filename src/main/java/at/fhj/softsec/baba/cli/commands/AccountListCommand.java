package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class AccountListCommand implements Command {
    @Override
    public String name() {
        return "list";
    }

    @Override
    public String description() {
        return "List all accounts of the user";
    }

    @Override
    public void execute(String[] args, Application app, CliContext context) throws IOException {
        AuthenticatedUser user = app.session().getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        context.out.println("Account listing:");
        app.accounts().retrieveAcounts(user).stream()
                .map(account -> format("%s", account.number()))
                .forEach(context.out::println);
    }
}
