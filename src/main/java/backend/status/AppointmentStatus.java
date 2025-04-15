package backend.status;

/**
 * Enum AppointmentStatus reprezentuje status wizyty.
 * Statusy: SCHEDULED, COMPLETED, CANCELED, RESCHEDULED
 */
public enum AppointmentStatus {
    SCHEDULED("Zaplanowany"),
    COMPLETED("Zrealizowany"),
    CANCELED("Odwołany"),
    RESCHEDULED("Przełożony");

    private final String description;

    AppointmentStatus(String description) {
        this.description = description;
    }

    public String getDescribtion(){
        return description;
    }
}
