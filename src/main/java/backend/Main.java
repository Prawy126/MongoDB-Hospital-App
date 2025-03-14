package backend;

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

                // Testowanie metod PatientRepository
                System.out.println("\n=== Testowanie metod PatientRepository ===");
                patientRepo.testPatient();

                // Testowanie metod DoctorRepository
                System.out.println("\n=== Testowanie metod DoctorRepository ===");
                doctorRepo.testDoctor();

                // Testowanie metod AppointmentRepository
                System.out.println("\n=== Testowanie metod AppointmentRepository ===");
                appointmentRepo.testAppointment();

            } catch (Exception e) {
                System.err.println("[ERROR] Wystąpił błąd podczas testowania: " + e.getMessage());
                e.printStackTrace();
            } finally {
                MongoDatabaseConnector.close();
            }
        }
    }
}

