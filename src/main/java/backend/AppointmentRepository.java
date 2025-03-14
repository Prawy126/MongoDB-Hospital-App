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

/**
 * Klasa AppointmentRepository zarządza operacjami CRUD dla kolekcji wizyt w bazie danych MongoDB.
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
     */
    public Appointment createAppointment(Appointment appointment) {
        collection.insertOne(appointment);
        return appointment;
    }

    /**
     * Znajduje wizytę po jej ID.
     *
     * @param id ID wizyty
     * @return Optional zawierający znalezioną wizytę lub pusty, jeśli wizyta nie została znaleziona
     */
    public Optional<Appointment> findAppointmentById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    /**
     * Znajduje wizyty po ID pacjenta.
     *
     * @param patientId ID pacjenta
     * @return lista wizyt pacjenta
     */
    public List<Appointment> findAppointmentsByPatientId(ObjectId patientId) {
        return collection.find(eq("patientId", patientId)).into(new ArrayList<>());
    }

    /**
     * Znajduje wizyty po ID lekarza.
     *
     * @param doctorId ID lekarza
     * @return lista wizyt lekarza
     */
    public List<Appointment> findAppointmentsByDoctorId(ObjectId doctorId) {
        return collection.find(eq("doctorId", doctorId)).into(new ArrayList<>());
    }

    /**
     * Znajduje wizyty po dacie.
     *
     * @param date data wizyty
     * @return lista wizyt w danym dniu
     */
    public List<Appointment> findAppointmentsByDate(LocalDate date) {
        return collection.find(eq("date", date)).into(new ArrayList<>());
    }

    /**
     * Znajduje dostępne wizyty na dany dzień i specjalizację.
     *
     * @param date data wizyty
     * @param specialization specjalizacja lekarza
     * @return lista dostępnych wizyt
     */
    public List<Appointment> findAvailableAppointments(LocalDate date, String specialization) {
        return collection.find(
                and(
                        eq("date", date),
                        eq("status", AppointmentStatus.SCHEDULED),
                        eq("doctor.specialization", specialization)
                )
        ).into(new ArrayList<>());
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
     * Znajduje wszystkie wizyty w bazie danych.
     *
     * @return lista wszystkich wizyt
     */
    public List<Appointment> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    public void testAppointment() {
        System.out.println("\n=== Rozpoczynam testowanie AppointmentRepository ===");

        try {
            // Tworzenie pacjenta i lekarza dla wizyty
            Patient testPatient = new Patient.Builder()
                    .firstName("Tomasz")
                    .lastName("Nowak")
                    .pesel(555666777)
                    .birthDate(LocalDate.of(1990, 5, 10))
                    .address("ul. Przykładowa 15, Wrocław")
                    .age(34)
                    .build();

            Doctor testDoctor = new Doctor.Builder()
                    .firstName("Maria")
                    .lastName("Kowalska")
                    .specialization("Neurolog")
                    .availableDays(List.of("Poniedziałek", "Czwartek"))
                    .age(48)
                    .pesel(444333222)
                    .build();

            // Tworzenie wizyty
            Appointment testAppointment = new Appointment.Builder()
                    .patientId(testPatient)
                    .doctorId(testDoctor)
                    .date(LocalDate.now().plusDays(1))
                    .room("Gabinet 12")
                    .description("Badanie neurologiczne")
                    .build();

            Appointment createdAppointment = createAppointment(testAppointment);
            System.out.println("[OK] Utworzono wizytę w sali: " + createdAppointment.getRoom());

            // Wyszukiwanie wizyty po ID
            Optional<Appointment> foundById = findAppointmentById(createdAppointment.getId());
            System.out.println("[OK] Wyszukano wizytę po ID: " + foundById.orElse(null));

            // Wyszukiwanie wizyt po ID pacjenta
            List<Appointment> patientAppointments = findAppointmentsByPatientId(testPatient.getId());
            System.out.println("[OK] Liczba wizyt pacjenta: " + patientAppointments.size());

            // Wyszukiwanie wizyt po ID lekarza
            List<Appointment> doctorAppointments = findAppointmentsByDoctorId(testDoctor.getId());
            System.out.println("[OK] Liczba wizyt lekarza: " + doctorAppointments.size());

            // Wyszukiwanie wizyt po dacie
            List<Appointment> appointmentsByDate = findAppointmentsByDate(LocalDate.now().plusDays(1));
            System.out.println("[OK] Liczba wizyt w danym dniu: " + appointmentsByDate.size());

            // Wyszukiwanie dostępnych wizyt na dany dzień i specjalizację
            List<Appointment> availableAppointments = findAvailableAppointments(LocalDate.now().plusDays(1), "Neurolog");
            System.out.println("[OK] Liczba dostępnych wizyt dla specjalizacji 'Neurolog': " + availableAppointments.size());

            // Aktualizacja wizyty
            createdAppointment.setRoom("Gabinet 20");
            createdAppointment.setDescription("Badanie neurologiczne i testy");
            Appointment updatedAppointment = updateAppointment(createdAppointment);
            System.out.println("[OK] Zaktualizowano wizytę: " + updatedAppointment.getDescription());

            // Usuwanie wizyty
            deleteAppointment(createdAppointment.getId());
            System.out.println("[OK] Usunięto wizytę o ID: " + createdAppointment.getId());

            System.out.println("[SUCCESS] Wszystkie testy dla AppointmentRepository zakończone pomyślnie!");

        } catch (Exception e) {
            System.err.println("[ERROR] Wystąpił błąd podczas testowania AppointmentRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }

}