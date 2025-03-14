package backend.mongo;

import backend.klasy.Doctor;
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

    public void testDoctor() {
        System.out.println("\n=== Rozpoczynam testowanie DoctorRepository ===");

        try {
            // Tworzenie lekarza
            Doctor testDoctor = new Doctor.Builder()
                    .firstName("Marek")
                    .lastName("Zieliński")
                    .specialization("Ortopeda")
                    .availableDays(List.of("Poniedziałek", "Środa"))
                    .age(50)
                    .pesel(888999000)
                    .build();

            Doctor createdDoctor = createDoctor(testDoctor);
            System.out.println("[OK] Utworzono lekarza: " + createdDoctor);

            // Wyszukiwanie po ID
            Optional<Doctor> foundById = findDoctorById(createdDoctor.getId());
            System.out.println("[OK] Wyszukano lekarza po ID: " + foundById.orElse(null));

            // Wyszukiwanie lekarzy po specjalizacji
            List<Doctor> doctorsBySpecialization = findDoctorsBySpecialization("Ortopeda");
            System.out.println("[OK] Wyszukano lekarzy specjalizujących się w Ortopedii: " + doctorsBySpecialization.size());

            // Wyszukiwanie dostępnych lekarzy na określony dzień
            List<Doctor> availableDoctors = findAvailableDoctorsOnDate(LocalDate.now());
            System.out.println("[OK] Wyszukano dostępnych lekarzy na dzisiejszy dzień: " + availableDoctors.size());

            // Pobranie wszystkich lekarzy
            List<Doctor> allDoctors = findAll();
            System.out.println("[OK] Liczba wszystkich lekarzy w bazie: " + allDoctors.size());

            // Aktualizacja lekarza
            createdDoctor.setAvailableDays(List.of("Wtorek", "Czwartek"));
            Doctor updatedDoctor = updateDoctor(createdDoctor);
            System.out.println("[OK] Zaktualizowano dni przyjęć lekarza: " + updatedDoctor.getAvailableDays());

            // Usuwanie lekarza
            deleteDoctor(createdDoctor.getId());
            System.out.println("[OK] Usunięto lekarza o ID: " + createdDoctor.getId());

            System.out.println("[SUCCESS] Wszystkie testy dla DoctorRepository zakończone pomyślnie!");

        } catch (Exception e) {
            System.err.println("[ERROR] Wystąpił błąd podczas testowania DoctorRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }

}