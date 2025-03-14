package backend;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * Klasa Nurse reprezentuje pielęgniarkę w systemie.
 */
public class Nurse extends Person {
    private String specialization;
    private List<Assignment> assignments;
    public Nurse(String firstName, String lastName, int pesel, int age,String specialization)throws PeselException {
        super(firstName, lastName, pesel, age);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    public void addAssignment(ObjectId patientId, ObjectId treatmentId, String role) {
        Assignment nowe = new Assignment(patientId, treatmentId, role, new Date());
        this.assignments.add(nowe);
    }
}
