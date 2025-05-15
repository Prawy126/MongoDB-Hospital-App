package backend.wyjatki;

public class DoctorIsNotAvailableException extends Exception {
    public DoctorIsNotAvailableException(String message) {
        super(message);
    }
}
