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
 * Klasa narzędziowa do obsługi połączenia z bazą danych MongoDB.
 * Utrzymuje jedno połączenie przez cały cykl życia aplikacji.
 */
public class MongoDatabaseConnector {
    
    private static final String DB_IP = "192.168.0.50";

    private static final int DB_PORT = 27017;
    private static final String DB_NAME = "hospitalDB";

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    /**
     * Zwraca instancję bazy danych. Tworzy połączenie, jeśli nie istnieje.
     */
    public static MongoDatabase connectToDatabase() {
        if (database == null) {
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
                database = mongoClient.getDatabase(DB_NAME);

                System.err.println("[SUCCESS] Połączono z bazą danych: " + DB_NAME);
            } catch (MongoException e) {
                System.err.println("[ERROR] Błąd połączenia z MongoDB: " + e.getMessage());
            }
        }
        return database;
    }

    /**
     * Zamyka połączenie z bazą danych. Powinno być wywołane tylko raz przy zamykaniu aplikacji.
     */
    public static void close() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                mongoClient = null;
                database = null;
                System.out.println("[SUCCESS] Połączenie z MongoDB zostało zamknięte.");
            } catch (Exception e) {
                System.err.println("[ERROR] Błąd podczas zamykania MongoDB: " + e.getMessage());
            }
        } else {
            System.out.println("[INFO] Brak aktywnego połączenia do zamknięcia.");
        }
    }

    /**
     * Zwraca klienta MongoDB. Głównie do testów lub rzadkich przypadków użycia.
     */
    public static MongoClient getClient() {
        if (mongoClient == null) {
            connectToDatabase();
        }
        return mongoClient;
    }
}
