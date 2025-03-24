// Zapisywanie funkcji klasy Patient w MongoDB
db.system.js.save({
    _id: "Patient",  // Nazwa funkcji
    value: function(firstName, lastName, pesel, birthDate, address, age) {
        // Walidacja
        if (!firstName || firstName.trim().length === 0) {
            throw new Error("Imię nie może być puste.");
        }
        if (!lastName || lastName.trim().length === 0) {
            throw new Error("Nazwisko nie może być puste.");
        }
        if (age <= 0) {
            throw new Error("Wiek pacjenta musi być większy niż 0.");
        }
        if (pesel < 10000000000 || pesel > 99999999999) {
            throw new Error("Pesel musi mieć dokładnie 11 cyfr.");
        }

        // Tworzenie obiektu "pacjenta"
        const patient = {
            firstName: firstName,
            lastName: lastName,
            pesel: pesel,
            birthDate: birthDate,
            address: address,
            age: age,
            id: new ObjectId(),  // Generowanie unikalnego ID

            // Metody
            getFirstName: function() {
                return this.firstName;
            },
            getLastName: function() {
                return this.lastName;
            },
            getPesel: function() {
                return this.pesel;
            },
            getAge: function() {
                return this.age;
            },
            getId: function() {
                return this.id;
            },
            setFirstName: function(newFirstName) {
                if (!newFirstName || newFirstName.trim().length === 0) {
                    throw new Error("Imię nie może być puste.");
                }
                this.firstName = newFirstName;
            },
            setLastName: function(newLastName) {
                if (!newLastName || newLastName.trim().length === 0) {
                    throw new Error("Nazwisko nie może być puste.");
                }
                this.lastName = newLastName;
            },
            setAge: function(newAge) {
                if (newAge <= 0) {
                    throw new Error("Wiek pacjenta musi być większy niż 0.");
                }
                this.age = newAge;
            },
            setPesel: function(newPesel) {
                if (newPesel < 10000000000 || newPesel > 99999999999) {
                    throw new Error("Pesel musi mieć dokładnie 11 cyfr.");
                }
                this.pesel = newPesel;
            },
            setBirthDate: function(newBirthDate) {
                this.birthDate = newBirthDate;
            },
            setAddress: function(newAddress) {
                this.address = newAddress;
            }
        };

        // Zwracamy utworzony obiekt
        return patient;
    }
});
