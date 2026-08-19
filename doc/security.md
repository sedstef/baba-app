# Security

All cryptographic code is centralized in the `at.fhj.softsec.baba.security` package (`CryptoUtils`, `MasterKeyLoader`, `StorageEncryptor`). No other package touches `javax.crypto` or `java.security` directly.

## Master key

- On first run, the user picks a master password. It is stretched into a 256-bit AES key using **PBKDF2WithHmacSHA256** with a random 16-byte salt and 200,000 iterations (`CryptoUtils`), and the resulting key material is recorded (iterations, salt, key) in `data/master.key`.
- On every subsequent run, the same password is re-derived with the stored salt/iteration count and compared against the stored key bytes using a constant-time comparison (`MessageDigest.isEqual`) to avoid timing side-channels. A mismatch raises `MasterKeyUnlockException`, and `Main` inserts a fixed delay before allowing another attempt, as a simple brute-force throttle.
- The derived key never leaves the process; the password `char[]` is explicitly zeroed (`Arrays.fill(..., '\0')`) as soon as it's no longer needed.

## Data at rest

- All account and user data is stored as one encrypted file per aggregate under `data/users/<userId>/...` (see `Storage`).
- Each file is JSON-serialized (Jackson) and then encrypted with **AES/GCM/NoPadding** (`StorageEncryptor`), using a random 12-byte IV per file and a 128-bit GCM authentication tag — so tampering with a data file is detectable, not just its contents hidden.
- Encrypted files are laid out as `salt (16 bytes) || iv (12 bytes) || ciphertext`, written with `Files.write`.

## Password storage

User login passwords are stored independently of the master key, hashed with the same PBKDF2WithHmacSHA256 / 200,000-iteration scheme (`CryptoUtils.hash` / `verifyHash`), each with its own random salt. The stored format is `algorithm:iterations:salt:hash`, so the work factor can be changed for new hashes without invalidating old ones. Verification uses a constant-time comparison.

## Access control

Even though the master key can decrypt *any* file in the data directory, the application logic never grants a logged-in user access to another user's accounts — repository lookups are always scoped by the authenticated `User`, and the type system additionally restricts what can be done with any account that isn't the caller's own (see [domain-model.md](domain-model.md#type-safe-account-access-ownedaccount-vs-foreignaccount)). Encryption protects the data on disk; authorization in the service layer protects it at runtime.

## Audit trail

Every service-layer call (login, account operations, transfers, ...) is recorded to `data/audit.log` via a dynamic proxy (`AuditInvocationHandler`), independent of the business logic itself — see [architecture.md](architecture.md#audit-logging).

## Known limitations (threat analysis)

These are conscious trade-offs / open risks, not oversights to silently work around:

- **No single-instance guard.** Nothing prevents two instances of the application from running concurrently against the same data directory; behavior in that case is undefined (last write wins, no locking).
- **Key material lifetime.** Passwords are cleared from memory as soon as possible, but the derived `SecretKey` necessarily stays resident for the life of the process and could in principle be recovered from a heap/core dump.
- **Session switching.** When a user logs out and a different user logs in within the same process, previously loaded account objects for the first user are not forcibly scrubbed from memory — though the CLI exposes no command path to reach another user's data, so this is not an externally reachable vulnerability, only a defense-in-depth gap.
- **No generic exceptions surfaced to the user.** All errors are translated into application-specific exceptions before reaching the CLI, specifically to avoid leaking internal state (stack traces, file paths, etc.) to the console.
