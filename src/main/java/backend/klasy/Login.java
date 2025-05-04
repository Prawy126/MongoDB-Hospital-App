package backend.klasy;

import backend.mongo.*;
import java.util.Optional;

/** Obsługa uwierzytelniania.  Po udanym logowaniu przechowuje obiekt użytkownika. */
public class Login {

    public enum Role { ADMIN, DOCTOR, PATIENT }

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private final DoctorRepository  doctorRepo;
    private final PatientRepository patientRepo;

    private Doctor  authenticatedDoctor;
    private Patient authenticatedPatient;

    public Login() {
        var db = MongoDatabaseConnector.connectToDatabase();
        this.doctorRepo  = new DoctorRepository(db);
        this.patientRepo = new PatientRepository(db);
    }

    /** Próbuje zalogować – zwraca rolę lub null; obiekt użytkownika można pobrać getterem. */
    public Role authenticate(String login, String password) {

        authenticatedDoctor  = null;
        authenticatedPatient = null;

        if (ADMIN_LOGIN.equals(login) && ADMIN_PASSWORD.equals(password)) {
            return Role.ADMIN;
        }

        long pesel;
        try { pesel = Long.parseLong(login); }
        catch (NumberFormatException ex) { return null; }

        /* lekarz */
        Optional<Doctor> doc = doctorRepo.findAll().stream()
                .filter(d -> d.getPesel() == pesel)
                .findFirst();
        if (doc.isPresent()) {
            doc.get().reconstructPasswordObject();
            if (doc.get().getPassword().verify(password)) {
                authenticatedDoctor = doc.get();
                return Role.DOCTOR;
            }
            return null;
        }

        /* pacjent */
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

    public Doctor  getAuthenticatedDoctor()  { return authenticatedDoctor;  }
    public Patient getAuthenticatedPatient() { return authenticatedPatient; }
}
