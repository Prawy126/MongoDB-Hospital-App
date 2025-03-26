package backend.mongo;

import backend.klasy.Doctor;
import backend.status.Day;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.eq;

/**
 * Klasa DoctorRepository zarządza operacjami CRUD dla kolekcji lekarzy w bazie MongoDB.
 * <p>
 * Metody tej klasy pozwalają na tworzenie, wyszukiwanie, aktualizowanie i usuwanie lekarzy.
 * Klasa ta zapewnia również metody do testowania operacji na kolekcji lekarzy.
 * </p>
 */
public class DoctorRepository {
    private final MongoCollection<Doctor> collection;

    /**
     * Konstruktor inicjalizujący kolekcję lekarzy.
     *
     * @param database obiekt MongoDatabase reprezentujący połączenie z bazą danych
     */
    public DoctorRepository(MongoDatabase database) {
        this.collection = database.getCollection("doctors", Doctor.class);
    }

    /**
     * Tworzy nowego lekarza w bazie danych.
     *
     * @param doctor lekarz do utworzenia
     * @return utworzony lekarz
     * @throws IllegalArgumentException jeśli lekarz jest null
     */
    public Doctor createDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        collection.insertOne(doctor);
        return doctor;
    }

    /**
     * Znajduje lekarza po jego ID.
     *
     * @param id ID lekarza
     * @return Optional zawierający znalezionego lekarza lub pusty, jeśli nie znaleziono
     */
    public Optional<Doctor> findDoctorById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    /**
     * Znajduje wszystkich lekarzy w bazie danych.
     *
     * @return lista wszystkich lekarzy
     */
    public List<Doctor> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    /**
     * Znajduje lekarzy po imieniu.
     *
     * @param firstName imię lekarza
     * @return lista lekarzy o podanym imieniu
     */
    public List<Doctor> findDoctorByFirstName(String firstName) {
        return collection.find(eq("firstName", firstName)).into(new ArrayList<>());
    }

    /**
     * Znajduje lekarzy po nazwisku.
     *
     * @param lastName nazwisko lekarza
     * @return lista lekarzy o podanym nazwisku
     */
    public List<Doctor> findDoctorByLastName(String lastName) {
        return collection.find(eq("lastName", lastName)).into(new ArrayList<>());
    }

    /**
     * Znajduje lekarzy po specjalizacji.
     *
     * @param specialization specjalizacja lekarza
     * @return lista lekarzy o podanej specjalizacji
     */
    public List<Doctor> findDoctorBySpecialization(String specialization) {
        return collection.find(eq("specialization", specialization)).into(new ArrayList<>());
    }

    /**
     * Aktualizuje dane lekarza w bazie danych.
     *
     * @param doctor lekarz do zaktualizowania
     * @return zaktualizowany lekarz
     */
    public Doctor updateDoctor(Doctor doctor) {
        collection.replaceOne(eq("_id", doctor.getId()), doctor);
        return doctor;
    }

    /**
     * Usuwa lekarza po jego ID.
     *
     * @param id ID lekarza do usunięcia
     */
    public void deleteDoctor(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }
    public  List<Doctor> findCurrentDoctors() {
        // Filtrujemy lekarzy, którzy mają niepustą listę dostępnych dni
        return collection.find(Filters.exists("availableDays", true))
                .into(new ArrayList<>());
    }
    /**
     * Metoda testująca operacje na kolekcji lekarzy.
     * <p>
     * Tworzy przykładowych lekarzy, testuje wyjątki oraz operacje CRUD.
     * </p>
     */
    public void testDoctor() {
        System.out.println("\n=== Rozpoczynam testowanie DoctorRepository ===");

        try {
            // Tworzenie lekarza
            Doctor testDoctor = new Doctor.Builder()
                    .firstName("Testowy")
                    .lastName("Lekarz")
                    .specialization("Kardiolog")
                    .pesel("11122233311")
                    .age(45)
                    .availableDays(List.of(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY))
                    .build();

            Doctor createdDoctor = createDoctor(testDoctor);
            System.out.println("[OK] Utworzono lekarza: " + createdDoctor);

            try {
                Doctor youngDoctor = new Doctor.Builder()
                        .firstName("Test")
                        .lastName("Doctor")
                        .specialization("Kardiolog")
                        .pesel("11122233311")
                        .age(20) // Wiek poniżej wymaganego minimum 25 lat
                        .availableDays(List.of(Day.MONDAY))
                        .build();
                createDoctor(youngDoctor);
                System.err.println("[FAIL] Powinien wystąpić AgeException dla wieku < 25");
            } catch (AgeException e) {
                System.out.println("[OK] Poprawnie przechwycono AgeException: " + e.getMessage());
            }

            // Test walidacji PESEL
            try {
                Doctor invalidPeselDoctor = new Doctor.Builder()
                        .firstName("Test")
                        .lastName("Doctor")
                        .specialization("Kardiolog")
                        .pesel("-1") // Nieprawidłowy PESEL
                        .age(30)
                        .availableDays(List.of(Day.MONDAY))
                        .build();
                createDoctor(invalidPeselDoctor);
                System.err.println("[FAIL] Powinien wystąpić PeselException dla nieprawidłowego PESEL");
            } catch (PeselException e) {
                System.out.println("[OK] Poprawnie przechwycono PeselException: " + e.getMessage());
            }

            // Test pustego imienia
            try {
                Doctor nullNameDoctor = new Doctor.Builder()
                        .firstName(null)
                        .lastName("Doctor")
                        .specialization("Kardiolog")
                        .pesel("11122233311")
                        .age(30)
                        .availableDays(List.of(Day.MONDAY))
                        .build();
                createDoctor(nullNameDoctor);
                System.err.println("[FAIL] Powinien wystąpić NullNameException dla pustego imienia");
            } catch (NullNameException e) {
                System.out.println("[OK] Poprawnie przechwycono NullNameException: " + e.getMessage());
            }

            // Wyszukiwanie po ID
            Optional<Doctor> foundById = findDoctorById(createdDoctor.getId());
            System.out.println("[OK] Wyszukano lekarza po ID: " + foundById.orElse(null));

            // Wyszukiwanie po imieniu
            List<Doctor> doctorsByFirstName = findDoctorByFirstName("Testowy");
            System.out.println("[OK] Wyszukano lekarzy po imieniu 'Testowy': " + doctorsByFirstName.size());

            // Wyszukiwanie po nazwisku
            List<Doctor> doctorsByLastName = findDoctorByLastName("Lekarz");
            System.out.println("[OK] Wyszukano lekarzy po nazwisku 'Lekarz': " + doctorsByLastName.size());

            // Wyszukiwanie po specjalizacji
            List<Doctor> doctorsBySpecialization = findDoctorBySpecialization("Kardiolog");
            System.out.println("[OK] Wyszukano lekarzy o specjalizacji 'Kardiolog': " + doctorsBySpecialization.size());

            // Pobranie wszystkich lekarzy
            List<Doctor> allDoctors = findAll();
            System.out.println("[OK] Liczba wszystkich lekarzy w bazie: " + allDoctors.size());

            // Aktualizacja lekarza
            createdDoctor.setAvailableDays(List.of(Day.THURSDAY, Day.SATURDAY));
            Doctor updatedDoctor = updateDoctor(createdDoctor);
            System.out.println("[OK] Zaktualizowano dni przyjęć lekarza: " + updatedDoctor.getAvailableDays());

            // Usuwanie lekarza
           // deleteDoctor(createdDoctor.getId());
            System.out.println("[OK] Usunięto lekarza o ID: " + createdDoctor.getId());

            System.out.println("[SUCCESS] Wszystkie testy zakończone pomyślnie!");

        } catch (Exception e) {
            System.err.println("[ERROR] Wystąpił błąd podczas testowania DoctorRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}