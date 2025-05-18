package backend.klasy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Klasa {@code Password} odpowiada za bezpieczne przechowywanie haseł
 * przy użyciu hashowania i losowej soli. Umożliwia również ich weryfikację.
 */
public class Password {

    private String salt;
    private String hashedPassword;

    /**
     * Tworzy nowy obiekt {@code Password} z hasła w postaci zwykłego tekstu.
     * Generuje nową losową sól i hashuje hasło.
     *
     * @param plainTextPassword hasło użytkownika w formie jawnej
     */
    public Password(String plainTextPassword) {
        this.salt = generateSalt();
        this.hashedPassword = hashPassword(plainTextPassword, salt);
    }

    /**
     * Tworzy obiekt {@code Password} na podstawie istniejących danych:
     * soli i zahashowanego hasła (np. z bazy danych).
     *
     * @param salt           sól w formacie Base64
     * @param hashedPassword hasło zahashowane i zakodowane w Base64
     */
    public Password(String salt, String hashedPassword) {
        this.salt = salt;
        this.hashedPassword = hashedPassword;
    }

    /**
     * Generuje losową sól o długości 16 bajtów i koduje ją w Base64.
     *
     * @return sól zakodowana w Base64
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * Hashuje podane hasło z użyciem podanej soli, stosując algorytm SHA-256.
     *
     * @param password hasło w postaci jawnej
     * @param salt     sól zakodowana w Base64
     * @return zahashowane hasło zakodowane w Base64
     */
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Błąd podczas hashowania hasła", e);
        }
    }

    /**
     * Weryfikuje poprawność podanego hasła względem przechowywanego hash'a i soli.
     *
     * @param plainTextPassword hasło do sprawdzenia
     * @return {@code true} jeśli hasło jest poprawne, {@code false} w przeciwnym razie
     */
    public boolean verify(String plainTextPassword) {
        String inputHash = hashPassword(plainTextPassword, this.salt);
        return inputHash.equals(this.hashedPassword);
    }

    /**
     * Zwraca sól hasła.
     *
     * @return sól w formacie Base64
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Zwraca zahashowane hasło.
     *
     * @return hash hasła zakodowany w Base64
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Przykładowe użycie klasy {@code Password}.
     *
     * @param args argumenty wejściowe (nieużywane)
     */
    public static void main(String[] args) {
        Password password = new Password("haslo");
        System.out.println("Hashed Password: " + password.getHashedPassword());
        System.out.println("Salt: " + password.getSalt());
    }
}
