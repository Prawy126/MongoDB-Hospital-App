package backend;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDatabaseConnector {
    private static final String DB_IP = "192.168.0.104";
    private static final int DB_PORT = 27017;
    private static final String DB_NAME = "hospitalDB";

    private static MongoClient mongoClient;

    public static MongoDatabase connectToDatabase() {
        try {
            // Create a CodecRegistry that includes POJO support
            CodecRegistry pojoCodecRegistry = fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            // Configure the client settings with the codec registry
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new com.mongodb.ConnectionString(
                            String.format("mongodb://%s:%d", DB_IP, DB_PORT)))
                    .codecRegistry(pojoCodecRegistry)
                    .build();

            // Create the client with the configured settings
            mongoClient = MongoClients.create(settings);
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