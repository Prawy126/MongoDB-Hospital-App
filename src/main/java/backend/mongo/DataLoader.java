package backend.mongo;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.status.Day;
import com.mongodb.client.MongoDatabase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * Klasa DataLoader służy do ładowania przykładowych danych do bazy MongoDB.
 * Zawiera metody do generowania losowych danych pacjentów, lekarzy i wizyt.
 */
public class DataLoader {


// Predefiniowane listy imion, nazwisk i ulic możemy jeszcze coś dopisać aktualnie są to przykładowe dane
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

    private String generateRandomAddress() {
        String street = getRandomStreet();
        String buildingNumber = String.format("%d", random.nextInt(200) + 1);
        String apartmentNumber = random.nextBoolean() ?
                String.format("/%d", random.nextInt(50) + 1) : "";
        return String.format("ul. %s %s%s", street, buildingNumber, apartmentNumber);
    }

    private LocalDate generateRandomBirthDate() {
        int year = 1950 + random.nextInt(70); // 1950-2020
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1; // Bezpieczne dni
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
        int day = random.nextInt(28) + 1; // Bezpieczne dni
        int hour = 8 + random.nextInt(9); // 8:00 - 16:59
        int minute = random.nextInt(60);
        return LocalDateTime.of(year, month, day, hour, minute);
    }
    //private static final Random random = new Random();

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
                patient.setFirstName(getRandomFirstName());
                patient.setLastName(getRandomLastName());
                patient.setPesel(10000000000L + i); // PESEL 11 cyfr
                patient.setBirthDate(generateRandomBirthDate());
                patient.setAddress(generateRandomAddress());
                patientRepository.createPatient(patient);
            } catch (Exception e) {
                System.out.println("Błąd pacjenta: " + e.getMessage());
            }
        }

        // Dodanie przykładowych lekarzy
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] specializations = {"Kardiolog", "Neurolog", "Ortopeda", "Dermatolog",
                "Ginekolog", "Pediatra", "Chirurg", "Internista"};

        for (int i = 1; i <= 10; i++) {
            Doctor doctor = new Doctor();
            try {
                doctor.setFirstName(getRandomFirstName());
                doctor.setLastName(getRandomLastName());
                doctor.setAge(25 + random.nextInt(56)); // Wiek 25-80
                doctor.setPesel(20000000000L + i); // PESEL 11 cyfr
                doctor.setRoom(String.format("%03d", random.nextInt(500) + 1)); // Numery 001-500
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
                doctorRepository.createDoctor(doctor);
            } catch (Exception e) {
                System.out.println("Błąd lekarza: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Dodanie przykładowych wizyt
        List<Doctor> doctors = doctorRepository.findAll();
        List<Patient> patients = patientRepository.findAll();

        for (int i = 0; i < 10; i++) {
            try {
                Appointment appointment = new Appointment();
                appointment.setDoctorId(doctors.get(random.nextInt(doctors.size())).getId());
                appointment.setPatientId(patients.get(random.nextInt(patients.size())).getId());
                appointment.setDate(generateRandomAppointmentDateTime());
                //appointment.setEndTime(appointment.getDate().plusMinutes(30 + random.nextInt(60)));
                appointmentRepository.createAppointment(appointment);
            } catch (Exception e) {
                System.out.println("Błąd wizyty: " + e.getMessage());
            }
        }
    }

    // Metody pomocnicze do losowania
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
    //aktualnie metoda do testowania później się zobaczy czy zostanie w tym miejscu czy z innego miejsca będą generowane dane
    public static void main(String[] args) {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();
        new DataLoader(database).loadData();
        System.out.println("Dane załadowane pomyślnie!");
    }
}