package backend.status;

/**
*
* Enum odpowiedzialny za dni tygodnia
* Każdy dzień tygodnia ma tutaj swoją odpowiednią wartość
* */
public enum Day {
    MONDAY("Poniedziałek"),
    TUESDAY("Wtorek"),
    WEDNESDAY("Środa"),
    THURSDAY("Czwartek"),
    FRIDAY("Piątek"),
    SATURDAY("Sobota"),
    SUNDAY("Niedziela");

    private final String description;

    Day(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
