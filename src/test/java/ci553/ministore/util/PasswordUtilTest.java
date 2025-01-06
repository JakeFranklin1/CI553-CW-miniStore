package ci553.ministore.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Util Tests")
public class PasswordUtilTest {

    @Test
    @DisplayName("Should generate a salt of correct length")
    void testGenerateSalt() {
        String salt = PasswordUtil.generateSalt();
        assertNotNull(salt, "Salt should not be null");
        assertEquals(24, salt.length(), "Salt length should be 24 characters (base64 encoded 16 bytes)");
    }

    @Test
    @DisplayName("Should hash password correctly")
    void testHashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "password123";
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        assertNotNull(hash, "Hash should not be null");
        assertEquals(88, hash.length(), "Hash length should be 88 characters (base64 encoded 64 bytes)");
    }

    @Test
    @DisplayName("Should validate password correctly")
    void testValidatePassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "password123";
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        assertTrue(PasswordUtil.validatePassword(password, salt, hash), "Password should be valid");
        assertFalse(PasswordUtil.validatePassword("wrongpassword", salt, hash), "Password should be invalid");
    }

    @Test
    @DisplayName("Should throw exception for invalid salt")
    void testHashPasswordWithInvalidSalt() {
        String password = "password123";
        String invalidSalt = "invalid_salt";

        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword(password, invalidSalt);
        }, "Should throw IllegalArgumentException for invalid salt");
    }
}
