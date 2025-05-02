db.runCommand({
  collMod: "patients",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["firstName", "lastName", "age", "birthDate", "pesel", "password", "salt", "diagnosis"], // Dodano 'password'
      properties: {
        firstName: {
          bsonType: "string",
          minLength: 2,
          description: "Imię musi być stringiem o długości min. 2 znaków"
        },
        lastName: {
          bsonType: "string",
          minLength: 2,
          description: "Nazwisko musi być stringiem o długości min. 2 znaków"
        },
        age: {
          bsonType: "number",
          minimum: 0,
          maximum: 120,
          description: "Wiek musi być liczbą z zakresu 0-120"
        },
        birthDate: {
          bsonType: "date",
          description: "Data urodzenia musi być typu Date"
        },
        pesel: {
          bsonType: "long", // Zachowujemy typ Long
          minimum: 10000000000, // 11 cyfr (najmniejsza wartość)
          maximum: 99999999999, // 11 cyfr (największa wartość)
          description: "PESEL musi być 11-cyfrową liczbą"
        },
        address: {
          bsonType: "string",
          description: "Adres musi być stringiem (opcjonalny)"
        },
        password: {
          bsonType: "string",
          minLength: 8, // Minimalna długość hasła
          description: "Hasło musi być stringiem o długości min. 8 znaków"
        },
        salt: {
          bsonType: "string",
          description: "Sól musi być stringiem (opcjonalna)"
        },
        diagnosis: {
          bsonType: "string",
          enum: ["AWAITING", "POSITIVE", "NEGATIVE"], // dostosuj do swoich wartości
          description: "Diagnoza musi być jednym z dozwolonych statusów"
        }
      }
    }
  },
  validationAction: "error"
});