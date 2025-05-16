package backend.status;

import backend.status.TypeOfRoom;

public enum Specialization {
    CARDIOLOGY("Kardiologia"),
    NEUROLOGY("Neurologia"),
    PEDIATRICS("Pediatria"),
    SURGERY("Chirurgia"),
    INTERNAL_MEDICINE("Medycyna wewnętrzna"),
    FIRST_CONTACT("Lekarz pierwszego kontaktu");

    private final String description;

    Specialization(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Specialization fromDescription(String description) {
        for (Specialization spec : values()) {
            if (spec.getDescription().equals(description)) {
                return spec;
            }
        }
        throw new IllegalArgumentException("Nie znaleziono specjalizacji o opisie: " + description);
    }

    public TypeOfRoom getCompatibleRoomType() {
        TypeOfRoom result;
        switch (this) {
            case CARDIOLOGY:
                result = TypeOfRoom.CARDIOLOGY;
                break;
            case NEUROLOGY:
                result = TypeOfRoom.NEUROLOGY;
                break;
            case PEDIATRICS:
                result = TypeOfRoom.PEDIATRIC;
                break;
            case SURGERY:
                result = TypeOfRoom.SURGICAL;
                break;
            case INTERNAL_MEDICINE:
                result = TypeOfRoom.INTERNAL;
                break;
            case FIRST_CONTACT:
                result = TypeOfRoom.ADMISSION;
                break;
            default:
                result = TypeOfRoom.INTERNAL;
                break;
        }
        System.out.println("Dla specjalizacji " + this.getDescription() + " kompatybilny typ sali to: " + result.getDescription());
        return result;
    }
}