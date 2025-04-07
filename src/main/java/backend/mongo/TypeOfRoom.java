package backend.mongo;

import backend.klasy.Patient;

public enum TypeOfRoom {
    PEDIATRIC("Dziecięcy"),
    MATERNITY("Położniczy"),
    INTERNAL("Wewnętrzny"),
    SURGICAL("Chirurgiczny"),
    NEUROLOGY("Neurologiczny"),
    CARDIOLOGY("Kardiologiczny");

    private final String description;

    TypeOfRoom(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // Metoda do automatycznego wyznaczania oddziału na podstawie danych pacjenta
    public static TypeOfRoom determineDepartment(Patient patient) {
        if (patient.getAge() < 18) {
            return PEDIATRIC;
        }

        //Musimy się zastanowić jak zarządzać podziałem reszty pacjentów

        return INTERNAL;
    }
}