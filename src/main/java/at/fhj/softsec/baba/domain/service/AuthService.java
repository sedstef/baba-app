package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.security.CryptoUtils;
import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.domain.repository.UserRepository;

import java.util.Objects;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String userId, char[] password) {

        validateUserId(userId);
        Objects.requireNonNull(password);

        if (userRepository.exists(userId)) {
            throw new AuthenticationException("User already exists");
        }

        String passwordHash = CryptoUtils.hash(password);

        User user = new User(userId, passwordHash);
        return userRepository.save(user);
    }

    public AuthenticatedUser login(String userId, char[] password) {

        Objects.requireNonNull(password);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new AuthenticationException("Invalid credentials")
                );

        boolean valid = CryptoUtils.verifyHash(
                password,
                user.passwordHash()
        );
        if (!valid) {
            throw new AuthenticationException("Invalid credentials");
        }
        return new AuthenticatedUser(user.userId());
    }

    private void validateUserId(String userId) {
        if (!userId.matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("Invalid user id");
        }
    }
}
