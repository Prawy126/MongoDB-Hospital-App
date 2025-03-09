package backend;
import com.mongodb.MongoClientSettings;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDatabaseConnector {
    private static final String DB_IP = "192.168.8.102";
    private static final int DB_PORT = 27017;
    private static final String DB_NAME = "hospitalDB";

    private static MongoClient mongoClient;
    private static com.mongodb.ConnectionString connectionString = new com.mongodb.ConnectionString("mongodb://" + DB_IP + ":" + DB_PORT);

    public static MongoDatabase connectToDatabase() {
        if (mongoClient == null) {
            // Konfiguracja codec'ów dla POJO
            CodecRegistry pojoCodecRegistry = fromProviders(
                    PojoCodecProvider.builder()
                            .automatic(true)
                            .build()
            );

            // Połączenie z bazą danych
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyConnectionString(connectionString)
                            .codecRegistry(fromRegistries(
                                    MongoClientSettings.getDefaultCodecRegistry(),
                                    pojoCodecRegistry
                            ))
                            .build()
            );
        }

        return mongoClient.getDatabase("hospitalDB");
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