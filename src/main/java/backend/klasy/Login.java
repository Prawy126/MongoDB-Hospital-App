package backend.klasy;

import backend.mongo.DoctorRepository;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;

import java.util.Optional;

/**
 * Odpowiada tylko za uwierzytelnianie użytkownika.
 * W przypadku błędnych danych zwraca null.
 */
public class Login {

    public enum Role { ADMIN, DOCTOR, PATIENT }

    private static final String ADMIN_LOGIN    = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private final DoctorRepository  doctorRepo;
    private final PatientRepository patientRepo;

    public Login() {
        var db = MongoDatabaseConnector.connectToDatabase();
        this.doctorRepo  = new DoctorRepository(db);
        this.patientRepo = new PatientRepository(db);
    }

    /**
     * Próbuje zalogować użytkownika.
     *
     * @param login    PESEL lub login admina
     * @param password hasło w postaci jawnej
     * @return         rola zalogowanego użytkownika; null gdy uwierzytelnienie się nie powiedzie
     */
    public Role authenticate(String login, String password) {
        if (ADMIN_LOGIN.equals(login) && ADMIN_PASSWORD.equals(password)) {
            return Role.ADMIN;
        }

        long pesel;
        try { pesel = Long.parseLong(login); }
        catch (NumberFormatException ex) { return null; }

        Optional<Doctor> doc = doctorRepo.findAll()
                .stream()
                .filter(d -> d.getPesel() == pesel)
                .findFirst();
        if (doc.isPresent()) {
            doc.get().reconstructPasswordObject();
            return doc.get().getPassword().verify(password) ? Role.DOCTOR : null;
        }

        Optional<Patient> pat = patientRepo.findAll()
                .stream()
                .filter(p -> p.getPesel() == pesel)
                .findFirst();
        if (pat.isPresent()) {
            pat.get().reconstructPasswordObject();
            return pat.get().getPassword().verify(password) ? Role.PATIENT : null;
        }

        return null;
    }
}
