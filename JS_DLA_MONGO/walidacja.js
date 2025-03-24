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

//sprawdzanie poprawności dla peselu
// Funkcja walidująca PESEL na podstawie daty urodzenia z dokumentu [[3]][[5]]
function validatePeselWithBirthDate(pesel, birthDateStr) {
  // Sprawdź długość PESEL [[3]]
  if (pesel.length !== 11) return false;

  // Wyodrębnij datę z PESEL [[3]]
  const yearPart = pesel.slice(0, 2);
  let monthPart = pesel.slice(2, 4);
  const dayPart = pesel.slice(4, 6);

  // Obsługa wieku na podstawie miesiąca [[3]]
  let century = 1900;
  if (monthPart >= 21 && monthPart <= 32) {
    monthPart -= 20;
    century = 2000;
  } else if (monthPart >= 81 && monthPart <= 92) {
    monthPart -= 80;
    century = 1800;
  }

  const peselDate = new Date(
    century + parseInt(yearPart, 10),
    parseInt(monthPart, 10) - 1, // Miesiące w JS są 0-indeksowane
    parseInt(dayPart, 10)
  );

  // Porównaj z datą z dokumentu [[5]]
  const documentDate = new Date(birthDateStr);
  if (peselDate.getTime() !== documentDate.getTime()) return false;

  // Sprawdź sumę kontrolną [[3]]
  const weights = [1, 3, 7, 9, 1, 3, 7, 9, 1, 3];
  let sum = 0;
  for (let i = 0; i < 10; i++) {
    sum += parseInt(pesel[i]) * weights[i];
  }
  const controlDigit = (10 - (sum % 10)) % 10;
  return controlDigit === parseInt(pesel[10]);
}