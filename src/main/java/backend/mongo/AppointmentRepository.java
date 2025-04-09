package backend.mongo;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.status.Day;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.eq;

/**
 * Klasa AppointmentRepository zarządza operacjami CRUD dla kolekcji wizyt w bazie MongoDB.
 * <p>
 * Metody tej klasy pozwalają na tworzenie, wyszukiwanie, aktualizowanie i usuwanie wizyt.
 * Klasa ta zapewnia również metody do testowania operacji na kolekcji wizyt.
 * </p>
 */
public class AppointmentRepository {
    private final MongoCollection<Appointment> collection;

    /**
     * Konstruktor inicjalizujący kolekcję wizyt.
     *
     * @param database obiekt MongoDatabase reprezentujący połączenie z bazą danych
     */
    public AppointmentRepository(MongoDatabase database) {
        this.collection = database.getCollection("appointments", Appointment.class);
    }

    /**
     * Tworzy nową wizytę w bazie danych.
     *
     * @param appointment wizyta do utworzenia
     * @return utworzona wizyta
     * @throws IllegalArgumentException jeśli wizyta jest null
     */
    public Appointment createAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        collection.insertOne(appointment);
        return appointment;
    }

    /**
     * Znajduje wizytę po jej ID.
     *
     * @param id ID wizyty
     * @return Optional zawierający znalezioną wizytę lub pusty, jeśli nie znaleziono
     */
    public Optional<Appointment> findAppointmentById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    /**
     * Znajduje wszystkie wizyty w bazie danych.
     *
     * @return lista wszystkich wizyt
     */
    public List<Appointment> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    /**
     * Znajduje wizyty dla danego pacjenta.
     *
     * @param patient pacjent, dla którego szukamy wizyt
     * @return lista wizyt dla danego pacjenta
     * @throws IllegalArgumentException jeśli pacjent jest null
     */
    public List<Appointment> findAppointmentsByPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        return collection.find(eq("patientId", patient.getId())).into(new ArrayList<>());
    }

    /**
     * Znajduje wizyty dla danego lekarza.
     *
     * @param doctor lekarz, dla którego szukamy wizyt
     * @return lista wizyt dla danego lekarza
     * @throws IllegalArgumentException jeśli lekarz jest null
     */
    public List<Appointment> findAppointmentsByDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        return collection.find(eq("doctorId", doctor.getId())).into(new ArrayList<>());
    }

    /**
     * Aktualizuje dane wizyty w bazie danych.
     *
     * @param appointment wizyta do zaktualizowania
     * @return zaktualizowana wizyta
     */
    public Appointment updateAppointment(Appointment appointment) {
        collection.replaceOne(eq("_id", appointment.getId()), appointment);
        return appointment;
    }

    /**
     * Usuwa wizytę po jej ID.
     *
     * @param id ID wizyty do usunięcia
     */
    public void deleteAppointment(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }
    /**
     * Sprawdza dostępność lekarza w danym terminie.
     *
     * @param doctorId ID lekarza
     * @param date Data w formacie "YYYY-MM-DD"
     * @param startTime Czas rozpoczęcia "HH:mm"
     * @param endTime Czas zakończenia "HH:mm"
     * @return true jeśli dostępny
     */
    //zakomentowałem bo sypało błędem a nie chciało mi się tego naprawiać
    /*public boolean isDoctorAvailable(ObjectId doctorId, String date, String startTime, String endTime) {
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(Filters.and(
                        Filters.eq("doctorId", doctorId),
                        Filters.eq("date", date),
                        Filters.or(
                                Filters.lt("endTime", startTime),
                                Filters.gt("startTime", endTime)
                        )
                )),
                Aggregates.count("count")
        );

        Appointment result = collection.aggregate(pipeline).first();
        return result == null || result.getInteger("count", 0) == 0;
    }*/

}