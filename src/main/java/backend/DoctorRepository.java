// repository/DoctorRepository.java
package backend;

import backend.Doctor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

public class DoctorRepository {
    private final MongoCollection<Document> collection;

    public DoctorRepository(MongoDatabase database) {
        this.collection = database.getCollection("doctors");
    }

    public String createDoctor(Doctor doctor) {
        Document doc = new Document()
                .append("firstName", doctor.getFirstName())
                .append("lastName", doctor.getLastName())
                .append("specialization", doctor.getSpecialization())
                .append("availableDays", doctor.getAvailableDays());

        collection.insertOne(doc);
        return doc.getObjectId("_id").toString();
    }
}