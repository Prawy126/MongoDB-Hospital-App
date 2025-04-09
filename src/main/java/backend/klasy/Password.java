package backend.klasy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Password {
    private String salt;
    private String hashedPassword;

    public Password(String plainTextPassword) {
        this.salt = generateSalt();
        this.hashedPassword = hashPassword(plainTextPassword, salt);
    }

    //Konstruktor do wczytywania danych z bazy
    //Musiałem jakoś to rozwiązać, problem bo jak się odpalało program to hash zawsze był inny nawet przy takim samym haśle
    public Password(String salt, String hashedPassword) {
        this.salt = salt;
        this.hashedPassword = hashedPassword;
    }
    /**
     * Funkcja do generowanie "soli"
     * @return zwraca wygenerowaną sól w postaci stringa
     * */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Metoda do hashowania hasła z wykorzystaniem soli
     * @param password hasło w postaci stringa
     * @param salt sól, którą można wygenerować za pomocą funckji generateSalt()
     * @return zwraca zahashowane hasło
     * */
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Błąd podczas hashowania", e);
        }
    }

    /**
     * Metoda do sprawdzania czy hasło jest poprawne
     * @param plainTextPassword hasło do sprawdzenia z oryginalnym hasłem
     * @return wartość boolean jeśli true to hasło poprawne, jeśli false hasło nie poprawne
     * */
    public boolean verify(String plainTextPassword) {
        String inputHash = hashPassword(plainTextPassword, this.salt);
        return inputHash.equals(this.hashedPassword);
    }

    // Należy się zastanowić czy w ogóle jest nam to potrzebne?
    public String getSalt() {
        return salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}