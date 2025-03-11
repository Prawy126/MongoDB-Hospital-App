package backend;

public class Person {
    private String firstName;
    private String lastName;
    private int age;
    private int pesel;
    Person(){

    };
    public Person(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName= lastName;
    }
    public Person(String firstName, String lastName, int pesel){
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
    }
    public Person(String firstName, String lastName, int pesel, int age){
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.age = age;
    }

    public String getFistName() {
        return firstName;
    }

    public int getAge() {
        return age;
    }

    public String getLastName() {
        return lastName;
    }

    public int getPesel() {
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

    public void setPesel(int pesel) {
        this.pesel = pesel;
    }
}
