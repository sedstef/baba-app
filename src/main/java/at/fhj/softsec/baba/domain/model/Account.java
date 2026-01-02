package at.fhj.softsec.baba.domain.model;

import at.fhj.softsec.baba.exception.InsufficientFundsException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.String.format;

/**
 * Domain aggregate root.
 * Not intended for direct use outside repositories/services.
 */
public class Account implements OwnedAccount, ForeignAccount {
    private static final int SCALE = 2;

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

    @Override
    public Long getNumber() {
        return number;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @JsonIgnore
    @Override
    public BigDecimal getBalance() {
        return movements.stream()
                .map(Movement::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void deposit(BigDecimal amount) {
        amount = normalize(amount);
        requirePositive(amount);
        addMovement("Deposit", amount);
    }

    @Override
    public void withdraw(BigDecimal amount) throws InsufficientFundsException {
        amount = normalize(amount);
        requirePositive(amount);

        if (getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        addMovement("Withdraw", amount.negate());
    }

    @Override
    public void transferOut(AccountView target, BigDecimal amount) throws InsufficientFundsException {
        amount = normalize(amount);
        requirePositive(amount);

        if (getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        addMovement(format("Transfer to %s", target.getNumber()), amount.negate());
    }

    @Override
    public void transferIn(AccountView source, BigDecimal amount) {
        amount = normalize(amount);
        requirePositive(amount);
        addMovement(format("Transfer from %s", source.getNumber()), amount);
    }


    private BigDecimal normalize(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_EVEN);
    }

    private void requirePositive(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public Collection<Movement> getMovements() {
        return Collections.unmodifiableCollection(movements);
    }

    private void addMovement(String description, BigDecimal amount) {
        movements.add(new Movement(LocalDateTime.now(), description, amount));
    }

}
