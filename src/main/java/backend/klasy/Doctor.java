package backend.klasy;

import backend.status.Day;
import backend.status.Diagnosis;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Klasa Doctor reprezentuje lekarza w systemie.
 */
public class Doctor extends Person {

    private ObjectId id;
    private String specialization;
    private List<Day> availableDays;
    private String room;
    private String contactInformation;

    /**
     * Domyślny konstruktor.
     */
    public Doctor() {}

    /**
     * Konstruktor bez hasła.
     */
    public Doctor(String firstName, String lastName, String specialization,
                  List<Day> availableDays, String room, String contactInformation) throws NullNameException {
        super(firstName, lastName);
        this.specialization = specialization;
        this.availableDays = availableDays;
        this.room = room;
        this.contactInformation = contactInformation;
    }

    /**
     * Konstruktor z hasłem.
     */
    public Doctor(String firstName, String lastName, String specialization,
                  List<Day> availableDays, String room, String contactInformation, String password) throws NullNameException {
        super(firstName, lastName, password);
        this.specialization = specialization;
        this.availableDays = availableDays;
        this.room = room;
        this.contactInformation = contactInformation;
    }

    public ObjectId getId() { return id; }
    public String getFirstName() { return super.getFirstName(); }
    public String getLastName() { return super.getLastName(); }
    public String getSpecialization() { return specialization; }
    public List<Day> getAvailableDays() { return availableDays; }
    public String getRoom() { return room; }
    public String getContactInformation() { return contactInformation; }
    public int getAge() { return super.getAge(); }
    public long getPesel() { return super.getPesel(); }

    public void setId(ObjectId id) { this.id = id; }
    public void setFirstName(String firstName) throws NullNameException { super.setFirstName(firstName); }
    public void setLastName(String lastName) throws NullNameException { super.setLastName(lastName); }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setAvailableDays(List<Day> availableDays) { this.availableDays = availableDays; }
    public void setRoom(String room) { this.room = room; }
    public void setContactInformation(String contactInformation) { this.contactInformation = contactInformation; }

    /**
     * Ustawia wiek, który musi być większy lub równy 25.
     */
    public void setAge(int age) throws AgeException {
        if (age >= 25) {
            super.setAge(age);
        } else {
            throw new AgeException("Wiek lekarza nie może być mniejszy niż 25");
        }
    }

    /**
     * Ustawia numer PESEL, który musi mieć 11 cyfr.
     */
    public void setPesel(long pesel) throws PeselException { super.setPesel(pesel); }

    /**
     * Ustawia hasło.
     */
    public void setPassword(String password) { super.setPassword(password); }

    /**
     * Ustawia diagnozę pacjenta.
     * @param diagnosis enum diagnozy
     * @param patient pacjent
     *
     */
    public void setDiagnosis(Diagnosis diagnosis, Patient patient) { patient.setDiagnosis(diagnosis); }

    /**
     * Budowniczy obiektu Doctor.
     */
    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private String specialization;
        private List<Day> availableDays;
        private String room;
        private int age;
        private long pesel;
        private String contactInformation;
        private String password;
        private String salt;

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

        public Builder specialization(String specialization) {
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

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder pesel(long pesel) {
            this.pesel = pesel;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder salt(String salt) {
            this.salt = salt;
            return this;
        }

        public Doctor build() throws PeselException, NullNameException, AgeException {
            if (firstName == null || firstName.trim().isEmpty()) {
                throw new NullNameException("Imię nie może być puste.");
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                throw new NullNameException("Nazwisko nie może być puste.");
            }
            if (age < 25) {
                throw new AgeException("Wiek lekarza musi być co najmniej 25 lat.");
            }
            if (pesel <= 9999999999L || pesel >= 100000000000L) {
                throw new PeselException("Pesel musi mieć dokładnie 11 cyfr.");
            }

            Doctor doctor = new Doctor();
            doctor.setFirstName(firstName);
            doctor.setLastName(lastName);
            doctor.setSpecialization(specialization);
            doctor.setAvailableDays(availableDays);
            doctor.setRoom(room);
            doctor.setPesel(pesel);
            doctor.setAge(age);
            doctor.setPassword(password);
            doctor.setSalt(salt);
            doctor.setContactInformation(contactInformation);

            if (id == null) {
                doctor.setId(new ObjectId());
            } else {
                doctor.setId(id);
            }

            return doctor;
        }
    }


    // w ogóle to porównanie ma sens wykorzystujemy je gdzieś?!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor)) return false;
        Doctor doctor = (Doctor) o;
        return getId().equals(doctor.getId());
    }

    // tak samo to jest gdzieś wykorzystywane lub będzie?!
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " (" + specialization + ")";
    }
}
