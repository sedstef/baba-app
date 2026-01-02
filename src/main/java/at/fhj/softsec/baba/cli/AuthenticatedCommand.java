package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.exception.ApplicationException;
import at.fhj.softsec.baba.exception.InputParseException;
import at.fhj.softsec.baba.exception.NotAuthenticatedException;

public abstract class AuthenticatedCommand implements Command {

    @Override
    public final void execute(String[] args, Application app, CliContext context) throws ApplicationException {
        AuthenticatedUser user = app.session().getCurrentUser()
                .orElseThrow(() -> new NotAuthenticatedException("User not authenticated"));
        execute(args, app, context, user);
    }

    protected abstract void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws ApplicationException;
}
