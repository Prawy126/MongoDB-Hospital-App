package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

public class PatientRepository {
    private final MongoCollection<Patient> collection;

    public PatientRepository(MongoDatabase database) {
        this.collection = database.getCollection("patients", Patient.class);
    }

    public Patient createPatient(Patient patient) {
        collection.insertOne(patient);
        return patient;
    }
    public Optional<Patient> findPatientById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }
    public List<Patient> findAll() {
        return collection.find().into(List.of());
    }
    public List<Patient> findPatientByFirstName(String firstName) {
        return collection.find(eq("firstName", firstName)).into(List.of());
    }
    public List<Patient> findPatientByLastName(String lastName) {
        return collection.find(eq("lastName", lastName)).into(List.of());
    }
    public List<Patient> findPatientByPesel(int pesel) {
        return collection.find(eq("pesel", pesel)).into(List.of());
    }
    public List<Patient> findPatientByAddress(String address) {
        return collection.find(eq("address", address)).into(List.of());
    }
    public List<Patient> findPatientByBirthDate(String birthDate) {
        return collection.find(eq("birthDate", birthDate)).into(List.of());
    }
    public Patient updatePatient(Patient patient) {
        collection.replaceOne(eq("_id", patient.getId()), patient);
        return patient;
    }
    public void deletePatient(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }
    
}