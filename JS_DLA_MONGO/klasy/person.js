db.system.js.save({
    _id: "Person",  // Nazwa funkcji
    value: function(firstName, lastName, pesel, age) {
        // Walidacja
        if (!firstName || firstName.trim().length === 0 || !lastName || lastName.trim().length === 0) {
            throw new Error("Imię i nazwisko nie mogą być puste");
        }

        if (pesel.toString().length !== 11 || pesel < 0) {
            throw new Error("Pesel musi składać się dokładnie z 11 cyfr");
        }

        if (age < 0) {
            throw new Error("Wiek nie może być ujemny");
        }

        // Zwracamy obiekt, który emuluje instancję klasy Person
        return {
            firstName: firstName,
            lastName: lastName,
            pesel: pesel,
            age: age,

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
            setFirstName: function(newFirstName) {
                if (!newFirstName || newFirstName.trim().length === 0) {
                    throw new Error("Imię nie może być puste");
                }
                this.firstName = newFirstName;
            },
            setLastName: function(newLastName) {
                if (!newLastName || newLastName.trim().length === 0) {
                    throw new Error("Nazwisko nie może być puste");
                }
                this.lastName = newLastName;
            },
            setPesel: function(newPesel) {
                if (newPesel.toString().length !== 11 || newPesel < 0) {
                    throw new Error("Pesel musi składać się dokładnie z 11 cyfr");
                }
                this.pesel = newPesel;
            },
            setAge: function(newAge) {
                if (newAge < 0) {
                    throw new Error("Wiek nie może być ujemny");
                }
                this.age = newAge;
            }
        };
    }
});
