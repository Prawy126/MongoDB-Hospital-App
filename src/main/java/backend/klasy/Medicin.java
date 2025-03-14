package backend.klasy;

import backend.status.Type;
import backend.wyjatki.DoseException;
import backend.wyjatki.NullNameException;
import org.bson.types.ObjectId;
import java.util.List;

/**
 * Klasa Medicin reprezentuje lek w systemie.
 */
public class Medicin {
    private ObjectId id;
    private String name;
    private Type type; //String na przykład syrop lub taboletki
    private float dose; // dawka w int na przykład 500mg lub 2,4g
    private List<String> allergies;
    private boolean requiresPrescription;

    public Medicin() {}

    public Medicin(String name, Type type, float dose, List<String> allergies, boolean requiresPrescription) throws NullNameException, DoseException {
        if(name.length()>0)this.name = name;
        else throw new NullNameException("Nazwa leku nie może być pusta");
        this.type = type;
        if(dose!=0)this.dose = dose;
        else throw new DoseException("Dawka leku nie może być pusta");
        this.allergies = allergies;
        this.requiresPrescription = requiresPrescription;
    }
    public Medicin(String name, Type type, float dose, List<String> allergies) throws NullNameException, DoseException {
        if(name.length()!= 0)this.name = name;
        else throw new NullNameException("Nazwa leku nie może być pusta");
        this.type = type;
        if(dose != 0)this.dose = dose;
        else throw new DoseException("Dawka leku nie może być pusta");
        this.allergies = allergies;
        this.requiresPrescription = false;
    }
    public Medicin(String name, Type type, float dose) throws NullNameException, DoseException {
        if(name.length()!=0) this.name = name;
        else throw new NullNameException("Nazwa leku nie może być pusta");
        this.type = type;
        if(dose != 0)this.dose = dose;
        else throw new DoseException("Dawka leku nie może być pusta");
        this.allergies = null;
        this.requiresPrescription = false;
    }
    public ObjectId getId() { return id; }
    public String getName() { return name; }
    public Type getType() { return type; }
    public float getDose() { return dose; }
    public List<String> getAllergies() { return allergies; }
    public boolean getRequiresPrescription() { return requiresPrescription; }

    public void setId(ObjectId id) { this.id = id; }
    public void setName(String name) throws NullNameException{ if(name.length()!=0)this.name = name;else throw new NullNameException("Nazwa leku nie może być pusta"); }
    public void setType(Type type) { this.type = type; }
    public void setDose(float dose) throws DoseException{ if(dose!=0)this.dose = dose; else throw new DoseException("Dawka leku nie może być pusta"); }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    public void setRequiresPrescription(boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }

}
