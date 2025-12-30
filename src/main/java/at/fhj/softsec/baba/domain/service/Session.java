package at.fhj.softsec.baba.domain.service;

import java.util.Optional;

public class Session {

    private AuthenticatedUser currentUser;

    public Optional<AuthenticatedUser> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public void login(AuthenticatedUser user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
