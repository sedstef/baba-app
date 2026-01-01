package at.fhj.softsec.baba.persistence;

import at.fhj.softsec.baba.exception.StorageAccessException;
import at.fhj.softsec.baba.security.StorageEncryptor;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Storage {

    private final SecretKey secretKey;
    private final Path dataDir;

    public Storage(Path dataDir, SecretKey secretKey) {
        this.dataDir = dataDir;
        this.secretKey = secretKey;
    }

    public <T> T load(Path path, Class<T> type) {
        try {
            byte[] bytes = StorageEncryptor.decrypt(secretKey, path);
            return new ObjectMapper().readValue(bytes, type);
        } catch (IOException e) {
            throw new StorageAccessException(e);
        }
    }

    public <T> void save(Path path, T object) {
        try {
            Files.createDirectories(path.getParent());
            byte[] bytes = new ObjectMapper().writeValueAsBytes(object);
            StorageEncryptor.encrypt(secretKey, path, bytes);
        } catch (IOException e) {
            throw new StorageAccessException(e);
        }
    }


    public Path dataDir() {
        return dataDir;
    }

    public Path userDir() {
        return dataDir.resolve("users");
    }

    public Path userDir(String userId) {
        return userDir().resolve(userId);
    }

    public Path accountsDir(String userId) {
        return userDir(userId).resolve("accounts");
    }

}
