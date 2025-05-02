package backend.klasy;

import backend.status.Diagnosis;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import org.bson.types.ObjectId;

import java.time.LocalDate;

public class Patient extends Person {

    private ObjectId id;
    private LocalDate birthDate;
    private String address;
    private Diagnosis diagnosis;

    public Patient() {}

    public Patient(
            String firstName,
            String lastName,
            long pesel,
            LocalDate birthDate,
            String address,
            int age
    ) throws PeselException, NullNameException, AgeException {
        super(firstName, lastName, pesel, age);
        this.birthDate = birthDate;
        this.address = address;
        this.diagnosis = Diagnosis.AWAITING;
    }

    public Patient(
            String firstName,
            String lastName,
            long pesel,
            LocalDate birthDate,
            String address,
            int age,
            String plainPassword
    ) throws PeselException, NullNameException, AgeException {
        super(firstName, lastName, pesel, age, plainPassword);
        this.birthDate = birthDate;
        this.address = address;
        this.diagnosis = Diagnosis.AWAITING;
    }

    public Patient(
            String firstName,
            String lastName,
            long pesel,
            LocalDate birthDate,
            String address,
            int age,
            String plainPassword,
            Diagnosis diagnosis
    ) throws PeselException, NullNameException, AgeException {
        super(firstName, lastName, pesel, age, plainPassword);
        this.birthDate = birthDate;
        this.address = address;
        this.diagnosis = diagnosis;
    }

    public Patient(
            String firstName,
            String lastName,
            long pesel,
            int age,
            String passwordHash,
            String passwordSalt
    ) throws PeselException, NullNameException, AgeException {
        super(firstName, lastName, pesel, age, passwordHash, passwordSalt);
        setDiagnosis(Diagnosis.AWAITING);
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

    public void setDiagnosis(Diagnosis diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " (" + getPesel() + ")";
    }

    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private long pesel;
        private LocalDate birthDate;
        private int age;
        private String address;
        private String plainPassword;
        private String passwordHash;
        private String passwordSalt;
        private boolean skipValidation = false;
        private Diagnosis diagnosis = Diagnosis.AWAITING;

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


        public Builder skipValidation(boolean skipValidation) {
            this.skipValidation = skipValidation;
            return this;
        }

        public Builder diagnosis(Diagnosis diagnosis) {
            this.diagnosis = diagnosis;
            return this;
        }

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

            Patient patient;
            if (plainPassword != null) {
                patient = new Patient(firstName, lastName, pesel, birthDate, address, age, plainPassword);
            } else {
                patient = new Patient(firstName, lastName, pesel, age, passwordHash, passwordSalt);
                patient.setBirthDate(birthDate);
                patient.setAddress(address);

            }

            patient.setId(id != null ? id : new ObjectId());
            return patient;
        }

    }
}
