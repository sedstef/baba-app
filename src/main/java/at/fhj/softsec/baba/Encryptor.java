package at.fhj.softsec.baba;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

public class Encryptor {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int KEY_LENGTH = 256;
    private static final int ITERATIONS = 65536;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static byte[] encrypt(
            byte[] plaintext,
            char[] password) throws GeneralSecurityException {

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        byte[] iv = new byte[IV_LENGTH];
        RANDOM.nextBytes(iv);

        byte[] ciphertext = getCipher(Cipher.ENCRYPT_MODE, password, salt, iv)
                .doFinal(plaintext);

        // Write salt + iv + ciphertext
        byte[] encrypted = new byte[salt.length + iv.length + ciphertext.length];
        System.arraycopy(salt, 0, encrypted, 0, salt.length);
        System.arraycopy(iv, 0, encrypted, salt.length, iv.length);
        System.arraycopy(ciphertext, 0, encrypted, salt.length + iv.length, ciphertext.length);

        return encrypted;
    }

    public static byte[] decrypt(
            byte[] fileData,
            char[] password) throws GeneralSecurityException {
        byte[] salt = Arrays.copyOfRange(fileData, 0, 16);
        byte[] iv = Arrays.copyOfRange(fileData, 16, 28);
        byte[] ciphertext = Arrays.copyOfRange(fileData, 28, fileData.length);

        return getCipher(Cipher.DECRYPT_MODE, password, salt, iv)
                .doFinal(ciphertext);
    }

    private static Cipher getCipher(int encryptMode, char[] password, byte[] salt, byte[] iv) throws GeneralSecurityException {
        // Key derivation
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        // Cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(
                encryptMode,
                secretKey,
                new GCMParameterSpec(128, iv));
        return cipher;
    }
}

