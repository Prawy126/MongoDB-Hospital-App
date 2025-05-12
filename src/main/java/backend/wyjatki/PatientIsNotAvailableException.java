package backend.wyjatki;

/**
 * Wyjątek rzucany, gdy pacjent nie jest dostępny w danym terminie.
 */
public class PatientIsNotAvailableException extends Exception {

    /**
     * Konstruktor z komunikatem błędu.
     *
     * @param message komunikat błędu
     */
    public PatientIsNotAvailableException(String message) {
        super(message);
    }
}
