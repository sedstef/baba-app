package at.fhj.softsec.baba;

import at.fhj.softsec.baba.service.AuthService;
import at.fhj.softsec.baba.service.Session;
import at.fhj.softsec.baba.storage.Storage;
import at.fhj.softsec.baba.storage.UserFileRepository;
import at.fhj.softsec.baba.storage.UserRepository;

import javax.crypto.SecretKey;
import java.nio.file.Path;

public class ApplicationBootstrap {

    public static Application create(Path dataDir, SecretKey secretKey) {
        final Session session = new Session();

        Storage storage = new Storage(dataDir, secretKey);

        UserRepository userRepository = new UserFileRepository(storage);
        final AuthService authService = new AuthService(userRepository);

        return new Application() {

            @Override
            public Session session() {
                return session;
            }

            @Override
            public AuthService auth() {
                return authService;
            }
        };
    }
}
