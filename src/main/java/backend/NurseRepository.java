package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;

public class NurseRepository {
    private final MongoCollection<Nurse> collection;

    public NurseRepository(MongoDatabase database) {
        this.collection = database.getCollection("nurses", Nurse.class);
    }

    public Nurse createNurse(Nurse nurse) {
        collection.insertOne(nurse);
        return nurse;
    }

    public Nurse getNurseById(ObjectId id) {
        return collection.find(eq("_id", id)).first();
    }

    public List<Nurse> getAllNurses() {
        List<Nurse> nurses = new ArrayList<>();
        collection.find().into(nurses);
        return nurses;
    }

    public void updateNurse(ObjectId id, Nurse updatedNurse) {
        Bson filter = eq("_id", id);
        collection.replaceOne(filter, updatedNurse);
    }

    public void deleteNurse(ObjectId id) {
        Bson filter = eq("_id", id);
        collection.deleteOne(filter);
    }

    public List<Nurse> findNursesBySpecialization(String specialization) {
        List<Nurse> nurses = new ArrayList<>();
        collection.find(eq("specialization", specialization)).into(nurses);
        return nurses;
    }
}