package backend.mongo;

import backend.klasy.Medicin;
import backend.status.Type;
import backend.wyjatki.DoseException;
import backend.wyjatki.NullNameException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

/**
 * Klasa MedicinRepository zarządza operacjami CRUD dla kolekcji leków w bazie MongoDB.
 * <p>
 * Metody tej klasy pozwalają na tworzenie, wyszukiwanie, aktualizowanie i usuwanie leków.
 * Klasa ta zapewnia również metody do testowania operacji na kolekcji leków.
 * </p>
 */
public class MedicinRepository {
    private final MongoCollection<Medicin> collection;

    /**
     * Konstruktor inicjalizujący kolekcję leków.
     *
     * @param database obiekt MongoDatabase reprezentujący połączenie z bazą danych
     */
    public MedicinRepository(MongoDatabase database) {
        this.collection = database.getCollection("medicins", Medicin.class);
    }

    /**
     * Dodaje nowy lek do bazy danych.
     *
     * @param medicin lek do dodania
     * @return dodany lek
     */
    public Medicin addMedicin(Medicin medicin) {
        collection.insertOne(medicin);
        return medicin;
    }

    /**
     * Znajduje lek po jego ID.
     *
     * @param id ID leku
     * @return Optional zawierający znaleziony lek lub pusty, jeśli nie znaleziono
     */
    public Optional<Medicin> findMedicinById(ObjectId id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    /**
     * Znajduje wszystkie leki w bazie danych.
     *
     * @return lista wszystkich leków
     */
    public List<Medicin> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    /**
     * Usuwa lek po jego ID.
     *
     * @param id ID leku do usunięcia
     */
    public void deleteMedicin(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    /**
     * Metoda testująca operacje na kolekcji leków.
     * <p>
     * Tworzy przykładowe leki, testuje wyjątki oraz operacje CRUD.
     * </p>
     */
    public void testMedicin() {
        System.out.println("\n=== Rozpoczynam testowanie MedicinRepository ===");

        try {
            Medicin testMedicin = new Medicin.Builder()
                    .name("Paracetamol")
                    .type(Type.tablets)
                    .dose(500.0)
                    .allergies(List.of("Brak"))
                    .requiresPrescription(false)
                    .build();

            Medicin createdMedicin = addMedicin(testMedicin);
            System.out.println("[OK] Dodano lek: " + createdMedicin);

            // Test dla niepoprawnej dawki
            try {
                Medicin invalidDoseMedicin = new Medicin.Builder()
                        .name("Test")
                        .type(Type.tablets)
                        .dose(-1.0)
                        .allergies(List.of("Brak"))
                        .requiresPrescription(false)
                        .build();
                addMedicin(invalidDoseMedicin);
                System.err.println("[FAIL] Powinien wystąpić DoseException dla ujemnej dawki.");
            } catch (DoseException e) {
                System.out.println("[OK] Poprawnie przechwycono DoseException: " + e.getMessage());
            }

            // Wyszukiwanie po ID
            Optional<Medicin> foundById = findMedicinById(createdMedicin.getId());
            if (foundById.isPresent()) {
                System.out.println("[OK] Wyszukano lek po ID: " + foundById.get());
            } else {
                System.err.println("[ERROR] Nie znaleziono leku o ID: " + createdMedicin.getId());
            }

            // Pobranie wszystkich leków
            List<Medicin> allMedicins = findAll();
            System.out.println("[OK] Liczba wszystkich leków w bazie: " + allMedicins.size());

            // Usuwanie leku
            deleteMedicin(createdMedicin.getId());
            System.out.println("[OK] Usunięto lek o ID: " + createdMedicin.getId());

            System.out.println("[SUCCESS] Wszystkie testy zakończone pomyślnie!");

        } catch (Exception e) {
            System.err.println("[ERROR] Wystąpił błąd podczas testowania MedicinRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}