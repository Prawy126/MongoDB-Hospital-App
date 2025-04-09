package backend;

import backend.klasy.Doctor;
import backend.klasy.Password;
import backend.klasy.Patient;
import backend.mongo.DoctorRepository;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import backend.status.Day;
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

        Password password = new Password("No9lhtUt/hnXZOGU7XhABQ==", "1QCfaMmedBVJKhgf0dMETAaa/YhI+pp/uZZ/NmWCg3I=");
        System.out.println("Password: " + password.verify("admin"));
    }
}