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
    private Type type;
    private Double dose;
    private List<String> allergies;
    private boolean requiresPrescription;

    public Medicin() {}

    private Medicin(String name, Type type, Double dose, List<String> allergies, boolean requiresPrescription) throws NullNameException, DoseException {
        if (name == null || name.isEmpty()) {
            throw new NullNameException("Nazwa leku nie może być pusta.");
        }
        this.name = name;
        this.type = type;

        if (dose == null || dose <= 0) {
            throw new DoseException("Dawka leku musi być większa niż 0.");
        }
        this.dose = dose;

        this.allergies = allergies;
        this.requiresPrescription = requiresPrescription;
    }

    public ObjectId getId() { return id; }
    public String getName() { return name; }
    public Type getType() { return type; }
    public Double getDose() { return dose; }
    public List<String> getAllergies() { return allergies; }
    public boolean getRequiresPrescription() { return requiresPrescription; }

    public void setId(ObjectId id) { this.id = id; }
    public void setName(String name) throws NullNameException {
        if (name == null || name.isEmpty()) {
            throw new NullNameException("Nazwa leku nie może być pusta.");
        }
        this.name = name;
    }

    public void setType(Type type) { this.type = type; }

    public void setDose(Double dose) throws DoseException {
        if (dose == null || dose <= 0) {
            throw new DoseException("Dawka leku musi być większa niż 0.");
        }
        this.dose = dose;
    }

    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    public void setRequiresPrescription(boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }

    @Override
    public String toString() {
        return "Medicin{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", dose=" + dose +
                ", allergies=" + allergies +
                ", requiresPrescription=" + requiresPrescription +
                '}';
    }

    public static class Builder {
        private ObjectId id;
        private String name;
        private Type type;
        private Double dose;
        private List<String> allergies;
        private boolean requiresPrescription;

        public Builder() {}

        public Builder withId(ObjectId id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder dose(Double dose) {
            this.dose = dose;
            return this;
        }

        public Builder allergies(List<String> allergies) {
            this.allergies = allergies;
            return this;
        }

        public Builder requiresPrescription(boolean requiresPrescription) {
            this.requiresPrescription = requiresPrescription;
            return this;
        }

        public Medicin build() throws NullNameException, DoseException {
            if (name == null || name.isEmpty()) {
                throw new NullNameException("Nazwa leku nie może być pusta.");
            }
            if (dose == null || dose <= 0) {
                throw new DoseException("Dawka leku musi być większa niż 0.");
            }

            Medicin medicin = new Medicin(name, type, dose, allergies, requiresPrescription);
            if (id == null) {
                medicin.setId(new ObjectId());
            } else {
                medicin.setId(id);
            }
            return medicin;
        }
    }
}
