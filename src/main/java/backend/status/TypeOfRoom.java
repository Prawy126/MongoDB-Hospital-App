package backend.status;

import backend.klasy.Patient;

/**
 * Enum dla rodzaju sal/odziałów w szpitalu
 * <p>Posiada aktualnie 6 możliwych wartości</p>
 * <p>PEDIATRIC</p>
 * <p>MATERNITY</p>
 * <p>INTERNAL</p>
 * <p>SURGICAL</p>
 * <p>NEUROLOGY</p>
 * <p>CARDIOLOGY</p>*/
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
            return TypeOfRoom.PEDIATRIC;
        }
        if (patient.getDiagnosis() != null) {
            return patient.getDiagnosis().getDepartment();
        }
        return TypeOfRoom.INTERNAL; // Domyślny oddział
    }
}