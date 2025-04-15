package backend.klasy;

import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;

import org.bson.codecs.pojo.annotations.BsonIgnore;

/**
 * Klasa reprezentująca osobę. Jest klasą bazową dla innych klas.
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
     */
    public Person(
            String firstName,
            String lastName
    ) throws NullNameException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Konstruktor z imieniem, nazwiskiem i hasłem.
     */
    public Person(
            String firstName,
            String lastName,
            String plainTextPassword
    ) throws NullNameException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPassword(plainTextPassword);
    }

    /**
     * Konstruktor z imieniem, nazwiskiem i PESELem.
     */
    public Person(
            String firstName,
            String lastName,
            long pesel
    ) throws PeselException, NullNameException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
    }

    /**
     * Konstruktor z imieniem, nazwiskiem, PESELem i wiekiem.
     */
    public Person(String firstName,
                  String lastName,
                  long pesel,
                  int age
    ) throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
        setAge(age);
    }

    /**
     * Konstruktor z imieniem, nazwiskiem, PESELem, wiekiem i hasłem.
     */
    public Person(
            String firstName,
            String lastName,
            long pesel,
            int age,
            String plainTextPassword
    ) throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
        setAge(age);
        setPassword(plainTextPassword);
    }

    /**
     * Konstruktor do odtworzenia obiektu z bazy danych (hash + sól).
     */
    public Person(String firstName, String lastName, long pesel, int age, String hashedPassword, String salt) throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
        setAge(age);
        this.passwordHash = hashedPassword;
        this.passwordSalt = salt;
        this.password = new Password(salt, hashedPassword);
    }

    // === Gettery i settery ===

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) throws NullNameException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new NullNameException("Imię nie może być puste");
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) throws NullNameException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new NullNameException("Nazwisko nie może być puste");
        }
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) throws AgeException {
        if (age < 0) {
            throw new AgeException("Wiek musi być większy lub równy 0");
        }
        this.age = age;
    }

    public long getPesel() {
        return pesel;
    }

    public void setPesel(long pesel) throws PeselException {
        if (pesel < 10000000000L || pesel > 99999999999L) {
            throw new PeselException("Pesel musi składać się z dokładnie 11 cyfr.");
        }
        this.pesel = pesel;
    }

    @BsonIgnore
    public Password getPassword() {
        return password;
    }

    @BsonIgnore
    public void setPassword(String plainTextPassword) {
        Password passwordObj = new Password(plainTextPassword);
        this.password = passwordObj;
        this.passwordHash = passwordObj.getHashedPassword();
        this.passwordSalt = passwordObj.getSalt();
    }

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
     * Waliduje imię i nazwisko.
     */
    private void validateName(String firstName, String lastName) throws NullNameException {
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new NullNameException("Imię i nazwisko nie mogą być puste");
        }
    }
}
