package backend;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import com.mongodb.MongoException;

import java.util.List;

public class MorphiaDatabaseConnector {

    private static final String DB_IP = "192.168.8.102";
    private static final int DB_PORT = 27017;
    private static final String DB_NAME = "hospital";

    private static Datastore datastore;

    static {
        try {
            MongoClient mongoClient = MongoClients.create(String.format("mongodb://%s:%d", DB_IP, DB_PORT));
            datastore = Morphia.createDatastore(mongoClient, DB_NAME);

            datastore.getMapper().mapPackage("backend"); // Nowy sposób na mapowanie całej paczki

            System.out.println("Połączono z bazą danych: " + DB_NAME);
        } catch (MongoException e) {
            System.err.println("Błąd połączenia: " + e.getMessage());
        }
    }

    public static void savePatient(Patient patient) {
        datastore.save(patient);
    }

    public static Patient findPatientByName(String name) {
        Query<Patient> query = datastore.find(Patient.class).filter(dev.morphia.query.filters.Filters.eq("name", name));
        return query.first();
    }

    public static List<Patient> getAllPatients() {
        return datastore.find(Patient.class).iterator().toList();
    }

    public static void deletePatientByName(String name) {
        datastore.find(Patient.class).filter(Filters.eq("name", name)).delete();
    }
}