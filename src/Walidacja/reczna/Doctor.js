db.runCommand({
  collMod: "doctors",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["firstName", "lastName", "age", "pesel", "room", "specialization", "availableDays", "contactInformation", "password", "salt"], // Dodano 'password' i 'salt'
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
          minimum: 24, // Minimalny wiek dla lekarza (przykładowo)
          maximum: 80,
          description: "Wiek musi być liczbą z zakresu 24-80"
        },
        pesel: {
          bsonType: "long",
          minimum: 10000000000, // 11 cyfr
          maximum: 99999999999,
          description: "PESEL musi być 11-cyfrową liczbą"
        },
        room: {
          bsonType: "string",
          pattern: "^[0-9A-Za-z-]{1,10}$", // Przykładowy format sali (np. "123", "A-15")
          description: "Numer sali musi być krótkim stringiem (max 10 znaków)"
        },
        specialization: {
          bsonType: "string",
          minLength: 3,
          description: "Specjalizacja musi być stringiem o długości min. 3 znaków"
        },
        availableDays: {
          bsonType: "array",
          items: {
            bsonType: "string",
            enum: ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"]
          },
          minItems: 1,
          description: "Lista dostępnych dni (co najmniej 1 dzień)"
        },
        contactInformation: {
          bsonType: "string",
          minLength: 5,
          description: "Kontakt musi być stringiem o długości min. 5 znaków"
        },
        password: {
          bsonType: "string",
          minLength: 8, // Minimalna długość hasła
          description: "Hasło musi być stringiem o długości min. 8 znaków"
        },
        salt: {
          bsonType: "string",
          description: "Sól musi być stringiem (opcjonalna)"
        }
      }
    }
  },
  validationAction: "error"
});