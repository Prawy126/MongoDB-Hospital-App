package backend.klasy;

import backend.Exhibitor;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Klasa Prescription reprezentuje receptę wystawioną przez lekarza.
 */
public class Prescription {
    private ObjectId id;
    private ObjectId pacjentId;
    private Exhibitor wystawiajacy;
    private ObjectId lekId;
    private String dawkowanie;
    private Date dataWystawienia;
    private String status;

    public Prescription() {}

    public Prescription(ObjectId pacjentId, Exhibitor wystawiajacy, ObjectId lekId, String dawkowanie, Date dataWystawienia, String status) {
        this.pacjentId = pacjentId;
        this.wystawiajacy = wystawiajacy;
        this.lekId = lekId;
        this.dawkowanie = dawkowanie;
        this.dataWystawienia = dataWystawienia;
        this.status = status;
    }

    public ObjectId getId() { return id; }
    public ObjectId getPacjentId() { return pacjentId; }
    public Exhibitor getWystawiajacy() { return wystawiajacy; }
    public ObjectId getLekId() { return lekId; }
    public String getDawkowanie() { return dawkowanie; }
    public Date getDataWystawienia() { return dataWystawienia; }
    public String getStatus() { return status; }

    public void setId(ObjectId id) { this.id = id; }
    public void setPacjentId(ObjectId pacjentId) { this.pacjentId = pacjentId; }
    public void setWystawiajacy(Exhibitor wystawiajacy) { this.wystawiajacy = wystawiajacy; }
    public void setLekId(ObjectId lekId) { this.lekId = lekId; }
    public void setDawkowanie(String dawkowanie) { this.dawkowanie = dawkowanie; }
    public void setDataWystawienia(Date dataWystawienia) { this.dataWystawienia = dataWystawienia; }
    public void setStatus(String status) { this.status = status; }

}
