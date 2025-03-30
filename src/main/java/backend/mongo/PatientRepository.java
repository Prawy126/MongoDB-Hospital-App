package backend.mongo;

import com.mongodb.client.model.*;
import org.bson.Document;
import backend.klasy.Patient;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
     * @return true jeśli pacjent został dodany, false w przeciwnym razie
     */
    //pytanie dlaczego wiek pacjenta jest zerem taki aktualnie jest błąd
    public boolean createPatient(Patient patient) {
        // Walidacja wejścia
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }

        // Przygotowanie dokumentu wejściowego na podstawie obiektu patient
        Document patientInput = new Document("firstName", patient.getFirstName())
                .append("lastName", patient.getLastName())
                .append("pesel", String.valueOf(patient.getPesel())) // PESEL jako String
                .append("birthDate", patient.getBirthDate())
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
                "       id: new ObjectId()" + // Generujemy unikalne ID
                "   };" +
                "}";

        try {
            // Wstawienie dokumentu bezpośrednio do głównej kolekcji "patients"
            MongoCollection<Document> patientsColl = database.getCollection("patients");

            // Dodajemy ID pacjenta i walidujemy dane przed wstawieniem
            patientInput.append("_id", new ObjectId()); // Generowanie nowego ID

            // Wstawienie dokumentu pacjenta do kolekcji "patients"
            patientsColl.insertOne(patientInput);

            return true; // Sukces
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Błąd
        }
    }

    /**
     * Znajduje pacjenta po jego ID. Funckja korzysta z agregacji
     *
     * @param id ID pacjenta
     * @return Optional zawierający znalezionego pacjenta lub pusty, jeśli pacjent nie został znaleziony
     */
    public Optional<Patient> findPatientById(ObjectId id) {
        // Tworzymy potok agregacji z jednym etapem $match
        List<Bson> pipeline = Collections.singletonList(
                Aggregates.match(Filters.eq("_id", id))
        );

        // Wykonujemy agregację i pobieramy pierwszy wynik
        Patient patient = collection.aggregate(pipeline)
                .first();

        // Zwracamy wynik opakowany w Optional
        return Optional.ofNullable(patient);
    }

    /**
     * Znajduje wszystkich pacjentów w bazie danych. wykorzystuje agregację
     *
     * @return lista wszystkich pacjentów
     */
    public List<Patient> findAll() {
        List<Document> pipeline = Arrays.asList(
                new Document("$project", new Document("patientData", "$$ROOT"))
        );

        try {
            List<Document> results = collection.withDocumentClass(Document.class).aggregate(pipeline).into(new ArrayList<>());

            if (results.isEmpty()) {
                System.err.println("Brak pacjentów w bazie danych.");
                return Collections.emptyList();
            }

            return results.stream()
                    .map(doc -> {
                        try {
                            // Extract the nested patientData document if it exists
                            Document patientDoc = doc.containsKey("patientData") ?
                                    doc.get("patientData", Document.class) :
                                    doc;

                            String firstName = patientDoc.getString("firstName");
                            String lastName = patientDoc.getString("lastName");
                            String pesel = patientDoc.getString("pesel");

                            // Handle different date formats that might be in the database
                            LocalDate birthDateLocal = LocalDate.parse(patientDoc.getString("birthDate"));
                            String address = patientDoc.getString("address");

                            // Handle potential numeric type differences for age
                            Integer age = null;
                            Object ageObj = patientDoc.get("age");
                            if (ageObj instanceof Integer) {
                                age = (Integer) ageObj;
                            } else if (ageObj instanceof Double) {
                                age = ((Double) ageObj).intValue();
                            }

                            if (firstName == null || lastName == null || pesel == null ||
                                    birthDateLocal == null || address == null || age == null) {
                                System.err.println("Brak wymaganych danych dla pacjenta: " + patientDoc);
                                return null;
                            }

                            ObjectId id = patientDoc.getObjectId("_id");

                            return new Patient.Builder()
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .pesel(pesel)
                                    .birthDate(String.valueOf(birthDateLocal))
                                    .address(address)
                                    .age(age)
                                    .withId(id) // Make sure the ID is correctly set
                                    .build();
                        } catch (Exception e) {
                            System.err.println("Błąd przy mapowaniu dokumentu: " + doc);
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
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
    public List<Patient> findPatientByPesel(String pesel) {
        List<Bson> pipeline = Collections.singletonList(
                Aggregates.match(Filters.eq("pesel", pesel))
        );

        return collection.aggregate(pipeline)
                .into(new ArrayList<>());
    }

    /**
     * Znajduje pacjentów po ich adresie.
     *
     * @param address adres pacjentów
     * @return lista pacjentów o podanym adresie
     */
    public List<Patient> findPatientByAddress(String address) {
        List<Bson> pipeline = Collections.singletonList(
                Aggregates.match(Filters.eq("address", address))
        );

        return collection.aggregate(pipeline)
                .into(new ArrayList<>());
    }

    /**
     * Znajduje pacjentów po ich dacie urodzenia.
     *
     * @param birthDate data urodzenia pacjentów
     * @return lista pacjentów o podanej dacie urodzenia
     */
    public List<Patient> findPatientByBirthDate(String birthDate) {
        try {
            if (!birthDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new IllegalArgumentException("Niepoprawny format daty. Oczekiwany format: yyyy-MM-dd");
            }
            // Bezpośrednie porównanie Stringów
            return collection.find(eq("birthDate", birthDate)).into(new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania pacjentów po dacie urodzenia: " + e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje istniejącego pacjenta w bazie danych przy użyciu agregacji MongoDB. Funkcja działa na zasadzie jeśli id istnieje to pacjent zostanie zaktualicowany jeśli id nie istnieje to pacjent zostanie dodany
     *
     * //@param patient pacjent do zaktualizowania
     * //@return zaktualizowany pacjent
     */

    public Patient updatePatient(Patient patient) {
        if (patient == null || patient.getId() == null) {
            throw new IllegalArgumentException("Pacjent lub ID nie mogą być puste");
        }

        List<Bson> pipeline = Arrays.asList(
                // 1. Dopasowanie dokumentu po ID
                Aggregates.match(eq("_id", patient.getId())),

                // 2. Walidacja i aktualizacja
                Aggregates.addFields(
                        new Field<>("validated",
                                new Document()
                                        .append("firstName", patient.getFirstName())
                                        .append("lastName", patient.getLastName())
                                        .append("pesel", patient.getPesel())
                                        .append("birthDate", patient.getBirthDate())
                                        .append("address", patient.getAddress())
                                        .append("age", patient.getAge())
                        )
                ),

                // 3. Sprawdzenie warunków
                Aggregates.match(
                        new Document("$expr",
                                new Document("$and", Arrays.asList(
                                        new Document("$ne", Arrays.asList("$validated.firstName", "")),
                                        new Document("$eq", Arrays.asList(
                                                new Document("$strLenCP", "$validated.pesel"), 11
                                        )),
                                        new Document("$gt", Arrays.asList("$validated.age", 0))
                                ))
                        )
                ),

                // 4. Przygotowanie końcowego dokumentu
                Aggregates.replaceWith(
                        new Document("$mergeObjects", Arrays.asList(
                                "$$ROOT",
                                "$validated"
                        ))
                )
        );

        try {
            // Wykonaj agregację
            Patient updatedPatient = collection.aggregate(pipeline).first();

            if (updatedPatient == null) {
                throw new RuntimeException("Walidacja nie powiodła się");
            }

            // Aktualizuj dokument w bazie
            collection.replaceOne(
                    eq("_id", patient.getId()),
                    updatedPatient
            );

            return updatedPatient;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas aktualizacji pacjenta: " + e.getMessage(), e);
        }
    }
    public void deletePatient(ObjectId id) {
        // Krok 1: Użyj agregacji do sprawdzenia, czy pacjent istnieje
        Patient patient = collection.aggregate(Arrays.asList(
                Aggregates.match(eq("_id", id)),
                Aggregates.project(Projections.include("_id")) // Opcjonalne: wybierz tylko potrzebne pola
        )).first();

        if (patient != null) {
            // Krok 2: Jeśli dokument istnieje, usuń go
            collection.deleteOne(Filters.eq("_id", id));
            System.out.println("Pacjent o ID " + id + " został usunięty.");
        } else {
            System.out.println("Pacjent o ID " + id + " nie istnieje.");
        }
    }
    /**
     * Sprawdza poprawność PESEL pacjenta na podstawie daty urodzenia.
     *
     * @param patientId ID pacjenta
     * @return true jeśli PESEL jest poprawny
     */
    public boolean isPeselValid(ObjectId patientId) {
            List<Bson> pipeline = Arrays.asList(
                    Aggregates.match(eq("_id", patientId)),
                    Aggregates.addFields(new Field<>("peselValid",
                            new Document("$function",
                                    new Document()
                                            .append("body", "function(pesel, birthDateStr) {" +
                                                    "if (pesel.length !== 11) return false;" +
                                                    "const yearPart = pesel.slice(0, 2);" +
                                                    "let monthPart = pesel.slice(2, 4);" +
                                                    "const dayPart = pesel.slice(4, 6);" +
                                                    "let century = 1900;" +
                                                    "if (monthPart >= 21 && monthPart <= 32) {" +
                                                    "    monthPart -= 20;" +
                                                    "    century = 2000;" +
                                                    "} else if (monthPart >= 81 && monthPart <= 92) {" +
                                                    "    monthPart -= 80;" +
                                                    "    century = 1800;" +
                                                    "}" +
                                                    "const peselDate = new Date(century + parseInt(yearPart), parseInt(monthPart)-1, parseInt(dayPart));" +
                                                    "const documentDate = new Date(birthDateStr);" +
                                                    "if (peselDate.getTime() !== documentDate.getTime()) return false;" +
                                                    "const weights = [1, 3, 7, 9, 1, 3, 7, 9, 1, 3];" +
                                                    "let sum = 0;" +
                                                    "for (let i = 0; i < 10; i++) {" +
                                                    "    sum += parseInt(pesel[i]) * weights[i];" +
                                                    "}" +
                                                    "const controlDigit = (10 - (sum % 10)) % 10;" +
                                                    "return controlDigit === parseInt(pesel[10]);" +
                                                    "}")
                                            .append("args", Arrays.asList("$pesel", "$birthDate"))
                                            .append("lang", "js")
                            )
                    )),
                    Aggregates.project(Projections.fields(
                            Projections.include("peselValid"),
                            Projections.excludeId()
                    ))
            );

            // ZMIANA: Odczytaj jako Document, a nie Patient
            Document result = collection.withDocumentClass(Document.class).aggregate(pipeline).first();
            if (result == null) {
                throw new IllegalArgumentException("Pacjent o ID " + patientId + " nie istnieje");
            }

            return result.getBoolean("peselValid", false);
        }


}