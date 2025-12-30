package at.fhj.softsec.baba.storage.model;
import java.util.Objects;

public record User (String userId, String passwordHash) {
    public User {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(passwordHash);
    }
}
