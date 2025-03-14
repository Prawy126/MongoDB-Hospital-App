package backend.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Klasa MongoDatabaseConnector służy do zarządzania połączeniem z bazą danych MongoDB.
 */
public class MongoDatabaseConnector {
    private static final String DB_IP = "192.168.8.102";
    private static final int DB_PORT = 27017;
    private static final String DB_NAME = "hospitalDB";

    private static MongoClient mongoClient;

    /**
     * Nawiązuje połączenie z bazą danych MongoDB.
     *
     * @return obiekt MongoDatabase reprezentujący połączenie z bazą danych
     */
    public static MongoDatabase connectToDatabase() {
        try {
            CodecRegistry pojoCodecRegistry = fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new com.mongodb.ConnectionString(String.format("mongodb://%s:%d", DB_IP, DB_PORT)))
                    .codecRegistry(pojoCodecRegistry)
                    .build();

            mongoClient = MongoClients.create(settings);
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            System.out.println("Połączono z bazą danych: " + DB_NAME);
            return database;
        } catch (MongoException e) {
            System.err.println("Błąd połączenia: " + e.getMessage());
            return null;
        }
    }

    /**
     * Zamyka połączenie z bazą danych MongoDB.
     */
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