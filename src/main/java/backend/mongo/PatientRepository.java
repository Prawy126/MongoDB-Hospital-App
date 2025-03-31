package backend.mongo;

import org.bson.Document;
import backend.klasy.Patient;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Projections;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.eq;

/**
 * Klasa zarządzajaca zapisem danych pacjenta do bazy MongoDB w sposób obiektowy*/
public class PatientRepository {
    private final MongoCollection<Patient> collection;
    private final MongoDatabase database;

    public PatientRepository(MongoDatabase database) {
        this.database = database;
        this.collection = database.getCollection("patients", Patient.class);
    }

    /**
     * Tworzy nowego pacjenta w bazie danych.
     *
     * @param patient pacjent do utworzenia
     * @return utworzony pacjent
     */
    //pytanie dlaczego wiek pacjenta jest zerem taki aktualnie jest błąd
    public boolean createPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }

        // Przygotowanie dokumentu wejściowego na podstawie obiektu patient
        Document patientInput = new Document("firstName", patient.getFirstName())
                .append("lastName", patient.getLastName())
                .append("pesel", String.valueOf(patient.getPesel())) // <- PESEL jako String!
                .append("birthDate", patient.getBirthDate().toString())
                .append("address", patient.getAddress())
                .append("age", patient.getAge());


        // Definicja funkcji JS, która przetworzy dane pacjenta (walidacja, dodanie ID)
        String functionBody = "function(firstName, lastName, pesel, birthDate, address, age) {" +
                "   if (!firstName || firstName.trim().length === 0) {" +
                "       throw new Error('Imię nie może być puste.');" +
                "   }" +
                "   if (!lastName || lastName.trim().length === 0) {" +
                "       throw new Error('Nazwisko nie może być puste.');" +
                "   }" +
                "   if (age <= 0) {" +
                "       throw new Error('Wiek pacjenta musi być większy niż 0.');" +
                "   }" +
                "   if (pesel.length != 11) {" +
                "       throw new Error('Pesel musi mieć dokładnie 11 cyfr.');" +
                "   }" +
                "   return {" +
                "       firstName: firstName," +
                "       lastName: lastName," +
                "       pesel: pesel," +
                "       birthDate: birthDate," +
                "       address: address," +
                "       age: age," +
                "       id: new ObjectId()" +
                "   };" +
                "}";


        // Nazwa tymczasowej kolekcji – użyjemy jej tylko do przetworzenia dokumentu
        String tempCollectionName = "tempPatients";

        try {
            // Wstaw dokument wejściowy do kolekcji tymczasowej
            MongoCollection<Document> tempColl = database.getCollection(tempCollectionName, Document.class);
            tempColl.insertOne(patientInput);

            // Budujemy potok agregacyjny na kolekcji tymczasowej
            List<Document> pipeline = Arrays.asList(
                    new Document("$addFields", new Document("computedPatient",
                            new Document("$function", new Document()
                                    .append("body", functionBody)
                                    // Przekazujemy argumenty z dokumentu wejściowego – używamy ścieżek do pól
                                    .append("args", Arrays.asList("$firstName", "$lastName", "$pesel", "$birthDate", "$address", "$age"))
                                    .append("lang", "js")
                            )
                    )),
                    new Document("$replaceRoot", new Document("newRoot", "$computedPatient"))
            );

            // Wykonujemy agregację na kolekcji tymczasowej
            Document computedDoc = tempColl.aggregate(pipeline).first();
            if (computedDoc == null) {
                throw new RuntimeException("Agregacja nie zwróciła rezultatu.");
            }

            // Usuwamy dokument z kolekcji tymczasowej (opcjonalnie)
            tempColl.deleteOne(eq("firstName", patient.getFirstName()));

            // Wstawiamy obliczony dokument do głównej kolekcji "patients"
            MongoCollection<Document> patientsColl = database.getCollection("patients", Document.class);
            patientsColl.insertOne(computedDoc);

            // Mapujemy wynikowy dokument na obiekt Patient – możesz tu użyć własnego mapera
            Patient createdPatient = new Patient.Builder()
                    .firstName(computedDoc.getString("firstName"))
                    .lastName(computedDoc.getString("lastName"))
                    .pesel(computedDoc.getString("pesel"))
                    .birthDate(LocalDate.parse(computedDoc.getString("birthDate")))
                    .address(computedDoc.getString("address"))
                    .age(computedDoc.getInteger("age"))
                    .build();
            createdPatient.setId(computedDoc.getObjectId("_id"));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
    public void deletePatient(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

}