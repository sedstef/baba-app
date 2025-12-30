package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;

public abstract class AuthenticatedCommand implements Command {
    @Override
    public final void execute(String[] args, Application app, CliContext context) throws IOException {
        AuthenticatedUser user = app.session().getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        execute(args, app, context, user);
    }

    protected abstract void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws IOException;
}
