package backend;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

public class MedicinRepository {
    private final MongoCollection<Medicin> collection;

    public MedicinRepository(MongoDatabase database) {
        this.collection = database.getCollection("leki", Medicin.class);
    }

    // Dodawanie nowego leku
    public Medicin addMedicin(Medicin medicin) {
        collection.insertOne(medicin);
        return medicin;
    }

    // Wyszukiwanie leków po nazwie i dawce
    public List<Medicin> findMedicin(String name, String dose) {
        Bson filter = Filters.and(
                eq("nazwa", name),
                eq("dawka", dose)
        );
        return collection.find(filter).into(new ArrayList<>());
    }
}
