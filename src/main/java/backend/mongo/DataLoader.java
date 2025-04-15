package backend.mongo;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.status.Day;
import backend.wyjatki.NullNameException;
import com.mongodb.client.MongoDatabase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Klasa DataLoader służy do ładowania przykładowych danych do bazy MongoDB.
 * Zawiera metody do generowania losowych danych pacjentów, lekarzy i wizyt.
 */
public class DataLoader {

    private static final String[] FIRST_NAMES = {"Jan", "Anna", "Piotr", "Maria", "Krzysztof",
            "Agnieszka", "Andrzej", "Małgorzata", "Grzegorz", "Ewa"};
    private static final String[] LAST_NAMES = {"Nowak", "Kowalski", "Wiśniewski", "Wójcik",
            "Kowalczyk", "Kamiński", "Lewandowski", "Zieliński",
            "Szymański", "Woźniak"};
    private static final String[] STREET_NAMES = {"Polna", "Leśna", "Słoneczna", "Krótka", "Długa",
            "Warszawska", "Krakowska", "Gdańska", "Poznańska", "Łódzka",
            "Akacjowa", "Jesionowa", "Brzozowa", "Klonowa", "Dębowa",
            "Spacerowa", "Ogrodowa", "Parkowa", "Szkolna", "Mickiewicza"};
    private static final Random random = new Random();

    private String getRandomStreet() {
        return STREET_NAMES[random.nextInt(STREET_NAMES.length)];
    }

    /**
     * Generuje losowy poprawny numer PESEL
     */
    public static long generateRandomPesel() {
        LocalDate birthDate = generateRandomBirthDate1();
        boolean isMale = random.nextBoolean();
        return generatePesel(birthDate, isMale);
    }

    /**
     * Generuje PESEL na podstawie daty urodzenia i płci
     */
    public static long generatePesel(LocalDate birthDate, boolean isMale) {
        String datePart = formatDateForPesel(birthDate);
        String sequence = generateSequenceNumber(isMale);
        String checkDigit = calculateCheckDigit(datePart + sequence);

        String fullPesel = String.format("%11s", datePart + sequence + checkDigit)
                .replace(' ', '0');

        return Long.parseLong(fullPesel);
    }

    private static String formatDateForPesel(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // Dostosowanie miesiąca w zależności od epoki
        if (year >= 2000 && year < 2100) {
            month += 20;
        } else if (year >= 2100 && year < 2200) {
            month += 40;
        } else if (year >= 2200 && year < 2300) {
            month += 60;
        } else if (year < 1900) {
            month += 80;
        }

        return String.format("%02d%02d%02d",
                year % 100,
                month,
                day);
    }

    /**
     * Poprawiona metoda generująca 4-cyfrową sekwencję.
     * Wartość ostatniej cyfry (parzystość) wskazuje płeć: nieparzysta = mężczyzna, parzysta = kobieta.
     */
    private static String generateSequenceNumber(boolean isMale) {
        int seq = random.nextInt(1000); // wartość z zakresu 0-999
        int genderDigit = isMale ?
                (random.nextInt(5) * 2 + 1) : // możliwe wartości: 1,3,5,7,9
                (random.nextInt(5) * 2);      // możliwe wartości: 0,2,4,6,8
        int sequenceNumber = seq * 10 + genderDigit; // daje zakres 0-9999
        return String.format("%04d", sequenceNumber);
    }

    private static String calculateCheckDigit(String base) {
        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int sum = 0;

        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(base.charAt(i)) * weights[i];
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return String.valueOf(checkDigit);
    }

    private static LocalDate generateRandomBirthDate1() {
        long days = ChronoUnit.DAYS.between(
                LocalDate.of(1800, 1, 1),
                LocalDate.of(2299, 12, 31)
        );

        return LocalDate.of(1800, 1, 1)
                .plusDays(random.nextInt((int) days));
    }

    /**
     * Określa płeć na podstawie ostatniej litery imienia
     * @param firstName Imię do analizy
     * @return true jeśli płeć żeńska, false jeśli męska
     * @exception NullNameException jeśli imie jest puste
     */
    public static boolean detectGender(String firstName) throws NullNameException {
        if (firstName == null || firstName.isEmpty()) {
            throw new NullNameException("Nie można ustalić płci dla pustego imienia.");
        }

        char lastChar = firstName.toLowerCase().charAt(firstName.length() - 1);

        return switch (lastChar) {
            case 'a' -> true; // Końcówka na -a sugeruje kobietę
            default -> false;  // Pozostałe przypadki traktowane jako mężczyzna
        };
    }

    private String generateRandomAddress() {
        String street = getRandomStreet();
        String buildingNumber = String.format("%d", random.nextInt(200) + 1);
        String apartmentNumber = random.nextBoolean() ?
                String.format("/%d", random.nextInt(50) + 1) : "";
        return String.format("ul. %s %s%s", street, buildingNumber, apartmentNumber);
    }

    private LocalDate generateRandomBirthDate() {
        int year = 1950 + random.nextInt(70); // lata 1950-2020
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1; // dni bezpieczne
        return LocalDate.of(year, month, day);
    }

