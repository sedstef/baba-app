package at.fhj.softsec.baba.persistence;

import at.fhj.softsec.baba.domain.model.Account;
import at.fhj.softsec.baba.domain.repository.AccountRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static java.lang.String.format;

public class AccountFileRepository implements AccountRepository {

    private final Storage storage;

    public AccountFileRepository(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Long getNextAccountNumber() {
        try {
            Long lastAccountId = Files.find(storage.userDir(), Integer.MAX_VALUE,
                            (path, basicFileAttributes) -> Files.isDirectory(path) && path.getFileName().equals("accounts")
                    ).flatMap(accountsDir -> {
                        try {
                            return Files.list(accountsDir)
                                    .filter(Files::isRegularFile)
                                    .filter(path -> path.getFileName().toString().endsWith(".enc"));
                        } catch (Exception e) {
                            return Stream.empty();
                        }
                    })
                    .map(accountFile -> storage.load(accountFile, Account.class))
                    .map(Account::number)
                    .sorted(Comparator.naturalOrder())
                    .findFirst()
                    .orElse(0L);
            return ++lastAccountId;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Account account) {
        storage.save(accountFile(account.userId(), account.number()), account);
    }

    private Path accountFile(String userId, Long accountNumber) {
        return storage.accountsDir(userId).resolve(format("%s.enc", accountNumber));
    }

}
