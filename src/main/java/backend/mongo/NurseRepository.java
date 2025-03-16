package backend.mongo;

import backend.klasy.Nurse;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

/**
 * Klasa NurseRepository zarządza operacjami CRUD dla kolekcji pielęgniarek w bazie danych MongoDB.
 * <p>
 * Metody tej klasy pozwalają na tworzenie, wyszukiwanie, aktualizowanie i usuwanie pielęgniarek.
 * Klasa ta zapewnia również metody do testowania operacji na kolekcji pielęgniarek.
 * </p>
 */
public class NurseRepository {
    private final MongoCollection<Nurse> collection;

    /**
     * Konstruktor inicjalizujący kolekcję pielęgniarek.
     *
     * @param database obiekt MongoDatabase reprezentujący połączenie z bazą danych
     */
    public NurseRepository(MongoDatabase database) {
        this.collection = database.getCollection("nurses", Nurse.class);
    }

    /**
     * Tworzy nową pielęgniarkę w bazie danych.
     *
     * @param nurse pielęgniarka do utworzenia
     * @return utworzona pielęgniarka
     * @throws IllegalArgumentException jeśli pielęgniarka jest null
     */
    public Nurse createNurse(Nurse nurse) {
        if (nurse == null) {
            throw new IllegalArgumentException("Nurse cannot be null");
        }
        collection.insertOne(nurse);
        return nurse;
    }

    /**
     * Znajduje pielęgniarkę po jej ID.
     *
     * @param id ID pielęgniarki
     * @return Optional zawierający znalezioną pielęgniarkę lub pusty, jeśli nie znaleziono
     */
    public Optional<Nurse> findNurseById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    /**
     * Znajduje wszystkie pielęgniarki w bazie danych.
     *
     * @return lista wszystkich pielęgniarek
     */
    public List<Nurse> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    /**
     * Aktualizuje dane pielęgniarki w bazie danych.
     *
     * @param nurse pielęgniarka do zaktualizowania
     * @return zaktualizowana pielęgniarka
     */
    public Nurse updateNurse(Nurse nurse) {
        collection.replaceOne(eq("_id", nurse.getId()), nurse);
        return nurse;
    }

    /**
     * Usuwa pielęgniarkę po jej ID.
     *
     * @param id ID pielęgniarki do usunięcia
     */
    public void deleteNurse(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    /**
     * Metoda testująca operacje na kolekcji pielęgniarek.
     * <p>
     * Tworzy przykładowe pielęgniarki, testuje wyjątki oraz operacje CRUD.
     * </p>
     */
    public void testNurse() {
        System.out.println("\n=== Rozpoczynam testowanie NurseRepository ===");

        try {
            // Tworzenie pielęgniarki
            Nurse testNurse = new Nurse.Builder()
                    .firstName("Anna")
                    .lastName("Kowalska")
                    .specialization("Pediatria")
                    .pesel(12345678901L)
                    .age(30)
                    .build();

            Nurse createdNurse = createNurse(testNurse);
            System.out.println("[OK] Utworzono pielęgniarkę: " + createdNurse);

            // Testowanie wyjątku AgeException
            try {
                new Nurse.Builder()
                        .firstName("Test")
                        .lastName("Nurse")
                        .specialization("Pediatria")
                        .pesel(11122233311L)
                        .age(19) // Wiek poniżej 20
                        .build();
                System.err.println("[FAIL] Powinien wystąpić AgeException dla wieku < 20");
            } catch (AgeException e) {
                System.out.println("[OK] Poprawnie przechwycono AgeException: " + e.getMessage());
            }

            // Testowanie wyjątku PeselException
            try {
                new Nurse.Builder()
                        .firstName("Test")
                        .lastName("Nurse")
                        .specialization("Pediatria")
                        .pesel(999) // Nieprawidłowy PESEL
                        .age(25)
                        .build();
                System.err.println("[FAIL] Powinien wystąpić PeselException");
            } catch (PeselException e) {
                System.out.println("[OK] Poprawnie przechwycono PeselException: " + e.getMessage());
            }

            // Testowanie wyjątku NullNameException
            try {
                new Nurse.Builder()
                        .firstName(null) // Puste imię
                        .lastName("Nurse")
                        .specialization("Pediatria")
                        .pesel(11122233311L)
                        .age(25)
                        .build();
                System.err.println("[FAIL] Powinien wystąpić NullNameException");
            } catch (NullNameException e) {
                System.out.println("[OK] Poprawnie przechwycono NullNameException: " + e.getMessage());
            }

            // Wyszukiwanie pielęgniarki po ID
            Optional<Nurse> foundNurse = findNurseById(createdNurse.getId());
            if (foundNurse.isPresent()) {
                System.out.println("[OK] Wyszukano pielęgniarkę po ID: " + foundNurse.get());

                // **Test aktualizacji danych pielęgniarki**
                foundNurse.get().setSpecialization("Chirurgia");
                Nurse updatedNurse = updateNurse(foundNurse.get());
                System.out.println("[OK] Zaktualizowano pielęgniarkę: " + updatedNurse);

                // Weryfikacja aktualizacji
                Optional<Nurse> verifiedNurse = findNurseById(updatedNurse.getId());
                if (verifiedNurse.isPresent() && "Chirurgia".equals(verifiedNurse.get().getSpecialization())) {
                    System.out.println("[OK] Specjalizacja pielęgniarki została poprawnie zaktualizowana.");
                } else {
                    System.err.println("[FAIL] Specjalizacja pielęgniarki NIE została poprawnie zaktualizowana.");
                }
            } else {
                System.err.println("[ERROR] Nie znaleziono pielęgniarki po ID.");
            }

            // Usunięcie pielęgniarki
            deleteNurse(createdNurse.getId());
            System.out.println("[OK] Usunięto pielęgniarkę");

        } catch (Exception e) {
            System.err.println("[ERROR] Błąd podczas testowania NurseRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}