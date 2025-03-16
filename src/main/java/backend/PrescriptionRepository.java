package backend;

import backend.klasy.Medicin;
import backend.klasy.Prescription;
import backend.mongo.MedicinRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

public class PrescriptionRepository {
    private final MongoCollection<Prescription> collection;

    public PrescriptionRepository(MongoDatabase database) {
        this.collection = database.getCollection("recepty", Prescription.class);
    }

    // Wystawienie recepty
    public Prescription addPrescriptirion(Prescription prescripition) {
        collection.insertOne(prescripition);
        return prescripition;
    }

    // Sprawdzenie, czy lek wymaga recepty
    public boolean czyWymagaRecepty(ObjectId lekId) {
        Medicin medicin = new MedicinRepository(database).znajdzLekPoId(lekId);
        return medicin != null && medicin.getRequiresPrescription();
    }
}
