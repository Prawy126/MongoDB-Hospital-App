package backend;

public class Person {
    private String firstName;
    private String lastName;
    private int age;
    private long pesel;
    Person(){

    };
    public Person(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName= lastName;
    }
    public Person(String firstName, String lastName, long pesel) throws PeselException {
        this.firstName = firstName;
        this.lastName = lastName;
        if(pesel > 9999999999L){this.pesel = pesel;}else throw new PeselException("Pesel musi mieć 11 cyfr");
    }

    public Person(String firstName, String lastName, long pesel, int age)throws PeselException {
        this.firstName = firstName;
        this.lastName = lastName;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPesel(long pesel)throws PeselException {
        if(pesel > 9999999999L){this.pesel = pesel;}else throw new PeselException("Pesel musi mieć 11 cyfr");

    }
}
