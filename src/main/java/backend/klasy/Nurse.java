package backend.klasy;

import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Klasa Nurse reprezentuje pielęgniarkę w systemie.
 */
public class Nurse extends Person {
    private ObjectId id;
    private String specialization;
    private List<Assignment> assignments;

    public Nurse() {}

    private Nurse(String firstName, String lastName, String pesel, int age, String specialization, List<Assignment> assignments) throws PeselException, NullNameException, AgeException {
        super(firstName, lastName, pesel, age);
        this.specialization = specialization;
        this.assignments = assignments != null ? assignments : new ArrayList<>();
    }

    public ObjectId getId() { return id; }

    public String getFirstName() { return super.getFirstName(); }

    public String getLastName() { return super.getLastName(); }

    public int getAge() { return super.getAge(); }

    public String getPesel() { return super.getPesel(); }

    public String getSpecialization() { return specialization; }

    public List<Assignment> getAssignments() { return assignments; }

    public void setId(ObjectId id) { this.id = id; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public void setAssignments(List<Assignment> assignments) { this.assignments = assignments; }

    public void addAssignment(ObjectId patientId, ObjectId treatmentId, String role) {
        Assignment newAssignment = new Assignment(patientId, treatmentId, role, new Date());
        this.assignments.add(newAssignment);
    }

    /**
     * Builder Pattern for creating Nurse objects.
     */
    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private String pesel;
        private int age;
        private String specialization;
        private List<Assignment> assignments = new ArrayList<>();

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

        public Builder pesel(String pesel) {
            this.pesel = pesel;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder specialization(String specialization) {
            this.specialization = specialization;
            return this;
        }

        public Builder assignments(List<Assignment> assignments) {
            this.assignments = assignments;
            return this;
        }

        public Nurse build() throws PeselException, NullNameException, AgeException {
            if (firstName == null || firstName.isEmpty()) {
                throw new NullNameException("Imię nie może być puste.");
            }
            if (lastName == null || lastName.isEmpty()) {
                throw new NullNameException("Nazwisko nie może być puste.");
            }
            if (age < 20) {
                throw new AgeException("Wiek pielęgniarki musi wynosić co najmniej 20 lat.");
            }
            if (pesel.length() != 11) {
                throw new PeselException("Pesel musi mieć dokładnie 11 cyfr.");
            }

            Nurse nurse = new Nurse(firstName, lastName, pesel, age, specialization, assignments);
            nurse.setId(id != null ? id : new ObjectId());
            return nurse;
        }
    }
}
