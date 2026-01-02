package at.fhj.softsec.baba.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record Movement(LocalDateTime timestamp, String description, BigDecimal amount) implements Comparable<Movement> {
    public Movement {
        Objects.requireNonNull(timestamp, "Timestamp must be non null");
        Objects.requireNonNull(description, "Description must be non null");
        Objects.requireNonNull(amount, "Amount must be non null");
    }

    @Override
    public int compareTo(Movement movement) {
        return timestamp.compareTo(movement.timestamp());
    }
}
