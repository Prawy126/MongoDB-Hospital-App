package backend;

import org.bson.types.ObjectId;

import java.util.List;

public class Doctor {
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String specialization;
    private List<String> availableDays;

    // konstruktory, gettery, settery

    public ObjectId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }
}