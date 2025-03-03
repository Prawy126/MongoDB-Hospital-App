package org.example;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;

public class Main {
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017")) {
            MongoDatabase database = mongoClient.getDatabase("testdb");

            // Wymuś komunikację z serwerem (np. pobierz nazwy baz danych)
            mongoClient.listDatabaseNames().first(); // Sprawdza połączenie

            System.out.println("Połączono z bazą danych: " + database.getName());
        } catch (MongoException e) {
            System.err.println("Błąd połączenia: " + e.getMessage());
        }
    }
}