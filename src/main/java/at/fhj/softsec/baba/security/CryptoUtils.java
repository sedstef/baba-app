package at.fhj.softsec.baba.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public final class CryptoUtils {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String CIPHER_ALGORITHM = "AES";
    private static final int ITERATIONS = 200_000;
    private static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // bytes

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;

    private static final SecureRandom RANDOM = new SecureRandom();

    private CryptoUtils() {
        // utility class
    }

    public static SecretKey loadMasterKey(Path data, char[] masterPassword) throws GeneralSecurityException {
        byte[] salt = loadOrCreateSalt(data.resolve("master.salt"));
        byte[] keyBytes = pbkdf2(masterPassword, salt, ITERATIONS, KEY_LENGTH);
        return new SecretKeySpec(keyBytes, CIPHER_ALGORITHM);
    }

    public static byte[] encrypt(byte[] plaintext, SecretKey secretKey) throws GeneralSecurityException {
        byte[] salt = generateSalt();

        byte[] iv = new byte[IV_LENGTH];
        RANDOM.nextBytes(iv);

        byte[] ciphertext = getCipher(Cipher.ENCRYPT_MODE, secretKey, iv)
                .doFinal(plaintext);

        // Write salt + iv + ciphertext
        byte[] encrypted = new byte[salt.length + iv.length + ciphertext.length];
        System.arraycopy(salt, 0, encrypted, 0, salt.length);
        System.arraycopy(iv, 0, encrypted, salt.length, iv.length);
        System.arraycopy(ciphertext, 0, encrypted, salt.length + iv.length, ciphertext.length);

        return encrypted;
    }

    public static byte[] decrypt(byte[] encrypted, SecretKey secretKey) throws GeneralSecurityException {
        byte[] iv = Arrays.copyOfRange(encrypted, 16, 28);
        byte[] ciphertext = Arrays.copyOfRange(encrypted, 28, encrypted.length);

        return getCipher(Cipher.DECRYPT_MODE, secretKey, iv)
                .doFinal(ciphertext);
    }


    /**
     * Hashes a password for storage.
     */
    public static String hash(char[] password) {
        try {
            byte[] salt = generateSalt();
            byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

            return String.format(
                    "%s:%d:%s:%s",
                    ALGORITHM,
                    ITERATIONS,
                    Base64.getEncoder().encodeToString(salt),
                    Base64.getEncoder().encodeToString(hash)
            );
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }

    /**
     * Verifies a password against a stored hash.
     */
    public static boolean verifyHash(char[] password, String stored) {
        try {
            String[] parts = stored.split(":");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid password hash format");
            }

            String algorithm = parts[0];
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            if (!ALGORITHM.equals(algorithm)) {
                throw new IllegalStateException("Unsupported hash algorithm");
            }

            byte[] actualHash = pbkdf2(
                    password,
                    salt,
                    iterations,
                    expectedHash.length * 8
            );

            Arrays.fill(password, '\0');

            // Constant-time comparison
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to verify password hash", e);
        }
    }

    private static byte[] loadOrCreateSalt(Path saltFile) {
        try {
            if (Files.exists(saltFile)) {
                return Files.readAllBytes(saltFile);
            }

            Files.createDirectories(saltFile.getParent());

            byte[] salt = generateSalt();
            Files.write(saltFile, salt);
            return salt;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load or create master salt", e);
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return salt;
    }

    private static byte[] pbkdf2(
            char[] password,
            byte[] salt,
            int iterations,
            int keyLength
    ) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                keyLength
        );
        return SecretKeyFactory.getInstance(ALGORITHM)
                .generateSecret(spec).getEncoded();
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
