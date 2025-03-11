package backend;

import com.mongodb.client.MongoDatabase;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();

        if (database != null) {
            try {
                PatientRepository patientRepo = new PatientRepository(database);
                DoctorRepository doctorRepo = new DoctorRepository(database);
                AppointmentRepository appointmentRepo = new AppointmentRepository(database);

                Patient patient = new Patient.Builder()
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .pesel(123456789)
                        .birthDate(LocalDate.now())
                        .address("ul. Testowa 123, Warszawa")
                        .age(12)
                        .build();

                Patient createdPatient = patientRepo.createPatient(patient);

                Doctor doctor = new Doctor.Builder()
                        .firstName("Anna")
                        .lastName("Nowak")
                        .specialization("Kardiolog")
                        .availableDays(List.of("Poniedziałek", "Środa", "Piątek"))
                        .build();

                Doctor createdDoctor = doctorRepo.createDoctor(doctor);

                // Tworzenie wizyty
                Appointment appointment = new Appointment.Builder()
                        .patientId(createdPatient)
                        .doctorId(createdDoctor)
                        .date(LocalDate.now())
                        .room("Sala 205")
                        .description("Konsultacja kardiologiczna")
                        .build();

                Appointment createdAppointment = appointmentRepo.createAppointment(appointment);

                System.out.printf("""
                    Dodano nową wizytę:
                    ID: %s
                    Pacjent: %s %s
                    Lekarz: %s %s
                    Sala: %s
                    Opis: %s
                    """,
                        createdAppointment.getId(),
                        createdPatient.getFirstName(), createdPatient.getLastName(),
                        createdDoctor.getFirstName(), createdDoctor.getLastName(),
                        createdAppointment.getRoom(),
                        createdAppointment.getDescription()
                );
            } finally {
                MongoDatabaseConnector.close();
            }
        }
    }
}