# Architecture

## Overview

baba-app is a layered console application. Each layer has a single responsibility and only talks to the layer directly below it:

```
+----------------------------------------------------+
|                     CLI Layer                      |
|          (input parsing, commands, output)          |
+----------------------------------------------------+
|                   Service Layer            A       |
|  +------------------------------------+    u       |
|  |            Domain Model             |    d       |
|  +------------------------------------+    i       |
|  |           Business Logic            |    t       |
|  +------------------------------------+            |
+----------------------------------------------------+
|                  Persistence Layer                  |
|        (encrypted read/write of data files)         |
+----------------------------------------------------+
```

- **CLI Layer** parses console input and renders output. It has no business logic of its own.
- **Service Layer** owns the domain model and business logic (account management, transfers, authentication). This is the only layer the CLI is allowed to call into.
- **Persistence Layer** is responsible for reading and writing encrypted data files. It is only used by the service layer, never directly by the CLI.
- **Audit** is a cross-cutting concern applied around the service layer via a dynamic proxy (see [Audit logging](#audit-logging) below), so business logic classes stay free of logging code.

## Package structure

```
at.fhj.softsec.baba
|- cli                  CLI entry point, input parsing, command dispatch
|  |- commands          One class per CLI command (login, deposit, account create, ...)
|- domain
|  |- audit             Dynamic-proxy based audit logging for service calls
|  |- model             Domain model (Account, User, Movement, ...)
|  |- repository        Repository interfaces (storage-agnostic contracts)
|  |- service           Business logic (AuthService, AccountService, TransferService, Session)
|- exception            Application-specific checked exceptions
|- persistence          Filesystem-backed repository implementations, encrypted Storage
|- security             Cryptography: password hashing, master-key handling, file encryption
```

Root package: `at.fhj.softsec.baba`.

## Composition root

- `Main` is the process entry point. It prompts for the master password, unlocks the master key via `MasterKeyLoader`, then builds the `Application` via `ApplicationBootstrap` and hands control to the CLI loop (`Zion`).
- `ApplicationBootstrap` wires up the whole service layer: it creates a single `Storage` instance (bound to the data directory and the unlocked `SecretKey`), builds the file-backed repositories on top of it, constructs the service implementations, and wraps each service in an audit proxy before exposing it through the `Application` interface.
- `Application` is the single façade the CLI layer depends on. It only exposes the four services the CLI needs: `session()`, `auth()`, `account()`, `transfer()`. No CLI class ever touches a repository or the persistence/security packages directly.

## CLI layer

- `Zion` runs the main read-eval-print loop, reading a line of input and dispatching it.
- `CommandRegistry` maps command names to `Command` implementations. Commands are registered by their name tokens, so both **single-verb commands** (`login`, `deposit`, `withdrawal`, `logout`) and **multi-word commands** (`account create`, `account show`, `account list`, `account delete`) are resolved through the same lookup by matching the longest known token sequence in the input.
- `Options` / `InputParser` handle tokenizing and validating a command line into typed arguments.
- `AuthenticatedCommand` is the base for any command that requires an active session (most commands besides `login`/`register`); it checks the `Session` before delegating to the service layer and throws `NotAuthenticatedException` otherwise.
- Sensitive input (passwords) is never accepted as part of the single-line command — it is always requested separately through a masked prompt (`CliContext.promptPassword`), so it doesn't end up in shell history or in the audit log.
- A built-in help system (`help`, `help <command>`) renders each command's name, description and usage derived from the `Command` implementations themselves.

## Service layer

- `AuthService` — registration and login/logout, backed by `UserRepository`.
- `AccountService` — account creation, listing, lookup and deletion for the authenticated user.
- `TransferService` — deposits, withdrawals and transfers between accounts; see [domain-model.md](domain-model.md) for how transfers enforce that a user can only move money out of their own accounts.
- `Session` — holds the currently authenticated user for the lifetime of the CLI process.

Service implementations depend only on repository interfaces (`AccountRepository`, `UserRepository`), never on the concrete file-based persistence classes — persistence is swappable in principle without touching business logic.

## Persistence layer

- `Storage` is the low-level facade used by the repository implementations. It (de)serializes domain objects to/from JSON via Jackson (`ObjectMapper` with the `JavaTimeModule` for `LocalDateTime` support) and delegates the actual encryption/decryption of bytes to `StorageEncryptor`. It also owns the on-disk directory layout (`data/users/<userId>/accounts/...`).
- `UserFileRepository` and `AccountFileRepository` implement the domain repository interfaces on top of `Storage`, one encrypted file per aggregate.

## Audit logging

Every call into `AuthService`, `AccountService` and `TransferService` is intercepted by `AuditInvocationHandler`, a `java.lang.reflect.InvocationHandler` installed via `Proxy.newProxyInstance` in `ApplicationBootstrap.auditProxy`. On every successful call it appends a `method=<name> args=<args>` line to `data/audit.log`. This keeps audit logging entirely out of the business-logic classes — services are unaware they're being audited.

## Error handling

- All checked application exceptions extend the sealed-in-spirit `ApplicationException` (`AuthenticationException`, `NotAuthenticatedException`, `InsufficientFundsException`, `InputParseException`, `MasterKeyUnlockException`, `StorageAccessException`). No generic/standard-library exception is allowed to leak up to the CLI, so internal state is never exposed to the user.
- Recoverable errors (bad input, wrong password, insufficient funds, ...) are caught at the CLI layer and reported to the user; the prompt loop continues.
- Unrecoverable errors (e.g. an `IOException` from the filesystem, surfaced as `StorageAccessException`) terminate the process with a non-zero exit code (see `Main.main`).

## See also

- [domain-model.md](domain-model.md) — the account/user/movement model and how the type system prevents unauthorized account access.
- [security.md](security.md) — cryptography, master key handling, and the threat analysis.
