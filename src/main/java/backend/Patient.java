package backend;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Date;

public class Patient extends Person{
    private ObjectId id;
    private LocalDate birthDate;
    private String address;

    public ObjectId getId() {
        return id;
    }

    public String getFirstName() {
        return super.getImie();
    }

    public String getLastName() {
        return super.getNazwisko();
    }


    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void ustawWiek(int wiek){
        super.setWiek(wiek);
    }

    Patient() {

    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        setImie(firstName);
    }

    public void setLastName(String lastName) {
        setNazwisko(lastName);
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

    // Klasa Builder
    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private int pesel;
        private LocalDate birthDate;
        private int wiek;
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
        public Builder wiek(int wiek){
            this.wiek = wiek;
            return this;
        }

        public Patient build() {
            Patient patient = new Patient();
            patient.setId(id);
            patient.setFirstName(firstName);
            patient.setLastName(lastName);
            patient.setPesel(pesel);
            patient.setBirthDate(birthDate);
            patient.setWiek(wiek);
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
                ", firstName='" + super.getImie() + '\'' +
                ", lastName='" + super.getNazwisko() + '\'' +
                ", pesel='" + super.getPesel() + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                '}';
    }
}