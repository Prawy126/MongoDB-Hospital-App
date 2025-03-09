package backend;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DoctorRepository {
    private final MongoCollection<Doctor> collection;

    public DoctorRepository(MongoDatabase database) {
        this.collection = database.getCollection("doctors", Doctor.class);
    }

    public Doctor createDoctor(Doctor doctor) {
        collection.insertOne(doctor);
        return doctor;
    }
}