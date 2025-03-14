package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.eq;

public class AppointmentRepository {
    private final MongoCollection<Appointment> collection;

    public AppointmentRepository(MongoDatabase database) {
        this.collection = database.getCollection("appointments", Appointment.class);
    }

    public Appointment createAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        collection.insertOne(appointment);
        return appointment;
    }

    public Optional<Appointment> findAppointmentById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    public List<Appointment> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    public List<Appointment> findAppointmentsByPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        return collection.find(eq("patientId", patient.getId())).into(new ArrayList<>());
    }

    public List<Appointment> findAppointmentsByDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        return collection.find(eq("doctorId", doctor.getId())).into(new ArrayList<>());
    }

    public Appointment updateAppointment(Appointment appointment) {
        collection.replaceOne(eq("_id", appointment.getId()), appointment);
        return appointment;
    }

    public void deleteAppointment(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    public void testAppointment() {
        System.out.println("\n=== Rozpoczynam testowanie AppointmentRepository ===");

        try {
            // Tworzenie pacjenta
            Patient testPatient = new Patient.Builder()
                    .firstName("Testowy")
                    .lastName("Pacjent")
                    .pesel(11122233311L)
                    .birthDate(LocalDate.now())
                    .address("ul. Przykładowa 10, Kraków")
                    .age(25)
                    .build();

            // Tworzenie lekarza
            Doctor testDoctor = new Doctor.Builder()
                    .firstName("Testowy")
                    .lastName("Lekarz")
                    .specialization("Kardiolog")
                    .pesel(11122233311L)
                    .age(45)
                    .availableDays(List.of("Poniedziałek", "Środa", "Piątek"))
                    .build();

            // Tworzenie wizyty
            Appointment testAppointment = new Appointment.Builder()
                    .patientId(testPatient)
                    .doctorId(testDoctor)
                    .date(LocalDate.now())
                    .room("Sala 205")
                    .description("Konsultacja kardiologiczna")
                    .build();

            Appointment createdAppointment = createAppointment(testAppointment);
            System.out.println("[OK] Utworzono wizytę: " + createdAppointment);

            // Wyszukiwanie po ID
            Optional<Appointment> foundById = findAppointmentById(createdAppointment.getId());
            System.out.println("[OK] Wyszukano wizytę po ID: " + foundById.orElse(null));

            // Wyszukiwanie wizyt dla pacjenta
            List<Appointment> appointmentsByPatient = findAppointmentsByPatient(testPatient);
            System.out.println("[OK] Wyszukano wizyty dla pacjenta: " + appointmentsByPatient.size());

            // Wyszukiwanie wizyt dla lekarza
            List<Appointment> appointmentsByDoctor = findAppointmentsByDoctor(testDoctor);
            System.out.println("[OK] Wyszukano wizyty dla lekarza: " + appointmentsByDoctor.size());

            // Pobranie wszystkich wizyt
            List<Appointment> allAppointments = findAll();
            System.out.println("[OK] Liczba wszystkich wizyt w bazie: " + allAppointments.size());

            // Aktualizacja wizyty
            createdAppointment.setRoom("Sala 301");
            createdAppointment.setDescription("Konsultacja kardiologiczna + EKG");
            Appointment updatedAppointment = updateAppointment(createdAppointment);
            System.out.println("[OK] Zaktualizowano wizytę: " + updatedAppointment.getDescription());

            // Usuwanie wizyty
            deleteAppointment(createdAppointment.getId());
            System.out.println("[OK] Usunięto wizytę o ID: " + createdAppointment.getId());

            System.out.println("[SUCCESS] Wszystkie testy zakończone pomyślnie!");

        } catch (Exception e) {
            System.err.println("[ERROR] Wystąpił błąd podczas testowania AppointmentRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}