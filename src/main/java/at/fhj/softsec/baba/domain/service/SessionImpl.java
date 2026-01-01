package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.AuthenticatedUser;

import java.util.Optional;

public class SessionImpl implements Session {

    private AuthenticatedUser currentUser;

    @Override
    public Optional<AuthenticatedUser> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    @Override
    public void login(AuthenticatedUser user) {
        this.currentUser = user;
    }

    @Override
    public void logout() {
        this.currentUser = null;
    }

    @Override
    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
