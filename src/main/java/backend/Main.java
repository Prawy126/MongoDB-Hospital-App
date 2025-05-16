package backend;

import backend.klasy.Doctor;
import backend.klasy.Password;
import backend.klasy.Patient;
import backend.klasy.Room;
import backend.mongo.DoctorRepository;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import backend.mongo.RoomRepository;
import backend.status.Day;
import backend.status.Diagnosis;
import backend.status.TypeOfRoom;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Main {
    public static void main(String[] args) throws NullNameException, AgeException, PeselException {
        MongoDatabase database = MongoDatabaseConnector.connectToDatabase();
        DoctorRepository doctorRepo = new DoctorRepository(database);
    }
}