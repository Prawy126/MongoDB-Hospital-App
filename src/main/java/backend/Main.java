package backend;

import backend.klasy.Patient;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Main {
    public static void main(String[] args) throws NullNameException, AgeException, PeselException {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();

        PatientRepository patientRepository = new PatientRepository(database);

        // Tworzymy pacjenta używając wzorca Builder
        Patient patient = new Patient.Builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .pesel(12345678901L)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("ul. Testowa 123, Warszawa")
                .age(30)
                .build();
        patientRepository.createPatient(patient);

        //aktualnie zakomentowuję dla testów dodawania pacjenta do bazy
        /*
        if (database != null) {
            String firstName = "Jan";
            String lastName = "Kowalski";
            long pesel = 12345678901L;
            String birthDate = "1990-01-01";
            String address = "ul. Testowa 123, Warszawa";
            int age = 30;

            try {
                // Upewnij się, że kolekcja "orders" nie jest pusta.
                MongoCollection<Document> ordersCollection = database.getCollection("orders");
                long count = ordersCollection.countDocuments();
                if (count == 0) {
                    // Wstawienie przykładowego dokumentu
                    ordersCollection.insertOne(new Document("dummy", true));
                    System.out.println("[INFO] Wstawiono przykładowy dokument do kolekcji 'orders'.");
                }

                // Wywołanie funkcji Patient zdefiniowanej inline w operatorze $function
                Document command = new Document("aggregate", "orders")
                        .append("pipeline", Arrays.asList(
                                new Document("$addFields", new Document("patientInfo",
                                        new Document("$function", new Document()
                                                .append("body", "function() {" +
                                                        "  function Patient(firstName, lastName, pesel, birthDate, address, age) {" +
                                                        "      // ... (walidacje)" +
                                                        "      return {" +
                                                        "          firstName: firstName," +
                                                        "          lastName: lastName," +
                                                        "          pesel: pesel," +
                                                        "          birthDate: birthDate," +
                                                        "          address: address," +
                                                        "          age: age," +
                                                        "          getLastName: function() { return this.lastName; }," +
                                                        "          getPesel: function() { return this.pesel; }," +
                                                        "          get birthDate() { return this.birthDate; }," +  // Getter dla daty
                                                        "          getAddress: function() { return this.address; }," +  // ✅ Przecinek
                                                        "          getAge: function() { return this.age; }," +
                                                        "          getFirstName: function() { return this.firstName; }" +
                                                        "      };" +
                                                        "  }" +
                                                        "  var patient = Patient('" + firstName + "', '" + lastName + "', " + pesel + ", '" + birthDate + "', '" + address + "', " + age + ");" +
                                                        "  return {" +
                                                        "      firstName: patient.getFirstName()," +
                                                        "      age: patient.getAge()," +
                                                        "      lastName: patient.getLastName()," +
                                                        "      address: patient.getAddress()," +  // ✅ Poprawiona literówka
                                                        "      pesel: patient.getPesel()," +
                                                        "      birthDate: patient.birthDate" +  // ✅ Bez nawiasów ()
                                                        "  };" +
                                                        "}")
                                                .append("args", Arrays.asList())
                                                .append("lang", "js")
                                        )
                                ))
                        ))
                        .append("cursor", new Document());

                Document result = database.runCommand(command);

                // Odczytanie wyniku z kursora
                Document cursor = (Document) result.get("cursor");
                List<Document> firstBatch = cursor.get("firstBatch", List.class);
                if (firstBatch != null && !firstBatch.isEmpty()) {
                    Document firstBatchDoc = firstBatch.get(0);
                    Document patientInfo = firstBatchDoc.get("patientInfo", Document.class);
                    if (patientInfo != null) {
                        System.out.println("Imię: " + patientInfo.getString("firstName"));
                        System.out.println("Nazwisko: " + patientInfo.getString("getLastName"));
                        System.out.println("Pesel: " + patientInfo.getDouble("getPesel"));
                        System.out.println("Wiek: " + patientInfo.getInteger("age"));
                        System.out.println("Data urodzenia: " + patientInfo.getString("birthDate"));
                        System.out.println("Adres: " + patientInfo.getString("getAddress"));
                    } else {
                        System.out.println("Brak informacji o pacjencie.");
                    }
                } else {
                    System.out.println("Brak wyników w pierwszym batch'u.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                MongoDatabaseConnector.close();
            }
        } else {
            System.err.println("[ERROR] Połączenie z bazą danych nie powiodło się.");
        }*/
    }
}
