package at.fhj.softsec.baba.persistence;

import at.fhj.softsec.baba.domain.model.Account;
import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.domain.repository.AccountRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;

import static java.lang.String.format;

public class AccountFileRepository implements AccountRepository {

    private final Storage storage;

    public AccountFileRepository(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Collection<Account> retrieveAll(User user) {
        Path accountsDir = storage.accountsDir(user.userId());
        return Files.exists(accountsDir)
                ? retrieveAccounts(accountsDir)
                : Collections.emptyList();
    }

    @Override
    public Account retrieveByUserAndNumber(User user, Long accountNumber) {
        Path accountFile = accountFile(user.userId(), accountNumber);
        if (Files.notExists(accountFile)) {
            throw new IllegalArgumentException(format("Account %s doesn't exist", accountNumber));
        }
        return storage.load(accountFile, Account.class);
    }

    @Override
    public Account retrieveByNumber(Long accountNumber) {
        PathMatcher matcher = FileSystems.getDefault()
                .getPathMatcher("glob:**/accounts/*.enc");
        try (var paths = Files.walk(storage.dataDir())) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(matcher::matches)
                    .filter(file -> format("%s.enc", accountNumber).equals(file.getFileName().toString()))
                    .findFirst()
                    .map(accountFile -> storage.load(accountFile, Account.class))
                    .orElseThrow(() -> new IllegalArgumentException(format("Account %s doesn't exist", accountNumber)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Long getNextAccountNumber() {
        try (var paths = Files.list(storage.userDir())) {
            Long lastAccountId = paths
                    .map(userDir -> userDir.resolve("accounts"))
                    .filter(Files::exists)
                    .flatMap(accountsDir -> retrieveAccounts(accountsDir).stream())
                    .map(Account::getNumber)
                    .max(Comparator.naturalOrder())
                    .orElse(0L);
            return ++lastAccountId;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Account save(Account account) {
        storage.save(accountFile(account.getUserId(), account.getNumber()), account);
        return account;
    }

    @Override
    public void delete(User user, Long accountNumber) {
        Account account = retrieveByUserAndNumber(user, accountNumber);
        if (BigDecimal.ZERO.compareTo(account.getBalance()) != 0) {
            throw new IllegalStateException("Cannot delete a unbalanced account");
        }
        try {
            Files.delete(accountFile(user.userId(), accountNumber));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Path accountFile(String userId, Long accountNumber) {
        return storage.accountsDir(userId).resolve(format("%s.enc", accountNumber));
    }

    private Collection<Account> retrieveAccounts(Path accountsDir) {
        try (var paths = Files.list(accountsDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".enc"))
                    .map(accountFile -> storage.load(accountFile, Account.class))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
