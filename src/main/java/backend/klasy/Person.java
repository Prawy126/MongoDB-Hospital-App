package backend.klasy;

import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;

/**
 * Klasa ta jest klasą nadrzędną po której dziedziczą inne klasy*/
public class Person {
    private String firstName;
    private String lastName;
    private int age;
    private long pesel;
    Person(){

    };
    public Person(String firstName, String lastName)throws NullNameException {
        if(firstName.length() > 0 && lastName.length() > 0){
            this.firstName = firstName;
            this.lastName= lastName;
        }else throw new NullNameException("Imię i nazwisko nie mogą być puste");
    }
    public Person(String firstName, String lastName, long pesel) throws PeselException, NullNameException {
        this.firstName = firstName;
        this.lastName = lastName;
        if(firstName.length() > 0 && lastName.length() > 0){
            this.firstName = firstName;
            this.lastName = lastName;}else throw new NullNameException("Imię i nazwisko nie mogą być puste");
        if(pesel > 9999999999L){this.pesel = pesel;}else throw new PeselException("Pesel musi mieć 11 cyfr");
    }

    public Person(String firstName, String lastName, long pesel, int age)throws PeselException, NullNameException, AgeException {
        if(firstName.length() > 0 && lastName.length() > 0) {this.firstName = firstName;
        this.lastName = lastName;}else throw new NullNameException("Imię i nazwisko nie mogą być puste");
        if(pesel > 9999999999L){this.pesel = pesel;}else throw new PeselException("Pesel musi mieć 11 cyfr");
        if(age >= 0)this.age = age;
        else throw new AgeException("Wiek nie może być ujemny");
    }

    public String getFirstName() {
        return firstName;
    }

    public int getAge() {
        return age;
    }

    public String getLastName() {
        return lastName;
    }

    public long getPesel() {
        return pesel;
    }

    public void setFirstName(String firstName) throws NullNameException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new NullNameException("Imię nie może być puste");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) throws NullNameException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new NullNameException("Nazwisko nie może być puste");
        }
        this.lastName = lastName;
    }

    public void setAge(int age) throws AgeException {
        if (age < 0) {
            throw new AgeException("Wiek pacjenta musi być większy niż 0.");
        }
        this.age = age;
    }

    public void setPesel(long pesel) throws PeselException {
        String peselStr = String.valueOf(pesel);
        if (peselStr.length() != 11 || pesel < 0) {
            throw new PeselException("Pesel musi składać się dokładnie z 11 cyfr.");
        }
        this.pesel = pesel;
    }

}
