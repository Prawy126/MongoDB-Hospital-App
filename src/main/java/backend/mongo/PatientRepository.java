package backend.mongo;

import backend.klasy.Patient;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.eq;

/**
 * Klasa zarządzajaca zapisem danych pacjenta do bazy MongoDB w sposób obiektowy*/
public class PatientRepository {
    private final MongoCollection<Patient> collection;

    public PatientRepository(MongoDatabase database) {
        this.collection = database.getCollection("patients", Patient.class);
    }

    /**
     * Tworzy nowego pacjenta w bazie danych.
     *
     * @param patient pacjent do utworzenia
     * @return utworzony pacjent
     */
    public Patient createPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        collection.insertOne(patient);
        return patient;
    }

    /**
     * Znajduje pacjenta po jego ID.
     *
     * @param id ID pacjenta
     * @return Optional zawierający znalezionego pacjenta lub pusty, jeśli pacjent nie został znaleziony
     */
    public Optional<Patient> findPatientById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    /**
     * Znajduje wszystkich pacjentów w bazie danych.
     *
     * @return lista wszystkich pacjentów
     */
    public List<Patient> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    /**
     * Znajduje pacjentów po ich imieniu.
     *
     * @param firstName imię pacjentów
     * @return lista pacjentów o podanym imieniu
     */
    public List<Patient> findPatientByFirstName(String firstName) {
        return collection.find(eq("firstName", firstName)).into(new ArrayList<>());
    }

    /**
     * Znajduje pacjentów po ich nazwisku.
     *
     * @param lastName nazwisko pacjentów
     * @return lista pacjentów o podanym nazwisku
     */
    public List<Patient> findPatientByLastName(String lastName) {
        return collection.find(eq("lastName", lastName)).into(new ArrayList<>());
    }
    /**
     * Znajduje pacjentów po ich numerze PESEL.
     *
     * @param pesel numer PESEL pacjentów
     * @return lista pacjentów o podanym numerze PESEL
     */
    public List<Patient> findPatientByPesel(long pesel) {
        return collection.find(eq("pesel", pesel)).into(new ArrayList<>());
    }

    /**
     * Znajduje pacjentów po ich adresie.
     *
     * @param address adres pacjentów
     * @return lista pacjentów o podanym adresie
     */
    public List<Patient> findPatientByAddress(String address) {
        return collection.find(eq("address", address)).into(new ArrayList<>());
    }

    /**
     * Znajduje pacjentów po ich dacie urodzenia.
     *
     * @param birthDate data urodzenia pacjentów
     * @return lista pacjentów o podanej dacie urodzenia
     */
    public List<Patient> findPatientByBirthDate(String birthDate) {
        return collection.find(eq("birthDate", birthDate)).into(new ArrayList<>());
    }

    /**
     * Aktualizuje istniejącego pacjenta w bazie danych.
     *
     * @param patient pacjent do zaktualizowania
     * @return zaktualizowany pacjent
     */
    public Patient updatePatient(Patient patient) {
        if (patient == null || patient.getId() == null) {
            throw new IllegalArgumentException("Patient or ID cannot be null");
        }

        long modifiedCount = collection.replaceOne(eq("_id", patient.getId()), patient).getModifiedCount();
        if (modifiedCount == 0) {
            throw new IllegalStateException("Pacjent o ID " + patient.getId() + " nie istnieje w bazie.");
        }
        return patient;
    }

    /**
     * Usuwa pacjenta po jego ID.
     *
     * @param id ID pacjenta do usunięcia
     */
    public void deletePatient(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    public void testPatient() {
        System.out.println("\n=== Rozpoczynam testowanie PatientRepository ===");

        try {
            // Tworzenie pacjenta
            Patient testPatient = new Patient.Builder()
                    .firstName("Testowy")
                    .lastName("Pacjent")
                    .pesel(11122233311L) // Valid PESEL
                    .birthDate(LocalDate.now())
                    .address("ul. Przykładowa 10, Kraków")
                    .age(25)
                    .build();

            Patient createdPatient = createPatient(testPatient);
            System.out.println("[OK] Utworzono pacjenta: " + createdPatient);

            try {
                Patient youngPatient = new Patient.Builder()
                        .firstName("Test")
                        .lastName("Patient")
                        .pesel(11122233311L)
                        .birthDate(LocalDate.now())
                        .address("Test Address")
                        .age(0) // Nieprawidłowy wiek
                        .build();
                createPatient(youngPatient);
                System.err.println("[FAIL] Powinien wystąpić AgeException dla wieku = 0");
            } catch (AgeException e) {
                System.out.println("[OK] Poprawnie przechwycono AgeException: " + e.getMessage());
            }

            // Test walidacji PESEL
            try {
                Patient invalidPeselPatient = new Patient.Builder()
                        .firstName("Test")
                        .lastName("Patient")
                        .pesel(-1) // Nieprawidłowy PESEL
                        .birthDate(LocalDate.now())
                        .address("Test Address")
                        .age(25)
                        .build();
                createPatient(invalidPeselPatient);
                System.err.println("[FAIL] Powinien wystąpić PeselException dla nieprawidłowego PESEL");
            } catch (PeselException e) {
                System.out.println("[OK] Poprawnie przechwycono PeselException: " + e.getMessage());
            }

            // Test pustego imienia
            try {
                Patient nullNamePatient = new Patient.Builder()
                        .firstName(null)
                        .lastName("Patient")
                        .pesel(11122233311L)
                        .birthDate(LocalDate.now())
                        .address("Test Address")
                        .age(25)
                        .build();
                createPatient(nullNamePatient);
                System.err.println("[FAIL] Powinien wystąpić NullNameException dla pustego imienia");
            } catch (NullNameException e) {
                System.out.println("[OK] Poprawnie przechwycono NullNameException: " + e.getMessage());
            }

            try {
                System.out.println("Test dla nieprawidłowego MongoDB");
                Patient invalidPatient = new Patient.Builder()
                        .firstName("Test")
                        .lastName("Błąd")
                        .pesel(1111111111L)// Nieprawidłowy PESEL
                        .birthDate(LocalDate.of(2020, 1, 1))
                        .address("Test")
                        .age(10)
                        .build();
                createPatient(invalidPatient);
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage()); // Powinien zostać rzucony wyjątek
            }

            // Wyszukiwanie po ID
            Optional<Patient> foundById = findPatientById(createdPatient.getId());
            if (foundById.isPresent()) {
                System.out.println("[OK] Wyszukano pacjenta po ID: " + foundById.get());
            } else {
                System.err.println("[ERROR] Nie znaleziono pacjenta o ID: " + createdPatient.getId());
            }

            // Wyszukiwanie po imieniu
            List<Patient> patientsByFirstName = findPatientByFirstName("Testowy");
            System.out.println("[OK] Wyszukano pacjentów po imieniu 'Testowy': " + patientsByFirstName.size());

            // Wyszukiwanie po nazwisku
            List<Patient> patientsByLastName = findPatientByLastName("Pacjent");
            System.out.println("[OK] Wyszukano pacjentów po nazwisku 'Pacjent': " + patientsByLastName.size());

            // Wyszukiwanie po PESEL
            List<Patient> patientsByPesel = findPatientByPesel(11122233311L);
            System.out.println("[OK] Wyszukano pacjentów po PESEL '11122233311': " + patientsByPesel.size());

            // Wyszukiwanie po adresie
            List<Patient> patientsByAddress = findPatientByAddress("ul. Przykładowa 10, Kraków");
            System.out.println("[OK] Wyszukano pacjentów po adresie 'ul. Przykładowa 10, Kraków': " + patientsByAddress.size());

            // Wyszukiwanie po dacie urodzenia
            List<Patient> patientsByBirthDate = findPatientByBirthDate(LocalDate.now().toString());
            System.out.println("[OK] Wyszukano pacjentów urodzonych dziś: " + patientsByBirthDate.size());

            // Pobranie wszystkich pacjentów
            List<Patient> allPatients = findAll();
            System.out.println("[OK] Liczba wszystkich pacjentów w bazie: " + allPatients.size());

            // Aktualizacja pacjenta
            try {
                createdPatient.setAddress("ul. Zmieniona 20, Kraków");
                Patient updatedPatient = updatePatient(createdPatient);
                System.out.println("[OK] Zaktualizowano adres pacjenta: " + updatedPatient.getAddress());
            } catch (Exception e) {
                System.err.println("[ERROR] Nie udało się zaktualizować pacjenta: " + e.getMessage());
            }


            // Usuwanie pacjenta
            deletePatient(createdPatient.getId());
            System.out.println("[OK] Usunięto pacjenta o ID: " + createdPatient.getId());

            System.out.println("[SUCCESS] Wszystkie testy zakończone pomyślnie!");

        } catch (Exception e) {
            System.err.println("[ERROR] Wystąpił błąd podczas testowania PatientRepository: " + e.getMessage());
            e.printStackTrace();
        }

    }

}