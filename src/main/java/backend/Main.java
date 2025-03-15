package backend;

import backend.mongo.*;
import com.mongodb.client.MongoDatabase;

/**
 * Klasa Main zawiera kompleksowe testy funkcjonalności systemu medycznego.
 */
public class Main {
    public static void main(String[] args) {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();

        if (database != null) {
            try {
                PatientRepository patientRepo = new PatientRepository(database);
                DoctorRepository doctorRepo = new DoctorRepository(database);
                AppointmentRepository appointmentRepo = new AppointmentRepository(database);
                MedicinRepository medicinRepo = new MedicinRepository(database);
                NurseRepository nurseRepo = new NurseRepository(database);

                // Testowanie metod PatientRepository
                System.out.println("\n=== [TEST] PatientRepository ===");
                patientRepo.testPatient();

                // Testowanie metod DoctorRepository
                System.out.println("\n=== [TEST] DoctorRepository ===");
                doctorRepo.testDoctor();

                // Testowanie metod AppointmentRepository
                System.out.println("\n=== [TEST] AppointmentRepository ===");
                appointmentRepo.testAppointment();

                // Testowanie metod MedicinRepository
                System.out.println("\n=== [TEST] MedicinRepository ===");
                medicinRepo.testMedicin();

                // Testowanie metod NurseRepository
                System.out.println("\n=== [TEST] NurseRepository ===");
                nurseRepo.testNurse();

            } catch (Exception e) {
                System.err.println("[ERROR] Wystąpił błąd podczas testowania: " + e.getMessage());
                e.printStackTrace();
            } finally {
                MongoDatabaseConnector.close();
            }
        } else {
            System.err.println("[ERROR] Połączenie z bazą danych nie powiodło się.");
        }
    }
}
