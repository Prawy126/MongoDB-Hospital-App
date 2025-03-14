package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.eq;

/*
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
    public List<Patient> findPatientByPesel(int pesel) {
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
        collection.replaceOne(eq("_id", patient.getId()), patient);
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

            // Wyszukiwanie po ID
            Optional<Patient> foundById = findPatientById(createdPatient.getId());
            System.out.println("[OK] Wyszukano pacjenta po ID: " + foundById.orElse(null));

            // Wyszukiwanie po imieniu
            List<Patient> patientsByFirstName = findPatientByFirstName("Testowy");
            System.out.println("[OK] Wyszukano pacjentów po imieniu 'Testowy': " + patientsByFirstName.size());

            // Wyszukiwanie po nazwisku
            List<Patient> patientsByLastName = findPatientByLastName("Pacjent");
            System.out.println("[OK] Wyszukano pacjentów po nazwisku 'Pacjent': " + patientsByLastName.size());

            // Wyszukiwanie po PESEL
            List<Patient> patientsByPesel = findPatientByPesel(111222333);
            System.out.println("[OK] Wyszukano pacjentów po PESEL '111222333': " + patientsByPesel.size());

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
            createdPatient.setAddress("ul. Zmieniona 20, Kraków");
            Patient updatedPatient = updatePatient(createdPatient);
            System.out.println("[OK] Zaktualizowano adres pacjenta: " + updatedPatient.getAddress());

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