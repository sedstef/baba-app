# BaBa-App - Basic Banking Application

A console-based, security-focused banking application written in Java. The project was built to demonstrate secure software development principles — safe authentication, robust business logic, protection of sensitive data through cryptography, and a clean, layered architecture — rather than to be a feature-complete banking product.

## Features

- **Encrypted storage** — all application data is persisted in encrypted files. A master key, entered at startup, is used to decrypt and re-encrypt the data.
- **User authentication** — register, login, and logout via CLI commands.
- **Account management** — create, list, show, and delete accounts.
- **Money movements** — deposit, withdrawal, and transfer between accounts, including an `InsufficientFundsException` for invalid transfers.
- **Type-safe account access** — authenticated users have full access to their own accounts (`OwnedAccount`), while access to other users' accounts during a transfer is restricted at the type level (`ForeignAccount`), preventing accidental unauthorized access.
- **Audit logging** — service calls are logged transparently via a `java.lang.reflect.Proxy`, keeping the audit concern out of the business logic.
- **Built-in help system** — `help` and `help <command>` print usage information; invalid input prompts hints about the correct command structure.

## Architecture

The application follows a layered architecture (CLI → Service → Persistence), with type-safe account access control and centralized cryptography. See the [`doc/`](doc/) folder for details:

- [doc/architecture.md](doc/architecture.md) — layers, package structure, composition root, audit logging, error handling.
- [doc/domain-model.md](doc/domain-model.md) — the account/user/movement model, and how `OwnedAccount` / `ForeignAccount` use the type system to prevent unauthorized account access.
- [doc/security.md](doc/security.md) — master key derivation, encryption at rest, password hashing, and the threat analysis.

## Tech stack

- Java (standard JDK only for application logic)
- [Jackson](https://github.com/FasterXML/jackson) for data object serialization
- JUnit 5 and Hamcrest for unit testing
- Gradle build (`gradlew`)

## How to Run the Application

To run the application, use the following command in your terminal:

```bash
./gradlew installDist
./build/install/baba/bin/baba
```

If you don't care about passwords, you can run it also directly with gradle:
```bash
./gradlew run --console=plain
```
