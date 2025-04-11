package backend.klasy;

import backend.status.TypeOfRoom;

import java.util.ArrayList;

/**
 * Klasa reprezentująca pokój w placówce medycznej.
 */
public class Room {

    private String address;
    private int floor;
    private int number;
    private int maxPatients;
    private int currentPatients;
    private ArrayList<Patient> patients;
    private TypeOfRoom type;

    /**
     * Konstruktor z pełnym zestawem parametrów.
     *
     * @param address         Adres pokoju
     * @param floor           Piętro
     * @param number          Numer pokoju
     * @param maxPatients     Maksymalna liczba pacjentów
     * @param currentPatients Aktualna liczba pacjentów
     */
    public Room(String address, int floor, int number, int maxPatients, int currentPatients) {
        this.address = address;
        this.floor = floor;
        this.number = number;
        this.maxPatients = maxPatients;
        this.currentPatients = currentPatients;
        this.patients = new ArrayList<>();
    }

    /**
     * Domyślny konstruktor.
     */
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
        this.maxPatients = maxPatients;
    }

    public int getCurrentPatients() {
        return currentPatients;
    }

    public void setCurrentPatients(int currentPatients) {
        this.currentPatients = currentPatients;
    }

    /**
     * Dodaje pacjenta do pokoju.
     *
     * @param patient Pacjent do dodania
     */
    public void addPatient(Patient patient) {
        if (patients.size() < maxPatients) {
            patients.add(patient);
            currentPatients++;
        } else {
            throw new IllegalStateException("Pokój jest już pełny.");
        }
    }

    /**
     * Usuwa pacjenta z pokoju.
     *
     * @param patient Pacjent do usunięcia
     */
    public void removePatient(Patient patient) {
        if (patients.remove(patient)) {
            currentPatients--;
        }
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public void setPatients(ArrayList<Patient> patients) {
        this.patients = patients;
        this.currentPatients = patients != null ? patients.size() : 0;
    }

    public TypeOfRoom getType() {
        return type;
    }

    public void setType(TypeOfRoom type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return number + " - " + address;
    }
}
