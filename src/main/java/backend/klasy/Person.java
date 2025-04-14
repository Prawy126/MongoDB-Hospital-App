package backend.klasy;

import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;

/**
 * Klasa reprezentująca osobę. Jest klasą bazową dla innych klas.
 */
public class Person {

    private String firstName;
    private String lastName;
    private int age;
    private long pesel;
    private String password;
    private String salt;
    private String login;

    /**
     * Domyślny konstruktor.
     */
    Person() {
    }

    /**
     * Konstruktor z imieniem i nazwiskiem.
     *
     * @param firstName Imię
     * @param lastName  Nazwisko
     * @throws NullNameException jeśli imię lub nazwisko są puste
     */
    public Person(String firstName, String lastName) throws NullNameException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Konstruktor z imieniem, nazwiskiem i hasłem.
     *
     * @param firstName Imię
     * @param lastName  Nazwisko
     * @param password  Hasło
     * @throws NullNameException jeśli imię lub nazwisko są puste
     */
    public Person(String firstName, String lastName, String password) throws NullNameException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    /**
     * Konstruktor z imieniem, nazwiskiem i peselem.
     *
     * @param firstName Imię
     * @param lastName  Nazwisko
     * @param pesel     Numer PESEL
     * @throws PeselException    jeśli pesel nie ma 11 cyfr
     * @throws NullNameException jeśli imię lub nazwisko są puste
     */
    public Person(String firstName, String lastName, long pesel) throws PeselException, NullNameException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
    }

    /**
     * Konstruktor z imieniem, nazwiskiem, peselem i wiekiem.
     *
     * @param firstName Imię
     * @param lastName  Nazwisko
     * @param pesel     Numer PESEL
     * @param age       Wiek
     * @throws PeselException    jeśli pesel nie ma 11 cyfr
     * @throws NullNameException jeśli imię lub nazwisko są puste
     * @throws AgeException      jeśli wiek jest ujemny
     */
    public Person(String firstName, String lastName, long pesel, int age) throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
        setAge(age);
    }

    /**
     * Konstruktor z imieniem, nazwiskiem, peselem, wiekiem i hasłem.
     *
     * @param firstName Imię
     * @param lastName  Nazwisko
     * @param pesel     Numer PESEL
     * @param age       Wiek
     * @param password  Hasło
     * @throws PeselException    jeśli pesel nie ma 11 cyfr
     * @throws NullNameException jeśli imię lub nazwisko są puste
     * @throws AgeException      jeśli wiek jest ujemny
     */
    public Person(String firstName, String lastName, long pesel, int age, String password) throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
        setAge(age);
        setPassword(password);
    }

    /**
     * Konstruktor z imieniem, nazwiskiem, peselem, wiekiem, loginem i hasłem.
     *
     * @param firstName Imię
     * @param lastName  Nazwisko
     * @param pesel     Numer PESEL
     * @param age       Wiek
     * @param login     Login
     * @param password  Hasło
     * @throws PeselException    jeśli pesel nie ma 11 cyfr
     * @throws NullNameException jeśli imię lub nazwisko są puste
     * @throws AgeException      jeśli wiek jest ujemny
     */
    public Person(String firstName, String lastName, long pesel, int age, String login,String password) throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        setPesel(pesel);
        setAge(age);
        setPassword(password);
    }


    /**
     * Konstruktor z imieniem, nazwiskiem, peselem, wiekiem, loginem, hasłem(Zahashowanym) oraz solą.
     *
     * @param firstName Imię
     * @param lastName  Nazwisko
     * @param pesel     Numer PESEL
     * @param age       Wiek
     * @param password  Hasło
     * @throws PeselException    jeśli pesel nie ma 11 cyfr
     * @throws NullNameException jeśli imię lub nazwisko są puste
     * @throws AgeException      jeśli wiek jest ujemny
     */
    public Person(String firstName, String lastName, long pesel, int age, String login, String password, String salt) throws PeselException, NullNameException, AgeException {
        validateName(firstName, lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        setPesel(pesel);
        setAge(age);
        this.password = password;
        this.login = login;
        this.salt = salt;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public long getPesel() {
        return pesel;
    }

    /**
     * Zwraca hasło (zahashowane). Można rozważyć usunięcie tej metody ze względów bezpieczeństwa.
     */
    public String getPassword() {
        return password;
    }

    public void setFirstName(String firstName) throws NullNameException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new NullNameException("Imię nie może być puste");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) throws NullNameException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new NullNameException("Nazwisko nie może być puste");
        }
        this.lastName = lastName;
    }

    public void setAge(int age) throws AgeException {
        if (age < 0) {
            throw new AgeException("Wiek pacjenta musi być większy niż 0.");
        }
        this.age = age;
    }

    public void setPesel(long pesel) throws PeselException {
        if (pesel < 10000000000L || pesel > 99999999999L) {
            throw new PeselException("Pesel musi składać się dokładnie z 11 cyfr.");
        }
        this.pesel = pesel;
    }

    public void setPassword(String password) {
        //Password passwordObj = new Password(password);
        this.password = password;
    }

    /**
     * Waliduje imię i nazwisko.
     *
     * @param firstName Imię
     * @param lastName  Nazwisko
     * @throws NullNameException jeśli imię lub nazwisko są puste
     */
    private void validateName(String firstName, String lastName) throws NullNameException {
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new NullNameException("Imię i nazwisko nie mogą być puste");
        }
    }

    public void setPassword(String password, Boolean isHash) {
        if(isHash){
            Password passwordObj = new Password(password);
            this.password = passwordObj.getHashedPassword();
            this.salt = passwordObj.getSalt();
        }else {
            this.password = password;
        }
    }
    public String getSalt() {
        return salt;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    public String getLogin() {
        return login;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
}
