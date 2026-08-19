# Architecture Internals

This document goes one level deeper than [architecture.md](architecture.md): it explains the notable design decisions worth understanding before changing this codebase, and lists known weaknesses found during review. It reflects the state of the code as reviewed; check the current source before relying on line numbers.

## Notable patterns and design decisions

### Capability-based account access (`OwnedAccount` / `ForeignAccount`)

The single `Account` class implements two interfaces that express two different trust levels (`domain/model/OwnedAccount.java`, `domain/model/ForeignAccount.java`):

- `OwnedAccount` — full read/write view, only ever handed out for the authenticated user's own accounts (`AccountRepository.retrieveByUserAndNumber`).
- `ForeignAccount` — a narrow view exposing only `transferIn(...)`, returned for *any* account number looked up by `AccountRepository.retrieveByNumber`.

This turns "a user must not be able to withdraw from or inspect another user's account" from a runtime check into something the compiler enforces: there is no method to call that would do it. See [domain-model.md](domain-model.md) for the full write-up. This is the strongest architectural idea in the codebase and is worth preserving in any refactor.

### Cross-cutting audit logging via dynamic proxy

`ApplicationBootstrap.auditProxy` wraps each service in a `java.lang.reflect.Proxy` backed by `AuditInvocationHandler` (`domain/audit/AuditInvocationHandler.java`). Every successful call to `AuthService`, `AccountService`, `TransferService` is logged to `data/audit.log` without any service implementation being aware of it. Clean separation of a cross-cutting concern from business logic; if audit requirements grow (e.g. structured logging, log rotation, redacting arguments), this is the one place to change.

### Self-enforcing domain invariants

