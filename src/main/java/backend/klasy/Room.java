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
     */
    public Room(String address, int floor, int number, int maxPatients, TypeOfRoom type) {
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
     */
    public Room(String address, int floor, int number, int maxPatients, TypeOfRoom type, List<ObjectId> patientIds) {
        this.address = address;
        this.floor = floor;
        this.number = number;
        this.maxPatients = maxPatients;
        this.type = type;
        this.patientIds = patientIds;
    }

    /**
     * Domyślny konstruktor. Tworzy pustą listę pacjentów.
     */
    public Room() {
        this.patientIds = new ArrayList<>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
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

    public void setNumber(int number) {
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
     * @throws IllegalArgumentException jeśli nowy limit jest mniejszy niż aktualna liczba pacjentów
     */
    public void setMaxPatients(int maxPatients) {
        if (maxPatients < patientIds.size()) {
            throw new IllegalArgumentException("Limit mniejszy niż liczba pacjentów");
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
        if (patientIds.size() > maxPatients) {
            throw new IllegalArgumentException("Liczba pacjentów przekracza limit");
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
        if (isFull()) {
            throw new IllegalStateException("Pokój jest pełny");
        }
        this.patientIds.add(patientId);
    }

    public TypeOfRoom getType() {
        return type;
    }

    public void setType(TypeOfRoom type) {
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
