db.orderFunctions.insertOne({
    name: "addPatient",
    code: `
        function addPatient(firstName, lastName, pesel, birthDate, address, age) {
            // Sprawdzamy dane pacjenta
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

            // Tworzymy nowy dokument pacjenta
            const patient = {
                firstName: firstName,
                lastName: lastName,
                pesel: pesel,
                birthDate: birthDate,
                address: address,
                age: age,
                id: new ObjectId()  // Generowanie ID
            };

            // Wstawiamy pacjenta do bazy
            db.patients.insertOne(patient);
            return patient;  // Zwracamy pacjenta po zapisaniu
        }
    `
});
