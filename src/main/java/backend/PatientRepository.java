package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
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
        return collection.find().into(List.of());
    }

    /**
     * Znajduje pacjentów po ich imieniu.
     *
     * @param firstName imię pacjentów
     * @return lista pacjentów o podanym imieniu
     */
    public List<Patient> findPatientByFirstName(String firstName) {
        return collection.find(eq("firstName", firstName)).into(List.of());
    }

    /**
     * Znajduje pacjentów po ich nazwisku.
     *
     * @param lastName nazwisko pacjentów
     * @return lista pacjentów o podanym nazwisku
     */
    public List<Patient> findPatientByLastName(String lastName) {
        return collection.find(eq("lastName", lastName)).into(List.of());
    }

    /**
     * Znajduje pacjentów po ich numerze PESEL.
     *
     * @param pesel numer PESEL pacjentów
     * @return lista pacjentów o podanym numerze PESEL
     */
    public List<Patient> findPatientByPesel(int pesel) {
        return collection.find(eq("pesel", pesel)).into(List.of());
    }

    /**
     * Znajduje pacjentów po ich adresie.
     *
     * @param address adres pacjentów
     * @return lista pacjentów o podanym adresie
     */
    public List<Patient> findPatientByAddress(String address) {
        return collection.find(eq("address", address)).into(List.of());
    }

    /**
     * Znajduje pacjentów po ich dacie urodzenia.
     *
     * @param birthDate data urodzenia pacjentów
     * @return lista pacjentów o podanej dacie urodzenia
     */
    public List<Patient> findPatientByBirthDate(String birthDate) {
        return collection.find(eq("birthDate", birthDate)).into(List.of());
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
}