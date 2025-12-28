package at.fhj.softsec.baba;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EncryptorTest {

    @Test
    public void testDecryption() throws Exception {
        //arrange
        byte[] plaintext = "This is a placeholder test.".getBytes(StandardCharsets.UTF_8);
        String password = "password";

        //act
        byte[] encrypted = Encryptor.encrypt(plaintext, password.toCharArray());
        byte[] result = Encryptor.decrypt(encrypted, password.toCharArray());

        //assert
        assertThat(new String(result, StandardCharsets.UTF_8), is("This is a placeholder test."));
    }
}
