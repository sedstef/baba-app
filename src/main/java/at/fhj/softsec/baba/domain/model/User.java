package at.fhj.softsec.baba.domain.model;
import java.util.Objects;

public record User (String userId, String passwordHash) {
    public User {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(passwordHash);
    }
}
