package backend.mongo;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.klasy.Room;
import backend.status.AppointmentStatus;
import backend.status.Day;
import backend.status.TypeOfRoom;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataLoader {

    private static final String[] FIRST_NAMES = {"Jan", "Anna", "Piotr", "Maria", "Krzysztof", "Agnieszka", "Andrzej", "Małgorzata", "Grzegorz", "Ewa"};
    private static final String[] LAST_NAMES = {"Nowak", "Kowalski", "Wiśniewski", "Wójcik", "Kowalczyk", "Kamiński", "Lewandowski", "Zieliński", "Szymański", "Woźniak"};
    private static final String[] STREET_NAMES = {"Polna", "Leśna", "Słoneczna", "Krótka", "Długa", "Warszawska", "Krakowska", "Gdańska", "Poznańska", "Łódzka", "Akacjowa", "Jesionowa", "Brzozowa", "Klonowa", "Dębowa", "Spacerowa", "Ogrodowa", "Parkowa", "Szkolna", "Mickiewicza"};
    private static final Random random = new Random();

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final RoomRepository roomRepository;

    public DataLoader(MongoDatabase database) {
        this.patientRepository = new PatientRepository(database);
        this.doctorRepository = new DoctorRepository(database);
        this.appointmentRepository = new AppointmentRepository(database);
        this.roomRepository = new RoomRepository(database);
    }

    public void loadData() {
        String salt         = "iQnPQNj6A7VvqJCn4KJNiw==";
        String passwordHash = "ozTwnrhZJjD5vdCP5iG5G6XfC0Pp/3AU6B2iBaXOzk8=";

        createDemoPatients(passwordHash, salt);
        createDemoDoctors(passwordHash, salt);
        createDemoRooms();
        fillRoomsWithPatients();
        createDemoAppointments();

        System.out.println("Dane załadowane pomyślnie!");
    }

    private void createDemoPatients(String passwordHash, String salt) {
        for (int i = 1; i <= 10; i++) {
            try {
                Patient patient = new Patient.Builder()
                        .firstName(getRandomFirstName())
                        .lastName(getRandomLastName())
                        .pesel(10000000000L + i)
                        .birthDate(generateRandomBirthDate())
                        .address(generateRandomAddress())
                        .age(20 + random.nextInt(60))
                        .passwordHash(passwordHash)
                        .passwordSalt(salt)
                        .build();
                patientRepository.createPatient(patient);
            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia pacjenta: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createDemoDoctors(String passwordHash, String salt) {
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] specializations = {"Kardiolog", "Neurolog", "Ortopeda", "Dermatolog", "Ginekolog", "Pediatra", "Chirurg", "Internista"};

        for (int i = 1; i <= 10; i++) {
            try {
                String[] selectedDays = new String[random.nextInt(3) + 1];
                for (int j = 0; j < selectedDays.length; j++) {
                    selectedDays[j] = days[random.nextInt(days.length)];
                }
                Doctor doctor = new Doctor.Builder()
                        .firstName(getRandomFirstName())
                        .lastName(getRandomLastName())
                        .age(30 + random.nextInt(35))
                        .pesel(20000000000L + i)
                        .specialization(specializations[random.nextInt(specializations.length)])
                        .availableDays(Arrays.stream(selectedDays).map(Day::valueOf).collect(Collectors.toList()))
                        .room(String.format("%03d", random.nextInt(500) + 1))
                        .contactInformation(generateRandomPhoneNumber())
                        .passwordHash(passwordHash)
                        .passwordSalt(salt)
                        .build();
                doctorRepository.createDoctor(doctor);
            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia lekarza: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createDemoRooms() {
        TypeOfRoom[] roomTypes = TypeOfRoom.values();
        for (int i = 0; i < 15; i++) {
            try {
                Room room = new Room(
                        generateRandomAddress(),
                        random.nextInt(6),
                        100 + i,
                        2 + random.nextInt(5),
                        roomTypes[random.nextInt(roomTypes.length)]
                );
                roomRepository.createRoom(room);
            } catch (Exception e) {
                System.out.println("Błąd tworzenia pokoju: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void fillRoomsWithPatients() {
        List<Room> rooms = roomRepository.getAllRooms();
        List<Patient> allPatients = patientRepository.findAll();

        if (rooms.isEmpty() || allPatients.isEmpty()) {
            System.out.println("Brak sal lub pacjentów – pomijam przydział.");
            return;
        }

        Collections.shuffle(allPatients, random);
        Iterator<Patient> it = allPatients.iterator();

        for (Room room : rooms) {
            List<ObjectId> patientIds = new ArrayList<>();
            while (it.hasNext() && patientIds.size() < room.getMaxPatients()) {
                patientIds.add(it.next().getId());
            }
            room.setPatientIds(patientIds);
            roomRepository.updateRoom(room);
        }
    }

    private void createDemoAppointments() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Patient> patients = patientRepository.findAll();
        AppointmentStatus[] statuses = AppointmentStatus.values();

        if (doctors.isEmpty() || patients.isEmpty()) {
            System.out.println("Brak lekarzy lub pacjentów – pomijam tworzenie wizyt.");
            return;
        }

        for (int i = 0; i < 10; i++) {
            try {
                Appointment appt = new Appointment();
                appt.setDoctorId(doctors.get(random.nextInt(doctors.size())).getId());
                appt.setPatientId(patients.get(random.nextInt(patients.size())).getId());
                appt.setDate(generateRandomAppointmentDateTime());
                appt.setStatus(statuses[random.nextInt(statuses.length)]);
                appointmentRepository.createAppointment(appt);
            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia wizyty: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String getRandomFirstName()   { return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)]; }
    private String getRandomLastName()    { return LAST_NAMES[random.nextInt(LAST_NAMES.length)]; }
    private String getRandomStreet()      { return STREET_NAMES[random.nextInt(STREET_NAMES.length)]; }

    private String generateRandomAddress() {
        String street = getRandomStreet();
        String bnr = String.valueOf(random.nextInt(200) + 1);
        String apt = random.nextBoolean() ? "/" + (random.nextInt(50) + 1) : "";
        return "ul. " + street + " " + bnr + apt;
    }

    private LocalDate generateRandomBirthDate() {
        int year = 1950 + random.nextInt(70);
        int month = random.nextInt(12) + 1;
        int day   = random.nextInt(28) + 1;
        return LocalDate.of(year, month, day);
    }

    private String generateRandomPhoneNumber() {
        return String.format("%03d-%03d-%03d", random.nextInt(900) + 100, random.nextInt(900) + 100, random.nextInt(900) + 100);
    }

    private LocalDateTime generateRandomAppointmentDateTime() {
        int year = 2025;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        int hour = 8 + random.nextInt(9);
        int minute = random.nextInt(60);
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    public static void main(String[] args) {
        MongoDatabase db = MongoDatabaseConnector.connectToDatabase();
        new DataLoader(db).loadData();
    }

}