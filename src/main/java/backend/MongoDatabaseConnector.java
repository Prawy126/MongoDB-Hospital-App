package backend;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;

public class MongoDatabaseConnector {

    private static final String DB_IP = "192.168.0.104";
    private static final int DB_PORT = 27017;
    private static final String DB_NAME = "testdb";

    private static MongoClient mongoClient;
    public static MongoDatabase connectToDatabase() {
        String connectionString = String.format("mongodb://%s:%d", DB_IP, DB_PORT);

        try {
            MongoClient mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            // Wymuś komunikację z serwerem
            mongoClient.listDatabaseNames().first();
            return database;
        } catch (MongoException e) {
            System.err.println("Błąd połączenia: " + e.getMessage());
            return null;
        }
    }
    public static void close() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                System.out.println("Połączenie z bazą danych zostało zamknięte.");
            } catch (Exception e) {
                System.err.println("Błąd podczas zamykania połączenia: " + e.getMessage());
            }
        } else {
            System.out.println("Brak aktywnego połączenia do zamknięcia.");
        }
    }
}
