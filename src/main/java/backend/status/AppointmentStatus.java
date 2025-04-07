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

    private final String describtion;

    AppointmentStatus(String description) {
        this.describtion = description;
    }

    public String getDescribtion(){
        return describtion;
    }
}
