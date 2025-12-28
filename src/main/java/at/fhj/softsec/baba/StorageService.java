package at.fhj.softsec.baba;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class StorageService {

    private final static StorageService instance = new StorageService();

    public static StorageService getInstance() {
        return instance;
    }

    private File storageDir;
    private String currentUsername;

    private StorageService() {
        this.storageDir = new File("data");
    }

    public void setStorageDir(File storageDir) {
        this.storageDir = Objects.requireNonNull(storageDir);
    }

    public void createUser(String username, char[] password) {
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File file = new File(storageDir, String.format("%s.dat", username));
        if (file.exists()) {
            throw new RuntimeException("User already exists");
        }
        try {
            byte[] encrypted = Encryptor.encrypt("".getBytes(), password);
            Files.write(file.toPath(), encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loginUser(String username, char[] password) {
        File file = new File(storageDir, String.format("%s.dat", username));
        if (!file.exists()) {
            throw new RuntimeException("User or password invalid");
        }
        try {
            byte[] encrypted = Files.readAllBytes(file.toPath());
            // Try to decrypt to verify password
            Encryptor.decrypt(encrypted, password);
            this.currentUsername = username;
        } catch (Exception e) {
            throw new RuntimeException("User or password invalid");
        }
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
}
