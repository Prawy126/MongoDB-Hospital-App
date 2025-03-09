package backend;

import org.bson.types.ObjectId;

import java.util.List;

public class Doctor {
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String specialization;
    private List<String> availableDays;
    private String room;
    private String contactInformation;

    // Konstruktor domyślny wymagany przez MongoDB
    protected Doctor() {}

    // Konstruktor z parametrami
    public Doctor(String firstName, String lastName, String specialization,
                  List<String> availableDays, String room, String contactInformation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.availableDays = availableDays;
        this.room = room;
        this.contactInformation = contactInformation;
    }

    // Gettery
    public ObjectId getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSpecialization() { return specialization; }
    public List<String> getAvailableDays() { return availableDays; }
    public String getRoom() { return room; }
    public String getContactInformation() { return contactInformation; }

    // Settery
    public void setId(ObjectId id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setAvailableDays(List<String> availableDays) { this.availableDays = availableDays; }
    public void setRoom(String room) { this.room = room; }
    public void setContactInformation(String contactInformation) { this.contactInformation = contactInformation; }

    // Klasa Builder
    public static class Builder {
        private ObjectId id;
        private String firstName;
        private String lastName;
        private String specialization;
        private List<String> availableDays;
        private String room;
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

        public Doctor build() {
            Doctor doctor = new Doctor();
            doctor.setId(id);
            doctor.setFirstName(firstName);
            doctor.setLastName(lastName);
            doctor.setSpecialization(specialization);
            doctor.setAvailableDays(availableDays);
            doctor.setRoom(room);
            doctor.setContactInformation(contactInformation);
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
        return "Doctor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialization='" + specialization + '\'' +
                ", availableDays=" + availableDays +
                ", room='" + room + '\'' +
                ", contactInformation='" + contactInformation + '\'' +
                '}';
    }
}