`Account.withdraw` / `Account.transferOut` check sufficient funds *inside the aggregate itself* (`domain/model/Account.java`), not only in the service layer, so there is no alternate call path that bypasses the check. Balance is derived from the `Movement` ledger rather than stored redundantly — see [Negative findings](#negative-findings) below for a caveat on how that ledger is stored.

### Facade + dependency inversion

`Application` (`Application.java`) is the only type the CLI layer depends on; services depend on `AccountRepository`/`UserRepository` interfaces, never on the file-backed implementations directly. `ApplicationBootstrap` is the single composition root. This makes the persistence layer swappable in principle and keeps the CLI ignorant of encryption, file layout, and Jackson.

### Command dispatch without a custom grammar

`CommandRegistry` (`cli/CommandRegistry.java`) resolves both single-verb commands (`login`, `deposit`) and multi-word commands (`account create`, `account show`) by matching the longest known token sequence against a `LinkedHashMap<String[], Command>`. A small, effective solution that avoided building a parser for something this size.

### Testable I/O boundary

`CliContext` wraps `BufferedReader`/`PrintWriter` instead of using `System.in`/`System.out` directly, which is what lets `ZionTest` drive the whole application (real crypto, real encrypted file storage, no mocks) through scripted input and assert on rendered output. This is a deliberate and effective testability seam.

## Negative findings

### Architecture / persistence

- **The exception hierarchy is bypassed by the persistence layer.** `AccountFileRepository` throws raw `IllegalArgumentException`, `IllegalStateException`, and `UncheckedIOException` (`persistence/AccountFileRepository.java`, e.g. in `retrieveByUserAndNumber`, `retrieveByNumber`, `getNextAccountNumber`, `delete`, `save`) instead of an `ApplicationException` subtype. `Zion.promptLoop` only catches `ApplicationException` (`cli/Zion.java`), so these exceptions propagate uncaught and crash the process with a raw stack trace — contradicting the documented goal (see [security.md](security.md#known-limitations-threat-analysis)) that no generic exception is ever surfaced to the user.
- **Bare `orElseThrow()` on the authenticated-user lookup.** `AccountServiceImpl` and `TransferServiceImpl` call `userRepository.findById(authenticatedUser.getUserId()).orElseThrow()` with no message (`domain/service/AccountServiceImpl.java`, `domain/service/TransferServiceImpl.java`). If this path is ever hit (e.g. a user record deleted mid-session), it throws an undocumented, unchecked `NoSuchElementException` — same class of leak as above.
- **`AccountRepository.retrieveByNumber` and `getNextAccountNumber` do a full filesystem walk/list over every user's accounts** (`persistence/AccountFileRepository.java`) on every transfer-target lookup and every account creation. This is an O(total accounts) operation with no index, and `getNextAccountNumber` is a scan-then-increment with no locking — a real TOCTOU race for account-number collisions if two account creations ever ran concurrently (the project's own threat analysis already flags "no protection against concurrent instances"; this is the concrete mechanism that would misbehave).
- **`AccountFileRepository.save`'s generic signature is not actually type-safe.** `<T extends AccountView> T save(T account)` immediately does `instanceof Account persistableAccount`, throwing `IllegalStateException` for anything else (`persistence/AccountFileRepository.java`). The `AccountView` abstraction only provides real safety for the *capability* (read/write) split, not for identity — a second implementation of `OwnedAccount`/`ForeignAccount` would compile but fail at runtime.
- **Domain model is coupled to the serialization framework.** `Account` carries Jackson annotations (`@JsonCreator`, `@JsonProperty`, `@JsonIgnore`) directly (`domain/model/Account.java`), so `domain.model` has a compile-time dependency on a specific persistence-adjacent library, not just `persistence`.
- **Minor structural inconsistency:** the `help` command is built as an anonymous inner class inline in `Zion`'s constructor (`cli/Zion.java`) instead of its own `Command` implementation like every other command in `cli/commands/`.
- **Hardcoded, duplicated data directory.** `Main.DATA_DIR = Path.of("data")` is duplicated as a separate literal in the `ApplicationBootstrap.create(Path.of("data"), secretKey)` call (`Main.java`) rather than reusing the constant; no way to relocate the data directory via env var or flag.

### Domain model correctness

- **Latent ledger-corruption bug in `Account.movements`.** Movements are stored in a `TreeSet<Movement>` (`domain/model/Account.java`), and `Movement.compareTo` orders — and therefore, per `TreeSet` semantics, *deduplicates* — purely by `timestamp` (`domain/model/Movement.java`). If two movements on the same account are created with an identical `LocalDateTime.now()` value (plausible on coarse clock resolution or two rapid successive operations), `TreeSet.add` silently drops the second one and the account balance becomes wrong with no error raised anywhere. No current test would catch this — see below.

### Test coverage

- **Happy paths for CLI commands are covered end-to-end** (`cli/ZionTest.java`) via a real, unmocked stack (encryption, file storage, service layer, CLI rendering) for `help`, `register`, `login`, `logout`, `account list/create/show/delete`, `deposit`, `withdrawal`, `transfer`. This is a genuine strength, not a gap.
- **The `transfer` happy-path test only verifies the debit side.** The `transfer 1 2 20.00` case asserts the source account's balance drops correctly but never asserts the target account actually received the credited amount — the `ForeignAccount.transferIn` effect, which is the interesting part of the type-safety design, has no assertion behind it.
- **All assertions go through rendered CLI text**, not through direct calls to the service/domain layer. A wording change in a command's output and an actual balance-calculation bug are indistinguishable failures to this suite; there is no test that checks `Account`, `AccountServiceImpl`, or `TransferServiceImpl` state directly.
- **Currency assertions are locale-dependent** (e.g. `"€ 20,00"`, comma-decimal) with no `Locale.setDefault` pinned in the tests — these "happy path" tests can fail on a JVM with a different default locale for reasons unrelated to the code under test.
- **No test proves the two invariants the architecture is built around actually fire**: insufficient-funds rejection (`InsufficientFundsException`) and the cross-account type-safety block have no test coverage at all, positive or negative.
- **Other uncovered error/edge paths:** duplicate registration, wrong-password login, deleting an account with a nonzero balance, `Main`'s wrong-master-password retry loop (`MainTest` only exercises fresh-key creation followed by immediate `exit`).

## See also

- [architecture.md](architecture.md) — layers, package structure, composition root.
- [domain-model.md](domain-model.md) — the account/user/movement model in detail.
- [security.md](security.md) — cryptography and the threat analysis.
