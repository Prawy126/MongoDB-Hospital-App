package backend.klasy;

import backend.status.Diagnosis;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import org.bson.types.ObjectId;

import java.time.LocalDate;

/**
 * Klasa Patient reprezentuje pacjenta w systemie.
 */
public class Patient extends Person {

    private ObjectId id;
    private LocalDate birthDate;
    private String address;
    private Diagnosis diagnosis;

    public Patient() {
        // Publiczny pusty konstruktor
    }

    public Patient(String firstName, String lastName, long pesel, LocalDate birthDate, String address, int age) throws PeselException, NullNameException, AgeException {
        super(firstName, lastName, pesel, age);
        this.birthDate = birthDate;
        this.address = address;
    }

    public Patient(String firstName, String lastName, long pesel, LocalDate birthDate, String address, int age, String password) throws PeselException, NullNameException, AgeException {
        super(firstName, lastName, pesel, age, password);
        this.birthDate = birthDate;
        this.address = address;
    }

    public Patient(String firstName, String lastName, long pesel, LocalDate birthDate, String address, int age, String password, Diagnosis diagnosis) throws PeselException, NullNameException, AgeException {
        super(firstName, lastName, pesel, age, password);
        this.birthDate = birthDate;
        this.address = address;
        this.diagnosis = diagnosis;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Diagnosis getDiagnosis() {
        return diagnosis;
    }

    public void setAge(int age) throws AgeException {
        super.setAge(age);
    }

    public void setFirstName(String firstName) throws NullNameException {
        super.setFirstName(firstName);
    }

    public void setLastName(String lastName) throws NullNameException {
        super.setLastName(lastName);
    }

    public void setPesel(long pesel) throws PeselException {
        super.setPesel(pesel);
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " (" + getPesel() + ")";
    }

    /**
     * Klasa pomocnicza Builder do tworzenia obiektów Patient z możliwością konfiguracji pól.
     */
    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private long pesel;
        private LocalDate birthDate;
        private int age;
        private String address;
        private String password;
        private boolean skipValidation = false;

        public Builder() {
        }

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

        public Builder pesel(long pesel) {
            this.pesel = pesel;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Flaga pozwalająca pominąć walidację przy tworzeniu obiektu.
         */
        public Builder skipValidation(boolean skipValidation) {
            this.skipValidation = skipValidation;
            return this;
        }

        /**
         * Buduje obiekt Patient. Może rzucić wyjątki walidacyjne.
         */
        public Patient build() throws PeselException, NullNameException, AgeException {
            if (!skipValidation) {
                if (firstName == null || firstName.trim().isEmpty()) {
                    throw new NullNameException("Imię nie może być puste.");
                }
                if (lastName == null || lastName.trim().isEmpty()) {
                    throw new NullNameException("Nazwisko nie może być puste.");
                }
                if (age <= 0) {
                    throw new AgeException("Wiek pacjenta musi być większy niż 0.");
                }
                if (pesel < 10000000000L || pesel > 99999999999L) {
                    throw new PeselException("Pesel musi mieć dokładnie 11 cyfr.");
                }
            }

            Patient patient = new Patient(firstName, lastName, pesel, birthDate, address, age, password);
            if (id != null) {
                patient.setId(id);
            } else {
                patient.setId(new ObjectId());
            }
            return patient;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Patient patient = (Patient) o;
            return id != null && id.equals(patient.id);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }
}