    private String generateRandomPhoneNumber() {
        return String.format("+48 %03d-%03d-%03d",
                random.nextInt(900) + 100,
                random.nextInt(900) + 100,
                random.nextInt(900) + 100);
    }

    private LocalDateTime generateRandomAppointmentDateTime() {
        int year = 2025;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1; // dni bezpieczne
        int hour = 8 + random.nextInt(9); // od 8:00 do 16:59
        int minute = random.nextInt(60);
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Konstruktor klasy DataLoader.
     *
     * @param database instancja MongoDatabase
     */
    public DataLoader(MongoDatabase database) {
        this.patientRepository = new PatientRepository(database);
        this.doctorRepository = new DoctorRepository(database);
        this.appointmentRepository = new AppointmentRepository(database);
    }

    /**
     * Metoda do ładowania przykładowych danych do bazy danych.
     * Dodaje pacjentów, lekarzy i wizyty do bazy danych.
     */
    public void loadData() {
        // Dodanie przykładowych pacjentów
        for (int i = 1; i <= 10; i++) {
            Patient patient = new Patient();
            try {
                String firstName = getRandomFirstName();
                String lastName = getRandomLastName();
                //String login = generateUniqueLogin(firstName, lastName);
                String salt = "iQnPQNj6A7VvqJCn4KJNiw==";
                String passwordHash = "ozTwnrhZJjD5vdCP5iG5G6XfC0Pp/3AU6B2iBaXOzk8=";
                LocalDate date = generateRandomBirthDate();
                patient.setFirstName(firstName);
                patient.setLastName(lastName);
                patient.setPesel(generateRandomPesel());
                patient.setBirthDate(date);
                patient.setAge(LocalDate.now().getYear() - date.getYear());
                patient.setAddress(generateRandomAddress());
                patient.setPassword(passwordHash);
                patient.setSalt(salt);

                patientRepository.createPatient(patient);
            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia pacjenta: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Dodanie przykładowych lekarzy
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] specializations = {"Kardiolog", "Neurolog", "Ortopeda", "Dermatolog",
                "Ginekolog", "Pediatra", "Chirurg", "Internista"};

        for (int i = 1; i <= 10; i++) {
            Doctor doctor = new Doctor();
            try {
                String firstName = getRandomFirstName();
                String lastName = getRandomLastName();
                //String login = generateUniqueLogin(firstName, lastName);
                String salt = "iQnPQNj6A7VvqJCn4KJNiw==";
                String passwordHash = "ozTwnrhZJjD5vdCP5iG5G6XfC0Pp/3AU6B2iBaXOzk8=";

                doctor.setFirstName(firstName);
                doctor.setLastName(lastName);
                doctor.setAge(25 + random.nextInt(56)); // wiek 25-80 lat
                doctor.setPesel(20000000000L + i); // PESEL 11 cyfr
                doctor.setRoom(String.format("%03d", random.nextInt(500) + 1)); // numery pokoi 001-500
                doctor.setSpecialization(specializations[random.nextInt(specializations.length)]);

                // Losowy wybór 1-3 dni pracy
                String[] selectedDays = new String[random.nextInt(3) + 1];
                for (int j = 0; j < selectedDays.length; j++) {
                    selectedDays[j] = days[random.nextInt(days.length)];
                }
                doctor.setAvailableDays(
                        Arrays.stream(selectedDays)
                                .map(day -> Day.valueOf(day))
                                .collect(Collectors.toList())
                );

                doctor.setContactInformation(generateRandomPhoneNumber());
                //doctor.setLogin(login);
                doctor.setPassword(passwordHash);
                doctor.setSalt(salt);

                doctorRepository.createDoctor(doctor);
            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia lekarza: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Dodanie przykładowych wizyt
        List<Doctor> doctors = doctorRepository.findAll();
        List<Patient> patients = patientRepository.findAll();

        if (doctors.isEmpty() || patients.isEmpty()) {
            System.out.println("Brak lekarzy lub pacjentów, pomijam dodawanie wizyt.");
            return;
        }

        for (int i = 0; i < 10; i++) {
            try {
                Appointment appointment = new Appointment();
                appointment.setDoctorId(doctors.get(random.nextInt(doctors.size())).getId());
                appointment.setPatientId(patients.get(random.nextInt(patients.size())).getId());
                appointment.setDate(generateRandomAppointmentDateTime());
                appointmentRepository.createAppointment(appointment);
            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia wizyty: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Dane załadowane pomyślnie!");
    }

    private String getRandomFirstName() {
        return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
    }

    private String getRandomLastName() {
        return LAST_NAMES[random.nextInt(LAST_NAMES.length)];
    }

    private String getRandomPhoneNumber() {
        return String.format("%03d-%03d-%03d",
                random.nextInt(900) + 100,
                random.nextInt(900) + 100,
                random.nextInt(900) + 100);
    }

    /**
     * Metoda main do uruchomienia ładowania danych.
     *
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();
        new DataLoader(database).loadData();
    }
}
