package backend.klasy;

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

    public ObjectId getId() {
        return id;
    }

    public String getFirstName() {
        return super.getFirstName();
    }

    public String getLastName() {
        return super.getLastName();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAge(int age)throws AgeException {
        super.setAge(age);
    }

    public Patient() {
        // Public no-argument constructor
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setFirstName(String firstName) throws NullNameException {
        super.setFirstName(firstName);
    }

    public void setLastName(String lastName)throws NullNameException {
        super.setLastName(lastName);
    }

    public void setPesel(long pesel)throws PeselException {
        super.setPesel(pesel);
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Konstruktor z parametrami
    public Patient(String firstName, String lastName, long pesel, LocalDate birthDate, String address) throws PeselException, NullNameException {
        super(firstName, lastName, pesel);
        this.birthDate = birthDate;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstName='" + (getFirstName() != null ? getFirstName() : "null") + '\'' +
                ", lastName='" + (getLastName() != null ? getLastName() : "null") + '\'' +
                ", pesel='" + (getPesel() != 0 ? getPesel() : "null") + '\'' +
                ", birthDate=" + (birthDate != null ? birthDate : "null") +
                ", address='" + (address != null ? address : "null") + '\'' +
                '}';
    }

    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private long pesel;
        private LocalDate birthDate;
        private int age;
        private String address;

        private boolean skipValidation = false;

        public Builder() {}

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

        public Builder skipValidation(boolean skipValidation) {
            this.skipValidation = skipValidation;
            return this;
        }


        public Patient build() throws PeselException, NullNameException, AgeException {
            if (!skipValidation) {
                if (firstName == null || firstName.isEmpty()) {
                    throw new NullNameException("Imię nie może być puste.");
                }
                if (lastName == null || lastName.isEmpty()) {
                    throw new NullNameException("Nazwisko nie może być puste.");
                }
                if (age <= 0) {
                    throw new AgeException("Wiek pacjenta musi być większy niż 0.");
                }
                if (pesel < 10000000000L || pesel > 99999999999L) {
                    throw new PeselException("Pesel musi mieć dokładnie 11 cyfr.");
                }
            }

            Patient patient = new Patient(firstName, lastName, pesel, birthDate, address);
            if (id == null) {
                patient.setId(new ObjectId());
            } else {
                patient.setId(id);
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