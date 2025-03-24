// Tworzenie kolekcji 'orderFunctions' w MongoDB
db.createCollection("orderFunctions", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["name", "code"],
            properties: {
                name: {
                    bsonType: "string",
                    description: "name of the function"
                },
                code: {
                    bsonType: "string",
                    description: "JavaScript code of the function"
                }
            }
        }
    }
});

// Zapisanie funkcji klasy Patient w kolekcji 'orderFunctions'
db.orderFunctions.insertOne({
    name: "Patient",
    code: `
        function Patient(firstName, lastName, pesel, birthDate, address, age) {
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

            // Obiekt pacjenta
            return {
                firstName: firstName,
                lastName: lastName,
                pesel: pesel,
                birthDate: birthDate,
                address: address,
                age: age,
                id: new ObjectId(),  // Generowanie ID

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
        }
    `
});
