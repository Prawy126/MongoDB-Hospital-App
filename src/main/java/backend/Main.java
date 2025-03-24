package backend;

import backend.mongo.MongoDatabaseConnector;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Main {
    public static void main(String[] args) {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();

        if (database != null) {
            String firstName = "Jan";
            String lastName = "Kowalski";
            long pesel = 12345678901L;
            String birthDate = "1990-01-01";
            String address = "ul. Testowa 123, Warszawa";
            int age = 30;

            try {
                // 1. Pobierz kod funkcji Patient z kolekcji
                MongoCollection<Document> functionsCollection = database.getCollection("orderFunctions");
                Document patientFunctionDoc = functionsCollection.find(eq("name", "Patient")).first();
                String patientFunctionCode = patientFunctionDoc.getString("code");

                // 2. Wykonaj funkcję w agregacji
                Document result = database.runCommand(new Document("aggregate", "orders")
                        .append("pipeline", Arrays.asList(
                                new Document("$addFields", new Document("patientInfo",
                                        new Document("$function", new Document()
                                                .append("body", patientFunctionCode +
                                                        "; return new Patient('" + firstName + "', '" + lastName + "', " + pesel + ", '" + birthDate + "', '" + address + "', " + age + ");")
                                                .append("args", Arrays.asList())
                                                .append("lang", "js")
                                        )
                                ))
                        ))
                        .append("cursor", new Document()));

                // 3. Przetwórz wynik z walidacją
                Document cursor = (Document) result.get("cursor");
                List<Document> firstBatch = cursor.get("firstBatch", List.class);
                if (firstBatch != null && !firstBatch.isEmpty()) {
                    Document firstBatchDoc = firstBatch.get(0);
                    Document patientInfo = firstBatchDoc.get("patientInfo", Document.class);

                    if (patientInfo != null) {
                        System.out.println("Wynik:");
                        System.out.println("Imię: " + patientInfo.getString("firstName"));
                        System.out.println("Nazwisko: " + patientInfo.getString("lastName"));
                        System.out.println("Pesel: " + patientInfo.getLong("pesel"));
                        System.out.println("Wiek: " + patientInfo.getInteger("age"));
                    } else {
                        System.out.println("Brak informacji o pacjencie w wynikach");
                    }
                } else {
                    System.out.println("Brak wyników w pierwszym batch'u");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                MongoDatabaseConnector.close();
            }
        } else {
            System.err.println("[ERROR] Połączenie z bazą danych nie powiodło się.");
        }
    }
}