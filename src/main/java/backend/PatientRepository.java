package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PatientRepository {
    private final MongoCollection<Patient> collection;

    public PatientRepository(MongoDatabase database) {
        this.collection = database.getCollection("patients", Patient.class);
    }

    public Patient createPatient(Patient patient) {
        collection.insertOne(patient);
        return patient;
    }
}