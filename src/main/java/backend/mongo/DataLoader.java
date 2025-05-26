package backend.mongo;

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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Klasa odpowiedzialna za generowanie danych demo w bazie MongoDB.
 */
public class DataLoader {

    private static final String[] FIRST_NAMES = {"Jan", "Anna", "Piotr", "Maria", "Krzysztof", "Agnieszka", "Andrzej", "Małgorzata", "Grzegorz", "Ewa"};
    private static final String[] LAST_NAMES = {"Nowak", "Kowalski", "Wiśniewski", "Wójcik", "Kowalczyk", "Kamiński", "Lewandowski", "Zieliński", "Szymański", "Woźniak"};
    private static final String[] STREET_NAMES = {"Polna", "Leśna", "Słoneczna", "Krótka", "Długa", "Warszawska", "Krakowska", "Gdańska", "Poznańska", "Łódzka", "Akacjowa", "Jesionowa", "Brzozowa", "Klonowa", "Dębowa", "Spacerowa", "Ogrodowa", "Parkowa", "Szkolna", "Mickiewicza"};
    private static final Random random = new Random();

    private final String jsScriptsDirectory = "src/Walidacja/automatyczna";

    private final MongoDatabase database;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final RoomRepository roomRepository;

    /**
     * Inicjalizuje repozytoria i bazę danych.
     */
    public DataLoader(MongoDatabase database) {
        this.database = database;
        this.patientRepository = new PatientRepository(database);
        this.doctorRepository = new DoctorRepository(database);
        this.roomRepository = new RoomRepository(database);
    }

    /**
     * Główna metoda ładująca dane do bazy.
     */
    public void loadData() {
        String salt = "iQnPQNj6A7VvqJCn4KJNiw==";
        String passwordHash = "ozTwnrhZJjD5vdCP5iG5G6XfC0Pp/3AU6B2iBaXOzk8=";

        createDemoPatients(passwordHash, salt);
        createRoomsForAllTypes();
        createDemoDoctors(passwordHash, salt);

        try {
            applyValidationSchemas();
        } catch (Exception ignored) {
        }
    }

