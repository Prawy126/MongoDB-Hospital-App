//Na tej klasie skończyłem muszę od niej zacząć następnym razme
package backend;

public class MedicinRepository {
    private final MongoCollection<Lek> collection;

    public LekRepository(MongoDatabase database) {
        this.collection = database.getCollection("leki", Lek.class);
    }

    // Dodawanie nowego leku
    public Lek dodajLek(Lek lek) {
        collection.insertOne(lek);
        return lek;
    }

    // Wyszukiwanie leków po nazwie i dawce
    public List<Lek> znajdzLeki(String nazwa, String dawka) {
        Bson filter = Filters.and(
                eq("nazwa", nazwa),
                eq("dawka", dawka)
        );
        return collection.find(filter).into(new ArrayList<>());
    }
}
