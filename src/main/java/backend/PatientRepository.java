package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class PatientRepository {
    private final MongoCollection<Patient> collection;

    public PatientRepository(MongoDatabase database) {
        CodecRegistry pojoCodecRegistry = fromProviders(
                PojoCodecProvider.builder()
                        .automatic(true)
                        .build()
        );

        this.collection = database.getCollection("patients", Patient.class);
    }

    public Patient createPatient(Patient patient) {
        collection.insertOne(patient);
        return patient;
    }
}