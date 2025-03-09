package backend;

import org.bson.types.ObjectId;

import java.time.LocalDate;

public class Appointment {
    private ObjectId id;
    private ObjectId patientId;
    private ObjectId doctorId;
    private LocalDate date;
    private String room;
    private String description;
    private AppointmentStatus status;

    // Konstruktor domyślny wymagany przez MongoDB
    protected Appointment() {}

    // Konstruktor z parametrami
    public Appointment(ObjectId patientId, ObjectId doctorId, LocalDate date,
                       String room, String description, AppointmentStatus status) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.room = room;
        this.description = description;
        this.status = status;
    }

    // Gettery
    public ObjectId getId() { return id; }
    public ObjectId getPatientId() { return patientId; }
    public ObjectId getDoctorId() { return doctorId; }
    public LocalDate getDate() { return date; }
    public String getRoom() { return room; }
    public String getDescription() { return description; }
    public AppointmentStatus getStatus() { return status; }

    // Settery
    public void setId(ObjectId id) { this.id = id; }
    public void setPatientId(ObjectId patientId) { this.patientId = patientId; }
    public void setDoctorId(ObjectId doctorId) { this.doctorId = doctorId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setRoom(String room) { this.room = room; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    // Klasa Builder
    public static class Builder {
        private ObjectId id;
        private ObjectId patientId;
        private ObjectId doctorId;
        private LocalDate date;
        private String room;
        private String description;
        private AppointmentStatus status;

        public Builder() {}

        public Builder withId(ObjectId id) {
            this.id = id;
            return this;
        }

        public Builder patientId(Patient patientId) {
            this.patientId = patientId.getId();
            return this;
        }

        public Builder doctorId(Doctor doctorId) {
            this.doctorId = doctorId.getId();
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder room(String room) {
            this.room = room;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public Appointment build() {
            Appointment appointment = new Appointment();
            appointment.setId(id);
            appointment.setPatientId(patientId);
            appointment.setDoctorId(doctorId);
            appointment.setDate(date);
            appointment.setRoom(room);
            appointment.setDescription(description);
            appointment.setStatus(status);
            return appointment;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment appointment = (Appointment) o;
        return getId().equals(appointment.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", date=" + date +
                ", room='" + room + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}

