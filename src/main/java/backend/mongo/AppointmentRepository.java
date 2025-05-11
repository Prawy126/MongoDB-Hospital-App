package backend.mongo;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.status.AppointmentStatus;
import backend.status.Day;
import backend.wyjatki.DoctorIsNotAvailableException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.*;

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
     * Sprawdza czy lekarz jest dostępny w danym terminie.
     *
     * @param doctorId ID lekarza
     * @param appointmentDateTime Data i czas wizyty
     * @param excludeAppointmentId ID wizyty do wykluczenia z porównania (używane przy aktualizacji)
     * @return true jeśli lekarz jest dostępny, false w przeciwnym razie
     */
    private boolean isDoctorAvailable(ObjectId doctorId, LocalDateTime appointmentDateTime, ObjectId excludeAppointmentId) {
        DoctorRepository doctorRepo = new DoctorRepository(MongoDatabaseConnector.connectToDatabase());
        Doctor doctor = doctorRepo.findDoctorById(doctorId);

        if (doctor == null) {
            return false;
        }

        java.time.DayOfWeek javaDayOfWeek = appointmentDateTime.getDayOfWeek();
        Day appointmentDay = convertToDayEnum(javaDayOfWeek);

        if (!doctor.getAvailableDays().contains(appointmentDay)) {
            return false;
        }

        List<Appointment> doctorAppointments = collection.find(
                and(
                        eq("doctorId", doctorId),
                        ne("status", AppointmentStatus.COMPLETED)
                )
        ).into(new ArrayList<>());

        if (excludeAppointmentId != null) {
            doctorAppointments.removeIf(appointment -> appointment.getId().equals(excludeAppointmentId));
        }

        for (Appointment appointment : doctorAppointments) {
            LocalDateTime existingAppointmentTime = appointment.getDate();

            long minutesBetween = Math.abs(Duration.between(appointmentDateTime, existingAppointmentTime).toMinutes());
            if (minutesBetween < 30) {
                return false;
            }
        }

        return true;
    }

    /**
     * Konwertuje java.time.DayOfWeek na backend.status.Day
     *
     * @param dayOfWeek Dzień tygodnia z java.time
     * @return Odpowiadający enum Day z aplikacji
     */
    private Day convertToDayEnum(java.time.DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return Day.MONDAY;
            case TUESDAY:
                return Day.TUESDAY;
            case WEDNESDAY:
                return Day.WEDNESDAY;
            case THURSDAY:
                return Day.THURSDAY;
            case FRIDAY:
                return Day.FRIDAY;
            case SATURDAY:
                return Day.SATURDAY;
            case SUNDAY:
                return Day.SUNDAY;
            default:
                throw new IllegalArgumentException("Nieznany dzień tygodnia: " + dayOfWeek);
        }
    }

    /**
     * Tworzy nową wizytę w bazie danych.
     *
     * @param appointment wizyta do utworzenia
     * @throws IllegalArgumentException jeśli wizyta jest null
     * @throws DoctorIsNotAvailableException jeśli lekarz nie jest dostępny w danym terminie
     */
    public void createAppointment(Appointment appointment) throws DoctorIsNotAvailableException {
        if (appointment == null) {
            throw new IllegalArgumentException("Zabieg nie może być nullem!!");
        }

        // Sprawdź dostępność lekarza
        if (!isDoctorAvailable(appointment.getDoctorId(), appointment.getDate(), null)) {
            throw new DoctorIsNotAvailableException(
                    "Lekarz jest już przypisany do innego zabiegu w tym terminie lub w ciągu 30 minut od tego terminu."
            );
        }

        collection.insertOne(appointment);
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
     * @throws DoctorIsNotAvailableException jeśli lekarz nie jest dostępny w danym terminie
     */
    public Appointment updateAppointment(Appointment appointment) throws DoctorIsNotAvailableException {
        if (appointment == null) {
            throw new IllegalArgumentException("Zabieg nie może być nullem!!");
        }

        // Sprawdź dostępność lekarza (z wykluczeniem aktualnej wizyty)
        if (!isDoctorAvailable(appointment.getDoctorId(), appointment.getDate(), appointment.getId())) {
            throw new DoctorIsNotAvailableException(
                    "Lekarz jest już przypisany do innego zabiegu w tym terminie lub w ciągu 30 minut od tego terminu."
            );
        }

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
}