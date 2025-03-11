package backend;

import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Klasa Assignment reprezentuje przypisanie pacjenta do leczenia.
 */
class Assignment {
    private ObjectId patientId;
    private ObjectId treatmentId;
    private String role;
    private Date time;

    public Assignment(ObjectId patientId, ObjectId treatmetId, String role, Date time) {
        this.patientId = patientId;
        this.treatmentId = treatmetId;
        this.role = role;
        this.time = time;
    }

    public ObjectId getPatientId() {
        return patientId;
    }

    public ObjectId getTreatmentId() {
        return treatmentId;
    }

    public String getRole() {
        return role;
    }

    public Date getTime() {
        return time;
    }
}