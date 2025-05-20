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
 * Repozytorium do zarządzania wizytami w bazie MongoDB.
 */
public class AppointmentRepository {

    private final MongoCollection<Appointment> collection;
    private final RoomRepository roomRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    /**
     * Inicjalizuje repozytorium wizyt i zależne repozytoria.
     *
     * @param database baza danych MongoDB
     */
    public AppointmentRepository(MongoDatabase database) {
        this.collection = database.getCollection("appointments", Appointment.class);
        this.roomRepository = new RoomRepository(database);
        this.doctorRepository = new DoctorRepository(database);
        this.patientRepository = new PatientRepository(database);
    }

    /**
     * Tworzy nową wizytę po uprzedniej walidacji lekarza, pacjenta i sali.
     *
     * @param appointment obiekt wizyty
     * @throws DoctorIsNotAvailableException    lekarz zajęty w danym terminie
     * @throws PatientIsNotAvailableException   pacjent zajęty w danym terminie
     * @throws InappropriateRoomException       niezgodność sali ze specjalizacją
     */
    public void createAppointment(Appointment appointment)
            throws DoctorIsNotAvailableException, InappropriateRoomException, PatientIsNotAvailableException {
        if (appointment == null) throw new IllegalArgumentException("Zabieg nie może być nullem!!");

        if (!isDoctorAvailable(appointment.getDoctorId(), appointment.getDate(), null))
            throw new DoctorIsNotAvailableException("Lekarz jest zajęty w tym terminie.");

        if (!isPatientAvailable(appointment.getPatientId(), appointment.getDate(), null))
            throw new PatientIsNotAvailableException("Pacjent jest zajęty w tym terminie.");

        if (!isRoomAppropriateForDoctor(appointment.getDoctorId(), appointment.getRoom())) {
            Doctor doctor = doctorRepository.findDoctorById(appointment.getDoctorId());
            List<Room> rooms = roomRepository.findRoomsById(appointment.getRoom());
            throw new InappropriateRoomException("Lekarz o specjalizacji " +
                    (doctor != null ? doctor.getSpecialization().getDescription() : "nieznanej") +
                    " nie może korzystać z sali typu " +
                    (!rooms.isEmpty() ? rooms.getFirst().getType().getDescription() : "nieznany"));
        }

        collection.insertOne(appointment);
        updateRoomAfterAppointmentCreation(appointment);
    }

    /**
     * Zwraca wizytę o podanym ID.
     *
     * @param id identyfikator wizyty
     * @return wizyta jako Optional
     */
    public Optional<Appointment> findAppointmentById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    /**
     * Zwraca wszystkie wizyty z bazy.
     *
     * @return lista wizyt
     */
    public List<Appointment> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    /**
     * Zwraca wizyty danego pacjenta.
     *
     * @param patient obiekt pacjenta
     * @return lista wizyt
     */
    public List<Appointment> findAppointmentsByPatient(Patient patient) {
        if (patient == null) throw new IllegalArgumentException("Patient cannot be null");
        return collection.find(eq("patientId", patient.getId())).into(new ArrayList<>());
    }

    /**
     * Zwraca wizyty danego lekarza.
     *
     * @param doctor obiekt lekarza
     * @return lista wizyt
     */
    public List<Appointment> findAppointmentsByDoctor(Doctor doctor) {
        if (doctor == null) throw new IllegalArgumentException("Doctor cannot be null");
        return collection.find(eq("doctorId", doctor.getId())).into(new ArrayList<>());
    }

    /**
     * Aktualizuje istniejącą wizytę po walidacji.
     *
     * @param appointment zaktualizowany obiekt wizyty
     * @return zaktualizowana wizyta
     * @throws DoctorIsNotAvailableException    lekarz zajęty
     * @throws PatientIsNotAvailableException   pacjent zajęty
     * @throws InappropriateRoomException       sala niezgodna
     */
    public Appointment updateAppointment(Appointment appointment)
            throws DoctorIsNotAvailableException, InappropriateRoomException, PatientIsNotAvailableException {
        if (appointment == null) throw new IllegalArgumentException("Zabieg nie może być nullem!!");

        Optional<Appointment> oldOpt = findAppointmentById(appointment.getId());
        if (oldOpt.isEmpty()) throw new IllegalArgumentException("Nie znaleziono zabiegu o ID: " + appointment.getId());

        if (!isDoctorAvailable(appointment.getDoctorId(), appointment.getDate(), appointment.getId()))
            throw new DoctorIsNotAvailableException("Lekarz jest zajęty w tym terminie.");

        if (!isPatientAvailable(appointment.getPatientId(), appointment.getDate(), appointment.getId()))
            throw new PatientIsNotAvailableException("Pacjent jest zajęty w tym terminie.");

        if (!isRoomAppropriateForDoctor(appointment.getDoctorId(), appointment.getRoom()))
            throw new InappropriateRoomException("Wybrana sala nie odpowiada specjalizacji lekarza.");

        collection.replaceOne(eq("_id", appointment.getId()), appointment);
        updateRoomAfterAppointmentUpdate(oldOpt.get(), appointment);
        return appointment;
    }

    /**
     * Usuwa wizytę po ID i aktualizuje salę.
     *
     * @param id identyfikator wizyty
     */
    public void deleteAppointment(ObjectId id) {
        Optional<Appointment> appointmentOpt = findAppointmentById(id);
        appointmentOpt.ifPresent(this::updateRoomAfterAppointmentDeletion);
        collection.deleteOne(eq("_id", id));
    }

