package backend.klasy;

import backend.mongo.*;

import java.util.Optional;

/**
 * Klasa {@code Login} obsługuje proces uwierzytelniania użytkowników systemu.
 * Może uwierzytelniać administratorów, lekarzy (w tym lekarzy pierwszego kontaktu) i pacjentów.
 * Po udanym logowaniu przechowuje odpowiedni obiekt użytkownika.
 */
public class Login {

    /**
     * Role użytkowników dostępne w systemie.
     */
    public enum Role {
        ADMIN,           // Administrator systemu
        DOCTOR,          // Lekarz specjalista
        PATIENT,         // Pacjent
        DOCTOR_FIRST     // Lekarz pierwszego kontaktu
    }

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;

    private Doctor authenticatedDoctor;
    private Patient authenticatedPatient;

    /**
     * Konstruktor tworzy instancję klasy {@code Login} i inicjalizuje repozytoria na podstawie połączenia z bazą danych MongoDB.
     */
    public Login() {
        var db = MongoDatabaseConnector.connectToDatabase();
        this.doctorRepo = new DoctorRepository(db);
        this.patientRepo = new PatientRepository(db);
    }

    /**
     * Próbuje zalogować użytkownika na podstawie loginu i hasła.
     *
     * @param login    login użytkownika (PESEL lub "admin")
     * @param password hasło użytkownika
     * @return rola użytkownika po udanym logowaniu, lub {@code null} w przypadku niepowodzenia
     */
    public Role authenticate(String login, String password) {
        authenticatedDoctor = null;
        authenticatedPatient = null;

        // Sprawdzenie danych administratora
        if (ADMIN_LOGIN.equals(login) && ADMIN_PASSWORD.equals(password)) {
            return Role.ADMIN;
        }

        long pesel;
        try {
            pesel = Long.parseLong(login);
        } catch (NumberFormatException ex) {
            return null;
        }

        // Próba logowania jako lekarz
        Optional<Doctor> doc = doctorRepo.findAll().stream()
                .filter(d -> d.getPesel() == pesel)
                .findFirst();
        if (doc.isPresent()) {
            doc.get().reconstructPasswordObject();
            if (doc.get().getPassword().verify(password)) {
                authenticatedDoctor = doc.get();
                return doc.get().isFirstContact() ? Role.DOCTOR_FIRST : Role.DOCTOR;
            }
            return null;
        }

        // Próba logowania jako pacjent
        Optional<Patient> pat = patientRepo.findAll().stream()
                .filter(p -> p.getPesel() == pesel)
                .findFirst();
        if (pat.isPresent()) {
            pat.get().reconstructPasswordObject();
            if (pat.get().getPassword().verify(password)) {
                authenticatedPatient = pat.get();
                return Role.PATIENT;
            }
        }

        return null;
    }

    /**
     * Zwraca zalogowanego lekarza, jeśli uwierzytelnienie się powiodło i dotyczyło lekarza.
     *
     * @return obiekt {@link Doctor} lub {@code null}, jeśli nie zalogowano lekarza
     */
    public Doctor getAuthenticatedDoctor() {
        return authenticatedDoctor;
    }

    /**
     * Zwraca zalogowanego pacjenta, jeśli uwierzytelnienie się powiodło i dotyczyło pacjenta.
     *
     * @return obiekt {@link Patient} lub {@code null}, jeśli nie zalogowano pacjenta
     */
    public Patient getAuthenticatedPatient() {
        return authenticatedPatient;
    }
}
