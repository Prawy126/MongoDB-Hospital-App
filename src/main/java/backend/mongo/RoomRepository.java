package backend.mongo;

import backend.klasy.Room;
import backend.klasy.Patient;
import backend.klasy.Doctor;
import backend.status.TypeOfRoom;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Klasa RoomRepository zarządza operacjami CRUD dla kolekcji sal w bazie MongoDB.
 * <p>
 * Metody tej klasy pozwalają na tworzenie, wyszukiwanie, aktualizowanie i usuwanie sal.
 * Klasa ta zapewnia również metody do testowania operacji na kolekcji sal.
 * </p>
 */
public class RoomRepository {
    private final MongoCollection<Room> collection;
    private  CodecRegistry codecRegistry;
    private List<Room> rooms;

    /**
     * Konstruktor inicjalizujący kolekcję sal.
     *
     * @param database obiekt MongoDatabase reprezentujący połączenie z bazą danych
     */
    public RoomRepository(MongoDatabase database) {
        // Tworzymy CodecRegistry z odpowiednim CodecProviderem dla naszych klas (Room, Patient itd.)
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder()
                        .register(Room.class)  // Rejestrujemy klasę Room
                        .register(Patient.class)  // Rejestrujemy klasę Patient
                        .register(Doctor.class)  // Rejestrujemy klasę Doctor
                        .build()));

        // Ustawiamy nasz CodecRegistry dla kolekcji rooms
        this.collection = database.getCollection("rooms", Room.class)
                .withCodecRegistry(pojoCodecRegistry);
    }


    /**
     * Tworzy nową salę w bazie danych.
     *
     * @param room sala do utworzenia
     * @throws IllegalArgumentException jeśli sala jest null
     */
    public void createRoom(Room room) {
        try {
            collection.insertOne(room);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas tworzenia sali: " + e.getMessage(), e);
        }
    }

    /**
     * Znajduje salę po jej ID.
     *
     * @param address adres sali
     * @param floor piętro sali
     * @param number numer sali
     * @return Optional zawierający znalezioną salę lub pusty, jeśli nie znaleziono
     */
    public Room findByAddressFloorNumber(String address, int floor, int number) {
        return collection.find(
                and(
                        eq("address", address),
                        eq("floor", floor),
                        eq("number", number)
                )
        ).first();
    }

    /**
     * Znajduje salę po jej ID.
     *
     * @param roomId ID sali
     * @param updatedRoom zaktualizowane dane sali
     * @return Optional zawierający znalezioną salę lub pusty, jeśli nie znaleziono
     */
    public Room updateRoom(ObjectId roomId, Room updatedRoom) {
        if (roomId == null)
            throw new IllegalArgumentException("Id sali jest puste");

        if (updatedRoom == null)
            throw new IllegalArgumentException("Dane aktualizowanej sali są puste");

        updatedRoom.setId(roomId);

        UpdateResult r = collection.replaceOne(eq("_id", roomId), updatedRoom);

        if (r.getMatchedCount() == 0)
            throw new RuntimeException("Nie znaleziono sali o id: " + roomId.toString());

        return updatedRoom;
    }

    /**
     * Znajduje salę po jej ID.
     *
     * @param id ID sali
     * @return Optional zawierający znalezioną salę lub pusty, jeśli nie znaleziono
     */
    public boolean deleteRoom(ObjectId id) {
        if (id == null) throw new IllegalArgumentException("Brak id sali");

        return collection.deleteOne(eq("_id", id)).getDeletedCount() > 0;
    }

    /**
     * Znajduje wszystkie sale w bazie danych.
     *
     * @return lista wszystkich sal
     */
    public List<Room> getAllRooms() {
        try {
            return collection.find().into(new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas pobierania sal: " + e.getMessage(), e);
        }
    }

    // musimy się zastanowić czy ten konstruktor jest potrzebny czy nie jest zbędny
    /**
     * Znajduje sale po ich adresie.
     *
     * @param collection sale
     * @param codecRegistry rejestrator kodów
     * @param rooms lista sal
     * @return lista sal o podanym adresie
     */
    public RoomRepository(MongoCollection<Room> collection, CodecRegistry codecRegistry, List<Room> rooms) {
        this.collection = collection;
        this.codecRegistry = codecRegistry;
        this.rooms = rooms;
    }

    /**
     * Funkcja ta sprawdza czy dany pokój nie jest pełny
     * @param room konkretny pokój
     * @return zwraca treu jeśli pokój jest pełny lub false jeśli w pokoju są jeszcze miejsca*/
    public boolean isRoomNotFull(Room room) {
        return room.getCurrentPatientCount() < room.getMaxPatients();
    }

    /**
     * Funkcja ta zwraca listę pokoi które nie są pełne
     * @return zwraca listę pokoi które nie są pełne *
     * */
    public List<Room> findNotFullRooms() {
        return rooms.stream()
                .filter(this::isRoomNotFull)
                .collect(Collectors.toList());
    }

    public List<Room> findRoomByType(TypeOfRoom type) {
        return collection.find(eq("type", type)).into(new ArrayList<>());
    }



    public List<Room> findRoomsById(ObjectId id) {
        return collection.find(eq("_id", id)).into(new ArrayList<>());
    }
    /**
        Znajduje wszystkie pokoje przypisane do danego oddziału (departamentu).*,
        @param department typ oddziału (np. TypeOfRoom.CARDIOLOGY),
        @return lista pokoi przypisanych do tego oddziału
    */

    public List<Room> findRoomsByDepartment(TypeOfRoom department) {
        return collection.find(eq("type", department)).into(new ArrayList<>());
    }
}