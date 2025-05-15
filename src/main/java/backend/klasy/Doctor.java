package backend.klasy;

import backend.status.Day;
import backend.status.Diagnosis;
import backend.status.Specialization;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import org.bson.types.ObjectId;

import java.util.List;

public class Doctor extends Person {

    private ObjectId id;
    private Specialization specialization;
    private List<Day> availableDays;
    private String room;
    private String contactInformation;

    public Doctor() {}

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

    public ObjectId getId() { return id; }
    public Specialization getSpecialization() { return specialization; }
    public List<Day> getAvailableDays() { return availableDays; }
    public String getRoom() { return room; }
    public String getContactInformation() { return contactInformation; }
    public boolean isFirstContact(){
        return specialization == Specialization.FIRST_CONTACT;
    }

    public void setId(ObjectId id) { this.id = id; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }
    public void setAvailableDays(List<Day> availableDays) { this.availableDays = availableDays; }
    public void setRoom(String room) { this.room = room; }
    public void setContactInformation(String contactInformation) { this.contactInformation = contactInformation; }

    public void setDiagnosis(Diagnosis diagnosis, Patient patient) {
        patient.setDiagnosis(diagnosis);
    }

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
                doctor = new Doctor(
                        firstName,
                        lastName,
                        age,
                        pesel,
                        specialization,
                        availableDays,
                        room,
                        contactInformation,
                        plainPassword
                );
            } else if (passwordHash != null && passwordSalt != null) {
                doctor = new Doctor(
                        firstName,
                        lastName,
                        age,
                        pesel,
                        specialization,
                        availableDays,
                        room,
                        contactInformation,
                        passwordHash,
                        passwordSalt
                );
            } else {
                throw new IllegalArgumentException("Nie podano hasła ani zahashowanego hasła i soli.");
            }

            doctor.setId(id != null ? id : new ObjectId());
            return doctor;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor)) return false;
        Doctor doctor = (Doctor) o;
        return getId().equals(doctor.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " (" + specialization.getDescription() + ")";
    }
}