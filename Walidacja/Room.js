db.runCommand({
  collMod: "rooms",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["address", "floor", "number", "maxPatients", "patientIds", "type"],
      properties: {
        address: {
          bsonType: "string",
          minLength: 1,
          description: "Adres musi być stringiem o długości min. 1 znaku"
        },
        floor: {
          bsonType: "int",
          description: "Piętro musi być liczbą całkowitą"
        },
        number: {
          bsonType: "int",
          minimum: 1,
          description: "Numer pokoju musi być liczbą całkowitą większą od 0"
        },
        maxPatients: {
          bsonType: "int",
          minimum: 1,
          description: "Maksymalna liczba pacjentów musi być liczbą całkowitą większą od 0"
        },
        patientIds: {
          bsonType: "array",
          items: {
            bsonType: "objectId",
            description: "Identyfikator pacjenta musi być typu ObjectId"
          },
          description: "Lista identyfikatorów pacjentów"
        },
        type: {
          bsonType: "string",
          enum: ["ADMISSION", "PEDIATRIC", "MATERNITY", "INTERNAL", "SURGICAL", "NEUROLOGY", "CARDIOLOGY"],
          description: "Typ pokoju musi być jednym z dozwolonych typów: STANDARD, DELUXE lub SUITE"
        }
          description: "Typ pokoju zawierający opis"
        }
      }
    }
  },
  validationAction: "error"
});