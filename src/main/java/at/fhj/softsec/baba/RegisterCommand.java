package at.fhj.softsec.baba;

import java.io.IOException;
import java.util.Arrays;

public class RegisterCommand implements Command {

    @Override
    public String name() {
        return "register";
    }

    @Override
    public String description() {
        return "Add a new user";
    }

    @Override
    public void execute(String[] args, CliContext ctx) throws IOException {

        //ctx.out.println("Creating new user (Ctrl+C to cancel)");

        String username = ctx.prompt("Username: ");

        char[] pw1 = ctx.promptPassword("Password: ");
        char[] pw2 = ctx.promptPassword("Verify password: ");

        try {
            if (!Arrays.equals(pw1, pw2)) {
                ctx.out.println("Passwords do not match.");
                return;
            }
            AuthService.getInstance().createUser(username, pw1);
        } finally {
            // IMPORTANT: wipe passwords
            Arrays.fill(pw1, '\0');
            Arrays.fill(pw2, '\0');
        }

        //ctx.out.println("User '" + username + "' created.");
    }
}
