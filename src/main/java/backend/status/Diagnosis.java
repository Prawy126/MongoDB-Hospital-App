package backend.status;

/**
 * Enum dla diagozy pacjentów aktualnie posiada 5 w przyszłości może to rozbudujemy
 * <p>CARDIAC</p>
 * <p>NEUROLOGICAL</p>
 * <p>SURGICAL</p>
 * <p>PREGNANCY</p>
 * <p>GENERAL</p>*/
public enum Diagnosis {
    CARDIAC("Choroba serca", TypeOfRoom.CARDIOLOGY),
    NEUROLOGICAL("Choroba układu nerwowego", TypeOfRoom.NEUROLOGY),
    SURGICAL("Wymagana operacja", TypeOfRoom.SURGICAL),
    PREGNANCY("Ciąża", TypeOfRoom.MATERNITY),
    GENERAL("Ogólne schorzenie", TypeOfRoom.INTERNAL);

    private final String description;
    private final TypeOfRoom department;

    Diagnosis(String description, TypeOfRoom department) {
        this.description = description;
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public TypeOfRoom getDepartment() {
        return department;
    }
}