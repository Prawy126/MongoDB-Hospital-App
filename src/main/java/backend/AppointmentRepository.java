package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

public class AppointmentRepository {
    private final MongoCollection<Document> collection;

    public AppointmentRepository(MongoDatabase database) {
        this.collection = database.getCollection("appointments");
    }

    public String createAppointment(Appointment appointment) {
        Document doc = new Document()
                .append("patientId", appointment.getPatientId())
                .append("doctorId", appointment.getDoctorId())
                .append("date", appointment.getDate())
                .append("room", appointment.getRoom())
                .append("description", appointment.getDescription());

        collection.insertOne(doc);
        return doc.getObjectId("_id").toString();
    }
}