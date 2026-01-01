package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.AuthenticatedUser;

import java.util.Optional;

public interface Session {
    Optional<AuthenticatedUser> getCurrentUser();

    void login(AuthenticatedUser user);

    void logout();

    boolean isAuthenticated();
}
