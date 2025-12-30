package at.fhj.softsec.baba.persistence;

import at.fhj.softsec.baba.security.CryptoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

public class Storage {

    private final SecretKey secretKey;
    private final Path dataDir;

    public Storage(Path dataDir, SecretKey secretKey) {
        this.dataDir = dataDir;
        this.secretKey = secretKey;
    }

    public <T> T load(Path path, Class<T> type) {
        try {
            byte[] bytes = CryptoUtils.decrypt(Files.readAllBytes(path), secretKey);
            return new ObjectMapper().readValue(bytes, type);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void save(Path path, T object) {
        try {
            Files.createDirectories(path.getParent());
            byte[] bytes = new ObjectMapper().writeValueAsBytes(object);
            Files.write(path, CryptoUtils.encrypt(bytes, secretKey));
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path baseDir(String item) {
        return dataDir.resolve(item);
    }

    public Path userDir() {
        return baseDir("users");
    }

    public Path userDir(String userId) {
        return userDir().resolve(userId);
    }

    public Path accountsDir(String userId) {
        return userDir(userId).resolve("accounts");
    }
}
