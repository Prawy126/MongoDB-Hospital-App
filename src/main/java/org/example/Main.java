package org.example;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Main {
    public static void main(String[] args) {
        // Połączenie z lokalnym MongoDB (domyślnie na porcie 27017)
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // Pobranie bazy danych (jeśli nie istnieje, zostanie utworzona)
            MongoDatabase database = mongoClient.getDatabase("testdb");

            System.out.println("Połączono z bazą danych: " + database.getName());
        } catch (Exception e) {
            System.err.println("Błąd połączenia: " + e.getMessage());
        }
    }
}
