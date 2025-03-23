package backend.mongo;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;

import java.util.List;

public class PatientCollectionInitializer {

    public static void ensurePatientCollectionWithValidation(MongoDatabase database) {
        // JSON Schema walidator
        Document validator = new Document("$jsonSchema", new Document()
                .append("bsonType", "object")
                .append("required", List.of("firstName", "lastName", "pesel", "birthDate"))
                .append("properties", new Document()
                        .append("firstName", new Document("bsonType", "string"))
                        .append("lastName", new Document("bsonType", "string"))
                        .append("pesel", new Document("bsonType", "long")
                                .append("minimum", 10000000000L)
                                .append("maximum", 99999999999L))
                        .append("birthDate", new Document("bsonType", "date"))
                        .append("address", new Document("bsonType", "string"))
                        .append("age", new Document("bsonType", "int").append("minimum", 0))
                )
        );

        ValidationOptions validationOptions = new ValidationOptions()
                .validator(validator)
                .validationLevel(ValidationLevel.STRICT)
                .validationAction(ValidationAction.ERROR);

        boolean exists = database.listCollectionNames()
                .into(new java.util.ArrayList<>())
                .contains("patients");

        if (!exists) {
            // Tworzenie kolekcji z walidatorem
            database.createCollection("patients", new CreateCollectionOptions().validationOptions(validationOptions));
            System.out.println("[INFO] Kolekcja 'patients' utworzona z walidacją JSON Schema.");
        } else {
            // Modyfikacja istniejącej kolekcji (collMod)
            Document collModCommand = new Document("collMod", "patients")
                    .append("validator", validator)
                    .append("validationLevel", "strict")
                    .append("validationAction", "error");

            database.runCommand(collModCommand);
            System.out.println("[INFO] Kolekcja 'patients' została zaktualizowana z walidatorem.");
        }
    }
}
