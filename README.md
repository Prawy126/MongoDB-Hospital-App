# Bazy danych: Klucz-Wartość i Obiektowe

## 📚 Bazy Klucz-Wartość

### **Redis**
- **Opis**: Szybka baza danych działająca w pamięci RAM z obsługą trwałego zapisu.
- **Zastosowania**: Cache’owanie, systemy głosowań w czasie rzeczywistym, kolejki komunikatów.
- **Link**: [Dokumentacja Redis](https://redis.io/documentation)

### **Amazon DynamoDB**
- **Opis**: W pełni zarządzana baza NoSQL od AWS, skalowalna i wydajna.
- **Zastosowania**: Aplikacje webowe, systemy logowania, analiza danych w czasie rzeczywistym.
- **Link**: [Dokumentacja DynamoDB](https://docs.aws.amazon.com/dynamodb/)

### **TinyDB (Python)**
- **Opis**: Lekka, osadzona baza danych dla Pythina, przechowująca dane w formacie JSON.
- **Zastosowania**: Małe aplikacje lokalne, prototypowanie, projekty edukacyjne.
- **Link**: [TinyDB GitHub](https://github.com/msiemens/tinydb)

### **MapDB (Java)**
- **Opis**: Osadzona baza dla Javy, wspierająca struktury takie jak mapy i kolekcje.
- **Zastosowania**: Cache’owanie, przechowywanie dużych zbiorów danych w pamięci.
- **Link**: [MapDB GitHub](https://github.com/jankotek/mapdb)

---

## 🧩 Bazy Obiektowe

### **ObjectDB**
- **Opis**: Czysto obiektowa baza danych dla Javy, kompatybilna ze standardem JPA.
- **Zastosowania**: Aplikacje korporacyjne, systemy zarządzania relacjami między obiektami.
- **Link**: [ObjectDB Docs](https://www.objectdb.com/)

### **MongoDB**
- **Opis**: Dokumentowa baza NoSQL z mapowaniem obiektowym (przez format BSON).
- **Zastosowania**: Aplikacje webowe, systemy CMS, analiza danych.
- **Link**: [MongoDB Docs](https://www.mongodb.com/docs/)

### **Versant**
- **Opis**: Komercyjna baza obiektowa dla zaawansowanych zastosowań (np. finanse, telekomunikacja).
- **Zastosowania**: Systemy wymagające skomplikowanych hierarchii obiektów i niskich opóźnień.
- **Link**: [Versant Official](https://www.actian.com/versant/)

---

## 📋 Lista baz do wyboru

- [x] **ObjectDB** (obiektowa)  
- [x] **MongoDB** (obiektowa/dokumentowa)  
- [x] **Versant** (obiektowa)  
- [x] **Redis** (klucz-wartość)  
- [x] **DynamoDB (AWS)** (klucz-wartość)  
- [x] **TinyDB (Python)** (klucz-wartość, osadzona)  
- [x] **MapDB (Java)** (klucz-wartość, osadzona)  

---

## 💡 Propozycje projektów

### **Dla ObjectDB (obiektowa)**
- **Temat**: *System zarządzania szpitalem z hierarchią klas (Pacjent, Lekarz, Oddział)*.  
- **Poziom trudności**: 5.0/5.0  
- **Funkcje**:  
  - Dziedziczenie klas (`Osoba → Pacjent/Lekarz`).  
  - Zaawansowane zapytania JPQL (np. znajdź wolne łóżka na oddziale).  

### **Dla MongoDB (obiektowa/dokumentowa)**
- **Temat**: *Platforma blogowa z komentarzami i tagami*.  
- **Poziom trudności**: 4.0/5.0  
- **Funkcje**:  
  - Mapowanie obiektów do dokumentów BSON.  
  - Wyszukiwanie postów po tagach z użyciem agregacji.  

### **Dla Redis (klucz-wartość)**
- **Temat**: *System ankiet z wynikami w czasie rzeczywistym*.  
- **Poziom trudności**: 4.0/5.0  
- **Funkcje**:  
  - Atomowe operacje `INCR` do zliczania głosów.  
  - Wygaśnięcie ankiet po czasie (`EXPIRE`).  

