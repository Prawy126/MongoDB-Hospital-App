package org.example.projekt;

import backend.klasy.Patient;
import backend.mongo.PatientRepository;
import backend.status.Diagnosis;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Kontroler logiki rejestracji nowego pacjenta w systemie.
 * Odpowiada za walidację danych, tworzenie pacjenta oraz interakcję z repozytorium.
 */
public class RegisterPanelController {
    private TextField nameField;
    private TextField surnameField;
    private TextField peselField;
    private DatePicker birthDatePicker;
    private TextField addressField;
    private PasswordField passwordField;

    private final PatientRepository patientRepository;

    /**
     * Konstruktor inicjalizujący kontroler z repozytorium pacjentów.
     * @param patientRepository repozytorium pacjentów
     */
    public RegisterPanelController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Ustawia pole imienia.
     * @param nameField pole tekstowe imienia
     */
    public void setNameField(TextField nameField) {
        this.nameField = nameField;
    }

    /**
     * Ustawia pole nazwiska.
     * @param surnameField pole tekstowe nazwiska
     */
    public void setSurnameField(TextField surnameField) {
        this.surnameField = surnameField;
    }

    /**
     * Ustawia pole PESEL.
     * @param peselField pole tekstowe PESEL
     */
    public void setPeselField(TextField peselField) {
        this.peselField = peselField;
    }

    /**
     * Ustawia kontrolkę daty urodzenia.
     * @param birthDatePicker kontrolka wyboru daty
     */
    public void setBirthDatePicker(DatePicker birthDatePicker) {
        this.birthDatePicker = birthDatePicker;
    }

    /**
     * Ustawia pole adresu.
     * @param addressField pole tekstowe adresu
     */
    public void setAddressField(TextField addressField) {
        this.addressField = addressField;
    }

    /**
     * Ustawia pole hasła.
     * @param passwordField pole hasła
     */
    public void setPasswordField(PasswordField passwordField) {
        this.passwordField = passwordField;
    }

    /**
     * Konfiguruje walidację formularza rejestracyjnego (PESEL i data urodzenia).
     */
    public void setupFormValidation() {
        // Walidacja PESEL
        peselField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                peselField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Walidacja daty urodzenia
        birthDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isAfter(LocalDate.now())) {
                birthDatePicker.setValue(null);
                showAlert(Alert.AlertType.ERROR, "Błąd", "Data urodzenia nie może być w przyszłości");
            }
        });
    }

    /**
     * Obsługuje próbę zatwierdzenia formularza rejestracji.
     * @param stage scena do zamknięcia po pomyślnym zarejestrowaniu
     */
    public void handleSubmit(Stage stage) {
        try {
            validateForm();
            createPatient();
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Rejestracja zakończona pomyślnie!");
            stage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", e.getMessage());
        }
    }

    /**
     * Sprawdza poprawność danych wprowadzonych do formularza.
     * @throws Exception jeśli któreś pole jest nieprawidłowe
     */
    private void validateForm() throws Exception {
        if (nameField.getText().trim().isEmpty()) {
            throw new NullNameException("Imię nie może być puste");
        }
        if (surnameField.getText().trim().isEmpty()) {
            throw new NullNameException("Nazwisko nie może być puste");
        }
        if (peselField.getText().length() != 11) {
            throw new PeselException("PESEL musi mieć dokładnie 11 cyfr");
        }
        if (birthDatePicker.getValue() == null) {
            throw new AgeException("Data urodzenia jest wymagana");
        }

        // Sprawdzenie czy wiek jest większy od 0 używając metody z klasy Patient
        int age = Patient.calculateAge(birthDatePicker.getValue());
        if (age <= 0) {
            throw new AgeException("Wiek pacjenta musi być większy niż 0");
        }

        if (addressField.getText().trim().isEmpty()) {
            throw new Exception("Adres nie może być pusty");
        }
        if (passwordField.getText().trim().isEmpty()) {
            throw new Exception("Hasło nie może być puste");
        }
    }

    /**
     * Tworzy i zapisuje nowego pacjenta na podstawie danych formularza.
     * @throws PeselException jeśli PESEL jest nieprawidłowy
     * @throws NullNameException jeśli imię lub nazwisko są puste
     * @throws AgeException jeśli wiek pacjenta jest niepoprawny
     */
    private void createPatient() throws PeselException, NullNameException, AgeException {
        int age = Patient.calculateAge(birthDatePicker.getValue());

        Patient patient = new Patient.Builder()
                .firstName(nameField.getText().trim())
                .lastName(surnameField.getText().trim())
                .pesel(Long.parseLong(peselField.getText()))
                .birthDate(birthDatePicker.getValue())
                .address(addressField.getText().trim())
                .age(age)
                .plainPassword(passwordField.getText())
                .diagnosis(Diagnosis.AWAITING)
                .build();

        patientRepository.createPatient(patient);
    }

    /**
     * Wyświetla komunikat w formie alertu.
     * @param type typ alertu (informacja, błąd itp.)
     * @param title tytuł okna alertu
     * @param message treść wiadomości
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}