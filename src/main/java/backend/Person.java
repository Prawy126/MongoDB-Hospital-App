package backend;

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

    public Person(String firstName, String lastName, long pesel, int age)throws PeselException, NullNameException {
        if(firstName.length() > 0 && lastName.length() > 0) {this.firstName = firstName;
        this.lastName = lastName;}else throw new NullNameException("Imię i nazwisko nie mogą być puste");
        if(pesel > 9999999999L){this.pesel = pesel;}else throw new PeselException("Pesel musi mieć 11 cyfr");
        this.age = age;
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
        if(firstName.length()>0)this.firstName = firstName;
        else throw new NullNameException("Imię nie może być puste");
    }

    public void setLastName(String lastName) throws NullNameException {
        if(lastName.length()>0)this.lastName = lastName;
        else throw new NullNameException("Nazwisko nie może być puste");
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPesel(long pesel)throws PeselException {
        if(pesel > 9999999999L){this.pesel = pesel;}else throw new PeselException("Pesel musi mieć 11 cyfr");

    }
}
