package backend.mongo;

import backend.klasy.Room;
import backend.klasy.Patient;
import backend.klasy.Doctor;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class RoomRepository {
    private final MongoCollection<Room> collection;
    private  CodecRegistry codecRegistry;
    private List<Room> rooms;

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



    public Room createRoom(Room room) {
        try {
            collection.insertOne(room);
            return room;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas tworzenia sali: " + e.getMessage(), e);
        }
    }

    public Room findByAddressFloorNumber(String address, int floor, int number) {
        return collection.find(
                and(
                        eq("address", address),
                        eq("floor", floor),
                        eq("number", number)
                )
        ).first();
    }

    public Room updateRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Sala nie może być pusta");
        }

        try {
            Bson filter = and(
                    eq("address", room.getAddress()),
                    eq("floor", room.getFloor()),
                    eq("number", room.getNumber())
            );

            UpdateResult result = collection.replaceOne(filter, room);

            if (result.getModifiedCount() == 0) {
                throw new RuntimeException("Nie znaleziono sali do aktualizacji");
            }

            return room;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas aktualizacji sali: " + e.getMessage(), e);
        }
    }

    public boolean deleteRoom(String address, int floor, int number) {
        try {
            DeleteResult result = collection.deleteOne(
                    and(
                            eq("address", address),
                            eq("floor", floor),
                            eq("number", number)
                    )
            );
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas usuwania sali: " + e.getMessage(), e);
        }
    }

    public List<Room> getAllRooms() {
        try {
            return collection.find().into(new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas pobierania sal: " + e.getMessage(), e);
        }
    }

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
        return room.getCurrentPatients() < room.getMaxPatients();
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
}