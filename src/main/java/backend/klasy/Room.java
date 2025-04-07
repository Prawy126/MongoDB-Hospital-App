package backend.klasy;

import backend.mongo.TypeOfRoom;

import java.util.ArrayList;

public class Room {
    String adress;
    int floor;
    int number;
    int maxPatients;
    int currentPatients;
    ArrayList<Patient> patients = null;
    TypeOfRoom type;

    public Room(String adress, int floor, int number, int maxPatients, int currentPatients){
        this.adress = adress;
        this.floor = floor;
        this.number = number;
        this.maxPatients = maxPatients;
        this.currentPatients = currentPatients;
    }

    public String getAdress(){
        return adress;
    }

    public int getFloor(){
        return floor;
    }

    public int getNumber(){
        return number;
    }

    public int getMaxPatients(){
        return maxPatients;
    }

    public int getCurrentPatients(){
        return currentPatients;
    }

    public void setAdress(String adress){
        this.adress = adress;
    }

    public void setFloor(int floor){
        this.floor = floor;
    }

    public void setNumber(int number){
        this.number = number;
    }

    public void setMaxPatients(int maxPatients){
        this.maxPatients = maxPatients;
    }

    public void setCurrentPatients(int currentPatients){
        this.currentPatients = currentPatients;
    }

    public void addPatient(Patient patient){
        patients.add(patient);
    }

    public void removePatient(Patient patient){
        patients.remove(patient);
    }

    public ArrayList<Patient> getPatients(){
        return patients;
    }

    public void setPatients(ArrayList<Patient> patients){
        this.patients = patients;
    }

    public TypeOfRoom getType() {
        return type;
    }

    public void setType(TypeOfRoom type) {
        this.type = type;
    }

}
