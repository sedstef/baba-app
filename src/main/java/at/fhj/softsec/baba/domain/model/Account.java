package at.fhj.softsec.baba.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Account(Long number, String userId) {
    public Account {
        Objects.requireNonNull(number);
        Objects.requireNonNull(userId);
    }

    public BigDecimal balance() {
        return BigDecimal.ZERO;
    }
}
