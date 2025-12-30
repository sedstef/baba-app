package at.fhj.softsec.baba.service;

import at.fhj.softsec.baba.security.CryptoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EncryptorTest {

    @Test
    public void testDecryption(@TempDir Path tmpDir) throws Exception {
        //arrange
        byte[] plaintext = "This is a placeholder test.".getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = CryptoUtils.loadMasterKey(tmpDir, "password".toCharArray());

        //act
        byte[] encrypted = CryptoUtils.encrypt(plaintext, secretKey);
        byte[] result = CryptoUtils.decrypt(encrypted, secretKey);

        //assert
        assertThat(new String(result, StandardCharsets.UTF_8), is("This is a placeholder test."));
    }
}
