package backend;

public class Person {
    private String imie;
    private String nazwisko;
    private int wiek;
    private int pesel;
    Person(){

    };
    public Person(String imie, String nazwisko){
        this.imie = imie;
        this.nazwisko = nazwisko;
    }
    public Person(String imie, String nazwisko, int pesel){
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.pesel = pesel;
    }

    public String getImie() {
        return imie;
    }

    public int getWiek() {
        return wiek;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public int getPesel() {
        return pesel;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public void setWiek(int wiek) {
        this.wiek = wiek;
    }

    public void setPesel(int pesel) {
        this.pesel = pesel;
    }
}
