package at.fhj.softsec.baba;

import at.fhj.softsec.baba.domain.service.AccountsService;
import at.fhj.softsec.baba.domain.service.AuthService;
import at.fhj.softsec.baba.domain.service.Session;
import at.fhj.softsec.baba.persistence.AccountFileRepository;
import at.fhj.softsec.baba.persistence.Storage;
import at.fhj.softsec.baba.persistence.UserFileRepository;
import at.fhj.softsec.baba.domain.repository.UserRepository;

import javax.crypto.SecretKey;
import java.nio.file.Path;

public class ApplicationBootstrap {

    public static Application create(Path dataDir, SecretKey secretKey) {
        final Session session = new Session();

        Storage storage = new Storage(dataDir, secretKey);

        final AuthService authService = new AuthService(new UserFileRepository(storage));
        final AccountsService accountsService = new AccountsService(
                new AccountFileRepository(storage),
                new UserFileRepository(storage)
        );
        return new Application() {

            @Override
            public Session session() {
                return session;
            }

            @Override
            public AuthService auth() {
                return authService;
            }

            @Override
            public AccountsService accounts() {
                return accountsService;
            }
        };
    }
}
