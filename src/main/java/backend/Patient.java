package backend;

import org.bson.types.ObjectId;
import java.time.LocalDate;

public class Patient extends Person{
    private ObjectId id;
    private LocalDate birthDate;
    private String address;

    public ObjectId getId() {
        return id;
    }

    public String getFirstName() {
        return super.getFistName();
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

    public void setAge(int age){
        super.setAge(age);
    }

    Patient() {

    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        super.setFirstName(firstName);
    }

    public void setLastName(String lastName) {
        super.setLastName(lastName);
    }

    public void setPesel(int pesel) {
        super.setPesel(pesel);
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Konstruktor z parametrami
    public Patient(String firstName, String lastName, String pesel, LocalDate birthDate, String address) {
        super(firstName,lastName,Integer.parseInt(pesel));
        this.birthDate = birthDate;
        this.address = address;
    }

    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private int pesel;
        private LocalDate birthDate;
        private int age;
        private String address;

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

        public Builder pesel(int pesel) {
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
        public Builder age(int age){
            this.age = age;
            return this;
        }

        public Patient build() {
            Patient patient = new Patient();
            patient.setId(id);
            patient.setFirstName(firstName);
            patient.setLastName(lastName);
            patient.setPesel(pesel);
            patient.setBirthDate(birthDate);
            patient.setAge(age);
            patient.setAddress(address);
            return patient;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return getId().equals(patient.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstName='" + super.getFistName() + '\'' +
                ", lastName='" + super.getLastName() + '\'' +
                ", pesel='" + super.getPesel() + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                '}';
    }
}