package backend.status;

/**
 * Enum reprezentujący specjalizacje lekarskie.
 * Każda specjalizacja posiada opis tekstowy oraz odpowiadający jej typ sali.
 */
public enum Specialization {

    CARDIOLOGY("Kardiolog"),
    NEUROLOGY("Neurolog"),
    PEDIATRICS("Pediatra"),
    SURGERY("Chirurg"),
    FIRST_CONTACT("pierwszego kontaktu");

    /**
     * Opis specjalizacji w języku polskim.
     */
    private final String description;

    /**
     * Konstruktor przypisujący opis specjalizacji.
     *
     * @param description opis specjalizacji
     */
    Specialization(String description) {
        this.description = description;
    }

    /**
     * Zwraca opis specjalizacji.
     *
     * @return opis w języku polskim
     */
    public String getDescription() {
        return description;
    }

    /**
     * Zwraca specjalizację na podstawie opisu tekstowego.
     *
     * @param description opis specjalizacji
     * @return specjalizacja pasująca do podanego opisu
     * @throws IllegalArgumentException jeśli nie znaleziono pasującej specjalizacji
     */
    public static Specialization fromDescription(String description) {
        for (Specialization spec : values()) {
            if (spec.getDescription().equals(description)) {
                return spec;
            }
        }
        throw new IllegalArgumentException("Nie znaleziono specjalizacji o opisie: " + description);
    }

    /**
     * Zwraca kompatybilny typ sali dla danej specjalizacji.
     *
     * @return typ sali zgodny z wymaganiami specjalizacji
     */
    public TypeOfRoom getCompatibleRoomType() {
        return switch (this) {
            case CARDIOLOGY -> TypeOfRoom.CARDIOLOGY;
            case NEUROLOGY -> TypeOfRoom.NEUROLOGY;
            case PEDIATRICS -> TypeOfRoom.PEDIATRIC;
            case SURGERY -> TypeOfRoom.SURGICAL;
            case FIRST_CONTACT -> TypeOfRoom.ADMISSION;
            default -> TypeOfRoom.INTERNAL;
        };
    }
}
