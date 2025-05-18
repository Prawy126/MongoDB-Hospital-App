package backend.klasy;

import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import org.bson.codecs.pojo.annotations.BsonIgnore;

/**
 * Klasa bazowa {@code Person} reprezentuje ogólne informacje o osobie,
 * takie jak imię, nazwisko, PESEL, wiek oraz dane związane z hasłem.
 * Klasa ta jest dziedziczona przez klasy takie jak {@link Patient} i {@link Doctor}.
 */
public class Person {

    private String firstName;
    private String lastName;
    private int age;
    private long pesel;

    @BsonIgnore
    private Password password;

    private String passwordHash;
    private String passwordSalt;

    /**
     * Domyślny konstruktor wymagany przez MongoDB.
     */
    public Person() {}

    /**
     * Konstruktor z imieniem i nazwiskiem.
     *
     * @param firstName imię osoby
     * @param lastName  nazwisko osoby
     * @throws NullNameException jeśli imię lub nazwisko jest puste
     */
    public Person(String firstName, String lastName) throws NullNameException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Konstruktor z imieniem, nazwiskiem i hasłem jawnym.
     *
     * @param firstName         imię osoby
     * @param lastName          nazwisko osoby
     * @param plainTextPassword hasło w postaci jawnej
     * @throws NullNameException jeśli imię lub nazwisko jest puste
     */
    public Person(String firstName, String lastName, String plainTextPassword) throws NullNameException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPassword(plainTextPassword);
    }

    /**
     * Konstruktor z imieniem, nazwiskiem i PESEL-em.
     *
     * @param firstName imię osoby
     * @param lastName  nazwisko osoby
     * @param pesel     numer PESEL
     * @throws NullNameException jeśli imię lub nazwisko jest puste
     * @throws PeselException    jeśli PESEL ma nieprawidłowy format
     */
    public Person(String firstName, String lastName, long pesel) throws PeselException, NullNameException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
    }

    /**
     * Konstruktor z imieniem, nazwiskiem, PESEL-em i wiekiem.
     */
    public Person(String firstName, String lastName, long pesel, int age) throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
        setAge(age);
    }

    /**
     * Konstruktor z pełnymi danymi oraz hasłem jawnym.
     */
    public Person(String firstName, String lastName, long pesel, int age, String plainTextPassword)
            throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
        setAge(age);
        setPassword(plainTextPassword);
    }

    /**
     * Konstruktor do rekonstrukcji obiektu z bazy danych (hash + sól).
     */
    public Person(String firstName, String lastName, long pesel, int age, String hashedPassword, String salt)
            throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
        setAge(age);
        this.passwordHash = hashedPassword;
        this.passwordSalt = salt;
        this.password = new Password(salt, hashedPassword);
    }

    public String getFirstName() {
        return firstName;
    }

    /**
     * Ustawia imię osoby.
     *
     * @throws NullNameException jeśli imię jest puste
     */
    public void setFirstName(String firstName) throws NullNameException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new NullNameException("Imię nie może być puste");
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Ustawia nazwisko osoby.
     *
     * @throws NullNameException jeśli nazwisko jest puste
     */
    public void setLastName(String lastName) throws NullNameException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new NullNameException("Nazwisko nie może być puste");
        }
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    /**
     * Ustawia wiek osoby.
     *
     * @throws AgeException jeśli wiek jest ujemny
     */
    public void setAge(int age) throws AgeException {
        if (age < 0) {
            throw new AgeException("Wiek musi być większy lub równy 0");
        }
        this.age = age;
    }

    public long getPesel() {
        return pesel;
    }

    /**
     * Ustawia numer PESEL.
     *
     * @throws PeselException jeśli PESEL nie zawiera dokładnie 11 cyfr
     */
    public void setPesel(long pesel) throws PeselException {
        if (pesel < 10000000000L || pesel > 99999999999L) {
            throw new PeselException("Pesel musi składać się z dokładnie 11 cyfr.");
        }
        this.pesel = pesel;
    }

    /**
     * Zwraca obiekt {@link Password} reprezentujący hasło użytkownika.
     */
    @BsonIgnore
    public Password getPassword() {
        return password;
    }

    /**
     * Ustawia hasło użytkownika (jawne) i generuje hash oraz sól.
     */
    @BsonIgnore
    public void setPassword(String plainTextPassword) {
        Password passwordObj = new Password(plainTextPassword);
        this.password = passwordObj;
        this.passwordHash = passwordObj.getHashedPassword();
        this.passwordSalt = passwordObj.getSalt();
    }

    /**
     * Odtwarza obiekt {@link Password} na podstawie zapisanej soli i hasha.
     */
    @BsonIgnore
    public void reconstructPasswordObject() {
        if (passwordSalt != null && passwordHash != null) {
            this.password = new Password(passwordSalt, passwordHash);
        }
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    /**
     * Waliduje poprawność imienia i nazwiska.
     *
     * @throws NullNameException jeśli imię lub nazwisko jest puste
     */
    private void validateName(String firstName, String lastName) throws NullNameException {
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new NullNameException("Imię i nazwisko nie mogą być puste");
        }
    }
}
