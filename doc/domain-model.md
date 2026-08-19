# Domain Model

## Core types

- **`User`** — a registered user, identified by user ID, with a hashed password.
- **`AuthenticatedUser`** — represents the currently logged-in user within a `Session`.
- **`Account`** — the aggregate root for a bank account: an account number, the owning user's ID, and its `Movement` history. The balance is not stored directly; it is derived by summing all movements.
- **`Movement`** — an immutable, timestamped ledger entry (description + signed amount) recording a deposit, withdrawal or transfer leg.

## Type-safe account access: `OwnedAccount` vs. `ForeignAccount`

The single `Account` class implements two narrower interfaces that express two different trust levels:

```java
public interface AccountView {
    Long getNumber();
}

public interface OwnedAccount extends AccountView {
    String getUserId();
    BigDecimal getBalance();
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount) throws InsufficientFundsException;
    void transferOut(AccountView target, BigDecimal amount) throws InsufficientFundsException;
    Collection<Movement> getMovements();
}

public interface ForeignAccount extends AccountView {
    void transferIn(AccountView source, BigDecimal amount);
}
```

- **`OwnedAccount`** is the full read/write view. It is only ever handed out for accounts that belong to the currently authenticated user, e.g. via `AccountRepository.retrieveByUserAndNumber(user, accountNumber)`.
- **`ForeignAccount`** is a deliberately narrow view exposing only `transferIn(...)`. It is what `AccountRepository.retrieveByNumber(accountNumber)` returns for *any* account number, regardless of who owns it.

This means the compiler — not a runtime check — prevents code in the service layer from ever calling `deposit`, `withdraw`, reading the balance, or reading the movement history of an account that isn't the caller's own. A transfer target can only ever be credited (`transferIn`), never debited or inspected, no matter which account number is supplied.

### Transfer flow

`TransferServiceImpl.transfer(...)` (see [architecture.md](architecture.md#service-layer)) demonstrates the pattern:

```java
OwnedAccount sourceAccount = repository.retrieveByUserAndNumber(user, sourceNumber); // full access, must belong to user
ForeignAccount targetAccount = repository.retrieveByNumber(targetNumber);            // restricted access, any account

sourceAccount.transferOut(targetAccount, amount); // debits source, requires sufficient funds
targetAccount.transferIn(sourceAccount, amount);  // credits target only
```

If `sourceNumber` does not belong to the authenticated user, `retrieveByUserAndNumber` simply won't find it — there is no path by which a user can obtain an `OwnedAccount` for someone else's account.

## Money handling

- Balances and movement amounts are `BigDecimal`, normalized to 2 decimal places with `RoundingMode.HALF_EVEN` on every mutation.
- `deposit`, `withdraw`, `transferOut` and `transferIn` all reject non-positive amounts (`IllegalArgumentException`).
- `withdraw` and `transferOut` throw `InsufficientFundsException` (a checked `ApplicationException`) if the account balance is lower than the requested amount — this is enforced on the domain object itself, not just in the service layer, so it can't be bypassed by a different call path.

## Repositories

`AccountRepository` and `UserRepository` are storage-agnostic interfaces in `domain.repository`; `AccountFileRepository` / `UserFileRepository` in the `persistence` package are their only implementations, backed by encrypted JSON files (see [security.md](security.md)).
