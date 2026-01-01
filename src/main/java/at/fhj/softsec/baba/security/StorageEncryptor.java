package at.fhj.softsec.baba.security;

import at.fhj.softsec.baba.exception.StorageAccessException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

public class StorageEncryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;

    public static void encrypt(SecretKey secretKey, Path path, byte[] bytes) throws StorageAccessException {
        try {
            byte[] salt = CryptoUtils.generateSalt();

            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            byte[] ciphertext = getCipher(Cipher.ENCRYPT_MODE, secretKey, iv)
                    .doFinal(bytes);

            // Write salt + iv + ciphertext
            byte[] encrypted = new byte[salt.length + iv.length + ciphertext.length];
            System.arraycopy(salt, 0, encrypted, 0, salt.length);
            System.arraycopy(iv, 0, encrypted, salt.length, iv.length);
            System.arraycopy(ciphertext, 0, encrypted, salt.length + iv.length, ciphertext.length);

            Files.write(path, encrypted);
        } catch (GeneralSecurityException | IOException e) {
            throw new StorageAccessException(e.getMessage());
        }
    }

    public static byte[] decrypt(SecretKey secretKey, Path path) throws StorageAccessException {
        try {
            byte[] encrypted = Files.readAllBytes(path);

            byte[] iv = Arrays.copyOfRange(encrypted, 16, 28);
            byte[] ciphertext = Arrays.copyOfRange(encrypted, 28, encrypted.length);

            return getCipher(Cipher.DECRYPT_MODE, secretKey, iv)
                    .doFinal(ciphertext);
        } catch (GeneralSecurityException | IOException e) {
            throw new StorageAccessException(e.getMessage());
        }
    }


    private static Cipher getCipher(int encryptMode, SecretKey secretKey, byte[] iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(
                encryptMode,
                secretKey,
                new GCMParameterSpec(128, iv));
        return cipher;
    }
}
