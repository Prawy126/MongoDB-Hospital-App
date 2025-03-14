package backend.mongo;

import backend.klasy.Nurse;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa NurseRepository zarządza operacjami CRUD dla kolekcji pielęgniarek w bazie danych MongoDB w sposób obiektowy.
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
     */
    public Nurse createNurse(Nurse nurse) {
        collection.insertOne(nurse);
        return nurse;
    }

    /**
     * Znajduje pielęgniarkę po jej ID.
     *
     * @param id ID pielęgniarki
     * @return znaleziona pielęgniarka lub null, jeśli nie znaleziono
     */
    public Nurse getNurseById(ObjectId id) {
        return collection.find(eq("_id", id)).first();
    }

    /**
     * Znajduje wszystkie pielęgniarki w bazie danych.
     *
     * @return lista wszystkich pielęgniarek
     */
    public List<Nurse> getAllNurses() {
        List<Nurse> nurses = new ArrayList<>();
        collection.find().into(nurses);
        return nurses;
    }

    /**
     * Aktualizuje dane pielęgniarki w bazie danych.
     *
     * @param id ID pielęgniarki do zaktualizowania
     * @param updatedNurse zaktualizowane dane pielęgniarki
     */
    public void updateNurse(ObjectId id, Nurse updatedNurse) {
        Bson filter = eq("_id", id);
        collection.replaceOne(filter, updatedNurse);
    }

    /**
     * Usuwa pielęgniarkę po jej ID.
     *
     * @param id ID pielęgniarki do usunięcia
     */
    public void deleteNurse(ObjectId id) {
        Bson filter = eq("_id", id);
        collection.deleteOne(filter);
    }

    /**
     * Znajduje pielęgniarki po ich specjalizacji.
     *
     * @param specialization specjalizacja pielęgniarek
     * @return lista pielęgniarek o podanej specjalizacji
     */
    public List<Nurse> findNursesBySpecialization(String specialization) {
        List<Nurse> nurses = new ArrayList<>();
        collection.find(eq("specialization", specialization)).into(nurses);
        return nurses;
    }
}