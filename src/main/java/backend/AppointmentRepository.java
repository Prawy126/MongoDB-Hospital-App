package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

public class AppointmentRepository {
    private final MongoCollection<Appointment> collection;

    public AppointmentRepository(MongoDatabase database) {
        this.collection = database.getCollection("appointments", Appointment.class);
    }

    public Appointment createAppointment(Appointment appointment) {
        collection.insertOne(appointment);
        return appointment;
    }
}
