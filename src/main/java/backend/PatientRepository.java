// repository/PatientRepository.java
package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class PatientRepository {
    private final MongoCollection<Document> collection;

    public PatientRepository(MongoDatabase database) {
        this.collection = database.getCollection("patients");
    }

    public String createPatient(Patient patient) {
        Document doc = new Document()
                .append("firstName", patient.getFirstName())
                .append("lastName", patient.getLastName())
                .append("pesel", patient.getPesel())
                .append("birthDate", patient.getBirthDate())
                .append("address", patient.getAddress());

        collection.insertOne(doc);
        return doc.getObjectId("_id").toString();
    }

    // Metody do wyszukiwania, aktualizacji, usuwania itp.
}
