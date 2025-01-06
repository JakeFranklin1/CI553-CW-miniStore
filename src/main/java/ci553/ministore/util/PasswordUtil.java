package ci553.ministore.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for password hashing and validation.
 * Provides methods to generate a salt, hash a password, and validate a password.
 */
public class PasswordUtil {
    private static final int SALT_LENGTH = 16;  // Length of the salt in bytes
    private static final int HASH_LENGTH = 64;  // Length of the hash in bytes
    private static final int ITERATIONS = 10000;  // Number of iterations for the hashing algorithm

    /**
     * Generates a random salt for password hashing.
     *
     * @return A base64-encoded string representing the salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);  // Generate random salt
        return Base64.getEncoder().encodeToString(salt);  // Encode salt to base64 string
    }

    /**
     * Hashes a password using the provided salt.
     *
     * @param password The password to hash
     * @param salt The base64-encoded salt
     * @return A base64-encoded string representing the hashed password
     * @throws NoSuchAlgorithmException If the hashing algorithm is not available
     * @throws InvalidKeySpecException If the key specification is invalid
     */
    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Create PBEKeySpec with password, decoded salt, iterations, and hash length
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, HASH_LENGTH * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");  // Get instance of hashing algorithm
        byte[] hash = factory.generateSecret(spec).getEncoded();  // Generate hash
        return Base64.getEncoder().encodeToString(hash);  // Encode hash to base64 string
    }

    /**
     * Validates a password against the expected hash using the provided salt.
     *
     * @param password The password to validate
     * @param salt The base64-encoded salt
     * @param expectedHash The base64-encoded expected hash
     * @return true if the password is valid, false otherwise
     * @throws NoSuchAlgorithmException If the hashing algorithm is not available
     * @throws InvalidKeySpecException If the key specification is invalid
     */
    public static boolean validatePassword(String password, String salt, String expectedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hash = hashPassword(password, salt);  // Hash the password with the provided salt
        return hash.equals(expectedHash);  // Compare the generated hash with the expected hash
    }
}
