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
    private final PatientRepository patientRepository;

    /**
     * Konstruktor inicjalizujący kolekcję wizyt.
     *
     * @param database obiekt MongoDatabase reprezentujący połączenie z bazą danych
     */
    public AppointmentRepository(MongoDatabase database) {
        this.collection = database.getCollection("appointments", Appointment.class);
        this.roomRepository = new RoomRepository(database);
        this.doctorRepository = new DoctorRepository(database);
        this.patientRepository = new PatientRepository(database);
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

        // Najpierw zapisz zabieg, aby uzyskać jego ID
        collection.insertOne(appointment);

        // Następnie dodaj pacjenta do sali
        updateRoomAfterAppointmentCreation(appointment);
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

        // Pobierz stary zabieg, aby porównać sale i pacjentów
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

        // Najpierw zaktualizuj zabieg w bazie danych
        collection.replaceOne(eq("_id", appointment.getId()), appointment);

        // Następnie zaktualizuj sale
        updateRoomAfterAppointmentUpdate(oldAppointment, appointment);

        return appointment;
    }

    /**
     * Aktualizuje listę pacjentów w sali po utworzeniu zabiegu.
     * Dodaje ID pacjenta do listy pacjentów w sali.
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

            // Sprawdź, czy sala nie jest pełna
            if (room.isFull()) {
                throw new IllegalStateException("Sala " + room.getNumber() + " jest pełna, nie można dodać więcej pacjentów");
            }

            // Dodaj pacjenta do sali tylko jeśli jeszcze go tam nie ma
            List<ObjectId> patientIds = room.getPatientIds();
            if (!patientIds.contains(patientId)) {
                // Dodaj ID pacjenta do listy
                patientIds.add(patientId);
                room.setPatientIds(patientIds);

                // Zaktualizuj salę w bazie danych
                roomRepository.updateRoom(roomId, room);

                // Pobierz dane pacjenta dla lepszego logowania
                Patient patient = patientRepository.findPatientById(patientId).getFirst();
                String patientName = patient != null ?
                        patient.getFirstName() + " " + patient.getLastName() :
                        patientId.toString();

                System.out.println("Dodano pacjenta " + patientName + " do sali " + room.getNumber());
                System.out.println("Aktualna liczba pacjentów w sali: " + room.getCurrentPatientCount() + "/" + room.getMaxPatients());
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas aktualizacji sali: " + e.getMessage());
            throw new RuntimeException("Nie udało się zaktualizować sali: " + e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje listę pacjentów w sali po usunięciu zabiegu.
     * Usuwa ID pacjenta z listy pacjentów w sali.
     *
     * @param appointment usunięty zabieg
     */
    private void updateRoomAfterAppointmentDeletion(Appointment appointment) {
        try {
            ObjectId roomId = appointment.getRoom();
            ObjectId patientId = appointment.getPatientId();

            List<Room> roomList = roomRepository.findRoomsById(roomId);
            if (roomList.isEmpty()) {
                System.err.println("Ostrzeżenie: Nie znaleziono sali o ID: " + roomId);
                return;
            }

            Room room = roomList.getFirst();

            // Usuń pacjenta z sali
            List<ObjectId> patientIds = room.getPatientIds();
            if (patientIds.contains(patientId)) {
                // Usuń ID pacjenta z listy
                patientIds.remove(patientId);
                room.setPatientIds(patientIds);

                // Zaktualizuj salę w bazie danych
                roomRepository.updateRoom(roomId, room);

                // Pobierz dane pacjenta dla lepszego logowania
                Patient patient = patientRepository.findPatientById(patientId).getFirst();
                String patientName = patient != null ?
                        patient.getFirstName() + " " + patient.getLastName() :
                        patientId.toString();

                System.out.println("Usunięto pacjenta " + patientName + " z sali " + room.getNumber());
                System.out.println("Aktualna liczba pacjentów w sali: " + room.getCurrentPatientCount() + "/" + room.getMaxPatients());
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas aktualizacji sali: " + e.getMessage());
            throw new RuntimeException("Nie udało się zaktualizować sali: " + e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje listę pacjentów w salach po aktualizacji zabiegu.
     * Jeśli sala się zmieniła, usuwa ID pacjenta z listy pacjentów w starej sali
     * i dodaje do listy w nowej sali.
     *
     * @param oldAppointment stary zabieg
     * @param newAppointment nowy zabieg
     */
    private void updateRoomAfterAppointmentUpdate(Appointment oldAppointment, Appointment newAppointment) {
        try {
            // Jeśli sala się zmieniła, zaktualizuj obie sale
            if (!oldAppointment.getRoom().equals(newAppointment.getRoom())) {
                // Usuń pacjenta ze starej sali
                updateRoomAfterAppointmentDeletion(oldAppointment);

                // Dodaj pacjenta do nowej sali
                updateRoomAfterAppointmentCreation(newAppointment);
            }
            // Jeśli pacjent się zmienił, ale sala pozostała ta sama
            else if (!oldAppointment.getPatientId().equals(newAppointment.getPatientId())) {
                // Usuń starego pacjenta
                updateRoomAfterAppointmentDeletion(oldAppointment);

                // Dodaj nowego pacjenta
                updateRoomAfterAppointmentCreation(newAppointment);
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas aktualizacji sal: " + e.getMessage());
            throw new RuntimeException("Nie udało się zaktualizować sal: " + e.getMessage(), e);
        }
    }

    /**
     * Pobiera listę pacjentów znajdujących się w danej sali.
     *
     * @param roomId ID sali
     * @return lista pacjentów w sali
     */
    public List<Patient> getPatientsInRoom(ObjectId roomId) {
        List<Room> roomList = roomRepository.findRoomsById(roomId);
        if (roomList.isEmpty()) {
            return new ArrayList<>();
        }

        Room room = roomList.getFirst();
        List<ObjectId> patientIds = room.getPatientIds();

        List<Patient> patients = new ArrayList<>();
        for (ObjectId patientId : patientIds) {
            List<Patient> patientList = patientRepository.findPatientById(patientId);
            if (!patientList.isEmpty()) {
                patients.add(patientList.getFirst());
            }
        }

        return patients;
    }

    /**
     * Usuwa wizytę po jej ID.
     *
     * @param id ID wizyty do usunięcia
     */
    public void deleteAppointment(ObjectId id) {
        // Pobierz zabieg przed usunięciem, aby zaktualizować salę
        Optional<Appointment> appointmentOpt = findAppointmentById(id);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();

            // Najpierw usuń zabieg z bazy danych
            collection.deleteOne(eq("_id", id));

            // Następnie usuń pacjenta z sali
            updateRoomAfterAppointmentDeletion(appointment);
        } else {
            // Jeśli nie znaleziono zabiegu, po prostu usuń go z bazy
            collection.deleteOne(eq("_id", id));
        }
    }
}