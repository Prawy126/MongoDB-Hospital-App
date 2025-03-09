package backend;

import org.bson.types.ObjectId;

import java.util.Date;

public class Appointment {
    private ObjectId id;
    private ObjectId patientId;
    private ObjectId doctorId;
    private Date date;
    private String room;
    private String description;

    public ObjectId getId() {
        return id;
    }

    public ObjectId getDoctorId() {
        return doctorId;
    }

    public ObjectId getPatientId() {
        return patientId;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getRoom() {
        return room;
    }

    public void setPatientId(ObjectId patientId) {
        this.patientId = patientId;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDoctorId(ObjectId doctorId) {
        this.doctorId = doctorId;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}