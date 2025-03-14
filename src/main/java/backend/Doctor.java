package backend;

import org.bson.types.ObjectId;
import java.util.List;

/**
 * Klasa Doctor reprezentuje lekarza w systemie.
 */
public class Doctor extends Person{
    private ObjectId id;
    private String specialization;
    private List<String> availableDays;
    private String room;
    private String contactInformation;

    public Doctor() {}

    public Doctor(String firstName, String lastName, String specialization,
                  List<String> availableDays, String room, String contactInformation)throws NullNameException{
        super(firstName, lastName);
        this.specialization = specialization;
        this.availableDays = availableDays;
        this.room = room;
        this.contactInformation = contactInformation;
    }

    public ObjectId getId() { return id; }
    public String getFirstName() { return super.getFirstName(); }
    public String getLastName() { return super.getLastName(); }
    public String getSpecialization() { return specialization; }
    public List<String> getAvailableDays() { return availableDays; }
    public String getRoom() { return room; }
    public String getContactInformation() { return contactInformation; }
    public int getAge() { return super.getAge(); }
    public long getPesel() { return super.getPesel(); }

    public void setId(ObjectId id) { this.id = id; }
    public void setFirstName(String firstName) throws NullNameException{ super.setFirstName(firstName); }
    public void setLastName(String lastName) throws NullNameException{ super.setLastName(lastName); }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setAvailableDays(List<String> availableDays) { this.availableDays = availableDays; }
    public void setRoom(String room) { this.room = room; }
    public void setContactInformation(String contactInformation) { this.contactInformation = contactInformation; }
    public void setAge(int age) { super.setAge(age); }
    public void setPesel(long pesel)throws PeselException { super.setPesel(pesel); }

    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private String specialization;
        private List<String> availableDays;
        private String room;
        private int age;
        private long pesel;
        private String contactInformation;

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

        public Builder availableDays(List<String> availableDays) {
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

        public Doctor build() {
            try{Doctor doctor = new Doctor();
            doctor.setId(id);
            doctor.setFirstName(firstName);
            doctor.setLastName(lastName);
            doctor.setSpecialization(specialization);
            doctor.setAvailableDays(availableDays);
            doctor.setRoom(room);
            doctor.setPesel(pesel);
            doctor.setAge(age);
            doctor.setContactInformation(contactInformation);
            return doctor;}catch (PeselException e){
                System.out.println(e.getMessage());
                return null;
            }catch (NullNameException e) {
                System.out.println(e.getMessage());
                return null;
            }
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
        return "Doctor{" +
                "id=" + id +
                ", firstName='" + super.getFirstName() + '\'' +
                ", lastName='" + super.getLastName() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", availableDays=" + availableDays +
                ", room='" + room + '\'' +
                ", contactInformation='" + contactInformation + '\'' +
                '}';
    }
}