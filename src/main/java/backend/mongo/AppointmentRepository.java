package backend.mongo;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.klasy.Room;
import backend.status.AppointmentStatus;
import backend.status.Day;
import backend.status.TypeOfRoom;
import backend.wyjatki.DoctorIsNotAvailableException;
import backend.wyjatki.InappropriateRoomException;
import backend.wyjatki.PatientIsNotAvailableException;
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
    private final RoomRepository roomRepository;
    private final DoctorRepository doctorRepository;

    /**
     * Konstruktor inicjalizujący kolekcję wizyt.
     *
     * @param database obiekt MongoDatabase reprezentujący połączenie z bazą danych
     */
    public AppointmentRepository(MongoDatabase database) {
        this.collection = database.getCollection("appointments", Appointment.class);
        this.roomRepository = new RoomRepository(database);
        this.doctorRepository = new DoctorRepository(database);
    }

    /**
     * Sprawdza czy lekarz jest dostępny w danym terminie.
     * Sprawdza tylko wizyty w obrębie tego samego dnia.
     *
     * @param doctorId ID lekarza
     * @param appointmentDateTime Data i czas wizyty
     * @param excludeAppointmentId ID wizyty do wykluczenia z porównania (używane przy aktualizacji)
     * @return true jeśli lekarz jest dostępny, false w przeciwnym razie
     */
    private boolean isDoctorAvailable(ObjectId doctorId, LocalDateTime appointmentDateTime, ObjectId excludeAppointmentId) {
        Doctor doctor = doctorRepository.findDoctorById(doctorId);

        if (doctor == null) {
            return false;
        }

        java.time.DayOfWeek javaDayOfWeek = appointmentDateTime.getDayOfWeek();
        Day appointmentDay = convertToDayEnum(javaDayOfWeek);

        if (!doctor.getAvailableDays().contains(appointmentDay)) {
            return false;
        }

        LocalDateTime startOfDay = appointmentDateTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<Appointment> doctorAppointments = collection.find(
                and(
                        eq("doctorId", doctorId),
                        ne("status", AppointmentStatus.COMPLETED),
                        gte("date", startOfDay),
                        lte("date", endOfDay)
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
     * Sprawdza czy pacjent jest dostępny w danym terminie.
     * Sprawdza tylko wizyty w obrębie tego samego dnia.
     *
     * @param patientId ID pacjenta
     * @param appointmentDateTime Data i czas wizyty
     * @param excludeAppointmentId ID wizyty do wykluczenia z porównania (używane przy aktualizacji)
     * @return true jeśli pacjent jest dostępny, false w przeciwnym razie
     */
    private boolean isPatientAvailable(ObjectId patientId, LocalDateTime appointmentDateTime, ObjectId excludeAppointmentId) {
        LocalDateTime startOfDay = appointmentDateTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<Appointment> patientAppointments = collection.find(
                and(
                        eq("patientId", patientId),
                        ne("status", AppointmentStatus.COMPLETED),
                        gte("date", startOfDay),
                        lte("date", endOfDay)
                )
        ).into(new ArrayList<>());

        if (excludeAppointmentId != null) {
            patientAppointments.removeIf(appointment -> appointment.getId().equals(excludeAppointmentId));
        }

        for (Appointment appointment : patientAppointments) {
            LocalDateTime existingAppointmentTime = appointment.getDate();

            long minutesBetween = Math.abs(Duration.between(appointmentDateTime, existingAppointmentTime).toMinutes());
            if (minutesBetween < 30) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sprawdza czy sala jest odpowiednia dla specjalizacji lekarza.
     *
     * @param doctorId ID lekarza
     * @param roomId ID sali
     * @return true jeśli sala jest odpowiednia, false w przeciwnym razie
     */
    private boolean isRoomAppropriateForDoctor(ObjectId doctorId, ObjectId roomId) {
        Doctor doctor = doctorRepository.findDoctorById(doctorId);
        List<Room> roomList = roomRepository.findRoomsById(roomId);

        if (doctor == null || roomList.isEmpty()) {
            return false;
        }

        Room room = roomList.getFirst();
        TypeOfRoom compatibleRoomType = doctor.getSpecialization().getCompatibleRoomType();
        TypeOfRoom roomType = room.getType();

        System.out.println("Sprawdzam kompatybilność:");
        System.out.println("- Lekarz: " + doctor.getFirstName() + " " + doctor.getLastName());
        System.out.println("- Specjalizacja: " + doctor.getSpecialization().getDescription());
        System.out.println("- Kompatybilny typ sali: " + compatibleRoomType.getDescription());
        System.out.println("- Wybrana sala: " + room.getNumber() + " - " + room.getAddress());
        System.out.println("- Typ wybranej sali: " + room.getType().getDescription());
        System.out.println("- Wynik porównania: " + (compatibleRoomType == roomType));

        return compatibleRoomType == roomType;
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
     * @throws PatientIsNotAvailableException jeśli pacjent nie jest dostępny w danym terminie
     * @throws InappropriateRoomException jeśli sala nie jest odpowiednia dla specjalizacji lekarza
     */
    public void createAppointment(Appointment appointment)
            throws DoctorIsNotAvailableException, InappropriateRoomException, PatientIsNotAvailableException {
        if (appointment == null) {
            throw new IllegalArgumentException("Zabieg nie może być nullem!!");
        }

        if (!isDoctorAvailable(appointment.getDoctorId(), appointment.getDate(), null)) {
            throw new DoctorIsNotAvailableException(
                    "Lekarz jest już przypisany do innego zabiegu w tym terminie lub w ciągu 30 minut od tego terminu."
            );
        }

        if (!isPatientAvailable(appointment.getPatientId(), appointment.getDate(), null)) {
            throw new PatientIsNotAvailableException(
                    "Pacjent jest już przypisany do innego zabiegu w tym terminie lub w ciągu 30 minut od tego terminu."
            );
        }

        if (!isRoomAppropriateForDoctor(appointment.getDoctorId(), appointment.getRoom())) {
            Doctor doctor = doctorRepository.findDoctorById(appointment.getDoctorId());
            List<Room> room = roomRepository.findRoomsById(appointment.getRoom());

            String doctorSpec = doctor != null ? doctor.getSpecialization().getDescription() : "nieznana";
            String roomType = !room.isEmpty() ? room.getFirst().getType().getDescription() : "nieznana";

            throw new InappropriateRoomException(
                    "Lekarz o specjalizacji " + doctorSpec + " nie może przeprowadzać zabiegu w sali typu " + roomType
            );
        }

        // Dodaj pacjenta do sali przed zapisaniem zabiegu
        updateRoomAfterAppointmentCreation(appointment);

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
     * @throws PatientIsNotAvailableException jeśli pacjent nie jest dostępny w danym terminie
     * @throws InappropriateRoomException jeśli sala nie jest odpowiednia dla specjalizacji lekarza
     */
    public Appointment updateAppointment(Appointment appointment)
            throws DoctorIsNotAvailableException, InappropriateRoomException, PatientIsNotAvailableException {
        if (appointment == null) {
            throw new IllegalArgumentException("Zabieg nie może być nullem!!");
        }

        // Pobierz stary zabieg, aby porównać sale
        Optional<Appointment> oldAppointmentOpt = findAppointmentById(appointment.getId());
        if (oldAppointmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Nie znaleziono zabiegu o ID: " + appointment.getId());
        }
        Appointment oldAppointment = oldAppointmentOpt.get();

        if (!isDoctorAvailable(appointment.getDoctorId(), appointment.getDate(), appointment.getId())) {
            throw new DoctorIsNotAvailableException(
                    "Lekarz jest już przypisany do innego zabiegu w tym terminie lub w ciągu 30 minut od tego terminu."
            );
        }

        if (!isPatientAvailable(appointment.getPatientId(), appointment.getDate(), appointment.getId())) {
            throw new PatientIsNotAvailableException(
                    "Pacjent jest już przypisany do innego zabiegu w tym terminie lub w ciągu 30 minut od tego terminu."
            );
        }

        if (!isRoomAppropriateForDoctor(appointment.getDoctorId(), appointment.getRoom())) {
            Doctor doctor = doctorRepository.findDoctorById(appointment.getDoctorId());
            List<Room> room = roomRepository.findRoomsById(appointment.getRoom());

            String doctorSpec = doctor != null ? doctor.getSpecialization().getDescription() : "nieznana";
            String roomType = !room.isEmpty() ? room.getFirst().getType().getDescription() : "nieznana";

            throw new InappropriateRoomException(
                    "Lekarz o specjalizacji " + doctorSpec + " nie może przeprowadzać zabiegu w sali typu " + roomType
            );
        }

        updateRoomAfterAppointmentUpdate(oldAppointment, appointment);

        collection.replaceOne(eq("_id", appointment.getId()), appointment);
        return appointment;
    }

    /**
     * Aktualizuje listę pacjentów w sali po utworzeniu zabiegu.
     *
     * @param appointment utworzony zabieg
     */
    private void updateRoomAfterAppointmentCreation(Appointment appointment) {
        try {
            ObjectId roomId = appointment.getRoom();
            ObjectId patientId = appointment.getPatientId();

            List<Room> roomList = roomRepository.findRoomsById(roomId);
            if (roomList.isEmpty()) {
                System.err.println("Ostrzeżenie: Nie znaleziono sali o ID: " + roomId);
                return;
            }

            Room room = roomList.getFirst();

            if (room.isFull()) {
                throw new IllegalStateException("Sala " + room.getNumber() + " jest pełna, nie można dodać więcej pacjentów");
            }

            if (!room.getPatientIds().contains(patientId)) {
                room.addPatientId(patientId);
                roomRepository.updateRoom(roomId, room);
                System.out.println("Dodano pacjenta " + patientId + " do sali " + room.getNumber());
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas aktualizacji sali: " + e.getMessage());
            throw new RuntimeException("Nie udało się zaktualizować sali: " + e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje listę pacjentów w sali po usunięciu zabiegu.
     *
     * @param appointment usunięty zabieg
     */
    private void updateRoomAfterAppointmentDeletion(Appointment appointment) {
        ObjectId roomId = appointment.getRoom();
        ObjectId patientId = appointment.getPatientId();

        List<Room> roomList = roomRepository.findRoomsById(roomId);
        if (roomList.isEmpty()) {
            return;
        }

        Room room = roomList.getFirst();

        // Usuń pacjenta z sali
        List<ObjectId> patientIds = room.getPatientIds();
        if (patientIds.contains(patientId)) {
            patientIds.remove(patientId);
            room.setPatientIds(patientIds);
            roomRepository.updateRoom(roomId, room);
            System.out.println("Usunięto pacjenta " + patientId + " z sali " + room.getNumber());
        }
    }

    /**
     * Aktualizuje listę pacjentów w salach po aktualizacji zabiegu.
     *
     * @param oldAppointment stary zabieg
     * @param newAppointment nowy zabieg
     */
    private void updateRoomAfterAppointmentUpdate(Appointment oldAppointment, Appointment newAppointment) {
        if (!oldAppointment.getRoom().equals(newAppointment.getRoom())) {
            updateRoomAfterAppointmentDeletion(oldAppointment);
            updateRoomAfterAppointmentCreation(newAppointment);
        }
    }

    /**
     * Usuwa wizytę po jej ID.
     *
     * @param id ID wizyty do usunięcia
     */
    public void deleteAppointment(ObjectId id) {
        Optional<Appointment> appointmentOpt = findAppointmentById(id);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            updateRoomAfterAppointmentDeletion(appointment);
        }

        collection.deleteOne(eq("_id", id));
    }
}