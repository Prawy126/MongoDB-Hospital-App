package backend;

import com.mongodb.client.MongoDatabase;

public class Main {

    public static void main(String[] args) {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();
        if (database != null) {
            System.out.println("Połączono z bazą danych: " + database.getName());
        }
    }
}
