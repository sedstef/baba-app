package at.fhj.softsec.baba.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Account {
    private final Long number;
    private final String userId;
    private final Set<Movement> movements = new TreeSet<>();

    @JsonCreator
    public Account(
            @JsonProperty("number") Long number,
            @JsonProperty("userId") String userId) {
        this.number = Objects.requireNonNull(number, "Number must be non null");
        this.userId = Objects.requireNonNull(userId, "UserID must be non null");
    }

    public Long getNumber() {
        return number;
    }

    public String getUserId() {
        return userId;
    }

    @JsonIgnore
    public BigDecimal getBalance() {
        return movements.stream()
                .map(Movement::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addMovement(BigDecimal amount) {
        movements.add(new Movement(LocalDateTime.now(), amount));
    }

    public record Movement(LocalDateTime timestamp, BigDecimal amount) implements Comparable<Movement> {
        public Movement {
            Objects.requireNonNull(timestamp, "Timestamp must be non null");
            Objects.requireNonNull(amount, "Amount must be non null");
        }

        @Override
        public int compareTo(Movement movement) {
            return timestamp.compareTo(movement.timestamp());
        }
    }
}
