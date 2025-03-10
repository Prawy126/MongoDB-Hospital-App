package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

public class AppointmentRepository {
    private final MongoCollection<Appointment> collection;

    public AppointmentRepository(MongoDatabase database) {
        this.collection = database.getCollection("appointments", Appointment.class);
    }

    public Appointment createAppointment(Appointment appointment) {
        collection.insertOne(appointment);
        return appointment;
    }

    public Optional<Appointment> findAppointmentById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    public List<Appointment> findAppointmentsByPatientId(ObjectId patientId) {
        return collection.find(eq("patientId", patientId)).into(new ArrayList<>());
    }

    public List<Appointment> findAppointmentsByDoctorId(ObjectId doctorId) {
        return collection.find(eq("doctorId", doctorId)).into(new ArrayList<>());
    }

    public List<Appointment> findAppointmentsByDate(LocalDate date) {
        return collection.find(eq("date", date)).into(new ArrayList<>());
    }

    public List<Appointment> findAvailableAppointments(LocalDate date, String specialization) {
        return collection.find(
                and(
                        eq("date", date),
                        eq("status", AppointmentStatus.SCHEDULED),
                        eq("doctor.specialization", specialization)
                )
        ).into(new ArrayList<>());
    }

    public Appointment updateAppointment(Appointment appointment) {
        collection.replaceOne(eq("_id", appointment.getId()), appointment);
        return appointment;
    }

    public void deleteAppointment(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<Appointment> findAll() {
        return collection.find().into(new ArrayList<>());
    }
}
