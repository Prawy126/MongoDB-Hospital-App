package backend.klasy;

import backend.status.TypeOfRoom;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;

/**
 * Klasa {@code Room} reprezentuje pokój w placówce medycznej.
 * Zawiera informacje o lokalizacji, piętrze, numerze, typie oraz o pacjentach przypisanych do pokoju.
 */
public class Room {

    @BsonId
    private ObjectId id = new ObjectId();

    private String address;
    private int floor;
    private int number;
    private int maxPatients;
    private List<ObjectId> patientIds;
    private TypeOfRoom type;

    /**
     * Konstruktor tworzący pokój z podanymi parametrami i pustą listą pacjentów.
     *
     * @param address     adres budynku
     * @param floor       piętro
     * @param number      numer pokoju
     * @param maxPatients maksymalna liczba pacjentów
     * @param type        typ pokoju
     * @throws IllegalArgumentException jeśli któryś z parametrów jest nieprawidłowy
     */
    public Room(String address, int floor, int number, int maxPatients, TypeOfRoom type) {
        validateRoomData(address, number, maxPatients, type);
        this.address = address;
        this.floor = floor;
        this.number = number;
        this.maxPatients = maxPatients;
        this.type = type;
        this.patientIds = new ArrayList<>();
    }

    /**
     * Konstruktor tworzący pokój z podaną listą pacjentów.
     *
     * @param address     adres budynku
     * @param floor       piętro
     * @param number      numer pokoju
     * @param maxPatients maksymalna liczba pacjentów
     * @param type        typ pokoju
     * @param patientIds  lista ID pacjentów przypisanych do pokoju
     * @throws IllegalArgumentException jeśli któryś z parametrów jest nieprawidłowy
     */
    public Room(String address, int floor, int number, int maxPatients, TypeOfRoom type, List<ObjectId> patientIds) {
        validateRoomData(address, number, maxPatients, type);
        if (patientIds != null && patientIds.size() > maxPatients) {
            throw new IllegalArgumentException("Liczba pacjentów przekracza maksymalną pojemność sali");
        }
        this.address = address;
        this.floor = floor;
        this.number = number;
        this.maxPatients = maxPatients;
        this.type = type;
        this.patientIds = patientIds != null ? new ArrayList<>(patientIds) : new ArrayList<>();
    }

    /**
     * Domyślny konstruktor. Tworzy pustą listę pacjentów.
     */
    public Room() {
        this.patientIds = new ArrayList<>();
    }

    /**
     * Waliduje dane sali.
     *
     * @param address     adres budynku
     * @param number      numer pokoju
     * @param maxPatients maksymalna liczba pacjentów
     * @param type        typ pokoju
     * @throws IllegalArgumentException jeśli któryś z parametrów jest nieprawidłowy
     */
    private void validateRoomData(String address, int number, int maxPatients, TypeOfRoom type) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Adres budynku nie może być pusty");
        }
        if (number <= 0) {
            throw new IllegalArgumentException("Numer sali musi być liczbą dodatnią");
        }
        if (maxPatients <= 0) {
            throw new IllegalArgumentException("Maksymalna liczba pacjentów musi być większa od zera");
        }
        if (type == null) {
            throw new IllegalArgumentException("Typ sali nie może być pusty");
        }
    }

    public String getAddress() {
        return address;
    }

    /**
     * Ustawia adres budynku.
     *
     * @param address adres budynku
     * @throws IllegalArgumentException jeśli adres jest pusty
     */
    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Adres budynku nie może być pusty");
        }
        this.address = address;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getNumber() {
        return number;
    }

    /**
     * Ustawia numer sali.
     *
     * @param number numer sali
     * @throws IllegalArgumentException jeśli numer jest mniejszy lub równy zero
     */
    public void setNumber(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Numer sali musi być liczbą dodatnią");
        }
        this.number = number;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getMaxPatients() {
        return maxPatients;
    }

    /**
     * Ustawia maksymalną liczbę pacjentów. Nie może być mniejsza niż aktualna liczba pacjentów.
     *
     * @param maxPatients maksymalna liczba pacjentów
     * @throws IllegalArgumentException jeśli nowy limit jest mniejszy niż aktualna liczba pacjentów lub mniejszy od 1
     */
    public void setMaxPatients(int maxPatients) {
        if (maxPatients <= 0) {
            throw new IllegalArgumentException("Maksymalna liczba pacjentów musi być większa od zera");
        }
        if (maxPatients < patientIds.size()) {
            throw new IllegalArgumentException("Limit mniejszy niż aktualna liczba pacjentów (" + patientIds.size() + ")");
        }
        this.maxPatients = maxPatients;
    }

    /**
     * Zwraca listę identyfikatorów pacjentów przypisanych do pokoju.
     */
    public List<ObjectId> getPatientIds() {
        return new ArrayList<>(patientIds);
    }

    /**
     * Ustawia listę pacjentów w pokoju.
     *
     * @param patientIds nowa lista pacjentów
     * @throws IllegalArgumentException jeśli liczba pacjentów przekracza maksymalną pojemność
     */
    public void setPatientIds(List<ObjectId> patientIds) {
        if (patientIds == null) {
            this.patientIds = new ArrayList<>();
            return;
        }
        if (patientIds.size() > maxPatients) {
            throw new IllegalArgumentException("Liczba pacjentów (" + patientIds.size() +
                    ") przekracza limit (" + maxPatients + ")");
        }
        this.patientIds = new ArrayList<>(patientIds);
    }

    /**
     * Zwraca aktualną liczbę pacjentów w pokoju.
     */
    public int getCurrentPatientCount() {
        return patientIds.size();
    }

    /**
     * Sprawdza, czy pokój jest pełny.
     *
     * @return {@code true} jeśli liczba pacjentów osiąga maksymalną pojemność, w przeciwnym razie {@code false}
     */
    public boolean isFull() {
        return getCurrentPatientCount() >= maxPatients;
    }

    /**
     * Dodaje pacjenta do pokoju.
     *
     * @param patientId identyfikator pacjenta
     * @throws IllegalStateException jeśli pokój jest już pełny
     */
    public void addPatientId(ObjectId patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("ID pacjenta nie może być null");
        }
        if (isFull()) {
            throw new IllegalStateException("Pokój jest pełny (limit: " + maxPatients + ")");
        }
        this.patientIds.add(patientId);
    }

    /**
     * Usuwa pacjenta z pokoju.
     *
     * @param patientId identyfikator pacjenta
     * @return true jeśli pacjent został usunięty, false jeśli nie znaleziono pacjenta
     */
    public boolean removePatientId(ObjectId patientId) {
        if (patientId == null) {
            return false;
        }
        return this.patientIds.remove(patientId);
    }

    public TypeOfRoom getType() {
        return type;
    }

    /**
     * Ustawia typ sali.
     *
     * @param type typ sali
     * @throws IllegalArgumentException jeśli typ jest null
     */
    public void setType(TypeOfRoom type) {
        if (type == null) {
            throw new IllegalArgumentException("Typ sali nie może być pusty");
        }
        this.type = type;
    }

    /**
     * Zwraca tekstową reprezentację pokoju, zawierającą numer, adres, liczbę pacjentów i typ.
     */
    @Override
    public String toString() {
        return String.format("Pokój %d (%s) - %d/%d miejsc, oddział: %s",
                number, address, getCurrentPatientCount(), maxPatients, type.getDescription());
    }

    /**
     * Zwraca uproszczoną reprezentację pokoju w formacie "numer – adres".
     */
    public String toString2() {
        return number + " – " + address;
    }
}