    /**
     * Wczytuje schematy walidacyjne JSON z plików.
     */
    private void applyValidationSchemas() {
        File jsDirectory = new File(jsScriptsDirectory);
        if (!jsDirectory.exists() || !jsDirectory.isDirectory()) return;

        File[] jsonFiles = jsDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (jsonFiles == null || jsonFiles.length == 0) return;

        for (File jsonFile : jsonFiles) {
            try {
                String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath())));
                Document command = Document.parse(jsonContent);
                database.runCommand(command);
            } catch (IOException ignored) {}
        }
    }

    /**
     * Tworzy przykładowych pacjentów.
     */
    private void createDemoPatients(String passwordHash, String salt) {
        for (int i = 1; i <= 10; i++) {
            try {
                LocalDate birthDate = generateRandomBirthDate(0, 100); // Pacjenci mogą mieć dowolny wiek
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
            } catch (Exception ignored) {}
        }
    }

    /**
     * Tworzy pokoje dla wszystkich typów.
     */
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
            } catch (Exception ignored) {}
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
            } catch (Exception ignored) {}
        }
    }

    /**
     * Tworzy przykładowych lekarzy.
     */
    private void createDemoDoctors(String passwordHash, String salt) {
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        Specialization[] specializations = Specialization.values();

        for (int i = 1; i <= 10; i++) {
            try {
                String[] selectedDays = new String[random.nextInt(3) + 1];
                for (int j = 0; j < selectedDays.length; j++) {
                    selectedDays[j] = days[random.nextInt(days.length)];
                }

                // Generuj datę urodzenia dla lekarza (wiek 25-65 lat)
                LocalDate birthDate = generateRandomBirthDate(25, 65);
                int age = Doctor.calculateAge(birthDate);

                // Generuj numer telefonu w formacie 9 cyfr (bez myślników)
                String phoneNumber = generateRandomPhoneNumber9Digits();

                Doctor doctor = new Doctor.Builder()
                        .firstName(getRandomFirstName())
                        .lastName(getRandomLastName())
                        .birthDate(birthDate) // Ustawienie daty urodzenia
                        .age(age) // Wiek jest obliczany automatycznie, ale ustawiamy go dla pewności
                        .pesel(generateRandomPesel(birthDate))
                        .specialization(specializations[random.nextInt(specializations.length)])
                        .availableDays(Arrays.stream(selectedDays).map(Day::valueOf).collect(Collectors.toList()))
                        .room(String.format("%03d", random.nextInt(500) + 1))
                        .contactInformation(phoneNumber)
                        .passwordHash(passwordHash)
                        .passwordSalt(salt)
                        .build();
                doctorRepository.createDoctor(doctor);
            } catch (Exception ignored) {}
        }
    }

    /**
     * Zwraca losowe imię.
     */
    private String getRandomFirstName() {
        return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
    }

    /**
     * Zwraca losowe nazwisko.
     */
    private String getRandomLastName() {
        return LAST_NAMES[random.nextInt(LAST_NAMES.length)];
    }

    /**
     * Zwraca losową nazwę ulicy.
     */
    private String getRandomStreet() {
        return STREET_NAMES[random.nextInt(STREET_NAMES.length)];
    }

    /**
     * Generuje losowy adres.
     */
    private String generateRandomAddress() {
        String street = getRandomStreet();
        String bnr = String.valueOf(random.nextInt(200) + 1);
        String apt = random.nextBoolean() ? "/" + (random.nextInt(50) + 1) : "";
        return "ul. " + street + " " + bnr + apt;
    }

    /**
     * Generuje losową datę urodzenia w zakresie lat.
     *
     * @param minAge minimalny wiek w latach
     * @param maxAge maksymalny wiek w latach
     * @return losowa data urodzenia
     */
    private LocalDate generateRandomBirthDate(int minAge, int maxAge) {
        LocalDate now = LocalDate.now();
        int minYear = now.getYear() - maxAge;
        int maxYear = now.getYear() - minAge;
        int year = minYear + random.nextInt(maxYear - minYear + 1);
        int month = random.nextInt(12) + 1;

        // Upewnij się, że dzień jest prawidłowy dla danego miesiąca i roku
        int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
        int day = random.nextInt(maxDay) + 1;

        return LocalDate.of(year, month, day);
    }

    /**
     * Generuje losowy numer telefonu w formacie 9 cyfr bez myślników.
     */
    private String generateRandomPhoneNumber9Digits() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Generuje losowy numer telefonu.
     * @deprecated Zastąpione przez {@link #generateRandomPhoneNumber9Digits()}
     */
    @Deprecated
    private String generateRandomPhoneNumber() {
        return String.format("%03d-%03d-%03d",
                random.nextInt(900) + 100,
                random.nextInt(900) + 100,
                random.nextInt(900) + 100);
    }

    /**
     * Generuje losowy PESEL na podstawie daty urodzenia.
     */
    private long generateRandomPesel(LocalDate birthDate) {
        int year = birthDate.getYear();
        int month = birthDate.getMonthValue();
        int day = birthDate.getDayOfMonth();

        int yy = year % 100;
        int mm = year >= 2000 ? month + 20 : month;
        int serial = random.nextInt(10000);

        String datePart = String.format("%02d%02d%02d", yy, mm, day);
        String serialPart = String.format("%04d", serial);
        String firstTen = datePart + serialPart;

        char[] firstTenChars = firstTen.toCharArray();
        if (firstTenChars[0] == '0') firstTenChars[0] = (char) ('1' + random.nextInt(9));
        firstTen = new String(firstTenChars);

        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(firstTen.charAt(i));
            sum += digit * weights[i];
        }
        int checkDigit = (10 - sum % 10) % 10;
        return Long.parseLong(firstTen + checkDigit);
    }

    /**
     * Uruchamia ładowanie danych do bazy.
     */
    public static void main(String[] args) {
        MongoDatabase db = MongoDatabaseConnector.connectToDatabase();
        new DataLoader(db).loadData();
        MongoDatabaseConnector.close();
    }
}