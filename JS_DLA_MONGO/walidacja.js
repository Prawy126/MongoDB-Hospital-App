//walidacja dla patient
db.createCollection("patients", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["firstName", "lastName", "pesel", "birthDate"], // Wymagane pola
      properties: {
        firstName: { bsonType: "string" },
        lastName: { bsonType: "string" },
        pesel: {
          bsonType: "long",
          minimum: 10000000000, // PESEL musi mieć 11 cyfr
          maximum: 99999999999
        },
        birthDate: {
          bsonType: "date",
          pattern: "^\\d{4}-\\d{2}-\\d{2}$" // Format daty ISO (np. "2024-01-01")
        },
        address: { bsonType: "string" },
        age: { bsonType: "int", minimum: 0 }
      }
    }
  },
  validationLevel: "strict", // Opcje: "off", "strict", "moderate"
  validationAction: "error" // Opcje: "error" (blokuje), "warn" (loguje błąd)
});

//jeśli istnieje to
db.runCommand({
  collMod: "patients",
  validator: {
    $jsonSchema: { /* ... */ } // Ta sama definicja co wyżej
  }
});