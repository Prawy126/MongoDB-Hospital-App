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
          bsonType: "string",
          pattern: "^[0-9]{11}$" // PESEL jako string musi mieć dokładnie 11 cyfr
        },
        birthDate: {
          bsonType: "string",
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
    $jsonSchema: {
      bsonType: "object",
      required: ["firstName", "lastName", "pesel", "birthDate"],
      properties: {
        firstName: { bsonType: "string" },
        lastName: { bsonType: "string" },
        pesel: {
          bsonType: "string",
          pattern: "^[0-9]{11}$"
        },
        birthDate: {
          bsonType: "string",
          pattern: "^\\d{4}-\\d{2}-\\d{2}$"
        },
        address: { bsonType: "string" },
        age: { bsonType: "int", minimum: 0 }
      }
    }
  }
});

db.createCollection("doctors", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["firstName", "lastName", "specialization", "licenseNumber"], // Wymagane pola
      properties: {
        firstName: { bsonType: "string" },
        lastName: { bsonType: "string" },
        specialization: {
          bsonType: "string",
          enum: ["PEDIATRA", "KARDIOLOG", "NEUROLOG", "CHIRURG", "OKULISTA", "DERMATOLOG"]
        },
        licenseNumber: {
          bsonType: "int",
          minimum: 1000000,  // 7-cyfrowy numer
          maximum: 9999999
        },
        email: {
          bsonType: "string",
          pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        },
        phoneNumber: {
          bsonType: "string",
          pattern: "^\\+?[0-9]{9,12}$"  // Format numeru telefonu
        }
      }
    }
  },
  validationLevel: "strict",
  validationAction: "error"
});

// Alternatywnie, jeśli kolekcja już istnieje:
db.runCommand({
  collMod: "doctors",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["firstName", "lastName", "specialization", "licenseNumber"],
      properties: {
        firstName: { bsonType: "string" },
        lastName: { bsonType: "string" },
        specialization: {
          bsonType: "string",
          enum: ["PEDIATRA", "KARDIOLOG", "NEUROLOG", "CHIRURG", "OKULISTA", "DERMATOLOG"]
        },
        licenseNumber: {
          bsonType: "int",
          minimum: 1000000,
          maximum: 9999999
        },
        email: {
          bsonType: "string",
          pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        },
        phoneNumber: {
          bsonType: "string",
          pattern: "^\\+?[0-9]{9,12}$"
        }
      }
    }
  }
});

db.createCollection("nurses", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["firstName", "lastName", "nurseID", "qualification"], // Wymagane pola
      properties: {
        firstName: { bsonType: "string" },
        lastName: { bsonType: "string" },
        nurseID: {
          bsonType: "int",
          minimum: 100000,  // 6-cyfrowy numer
          maximum: 999999
        },
        qualification: { bsonType: "string" }, // Bez enum, tylko zwykły string
        education: { bsonType: "string" },
        department: { bsonType: "string" },
        email: {
          bsonType: "string",
          pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        },
        phoneNumber: {
          bsonType: "string",
          pattern: "^\\+?[0-9]{9,12}$"  // Format numeru telefonu
        }
      }
    }
  },
  validationLevel: "strict",
  validationAction: "error"
});

// Alternatywnie, jeśli kolekcja już istnieje:
db.runCommand({
  collMod: "nurses",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["firstName", "lastName", "nurseID", "qualification"],
      properties: {
        firstName: { bsonType: "string" },
        lastName: { bsonType: "string" },
        nurseID: {
          bsonType: "int",
          minimum: 100000,
          maximum: 999999
        },
        qualification: { bsonType: "string" },
        education: { bsonType: "string" },
        department: { bsonType: "string" },
        email: {
          bsonType: "string",
          pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        },
        phoneNumber: {
          bsonType: "string",
          pattern: "^\\+?[0-9]{9,12}$"
        }
      }
    }
  }
});