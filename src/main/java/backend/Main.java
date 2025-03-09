package backend;

public class Main {

    public static void main(String[] args) {
        // Tworzenie nowego pacjenta
        Patient patient = new Patient("Jan Kowalski", 45, "Nadciśnienie");
        MorphiaDatabaseConnector.savePatient(patient);
        System.out.println("Zapisano pacjenta: " + patient.getName());

        // Wyszukiwanie pacjenta
        Patient foundPatient = MorphiaDatabaseConnector.findPatientByName("Jan Kowalski");
        if (foundPatient != null) {
            System.out.println("Znaleziono pacjenta: " + foundPatient.getName() + ", wiek: " + foundPatient.getAge() + ", stan zdrowia: " + foundPatient.getMedicalCondition());
        } else {
            System.out.println("Nie znaleziono pacjenta.");
        }

        // Wyświetlanie wszystkich pacjentów
        System.out.println("Lista wszystkich pacjentów:");
        for (Patient p : MorphiaDatabaseConnector.getAllPatients()) {
            System.out.println("- " + p.getName() + ", wiek: " + p.getAge() + ", stan zdrowia: " + p.getMedicalCondition());
        }

        // Usuwanie pacjenta
        MorphiaDatabaseConnector.deletePatientByName("Jan Kowalski");
        System.out.println("Pacjent został usunięty.");
    }
}
