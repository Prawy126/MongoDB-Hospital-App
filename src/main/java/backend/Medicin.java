package backend;

import org.bson.types.ObjectId;
import java.util.List;

/**
 * Klasa Medicin reprezentuje lek w systemie.
 */
public class Medicin {
    private ObjectId id;
    private String name;
    private String type;
    private String dose;
    private List<String> allergies;
    private boolean requiresPrescription;

    public Medicin() {}

    public Medicin(String name, String type, String dose, List<String> allergies, boolean requiresPrescription) {
        this.name = name;
        this.type = type;
        this.dose = dose;
        this.allergies = allergies;
        this.requiresPrescription = requiresPrescription;
    }

    public ObjectId getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDose() { return dose; }
    public List<String> getAllergies() { return allergies; }
    public boolean getRequiresPrescription() { return requiresPrescription; }

    public void setId(ObjectId id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setDose(String dose) { this.dose = dose; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    public void setRequiresPrescription(boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }

}
