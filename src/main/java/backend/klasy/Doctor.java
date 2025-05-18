package backend.klasy;

import backend.status.Day;
import backend.status.Diagnosis;
import backend.status.Specialization;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Klasa {@code Doctor} reprezentuje lekarza w systemie.
 * Rozszerza klasę {@link Person}, zawiera informacje o specjalizacji, dostępnych dniach pracy, pokoju i danych kontaktowych.
 */
public class Doctor extends Person {

    private ObjectId id;
    private Specialization specialization;
    private List<Day> availableDays;
    private String room;
    private String contactInformation;

    /**
     * Domyślny konstruktor.
     */
    public Doctor() {}

    /**
     * Konstruktor tworzący lekarza na podstawie hasła jawnego.
     *
     * @param firstName          imię lekarza
     * @param lastName           nazwisko lekarza
     * @param age                wiek lekarza
     * @param pesel              numer PESEL
     * @param specialization     specjalizacja
     * @param availableDays      dostępne dni pracy
     * @param room               numer gabinetu
     * @param contactInformation dane kontaktowe
     * @param plainPassword      hasło jawne
     * @throws NullNameException jeśli imię lub nazwisko jest puste
     * @throws AgeException      jeśli wiek jest mniejszy niż 25
     * @throws PeselException    jeśli PESEL ma niepoprawny format
     */
    public Doctor(String firstName,
                  String lastName,
                  int age,
                  long pesel,
                  Specialization specialization,
                  List<Day> availableDays,
                  String room,
                  String contactInformation,
                  String plainPassword
    ) throws NullNameException, AgeException, PeselException {
        super(firstName, lastName, pesel, age, plainPassword);
        this.specialization = specialization;
        this.availableDays = availableDays;
        this.room = room;
        this.contactInformation = contactInformation;
    }

    /**
     * Konstruktor tworzący lekarza na podstawie hasła zahashowanego.
     *
     * @param firstName          imię lekarza
     * @param lastName           nazwisko lekarza
     * @param age                wiek lekarza
     * @param pesel              numer PESEL
     * @param specialization     specjalizacja
     * @param availableDays      dostępne dni pracy
     * @param room               numer gabinetu
     * @param contactInformation dane kontaktowe
     * @param passwordHash       hash hasła
     * @param passwordSalt       sól hasła
     * @throws NullNameException jeśli imię lub nazwisko jest puste
     * @throws AgeException      jeśli wiek jest mniejszy niż 25
     * @throws PeselException    jeśli PESEL ma niepoprawny format
     */
    public Doctor(String firstName,
                  String lastName,
                  int age,
                  long pesel,
                  Specialization specialization,
                  List<Day> availableDays,
                  String room,
                  String contactInformation,
                  String passwordHash,
                  String passwordSalt
    ) throws NullNameException, AgeException, PeselException {
        super(firstName, lastName, pesel, age, passwordHash, passwordSalt);
        this.specialization = specialization;
        this.availableDays = availableDays;
        this.room = room;
        this.contactInformation = contactInformation;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public List<Day> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<Day> availableDays) {
        this.availableDays = availableDays;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    /**
     * Sprawdza, czy lekarz jest lekarzem pierwszego kontaktu.
     *
     * @return {@code true} jeśli specjalizacja to FIRST_CONTACT
     */
    public boolean isFirstContact() {
        return specialization == Specialization.FIRST_CONTACT;
    }

    /**
     * Ustawia diagnozę dla pacjenta.
     *
     * @param diagnosis diagnoza do przypisania
     * @param patient   pacjent, któremu przypisywana jest diagnoza
     */
    public void setDiagnosis(Diagnosis diagnosis, Patient patient) {
        patient.setDiagnosis(diagnosis);
    }

    /**
     * Klasa {@code Builder} umożliwiająca wygodne tworzenie instancji klasy {@link Doctor}.
     */
    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private int age;
        private long pesel;
        private Specialization specialization;
        private List<Day> availableDays;
        private String room;
        private String contactInformation;
        private String plainPassword;
        private String passwordHash;
        private String passwordSalt;

        public Builder withId(ObjectId id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder pesel(long pesel) {
            this.pesel = pesel;
            return this;
        }

        public Builder specialization(Specialization specialization) {
            this.specialization = specialization;
            return this;
        }

        public Builder availableDays(List<Day> availableDays) {
            this.availableDays = availableDays;
            return this;
        }

        public Builder room(String room) {
            this.room = room;
            return this;
        }

        public Builder contactInformation(String contactInformation) {
            this.contactInformation = contactInformation;
            return this;
        }

        public Builder plainPassword(String plainPassword) {
            this.plainPassword = plainPassword;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder passwordSalt(String passwordSalt) {
            this.passwordSalt = passwordSalt;
            return this;
        }

        /**
         * Tworzy instancję klasy {@link Doctor} z ustawionymi parametrami.
         *
         * @return obiekt klasy {@link Doctor}
         * @throws NullNameException jeśli imię lub nazwisko jest puste
         * @throws AgeException      jeśli wiek jest mniejszy niż 25
         * @throws PeselException    jeśli PESEL ma niepoprawny format
         * @throws IllegalArgumentException jeśli brakuje specjalizacji lub hasła
         */
        public Doctor build() throws NullNameException, AgeException, PeselException {
            if (firstName == null || firstName.trim().isEmpty()) {
                throw new NullNameException("Imię nie może być puste.");
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                throw new NullNameException("Nazwisko nie może być puste.");
            }
            if (age < 25) {
                throw new AgeException("Wiek lekarza musi być co najmniej 25 lat.");
            }
            if (pesel < 10000000000L || pesel > 99999999999L) {
                throw new PeselException("Pesel musi mieć dokładnie 11 cyfr.");
            }
            if (specialization == null) {
                throw new IllegalArgumentException("Specjalizacja nie może być pusta.");
            }

            Doctor doctor;

            if (plainPassword != null) {
                doctor = new Doctor(firstName, lastName, age, pesel, specialization, availableDays, room, contactInformation, plainPassword);
            } else if (passwordHash != null && passwordSalt != null) {
                doctor = new Doctor(firstName, lastName, age, pesel, specialization, availableDays, room, contactInformation, passwordHash, passwordSalt);
            } else {
                throw new IllegalArgumentException("Nie podano hasła ani zahashowanego hasła i soli.");
            }

            doctor.setId(id != null ? id : new ObjectId());
            return doctor;
        }
    }

    /**
     * Porównuje obiekty klasy {@link Doctor} na podstawie ich identyfikatora.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor)) return false;
        Doctor doctor = (Doctor) o;
        return getId().equals(doctor.getId());
    }

    /**
     * Zwraca kod haszujący lekarza.
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * Zwraca reprezentację tekstową lekarza.
     */
    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " (" + specialization.getDescription() + ")";
    }
}
