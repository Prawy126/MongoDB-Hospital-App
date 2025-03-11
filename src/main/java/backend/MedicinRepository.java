//Na tej klasie skończyłem muszę od niej zacząć następnym razme
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
    public Medicin addMedicin(Medicin lek) {
        collection.insertOne(lek);
        return lek;
    }

    // Wyszukiwanie leków po nazwie i dawce
    public List<Medicin> znajdzLeki(String nazwa, String dawka) {
        Bson filter = Filters.and(
                eq("nazwa", nazwa),
                eq("dawka", dawka)
        );
        return collection.find(filter).into(new ArrayList<>());
    }
}
