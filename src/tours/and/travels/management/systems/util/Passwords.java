package tours.and.travels.management.systems.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Password / security-answer hashing (PBKDF2-HMAC-SHA256).
 * Legacy plain-text values from the old version of the app are still accepted
 * so existing accounts keep working; they are upgraded to a hash on next login.
 */
public final class Passwords {

    private static final int ITERATIONS = 120_000;
    private static final SecureRandom RNG = new SecureRandom();

    private Passwords() { }

    public static String hash(String secret) {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        byte[] dk = derive(secret.toCharArray(), salt, ITERATIONS);
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2$" + ITERATIONS + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(dk);
    }

    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith("pbkdf2$");
    }

    /** Verifies a password against a stored hash (or legacy plain text). */
    public static boolean verify(String secret, String stored) {
        if (stored == null || secret == null) return false;
        if (!isHashed(stored)) return stored.equals(secret);
        try {
            String[] p = stored.split("\\$");
            int iter = Integer.parseInt(p[1]);
            byte[] salt = Base64.getDecoder().decode(p[2]);
            byte[] expected = Base64.getDecoder().decode(p[3]);
            byte[] actual = derive(secret.toCharArray(), salt, iter);
            return MessageDigest.isEqual(expected, actual);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /** Security answers are compared case-insensitively and without surrounding spaces. */
    public static String normalizeAnswer(String answer) {
        return answer == null ? "" : answer.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean verifyAnswer(String answer, String stored) {
        if (stored == null) return false;
        if (!isHashed(stored)) return stored.trim().equalsIgnoreCase(answer == null ? "" : answer.trim());
        return verify(normalizeAnswer(answer), stored);
    }

    private static byte[] derive(char[] secret, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(secret, salt, iterations, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 not available", e);
        }
    }
}
