package backend;

import backend.klasy.Patient;
import backend.mongo.LocalDateCodec;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import com.mongodb.client.MongoDatabase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class Main {
    public static void main(String[] args) throws NullNameException, AgeException, PeselException {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();

        PatientRepository patientRepository = new PatientRepository(database);


// Wyszukiwanie pacjentów urodzonych w tym dniu
        List<Patient> lista = patientRepository.findPatientByBirthDate("1990-01-01");
        System.out.println("Znaleziono pacjentów: " + lista.size());


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
                                                        "      if (!firstName || firstName.trim().length === 0) {" +
                                                        "          throw new Error('Imię nie może być puste.');" +
                                                        "      }" +
                                                        "      if (!lastName || lastName.trim().length === 0) {" +
                                                        "          throw new Error('Nazwisko nie może być puste.');" +
                                                        "      }" +
                                                        "      if (age <= 0) {" +
                                                        "          throw new Error('Wiek pacjenta musi być większy niż 0.');" +
                                                        "      }" +
                                                        "      if (pesel < 10000000000 || pesel > 99999999999) {" +
                                                        "          throw new Error('Pesel musi mieć dokładnie 11 cyfr.');" +
                                                        "      }" +
                                                        "      return {" +
                                                        "          firstName: firstName," +
                                                        "          lastName: lastName," +
                                                        "          pesel: pesel," +
                                                        "          birthDate: birthDate," +
                                                        "          address: address," +
                                                        "          age: age," +
                                                        "          getFirstName: function() { return this.firstName; }," +
                                                        "          getLastName: function() {return this.lastName; }," +
                                                        "          getPesel: function() {return this.pesel; }," +
                                                        "          getBirthDate: function() {return this.birthDate;}" +
                                                        "          getAddress: function() {return this.address; }," +
                                                        "          getAge: function() {return this.age}" +
                                                        "      };" +
                                                        "  }" +
                                                        "  var patient = Patient('" + firstName + "', '" + lastName + "', " + pesel + ", '" + birthDate + "', '" + address + "', " + age + ");" +
                                                        "  return { firstName: patient.getFirstName(), lastName: patient.getLastName(), pesel: patient.getPesel(), birthDate: patient.getBirthDate(), address: patient.getAddress(), age: patient.getAge()};" +
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
                        System.out.println("Wynik:");
                        System.out.println("Imię: " + patientInfo.getString("firstName"));
                        System.out.println("Nazwisko: " + patientInfo.getString("lastName"));
                        System.out.println("Pesel: " + patientInfo.getDouble("pesel"));
                        System.out.println("Data urodzenia: " + patientInfo.getDate("birthDate"));
                        System.out.println("Adres: " + patientInfo.getString("address"));
                        System.out.println("Wiek: " + patientInfo.getDouble("age"));
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