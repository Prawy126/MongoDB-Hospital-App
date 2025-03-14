package backend.klasy;

import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * Klasa Nurse reprezentuje pielęgniarkę w systemie.
 */
public class Nurse extends Person {
    private String specialization;
    private List<Assignment> assignments;
    public Nurse(String firstName, String lastName, int pesel, int age,String specialization)throws PeselException, NullNameException, AgeException {
        super(firstName, lastName, pesel, age);
        this.specialization = specialization;
    }
    public String getFirstName(){
        return super.getFirstName();
    }
    public String getLastName(){
        return super.getLastName();
    }
    public int getAge(){
        return super.getAge();
    }
    public long getPesel(){
        return super.getPesel();
    }
    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
    public List<Assignment> getAssignments() {
        return assignments;
    }
    public void setFirstName(String firstName) throws NullNameException {
        super.setFirstName(firstName);
    }
    public void setLastName(String lastName) throws NullNameException {
        super.setLastName(lastName);
    }
    public void setAge(int age) throws AgeException {
        if(age>=20)super.setAge(age);
        else throw new AgeException("Wiek pielęgniarki nie może być mniejszy niż 20");
    }
    public void setPesel(long pesel) throws PeselException {
        super.setPesel(pesel);
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
