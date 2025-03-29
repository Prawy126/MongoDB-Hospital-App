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
                "       id: new ObjectId()" + // Generujemy unikalne ID
                "   };" +
                "}";

        // Nazwa kolekcji tymczasowej
        String tempCollectionName = "tempPatients";

        try {
            // Wstaw dokument wejściowy do kolekcji tymczasowej
            MongoCollection<Document> tempColl = database.getCollection(tempCollectionName);
            tempColl.insertOne(patientInput);

            // Budujemy potok agregacyjny na kolekcji tymczasowej
            List<Document> pipeline = Arrays.asList(
                    new Document("$addFields", new Document("computedPatient",
                            new Document("$function", new Document()
                                    .append("body", functionBody)
                                    .append("args", Arrays.asList("$firstName", "$lastName", "$pesel", "$birthDate", "$address", "$age"))
                                    .append("lang", "js")
                            )
                    )),
                    new Document("$replaceRoot", new Document("newRoot", "$computedPatient")),
                    new Document("$out", "patients") // Zapis do głównej kolekcji
            );

            // Wykonujemy agregację na kolekcji tymczasowej
            tempColl.aggregate(pipeline).first();

            // Usuwamy dokument z kolekcji tymczasowej (opcjonalnie)
            tempColl.deleteOne(new Document("_id", patientInput.get("_id")));

            return true; // Sukces
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Błąd
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
                            LocalDate birthDateLocal = null;
                            Object birthDateObj = patientDoc.get("birthDate");
                            if (birthDateObj instanceof Date) {
                                birthDateLocal = ((Date) birthDateObj).toInstant()
                                        .atZone(ZoneId.systemDefault()).toLocalDate();
                            } else if (birthDateObj instanceof String) {
                                birthDateLocal = LocalDate.parse((String) birthDateObj);
                            }

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
                                    .birthDate(birthDateLocal)
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
     * Aktualizuje istniejącego pacjenta w bazie danych przy użyciu agregacji MongoDB. Funkcja działa na zasadzie jeśli id istnieje to pacjent zostanie zaktualicowany jeśli id nie istnieje to pacjent zostanie dodany
     *
     * @param patient pacjent do zaktualizowania
     * @return zaktualizowany pacjent
     */
    public Patient updatePatient(Patient patient) {
        if (patient == null || patient.getId() == null) {
            throw new IllegalArgumentException("Pacjent lub ID nie mogą być puste");
        }

        try {
            // Przygotowanie dokumentu z danymi pacjenta
            // Store birthDate as a proper Date object, not a string
            Document patientDoc = new Document()
                    .append("firstName", patient.getFirstName())
                    .append("lastName", patient.getLastName())
                    .append("pesel", patient.getPesel())
                    .append("birthDate", Date.from(patient.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .append("address", patient.getAddress())
                    .append("age", patient.getAge());

            // Tworzenie kolekcji tymczasowej dla operacji agregacji
            String tempCollectionName = "tempUpdatePatients";
            MongoCollection<Document> tempColl = database.getCollection(tempCollectionName);

            // Dodanie dokumentu pacjenta do kolekcji tymczasowej
            patientDoc.append("_id", new ObjectId()); // Tymczasowe ID dla dokumentu
            tempColl.insertOne(patientDoc);

            // Funkcja JS do walidacji i aktualizacji danych pacjenta
            String functionBody = "function(firstName, lastName, pesel, birthDate, address, age, patientId) {" +
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
                    "       _id: ObjectId(patientId)" +
                    "   };" +
                    "}";

            // Potok agregacji do przetworzenia i aktualizacji danych
            List<Document> updatePipeline = Arrays.asList(
                    new Document("$addFields", new Document("updatedPatient",
                            new Document("$function", new Document()
                                    .append("body", functionBody)
                                    .append("args", Arrays.asList(
                                            "$firstName", "$lastName", "$pesel", "$birthDate",
                                            "$address", "$age", patient.getId().toString()))
                                    .append("lang", "js")
                            )
                    )),
                    new Document("$replaceRoot", new Document("newRoot", "$updatedPatient")),
                    new Document("$merge", new Document()
                            .append("into", "patients")
                            .append("on", "_id")
                            .append("whenMatched", "replace")
                            .append("whenNotMatched", "fail"))
            );

            // Wykonanie agregacji
            tempColl.aggregate(updatePipeline).toCollection();

            // Sprawdzenie, czy aktualizacja się powiodła
            Patient result = collection.find(eq("_id", patient.getId())).first();
            if (result == null) {
                throw new IllegalStateException("Pacjent o ID " + patient.getId() + " nie istnieje w bazie.");
            }

            // Czyszczenie kolekcji tymczasowej
            tempColl.deleteOne(eq("_id", patientDoc.getObjectId("_id")));

            return patient;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas aktualizacji pacjenta: " + e.getMessage(), e);
        }
    }
    public void deletePatient(ObjectId id) {
        collection.deleteOne(eq("_id", id));
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
   // Tutaj są przeprowadzane testy
    public void testPatient() {
        System.out.println("\n=== Rozpoczynam testowanie PatientRepository ===");

        try {
            // Tworzenie pacjenta
            Patient testPatient = new Patient.Builder()
                    .firstName("Testowy")
                    .lastName("Pacjent")
                    .pesel("11122233311") // Valid PESEL
                    .birthDate(LocalDate.now())
                    .address("ul. Przykładowa 10, Kraków")
                    .age(25)
                    .build();

            if(createPatient(testPatient)){
                System.out.println("Pacjent został dodany do bazy");
            }
            System.out.println("[OK] Utworzono pacjenta: " + testPatient);

            try {
                Patient youngPatient = new Patient.Builder()
                        .firstName("Test")
                        .lastName("Patient")
                        .pesel("11122233311")
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
                        .pesel("-1") // Nieprawidłowy PESEL
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
                        .pesel("11122233311")
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
                        .pesel("11111111110")// Nieprawidłowy PESEL
                        .birthDate(LocalDate.of(2020, 1, 1))
                        .address("Test")
                        .age(10)
                        .build();
                createPatient(invalidPatient);
                System.err.println("Czy pesel poprawny"+isPeselValid(invalidPatient.getId()));
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage()); // Powinien zostać rzucony wyjątek
            }

            // Wyszukiwanie po ID
            Optional<Patient> foundById = findPatientById(testPatient.getId());
            if (foundById.isPresent()) {
                System.out.println("[OK] Wyszukano pacjenta po ID: " + foundById.get());
            } else {
                System.err.println("[ERROR] Nie znaleziono pacjenta o ID: " + testPatient.getId());
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
                testPatient.setAddress("ul. Zmieniona 20, Kraków");
                Patient updatedPatient = updatePatient(testPatient);
                System.out.println("[OK] Zaktualizowano adres pacjenta: " + updatedPatient.getAddress());
            } catch (Exception e) {
                System.err.println("[ERROR] Nie udało się zaktualizować pacjenta: " + e.getMessage());
            }


            // Usuwanie pacjenta
            deletePatient(testPatient.getId());
            System.out.println("[OK] Usunięto pacjenta o ID: " + testPatient.getId());

            System.out.println("[SUCCESS] Wszystkie testy aplikacyjne zakończone.");

        } catch (Exception e) {
            System.err.println("[ERROR] Wystąpił błąd podczas testowania PatientRepository: " + e.getMessage());
            e.printStackTrace();
        }

        testMongoValidation();

    }

    private void testMongoValidation() {
        System.out.println("\n=== [TEST] Walidacja po stronie MongoDB ===");

        // Przykłady testów: każdy case ma opis i dane, które łamią reguły walidacji
        List<ValidationCase> testCases = List.of(
                new ValidationCase("Brak imienia (firstName = null)", new Patient.Builder()
                        .skipValidation(true)
                        .lastName("BezImienia")
                        .pesel("11122233311")
                        .birthDate(LocalDate.now())
                        .address("ul. Błędna 1")
                        .age(30)
                ),
                new ValidationCase("Nieprawidłowy PESEL (za krótki)", new Patient.Builder()
                        .skipValidation(true)
                        .firstName("Jan")
                        .lastName("ZłyPesel")
                        .pesel("123456789")
                        .birthDate(LocalDate.now())
                        .address("ul. Niepoprawna")
                        .age(40)
                ),
                new ValidationCase("Brak daty urodzenia", new Patient.Builder()
                        .skipValidation(true)
                        .firstName("Anna")
                        .lastName("BrakDaty")
                        .pesel("11122233344")
                        .address("ul. Brakowa 1")
                        .age(28)
                )
        );

        for (ValidationCase test : testCases) {
            runMongoValidationTest(test.description, test.builder);
        }
    }

    private void runMongoValidationTest(String description, Patient.Builder builder) {
        try {
            System.out.println("\n[TEST CASE] " + description);
            Patient p = builder.build(); // budujemy pacjenta bez walidacji aplikacyjnej (bo wyjątki zakomentowane)
            createPatient(p);
            System.err.println("[FAIL] Dokument powinien zostać odrzucony przez MongoDB, ale został zapisany.");
        } catch (Exception e) {
            System.out.println("[OK] MongoDB prawidłowo odrzucił dokument: " + e.getMessage());
        }
    }

    private static class ValidationCase {
        String description;
        Patient.Builder builder;

        public ValidationCase(String description, Patient.Builder builder) {
            this.description = description;
            this.builder = builder;
        }
    }



}