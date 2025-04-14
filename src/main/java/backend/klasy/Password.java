package backend.klasy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Klasa odpowiedzialna za hashowanie haseł i generowanie soli.
 */
public class Password {

    private String salt;
    private String hashedPassword;

    /**
     * Konstruktor tworzący nowe hasło z automatycznie wygenerowaną solą.
     *
     * @param plainTextPassword hasło w postaci zwykłego tekstu
     */
    public Password(String plainTextPassword) {
        this.salt = generateSalt();
        this.hashedPassword = hashPassword(plainTextPassword, salt);
    }

    /**
     * Konstruktor używany przy wczytywaniu zahashowanego hasła i soli z bazy danych.
     *
     * @param salt           wcześniej wygenerowana sól
     * @param hashedPassword wcześniej zahashowane hasło
     */
    public Password(String salt, String hashedPassword) {
        this.salt = salt;
        this.hashedPassword = hashedPassword;
    }

    /**
     * Generuje losową sól.
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
     * Hashuje hasło z podaną solą.
     *
     * @param password hasło w postaci zwykłego tekstu
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
     * Weryfikuje poprawność hasła na podstawie przechowywanego hasha i soli.
     *
     * @param plainTextPassword hasło do sprawdzenia
     * @return true jeśli hasło jest poprawne, false w przeciwnym razie
     */
    public boolean verify(String plainTextPassword) {
        String inputHash = hashPassword(plainTextPassword, this.salt);
        return inputHash.equals(this.hashedPassword);
    }

    /**
     * Zwraca sól hasła.
     *
     * @return sól zakodowana w Base64
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Zwraca zahashowane hasło.
     *
     * @return hash zakodowany w Base64
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    public static void main(String[] args) {
        Password password = new Password("haslo");
        System.out.println("Hashed Password: " + password.getHashedPassword());
        System.out.println("Salt: " + password.getSalt());

    }
}
