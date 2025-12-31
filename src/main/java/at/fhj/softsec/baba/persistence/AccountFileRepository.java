package at.fhj.softsec.baba.persistence;

import at.fhj.softsec.baba.domain.model.*;
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

import static java.lang.String.format;

public class AccountFileRepository implements AccountRepository {

    private final Storage storage;

    public AccountFileRepository(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Collection<OwnedAccount> retrieveAll(User user) {
        Path accountsDir = storage.accountsDir(user.userId());
        return Files.exists(accountsDir)
                ? retrieveAccounts(accountsDir)
                : Collections.emptyList();
    }

    @Override
    public OwnedAccount retrieveByUserAndNumber(User user, Long accountNumber) {
        Path accountFile = accountFile(user.userId(), accountNumber);
        if (Files.notExists(accountFile)) {
            throw new IllegalArgumentException(format("Account %s doesn't exist", accountNumber));
        }
        return loadAccount(accountFile);
    }

    @Override
    public ForeignAccount retrieveByNumber(Long accountNumber) {
        PathMatcher matcher = FileSystems.getDefault()
                .getPathMatcher("glob:**/accounts/*.enc");
        try (var paths = Files.walk(storage.dataDir())) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(matcher::matches)
                    .filter(file -> format("%s.enc", accountNumber).equals(file.getFileName().toString()))
                    .findFirst()
                    .map(accountFile -> loadAccount(accountFile))
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
                    .map(OwnedAccount::getNumber)
                    .max(Comparator.naturalOrder())
                    .orElse(0L);
            return ++lastAccountId;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public OwnedAccount create(User user, Long accountNumber) {
        return save(new Account(accountNumber, user.userId()));
    }

    @Override
    public <T extends AccountView> T save(T account) {
        if (!(account instanceof Account persistableAccount)) {
            throw new IllegalStateException("Account must be loaded from AccountRepository");
        }
        Path accountFile = accountFile(persistableAccount.getUserId(), persistableAccount.getNumber());
        storage.save(accountFile, persistableAccount);
        return account;
    }

    @Override
    public void delete(User user, Long accountNumber) {
        OwnedAccount ownedAccount = retrieveByUserAndNumber(user, accountNumber);
        if (BigDecimal.ZERO.compareTo(ownedAccount.getBalance()) != 0) {
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

    private Collection<OwnedAccount> retrieveAccounts(Path accountsDir) {
        try (var paths = Files.list(accountsDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".enc"))
                    .map(accountFile -> (OwnedAccount)loadAccount(accountFile))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Account loadAccount(Path accountFile) {
        return storage.load(accountFile, Account.class);
    }

}
