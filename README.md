# Propozycje baz:
1) Bazy dokumentowe (przechowują dane w formie dokumentów, np. JSON)

**MongoDB**

Dlaczego warto: Najpopularniejsza baza dokumentowa, prosta integracja, bogate zapytania.
Python: Biblioteka pymongo (oficjalna).
Java: Sterownik MongoDB Java Driver lub framework Spring Data MongoDB.
Przypadek użycia: Aplikacje społecznościowe, zarządzanie treścią, logi.
Docker: docker run -d -p 27017:27017 --name mongo mongo:latest.

**Couchbase**

Dlaczego warto: Łączy cechy bazy dokumentowej i klucz-wartość, skalowalność.
Python: couchbase-python-client.
Java: Oficjalny SDK.
Przypadek użycia: Aplikacje czasu rzeczywistego (np. czaty).

2) Bazy klucz-wartość (prosty model: klucz → wartość)

**Redis**
Dlaczego warto: Bardzo szybka, wspiera struktury danych (listy, hashe).
Python: redis-py.
Java: Jedis lub Lettuce.
Przypadek użycia: Cache, kolejki, sesje użytkowników.
Docker: docker run -d -p 6379:6379 --name redis redis:alpine.

**DynamoDB (AWS)**
Dlaczego warto: Zarządzana przez AWS, automatyczna skalowalność.
Python: boto3 (SDK AWS).
Java: AWS SDK for Java.
Przypadek użycia: Aplikacje serverless, wysokie obciążenia.

3) Bazy kolumnowe (dane przechowywane w kolumnach, nie wierszach)

**Apache Cassandra**
Dlaczego warto: Liniowa skalowalność, odporność na awarie.
Python: cassandra-driver.
Java: Oficjalny sterownik lub Spring Data Cassandra.
Przypadek użycia: Analiza dużych zbiorów danych, systemy IoT.

**ScylldDB**
Dlaczego warto: Zgodna z Cassandra, ale szybsza (napisana w C++).
Python/JAVA: Te same narzędzia co dla Cassandra.
Przypadek użycia: Aplikacje wymagające niskich opóźnień.

4) Bazy grafowe (oparte na węzłach i relacjach)

**Neo4j**
Dlaczego warto: Najpopularniejsza baza grafowa, język zapytań Cypher.
Python: neo4j-python-driver.
Java: Oficjalny sterownik lub Spring Data Neo4j.
Przypadek użycia: Sieci społecznościowe, rekomendacje, wykrywanie oszustw.
Docker: docker run -d -p 7474:7474 -p 7687:7687 --name neo4j neo4j:latest.

**ArangoDB**
Dlaczego warto: Łączy modele grafowe, dokumentowe i klucz-wartość.
Python: python-arango.
Java: arangodb-java-driver.
Przypadek użycia: Aplikacje wymagające wielomodelowości.

5) Lekkie bazy dla małych projektów

**TinyDB (Python)**
Dlaczego warto: Czysty Python, brak serwera, idealna do prototypów.
Przykład: from tinydb import TinyDB; db = TinyDB('db.json').

**MapDB (Java)**

Dlaczego warto: Embedded, wspiera struktury danych (mapy, kolekcje).
Przykład: DB db = DBMaker.fileDB("file.db").make(); HTreeMap map = db.hashMap("map").createOrOpen();.

## Lista baz

- [ ] MongoDB
- [ ] Couchbase
- [ ] Redis
- [ ] DynamoDB (AWS)
- [ ] Apache Cassandra
- [ ] Neo4j
- [ ] ArangoDB
- [ ] TinyDB (Python)
- [ ] MapDB (Java)
