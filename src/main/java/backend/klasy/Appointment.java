package backend.klasy;

import backend.status.AppointmentStatus;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

/**
 * Klasa {@code Appointment} reprezentuje wizytę pacjenta u lekarza.
 * Zawiera informacje o pacjencie, lekarzu, dacie, sali, opisie oraz statusie wizyty.
 */
public class Appointment {

    private ObjectId id;
    private ObjectId patientId;
    private ObjectId doctorId;
    private LocalDateTime date;
    private ObjectId room;
    private String description;
    private AppointmentStatus status;

    /**
     * Domyślny konstruktor bez parametrów.
     */
    public Appointment() {
    }

    /**
     * Konstruktor tworzący wizytę z podstawowymi informacjami.
     *
     * @param patientId   identyfikator pacjenta
     * @param doctorId    identyfikator lekarza
     * @param date        data i godzina wizyty
     * @param room        identyfikator sali
     * @param description opis wizyty
     * @param status      status wizyty
     */
    public Appointment(ObjectId patientId, ObjectId doctorId, LocalDateTime date,
                       ObjectId room, String description, AppointmentStatus status) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.room = room;
        this.description = description;
        this.status = status;
    }

    /**
     * Konstruktor tworzący wizytę z dodatkowymi parametrami (uwaga: roomId jest zbędny).
     *
     * @param patientId   identyfikator pacjenta
     * @param doctorId    identyfikator lekarza
     * @param roomId      nieużywany parametr (możliwy do usunięcia)
     * @param date        data i godzina wizyty
     * @param room        identyfikator sali
     * @param description opis wizyty
     * @param status      status wizyty
     */
    public Appointment(ObjectId patientId, ObjectId doctorId, ObjectId roomId, LocalDateTime date,
                       ObjectId room, String description, AppointmentStatus status) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.room = room;
        this.description = description;
        this.status = status;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getPatientId() {
        return patientId;
    }

    public void setPatientId(ObjectId patientId) {
        this.patientId = patientId;
    }

    public ObjectId getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public ObjectId getRoom() {
        return room;
    }

    public void setRoom(ObjectId room) {
        this.room = room;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    /**
     * Klasa pomocnicza do tworzenia obiektów typu {@link Appointment} przy użyciu wzorca builder.
     */
    public static class Builder {
        private ObjectId id;
        private ObjectId patientId;
        private ObjectId doctorId;
        private LocalDateTime date;
        private ObjectId room;
        private String description;
        private AppointmentStatus status;

        public Builder() {
        }

        public Builder withId(ObjectId id) {
            this.id = id;
            return this;
        }

        public Builder patientId(Patient patient) {
            this.patientId = patient.getId();
            return this;
        }

        public Builder doctorId(Doctor doctor) {
            this.doctorId = doctor.getId();
            return this;
        }

        public Builder date(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public Builder room(ObjectId room) {
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

        /**
         * Buduje i zwraca obiekt {@link Appointment}.
         */
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

    /**
     * Porównuje dwa obiekty typu {@link Appointment} na podstawie ich identyfikatora.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment appointment = (Appointment) o;
        return getId().equals(appointment.getId());
    }

    /**
     * Zwraca kod haszujący oparty na identyfikatorze.
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * Zwraca reprezentację tekstową obiektu {@link Appointment}.
     */
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
