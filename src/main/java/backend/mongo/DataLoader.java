package backend.mongo;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.status.AppointmentStatus;
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

    private static final String[] FIRST_NAMES = {
            "Jan", "Anna", "Piotr", "Maria", "Krzysztof",
            "Agnieszka", "Andrzej", "Małgorzata", "Grzegorz", "Ewa"
    };

    private static final String[] LAST_NAMES = {
            "Nowak", "Kowalski", "Wiśniewski", "Wójcik", "Kowalczyk",
            "Kamiński", "Lewandowski", "Zieliński", "Szymański", "Woźniak"
    };

    private static final String[] STREET_NAMES = {
            "Polna", "Leśna", "Słoneczna", "Krótka", "Długa",
            "Warszawska", "Krakowska", "Gdańska", "Poznańska", "Łódzka",
            "Akacjowa", "Jesionowa", "Brzozowa", "Klonowa", "Dębowa",
            "Spacerowa", "Ogrodowa", "Parkowa", "Szkolna", "Mickiewicza"
    };

    private static final Random random = new Random();

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public DataLoader(MongoDatabase database) {
        this.patientRepository = new PatientRepository(database);
        this.doctorRepository = new DoctorRepository(database);
        this.appointmentRepository = new AppointmentRepository(database);
    }

    public void loadData() {
        // Przykładowe hash i salt dla hasła "haslo"
        String salt = "JEu2g122xhlb7d9L7LT6ow==";
        String passwordHash = "sWjyOHrMnnI02SlAjId6MsGNkMQh/MWR5GJIAbukPAY=";

        // Dodaj pacjentów
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
                String firstName = getRandomFirstName();
                String lastName = getRandomLastName();
                String login = generateUniqueLogin(firstName, lastName);
                String salt = "iQnPQNj6A7VvqJCn4KJNiw==";
                String passwordHash = "ozTwnrhZJjD5vdCP5iG5G6XfC0Pp/3AU6B2iBaXOzk8=";
                LocalDate date = generateRandomBirthDate();
                patient.setFirstName(firstName);
                patient.setLastName(lastName);
                patient.setPesel(generateRandomPesel());
                patient.setBirthDate(date);
                patient.setAge(LocalDate.now().getYear() - date.getYear());
                patient.setAddress(generateRandomAddress());
                patient.setLogin(login);
                patient.setPassword(passwordHash);
                patient.setSalt(salt);

                patientRepository.createPatient(patient);
            } catch (Exception e) {
                System.out.println("Błąd podczas tworzenia pacjenta: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Dodaj lekarzy
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] specializations = {
                "Kardiolog", "Neurolog", "Ortopeda", "Dermatolog",
                "Ginekolog", "Pediatra", "Chirurg", "Internista"
        };

        for (int i = 1; i <= 10; i++) {
            Doctor doctor = new Doctor();
            try {
                String firstName = getRandomFirstName();
                String lastName = getRandomLastName();
                String login = generateUniqueLogin(firstName, lastName);
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

        // Dodaj wizyty z losowymi statusami
        List<Doctor> doctors = doctorRepository.findAll();
        List<Patient> patients = patientRepository.findAll();
        AppointmentStatus[] statuses = AppointmentStatus.values();

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
                appointment.setStatus(statuses[random.nextInt(statuses.length)]);

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

    private String getRandomStreet() {
        return STREET_NAMES[random.nextInt(STREET_NAMES.length)];
    }

    private String generateRandomAddress() {
        String street = getRandomStreet();
        String buildingNumber = String.format("%d", random.nextInt(200) + 1);
        String apartmentNumber = random.nextBoolean() ? String.format("/%d", random.nextInt(50) + 1) : "";
        return String.format("ul. %s %s%s", street, buildingNumber, apartmentNumber);
    }

    private LocalDate generateRandomBirthDate() {
        int year = 1950 + random.nextInt(70);
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        return LocalDate.of(year, month, day);
    }

    private String generateRandomPhoneNumber() {
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
    private LocalDateTime generateRandomAppointmentDateTime() {
        int year = 2025;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        int hour = 8 + random.nextInt(9);
        int minute = random.nextInt(60);
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    public static void main(String[] args) {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();
        new DataLoader(database).loadData();
        System.out.println("Dane załadowane pomyślnie!");
    }
}