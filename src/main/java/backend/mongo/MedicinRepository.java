package backend.mongo;

import backend.klasy.Medicin;
import backend.status.Type;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Klasa MedicinRepository zarządza operacjami CRUD dla kolekcji leków w bazie MongoDB.
 */
public class MedicinRepository {
    private final MongoCollection<Medicin> collection;

    public MedicinRepository(MongoDatabase database) {
        this.collection = database.getCollection("leki", Medicin.class);
    }

    // Dodawanie nowego leku
    public Medicin addMedicin(Medicin medicin) {
        collection.insertOne(medicin);
        return medicin;
    }

    // Wyszukiwanie leków po nazwie i dawce
    public List<Medicin> findMedicin(String name, String dose) {
        Bson filter = Filters.and(
                eq("name", name),
                eq("dose", dose)
        );
        return collection.find(filter).into(new ArrayList<>());
    }

    // Pobranie wszystkich leków
    public List<Medicin> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    // Aktualizacja danych leku
    public Medicin updateMedicin(Medicin medicin) {
        collection.replaceOne(eq("_id", medicin.getId()), medicin);
        return medicin;
    }

    // Usuwanie leku po ID
    public void deleteMedicin(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    // Testowanie metod repozytorium leków
    public void testMedicin() {
        System.out.println("\n=== Rozpoczynam testowanie MedicinRepository ===");

        try {
            // Dodawanie nowego leku
            Medicin testMedicin = new Medicin(
                    "Paracetamol",
                    Type.tablets,
                    500,
                    List.of("Brak"),
                    false
            );

            Medicin createdMedicin = addMedicin(testMedicin);
            System.out.println("[OK] Dodano lek: " + createdMedicin.getName() + " " + createdMedicin.getDose());

            // Wyszukiwanie leku po nazwie i dawce
            List<Medicin> foundMedicin = findMedicin("Paracetamol", "500mg");
            System.out.println("[OK] Wyszukano lek 'Paracetamol 500mg': " + foundMedicin.size());

            // Pobranie wszystkich leków
            List<Medicin> allMedicins = findAll();
            System.out.println("[OK] Liczba wszystkich leków w bazie: " + allMedicins.size());

            // Aktualizacja leku
            createdMedicin. setDose(650);
            Medicin updatedMedicin = updateMedicin(createdMedicin);
            System.out.println("[OK] Zaktualizowano dawkę leku: " + updatedMedicin.getDose());

            // Usunięcie leku
            deleteMedicin(createdMedicin.getId());
            System.out.println("[OK] Usunięto lek o ID: " + createdMedicin.getId());

            System.out.println("[SUCCESS] Wszystkie testy dla MedicinRepository zakończone pomyślnie!");

        } catch (Exception e) {
            System.err.println("[ERROR] Wystąpił błąd podczas testowania MedicinRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
