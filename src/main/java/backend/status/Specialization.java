package backend.status;

/**
 * Enum dla specjalizacji lekarzy
 */
public enum Specialization {
    CARDIOLOGIST("Kardiolog", TypeOfRoom.CARDIOLOGY),
    NEUROLOGIST("Neurolog", TypeOfRoom.NEUROLOGY),
    ORTHOPEDIST("Ortopeda", TypeOfRoom.SURGICAL),
    DERMATOLOGIST("Dermatolog", TypeOfRoom.INTERNAL),
    GYNECOLOGIST("Ginekolog", TypeOfRoom.MATERNITY),
    PEDIATRICIAN("Pediatra", TypeOfRoom.PEDIATRIC),
    SURGEON("Chirurg", TypeOfRoom.SURGICAL),
    INTERNIST("Internista", TypeOfRoom.INTERNAL),
    FIRST_CONTACT("Pierwszego kontaktu", TypeOfRoom.ADMISSION);

    private final String description;
    private final TypeOfRoom compatibleRoomType;

    Specialization(String description, TypeOfRoom compatibleRoomType) {
        this.description = description;
        this.compatibleRoomType = compatibleRoomType;
    }

    public String getDescription() {
        return description;
    }

    public TypeOfRoom getCompatibleRoomType() {
        return compatibleRoomType;
    }

    public static Specialization fromDescription(String description) {
        for (Specialization specialization : values()) {
            if (specialization.getDescription().equals(description)) {
                return specialization;
            }
        }
        throw new IllegalArgumentException("Nieznana specjalizacja: " + description);
    }
}