package backend;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("patients")
public class Patient {

    @Id
    private ObjectId id;
    private String name;
    private int age;
    private String medicalCondition;

    public Patient() {}

    public Patient(String name, int age, String medicalCondition) {
        this.name = name;
        this.age = age;
        this.medicalCondition = medicalCondition;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
    }
}
