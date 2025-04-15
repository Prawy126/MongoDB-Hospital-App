package backend.mongo;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.status.AppointmentStatus;
import backend.status.Day;
import com.mongodb.client.MongoDatabase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

                patientRepository.createPatient(patient);
            } catch (Exception e) {
                System.out.println("Błąd pacjenta: " + e.getMessage());
            }
        }

        // Dodaj lekarzy
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] specializations = {
                "Kardiolog", "Neurolog", "Ortopeda", "Dermatolog",
                "Ginekolog", "Pediatra", "Chirurg", "Internista"
        };

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
                System.out.println("Błąd lekarza: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Dodaj wizyty z losowymi statusami
        List<Doctor> doctors = doctorRepository.findAll();
        List<Patient> patients = patientRepository.findAll();
        AppointmentStatus[] statuses = AppointmentStatus.values();

        for (int i = 0; i < 10; i++) {
            try {
                Appointment appointment = new Appointment();
                appointment.setDoctorId(doctors.get(random.nextInt(doctors.size())).getId());
                appointment.setPatientId(patients.get(random.nextInt(patients.size())).getId());
                appointment.setDate(generateRandomAppointmentDateTime());
                appointment.setStatus(statuses[random.nextInt(statuses.length)]);

                appointmentRepository.createAppointment(appointment);
            } catch (Exception e) {
                System.out.println("Błąd wizyty: " + e.getMessage());
            }
        }
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
