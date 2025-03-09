package backend;

import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();

        if (database != null) {
            try {
                // Repozytoria
                PatientRepository patientRepo = new PatientRepository(database);
                DoctorRepository doctorRepo = new DoctorRepository(database);
                AppointmentRepository appointmentRepo = new AppointmentRepository(database);

                // Tworzenie pacjenta
                Patient patient = new Patient();
                patient.setFirstName("Jan");
                patient.setLastName("Kowalski");
                patient.setPesel("12345678901");
                patient.setBirthDate(new Date());
                patient.setAddress("ul. Testowa 123, Warszawa");
                String patientId = patientRepo.createPatient(patient);

                // Tworzenie lekarza
                Doctor doctor = new Doctor();
                doctor.setFirstName("Anna");
                doctor.setLastName("Nowak");
                doctor.setSpecialization("Kardiolog");
                doctor.setAvailableDays(List.of("Poniedziałek", "Środa", "Piątek"));
                String doctorId = doctorRepo.createDoctor(doctor);

                // Tworzenie wizyty
                Appointment appointment = new Appointment();
                appointment.setPatientId(new ObjectId(patientId));
                appointment.setDoctorId(new ObjectId(doctorId));
                appointment.setDate(new Date());
                appointment.setRoom("Sala 205");
                appointment.setDescription("Konsultacja kardiologiczna");
                String appointmentId = appointmentRepo.createAppointment(appointment);

                System.out.println("Dodano nową wizytę o ID: " + appointmentId);
            } finally {
                MongoDatabaseConnector.close();
            }
        }
    }
}