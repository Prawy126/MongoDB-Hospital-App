package backend;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;

public class MongoDatabaseConnector {
    private static final String DB_IP = "192.168.8.102";
    private static final int DB_PORT = 27017;
    private static final String DB_NAME = "hospitalDB";

    private static MongoClient mongoClient;

    public static MongoDatabase connectToDatabase() {
        try {
            String connectionString = String.format("mongodb://%s:%d", DB_IP, DB_PORT);
            mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            System.out.println("Połączono z bazą danych: " + DB_NAME);
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