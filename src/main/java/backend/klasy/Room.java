package backend.klasy;

import backend.status.TypeOfRoom;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private String address;
    private int floor;
    private int number;
    private int maxPatients;
    private List patientIds;
    private TypeOfRoom type;

    public Room(String address, int floor, int number, int maxPatients, TypeOfRoom type) {
        this.address = address;
        this.floor = floor;
        this.number = number;
        this.maxPatients = maxPatients;
        this.type = type;
        this.patientIds = new ArrayList<>();
    }

    public Room(String address, int floor, int number, int maxPatients, TypeOfRoom type, List patientIds) {
        this.address = address;
        this.floor = floor;
        this.number = number;
        this.maxPatients = maxPatients;
        this.type = type;
        this.patientIds = patientIds;
    }

    public Room() {
        this.patientIds = new ArrayList<>();
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public int getMaxPatients() { return maxPatients; }
    public void setMaxPatients(int maxPatients) {
        if (maxPatients < patientIds.size()) {
            throw new IllegalArgumentException("Limit mniejszy niż liczba pacjentów");
        }
        this.maxPatients = maxPatients;
    }

    public List<ObjectId> getPatientIds() {
        return new ArrayList<>(patientIds);
    }

    public int getCurrentPatients(){
        return patientIds.size();
    }

    public void setPatientIds(List<ObjectId> patientIds) {
        if (patientIds.size() > maxPatients) {
            throw new IllegalArgumentException("Liczba pacjentów przekracza limit");
        }
        this.patientIds = new ArrayList<>(patientIds);
    }

    public int getCurrentPatientCount() {
        return patientIds.size();
    }

    public boolean isFull() {
        return getCurrentPatientCount() >= maxPatients;
    }

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

    @Override
    public String toString() {
        return String.format("Pokój %d (%s) - %d/%d miejsc, oddział: %s",
                number, address, getCurrentPatientCount(), maxPatients, type.getDescription());
    }

}