package backend.klasy;

import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Klasa AdministrationMedicine reprezentuje podanie leku pacjentowi przez pielęgniarkę.
 */
public class AdministrationMedicine {
    private ObjectId id;
    private ObjectId prescriptionId;
    private ObjectId nurseId;
    private Date timeApplication;
    private String doseApplication;
    private String note;

    public AdministrationMedicine() {}

    public AdministrationMedicine(ObjectId prescriptionId, ObjectId nurseId, Date timeApplication, String doseApplication, String note) {
        this.prescriptionId = prescriptionId;
        this.nurseId = nurseId;
        this.timeApplication = timeApplication;
        this.doseApplication = doseApplication;
        this.note = note;
    }

    public ObjectId getId() { return id; }
    public ObjectId getPrescriptionId() { return prescriptionId; }
    public ObjectId getNurseId() { return nurseId; }
    public Date getTimeApplication() { return timeApplication; }
    public String getDoseApplication() { return doseApplication; }
    public String getNote() { return note; }

    public void setId(ObjectId id) { this.id = id; }
    public void setPrescriptionId(ObjectId prescriptionId) { this.prescriptionId = prescriptionId; }
    public void setNurseId(ObjectId nurseId) { this.nurseId = nurseId; }
    public void setTimeApplication(Date timeApplication) { this.timeApplication = timeApplication; }
    public void setDoseApplication(String doseApplication) { this.doseApplication = doseApplication; }
    public void setNote(String note) { this.note = note; }


}