    /**
     * Zwraca listę pacjentów w danej sali.
     *
     * @param roomId identyfikator sali
     * @return lista pacjentów
     */
    public List<Patient> getPatientsInRoom(ObjectId roomId) {
        List<Room> rooms = roomRepository.findRoomsById(roomId);
        if (rooms.isEmpty()) return new ArrayList<>();

        List<Patient> patients = new ArrayList<>();
        for (ObjectId pid : rooms.getFirst().getPatientIds()) {
            patientRepository.findPatientById(pid).stream().findFirst().ifPresent(patients::add);
        }
        return patients;
    }

    /**
     * Sprawdza dostępność lekarza w danym terminie.
     */
    private boolean isDoctorAvailable(ObjectId doctorId, LocalDateTime appointmentDateTime, ObjectId excludeAppointmentId) {
        Doctor doctor = doctorRepository.findDoctorById(doctorId);
        if (doctor == null) return false;

        Day appointmentDay = convertToDayEnum(appointmentDateTime.getDayOfWeek());
        if (!doctor.getAvailableDays().contains(appointmentDay)) return false;

        LocalDateTime start = appointmentDateTime.toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);

        List<Appointment> appointments = collection.find(and(
                eq("doctorId", doctorId),
                ne("status", AppointmentStatus.COMPLETED),
                gte("date", start),
                lte("date", end)
        )).into(new ArrayList<>());

        if (excludeAppointmentId != null)
            appointments.removeIf(a -> a.getId().equals(excludeAppointmentId));

        return appointments.stream()
                .noneMatch(a -> Math.abs(Duration.between(appointmentDateTime, a.getDate()).toMinutes()) < 30);
    }

    /**
     * Sprawdza dostępność pacjenta w danym terminie.
     */
    private boolean isPatientAvailable(ObjectId patientId, LocalDateTime appointmentDateTime, ObjectId excludeAppointmentId) {
        LocalDateTime start = appointmentDateTime.toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);

        List<Appointment> appointments = collection.find(and(
                eq("patientId", patientId),
                ne("status", AppointmentStatus.COMPLETED),
                gte("date", start),
                lte("date", end)
        )).into(new ArrayList<>());

        if (excludeAppointmentId != null)
            appointments.removeIf(a -> a.getId().equals(excludeAppointmentId));

        return appointments.stream()
                .noneMatch(a -> Math.abs(Duration.between(appointmentDateTime, a.getDate()).toMinutes()) < 30);
    }

    /**
     * Sprawdza, czy sala jest zgodna z typem specjalizacji lekarza.
     */
    private boolean isRoomAppropriateForDoctor(ObjectId doctorId, ObjectId roomId) {
        Doctor doctor = doctorRepository.findDoctorById(doctorId);
        List<Room> rooms = roomRepository.findRoomsById(roomId);
        return doctor != null && !rooms.isEmpty()
                && doctor.getSpecialization().getCompatibleRoomType() == rooms.getFirst().getType();
    }

    /**
     * Konwertuje {@link java.time.DayOfWeek} na {@link Day}.
     */
    private Day convertToDayEnum(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> Day.MONDAY;
            case TUESDAY -> Day.TUESDAY;
            case WEDNESDAY -> Day.WEDNESDAY;
            case THURSDAY -> Day.THURSDAY;
            case FRIDAY -> Day.FRIDAY;
            case SATURDAY -> Day.SATURDAY;
            case SUNDAY -> Day.SUNDAY;
        };
    }

    /**
     * Dodaje pacjenta do sali po utworzeniu wizyty.
     */
    private void updateRoomAfterAppointmentCreation(Appointment appointment) {
        List<Room> rooms = roomRepository.findRoomsById(appointment.getRoom());
        if (rooms.isEmpty()) return;
        Room room = rooms.getFirst();

        List<ObjectId> ids = room.getPatientIds();
        if (!ids.contains(appointment.getPatientId())) {
            ids.add(appointment.getPatientId());
            room.setPatientIds(ids);
            roomRepository.updateRoom(room.getId(), room);
        }
    }

    /**
     * Usuwa pacjenta z sali po usunięciu wizyty.
     */
    private void updateRoomAfterAppointmentDeletion(Appointment appointment) {
        List<Room> rooms = roomRepository.findRoomsById(appointment.getRoom());
        if (rooms.isEmpty()) return;
        Room room = rooms.getFirst();

        List<ObjectId> ids = room.getPatientIds();
        if (ids.remove(appointment.getPatientId())) {
            room.setPatientIds(ids);
            roomRepository.updateRoom(room.getId(), room);
        }
    }

    /**
     * Znajduje wszystkie zabiegi zaplanowane w danej sali.
     *
     * @param roomId identyfikator sali
     * @return lista zabiegów zaplanowanych w danej sali
     */
    public List<Appointment> findAppointmentsByRoom(ObjectId roomId) {
        if (roomId == null) {
            throw new IllegalArgumentException("ID sali nie może być null");
        }
        return collection.find(eq("room", roomId)).into(new ArrayList<>());
    }

    /**
     * Aktualizuje przypisania do sali po modyfikacji wizyty.
     */
    private void updateRoomAfterAppointmentUpdate(Appointment oldAppointment, Appointment newAppointment) {
        if (!oldAppointment.getRoom().equals(newAppointment.getRoom()) ||
                !oldAppointment.getPatientId().equals(newAppointment.getPatientId())) {
            updateRoomAfterAppointmentDeletion(oldAppointment);
            updateRoomAfterAppointmentCreation(newAppointment);
        }
    }
}
