package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

/**
 * Klasa DoctorRepository zarządza operacjami CRUD dla kolekcji lekarzy w bazie danych MongoDB.
 */
public class DoctorRepository {
    private final MongoCollection<Doctor> collection;

    public DoctorRepository(MongoDatabase database) {
        this.collection = database.getCollection("doctors", Doctor.class);
    }

    public Doctor createDoctor(Doctor doctor) {
        collection.insertOne(doctor);
        return doctor;
    }

    public Optional<Doctor> findDoctorById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    public List<Doctor> findDoctorsBySpecialization(String specialization) {
        return collection.find(eq("specialization", specialization)).into(new ArrayList<>());
    }

    public List<Doctor> findAvailableDoctorsOnDate(LocalDate date) {
        return collection.find(
                and(
                        eq("availableDays", date.getDayOfWeek().toString()),
                        ne("status", "UNAVAILABLE")
                )
        ).into(new ArrayList<>());
    }

    public Doctor updateDoctor(Doctor doctor) {
        collection.replaceOne(eq("_id", doctor.getId()), doctor);
        return doctor;
    }

    public void deleteDoctor(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<Doctor> findAll() {
        return collection.find().into(new ArrayList<>());
    }
}