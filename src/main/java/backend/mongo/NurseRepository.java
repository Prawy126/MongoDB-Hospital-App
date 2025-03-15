package backend.mongo;

import backend.klasy.Nurse;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa NurseRepository zarządza operacjami CRUD dla kolekcji pielęgniarek w bazie danych MongoDB w sposób obiektowy.
 */
public class NurseRepository {
    private final MongoCollection<Nurse> collection;

    public NurseRepository(MongoDatabase database) {
        this.collection = database.getCollection("nurses", Nurse.class);
    }

    public Nurse createNurse(Nurse nurse) {
        collection.insertOne(nurse);
        return nurse;
    }

    public Nurse getNurseById(ObjectId id) {
        return collection.find(eq("_id", id)).first();
    }

    public List<Nurse> getAllNurses() {
        return collection.find().into(new ArrayList<>());
    }

    public void updateNurse(ObjectId id, Nurse updatedNurse) {
        collection.replaceOne(eq("_id", id), updatedNurse);
    }

    public void deleteNurse(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<Nurse> findNursesBySpecialization(String specialization) {
        return collection.find(eq("specialization", specialization)).into(new ArrayList<>());
    }

    /**
     * Metoda testująca działanie metod klasy NurseRepository.
     */
    public void testNurse() {
        System.out.println("Rozpoczynam testowanie NurseRepository...");

        // Tworzenie pielęgniarki
        Nurse nurse = null;
        try {
            nurse = new Nurse("Anna", "Kowalska", 12345678901L, 30, "Pediatria");
        } catch (PeselException | NullNameException | AgeException e) {
            System.out.println("[ERROR] Błąd podczas tworzenia pielęgniarki: " + e.getMessage());
        }

        if (nurse != null) {
            createNurse(nurse);
            System.out.println("[OK] Utworzono pielęgniarkę: " + nurse);

            // Pobieranie wszystkich pielęgniarek
            List<Nurse> nurses = getAllNurses();
            System.out.println("[OK] Lista wszystkich pielęgniarek: " + nurses);

            // Pobieranie pielęgniarki po ID
            ObjectId nurseId = nurses.get(0).getId();
            Nurse foundNurse = getNurseById(nurseId);
            System.out.println("[OK] Znaleziono pielęgniarkę po ID: " + foundNurse);

            // Aktualizacja pielęgniarki
            foundNurse.setSpecialization("Chirurgia");
            updateNurse(nurseId, foundNurse);
            System.out.println("[OK] Zaktualizowano pielęgniarkę: " + getNurseById(nurseId));

            // Wyszukiwanie pielęgniarek po specjalizacji
            List<Nurse> specializedNurses = findNursesBySpecialization("Chirurgia");
            System.out.println("[OK] Pielęgniarki o specjalizacji Chirurgia: " + specializedNurses);

            // Usuwanie pielęgniarki
            deleteNurse(nurseId);
            System.out.println("[OK] Usunięto pielęgniarkę o ID: " + nurseId);
        } else {
            System.out.println("[ERROR] Nie udało się utworzyć pielęgniarki.");
        }

        System.out.println("[SUCCESS] Testowanie NurseRepository zakończone.");
    }
}