package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.exception.AuthenticationException;

public interface AuthService {
    User register(String userId, char[] password) throws AuthenticationException;

    AuthenticatedUser login(String userId, char[] password) throws AuthenticationException;
}
