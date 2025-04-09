package backend.mongo;


import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.mongo.AppointmentRepository;
import backend.mongo.DoctorRepository;
import backend.mongo.PatientRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DataLoader {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public DataLoader(MongoDatabase database) {
        this.patientRepository = new PatientRepository(database);
        this.doctorRepository = new DoctorRepository(database);
        this.appointmentRepository = new AppointmentRepository(database);
    }

    public void loadData() {
        // Dodanie przykładowych pacjentów
        for (int i = 1; i <= 10; i++) {
            Patient patient = new Patient();
            try{patient.setFirstName("PatientFirstName" + i);
            patient.setLastName("PatientLastName" + i);
            patient.setPesel(10000000000L + i);  // Przykładowy PESEL
            patient.setBirthDate(LocalDate.of(1990, 1, i));
            patient.setAddress("Patient Address " + i);
            }catch (Exception e){
                System.out.println("Wystąpił błąd podczas dodawania pacjenta: " + e.getMessage());
            }
            patientRepository.createPatient(patient);
        }

        // Dodanie przykładowych lekarzy
        for (int i = 1; i <= 10; i++) {
            Doctor doctor = new Doctor();
           try {
               doctor.setFirstName("DoctorFirstName" + i);
               doctor.setLastName("DoctorLastName" + i);
               doctor.setSpecialization("Specialization " + i);
               doctorRepository.createDoctor(doctor);
           }catch (Exception e){
                System.out.println("Wystąpił błąd podczas dodawania lekarza: " + e.getMessage());
           }
        }

        // Dodanie przykładowych wizyt
        List<Doctor> doctors = doctorRepository.findAll();
        List<Patient> patients = patientRepository.findAll();

        for (int i = 0; i < 10; i++) {
            Appointment appointment = new Appointment();
            try{appointment.setDoctorId(doctors.get(i % doctors.size()).getId());  // Wybieramy lekarza cyklicznie
            appointment.setPatientId(patients.get(i % patients.size()).getId());  // Wybieramy pacjenta cyklicznie
            appointment.setDate(LocalDate.of(2025, 5, i + 1).atStartOfDay());  // Przykładowe daty
            //appointment.setDate(LocalDateTime.of(2025, 5, i + 1, 9 + i, 0));
            //appointment.setEndTime(LocalDateTime.of(2025, 5, i + 1, 9 + i, 30));
            appointmentRepository.createAppointment(appointment);}
            catch (Exception e){
                System.out.println("Wystąpił błąd podczas dodawania wizyty: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://192.168.21.191:27017");
        MongoDatabase database = mongoClient.getDatabase("hospitalDB");
        DataLoader dataLoader = new DataLoader(database);
        dataLoader.loadData();
        System.out.println("Dane zostały załadowane!");
    }
}
