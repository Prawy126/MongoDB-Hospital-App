db.orderFunctions.insertOne({
    name: "createPatient",
    code: `
        function createPatient(firstName, lastName, pesel, birthDate, address, age) {
            if (!firstName || firstName.trim().length === 0) {
                throw new Error("Imię nie może być puste.");
            }
            if (!lastName || lastName.trim().length === 0) {
                throw new Error("Nazwisko nie może być puste.");
            }
            if (age <= 0) {
                throw new Error("Wiek pacjenta musi być większy niż 0.");
            }
            if (pesel.toString().length !== 11) {
                throw new Error("Pesel musi mieć dokładnie 11 cyfr.");
            }

            // Tworzenie dokumentu pacjenta
            var patient = {
                firstName: firstName,
                lastName: lastName,
                pesel: pesel,
                birthDate: birthDate,
                address: address,
                age: age,
                id: new ObjectId()  // Generowanie ID
            };

            // Wstawienie do kolekcji 'patients'
            db.patients.insertOne(patient);
            return patient;  // Zwróć wstawiony pacjent jako wynik
        }
    `
});
