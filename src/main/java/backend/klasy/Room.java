package backend.klasy;

import backend.status.TypeOfRoom;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private String address;
    private int floor;
    private int number;
    private int maxPatients;
    private List<Patient> patients;
    private TypeOfRoom type;

    public Room(String address, int floor, int number, int maxPatients, TypeOfRoom type) {
        this.address = address;
        this.floor = floor;
        this.number = number;
        this.maxPatients = maxPatients;
        this.type = type;
        this.patients = new ArrayList<>();
    }

    public Room() {
        this.patients = new ArrayList<>();
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

    public int getMaxPatients() {
        return maxPatients;
    }

    public void setMaxPatients(int maxPatients) {
        if (maxPatients < patients.size()) {
            throw new IllegalArgumentException("Nowy limit nie może być mniejszy niż liczba aktualnych pacjentów");
        }
        this.maxPatients = maxPatients;
    }

    public int getCurrentPatients() {
        return patients.size();
    }

    public void addPatient(Patient patient) {
        if (patients.size() >= maxPatients) {
            throw new IllegalStateException("Pokój jest pełny");
        }
        TypeOfRoom requiredType = TypeOfRoom.determineDepartment(patient);
        if (requiredType != this.type) {
            throw new IllegalArgumentException("Pacjent nie pasuje do typu pokoju");
        }
        patients.add(patient);

    }

    public void removePatient(Patient patient) {
        patients.remove(patient);
    }

    public List<Patient> getPatients() {
        return new ArrayList<>(patients); // Zwraca kopię listy
    }

    public void setPatients(List<Patient> patients) {
        if (patients.size() > maxPatients) {
            throw new IllegalArgumentException("Liczba pacjentów przekracza limit");
        }
        for (Patient patient : patients) {
            TypeOfRoom requiredType = TypeOfRoom.determineDepartment(patient);
            if (requiredType != this.type) {
                throw new IllegalArgumentException("Niekompatybilny pacjent: " + patient);
            }
        }
        this.patients = new ArrayList<>(patients);
    }

    public TypeOfRoom getType() {
        return type;
    }

    public void setType(TypeOfRoom type) {
        for (Patient patient : patients) {
            TypeOfRoom requiredType = TypeOfRoom.determineDepartment(patient);
            if (requiredType != type) {
                throw new IllegalArgumentException("Nie można zmienić typu - pacjenci są niekompatybilni");
            }
        }
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("Pokój %d (%s) - %d/%d miejsc, oddział: %s",
                number, address, getCurrentPatients(), maxPatients, type.getDescription());
    }
}