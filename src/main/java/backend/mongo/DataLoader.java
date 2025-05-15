package backend.mongo;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.klasy.Room;
import backend.status.*;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataLoader {

    private static final String[] FIRST_NAMES = {"Jan", "Anna", "Piotr", "Maria", "Krzysztof", "Agnieszka", "Andrzej", "Małgorzata", "Grzegorz", "Ewa"};
    private static final String[] LAST_NAMES = {"Nowak", "Kowalski", "Wiśniewski", "Wójcik", "Kowalczyk", "Kamiński", "Lewandowski", "Zieliński", "Szymański", "Woźniak"};
    private static final String[] STREET_NAMES = {"Polna", "Leśna", "Słoneczna", "Krótka", "Długa", "Warszawska", "Krakowska", "Gdańska", "Poznańska", "Łódzka", "Akacjowa", "Jesionowa", "Brzozowa", "Klonowa", "Dębowa", "Spacerowa", "Ogrodowa", "Parkowa", "Szkolna", "Mickiewicza"};
    private static final Random random = new Random();

    // Ścieżka do katalogu z plikami JS
    private final String jsScriptsDirectory = "src/Walidacja/automatyczna";

    private final MongoDatabase database;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final RoomRepository roomRepository;

    private final Map<ObjectId, Set<LocalDateTime>> doctorAppointmentTimes = new HashMap<>();

    public DataLoader(MongoDatabase database) {
        this.database = database;
        this.patientRepository = new PatientRepository(database);
        this.doctorRepository = new DoctorRepository(database);
        this.appointmentRepository = new AppointmentRepository(database);
        this.roomRepository = new RoomRepository(database);
    }

    public void loadData() {
        String salt         = "iQnPQNj6A7VvqJCn4KJNiw==";
        String passwordHash = "ozTwnrhZJjD5vdCP5iG5G6XfC0Pp/3AU6B2iBaXOzk8=";

        createDemoPatients(passwordHash, salt);

        createRoomsForAllTypes();

        createDemoDoctors(passwordHash, salt);
        createDemoAppointments();

        try {
            applyValidationSchemas();
        } catch (Exception e) {
            System.out.println("Uwaga: Walidacja schematów nie powiodła się. To normalne dla nowej bazy danych.");
            System.out.println("Szczegóły: " + e.getMessage());
        }
        System.out.println("Dane załadowane pomyślnie!");
    }

    private void applyValidationSchemas() {
        try {
            File jsDirectory = new File(jsScriptsDirectory);
            if (!jsDirectory.exists() || !jsDirectory.isDirectory()) {
                System.err.println("Katalog z plikami JSON nie istnieje: " + jsScriptsDirectory);
                return;
            }
            File[] jsonFiles = jsDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (jsonFiles == null || jsonFiles.length == 0) {
                System.err.println("Brak plików JSON w katalogu: " + jsScriptsDirectory);
                return;
            }
            for (File jsonFile : jsonFiles) {
                try {
                    String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath())));
                    Document command = Document.parse(jsonContent);
                    database.runCommand(command);
                    System.out.println("Wykonano walidację: " + jsonFile.getName());
                } catch (IOException e) {
                    System.err.println("Błąd odczytu JSON: " + jsonFile.getName() + " - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd walidacji schematów: " + e.getMessage());
            throw e;
        }
    }

    private void createDemoPatients(String passwordHash, String salt) {
        for (int i = 1; i <= 10; i++) {
            try {
                LocalDate birthDate = generateRandomBirthDate();

                int age = Patient.calculateAge(birthDate);

                Patient patient = new Patient.Builder()
                        .firstName(getRandomFirstName())
                        .lastName(getRandomLastName())
                        .pesel(generateRandomPesel(birthDate))
                        .birthDate(birthDate)
                        .address(generateRandomAddress())
                        .age(age)
                        .passwordHash(passwordHash)
                        .passwordSalt(salt)
                        .diagnosis(Diagnosis.AWAITING)
                        .build();
                patientRepository.createPatient(patient);
            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia pacjenta: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createRoomsForAllTypes() {
        TypeOfRoom[] roomTypes = TypeOfRoom.values();

        for (TypeOfRoom type : roomTypes) {
            try {
                Room room = new Room(
                        generateRandomAddress(),
                        random.nextInt(6),
                        100 + roomTypes.length + random.nextInt(100),
                        2 + random.nextInt(5),
                        type
                );
                roomRepository.createRoom(room);
                System.out.println("Utworzono pokój typu: " + type.getDescription());
            } catch (Exception e) {
                System.out.println("Błąd tworzenia pokoju typu " + type + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 10; i++) {
            try {
                Room room = new Room(
                        generateRandomAddress(),
                        random.nextInt(6),
                        200 + i,
                        2 + random.nextInt(5),
                        roomTypes[random.nextInt(roomTypes.length)]
                );
                roomRepository.createRoom(room);
            } catch (Exception e) {
                System.out.println("Błąd tworzenia dodatkowego pokoju: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createDemoDoctors(String passwordHash, String salt) {
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        Specialization[] specializations = Specialization.values();

        for (int i = 1; i <= 10; i++) {
            try {
                String[] selectedDays = new String[random.nextInt(3) + 1];
                for (int j = 0; j < selectedDays.length; j++) {
                    selectedDays[j] = days[random.nextInt(days.length)];
                }
                LocalDate birthDate = generateRandomBirthDate();
                Doctor doctor = new Doctor.Builder()
                        .firstName(getRandomFirstName())
                        .lastName(getRandomLastName())
                        .age(30 + random.nextInt(35))
                        .pesel(generateRandomPesel(birthDate))
                        .specialization(specializations[random.nextInt(specializations.length)]) // Używamy enuma
                        .availableDays(Arrays.stream(selectedDays).map(Day::valueOf).collect(Collectors.toList()))
                        .room(String.format("%03d", random.nextInt(500) + 1))
                        .contactInformation(generateRandomPhoneNumber())
                        .passwordHash(passwordHash)
                        .passwordSalt(salt)
                        .build();
                doctorRepository.createDoctor(doctor);
                doctorAppointmentTimes.put(doctor.getId(), new HashSet<>());
            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia lekarza: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createDemoAppointments() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Patient> patients = patientRepository.findAll();
        List<Room> rooms = roomRepository.getAllRooms();
        AppointmentStatus[] statuses = AppointmentStatus.values();

        if (doctors.isEmpty() || patients.isEmpty()) {
            System.out.println("Brak lekarzy lub pacjentów – pomijam tworzenie wizyt.");
            return;
        }

        int successfulAppointments = 0;
        int maxAttempts = 30;
        int attempts = 0;

        while (successfulAppointments < 10 && attempts < maxAttempts) {
            attempts++;
            try {
                Doctor doctor = doctors.get(random.nextInt(doctors.size()));
                Room room = findCompatibleRoom(doctor, rooms);

                if (room == null) {
                    System.out.println("Nie znaleziono kompatybilnej sali dla lekarza: " + doctor.getFirstName() + " " + doctor.getLastName() +
                            " (specjalizacja: " + doctor.getSpecialization().getDescription() + ")");
                    continue;
                }

                LocalDateTime appointmentTime = generateNonConflictingAppointmentTime(doctor);

                Appointment appt = new Appointment();
                appt.setDoctorId(doctor.getId());
                appt.setPatientId(patients.get(random.nextInt(patients.size())).getId());
                appt.setRoom(room.getId());
                appt.setDate(appointmentTime);
                appt.setStatus(statuses[random.nextInt(statuses.length)]);
                appt.setDescription("Wizyta u " + doctor.getSpecialization().getDescription());

                appointmentRepository.createAppointment(appt);
                doctorAppointmentTimes.get(doctor.getId()).add(appointmentTime);

                successfulAppointments++;
                System.out.println("Utworzono wizytę #" + successfulAppointments + " dla lekarza: " +
                        doctor.getFirstName() + " " + doctor.getLastName() +
                        " w sali typu: " + room.getType().getDescription());

            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia wizyty (próba " + attempts + "): " + e.getMessage());
            }
        }

        System.out.println("Utworzono " + successfulAppointments + " wizyt po " + attempts + " próbach.");
    }

    private Room findCompatibleRoom(Doctor doctor, List<Room> rooms) {
        TypeOfRoom compatibleRoomType = doctor.getSpecialization().getCompatibleRoomType();
        List<Room> compatibleRooms = rooms.stream()
                .filter(room -> room.getType() == compatibleRoomType)
                .collect(Collectors.toList());

        if (compatibleRooms.isEmpty()) {
            return null;
        }

        return compatibleRooms.get(random.nextInt(compatibleRooms.size()));
    }

    private LocalDateTime generateNonConflictingAppointmentTime(Doctor doctor) {
        Set<LocalDateTime> occupiedTimes = doctorAppointmentTimes.get(doctor.getId());
        LocalDateTime appointmentTime;
        boolean isConflict;
        int attempts = 0;
        int maxAttempts = 50;

        do {
            isConflict = false;
            appointmentTime = generateRandomAppointmentDateTime();
            Day appointmentDay = convertToDayEnum(appointmentTime.getDayOfWeek());
            if (!doctor.getAvailableDays().contains(appointmentDay)) {
                isConflict = true;
                continue;
            }

            for (LocalDateTime occupiedTime : occupiedTimes) {
                long minutesBetween = Math.abs(java.time.Duration.between(appointmentTime, occupiedTime).toMinutes());
                if (minutesBetween < 30) {
                    isConflict = true;
                    break;
                }
            }

            attempts++;
        } while (isConflict && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            System.out.println("Ostrzeżenie: Nie udało się znaleźć niekolidującego terminu dla lekarza po " + maxAttempts + " próbach.");
        }

        return appointmentTime;
    }

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
        int hour = 8 + random.nextInt(9); // Godziny 8-17
        int minute = (random.nextInt(4) * 15); // Minuty: 0, 15, 30, 45
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    private long generateRandomPesel(LocalDate birthDate) {
        int year = birthDate.getYear();
        int month = birthDate.getMonthValue();
        int day = birthDate.getDayOfMonth();

        int yy = year % 100;
        int mm;
        if (year >= 1900 && year < 2000) {
            mm = month;
        } else if (year >= 2000 && year < 2100) {
            mm = month + 20;
        } else {
            throw new IllegalArgumentException("Rok poza obsługiwanym zakresem (1900-2100)");
        }
        int serial = random.nextInt(10000);
        String datePart = String.format("%02d%02d%02d", yy, mm, day);
        String serialPart = String.format("%04d", serial);
        String firstTen = datePart + serialPart;

        char[] firstTenChars = firstTen.toCharArray();
        if (firstTenChars[0] == '0') {
            firstTenChars[0] = (char)('1' + random.nextInt(9));
        }
        firstTen = new String(firstTenChars);

        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(firstTen.charAt(i));
            sum += digit * weights[i];
        }
        int checkDigit = (10 - sum % 10) % 10;
        String fullPesel = firstTen + checkDigit;

        return Long.parseLong(fullPesel);
    }

    public static void main(String[] args) {
        MongoDatabase db = MongoDatabaseConnector.connectToDatabase();
        new DataLoader(db).loadData();
        MongoDatabaseConnector.close();
    }
}
