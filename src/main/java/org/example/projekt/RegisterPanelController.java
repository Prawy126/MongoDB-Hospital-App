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
import java.time.Period;

public class RegisterPanelController {
    private TextField nameField;
    private TextField surnameField;
    private TextField peselField;
    private DatePicker birthDatePicker;
    private TextField addressField;
    private PasswordField passwordField;

    private final PatientRepository patientRepository;

    public RegisterPanelController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Settery dla pól formularza
    public void setNameField(TextField nameField) {
        this.nameField = nameField;
    }

    public void setSurnameField(TextField surnameField) {
        this.surnameField = surnameField;
    }

    public void setPeselField(TextField peselField) {
        this.peselField = peselField;
    }

    public void setBirthDatePicker(DatePicker birthDatePicker) {
        this.birthDatePicker = birthDatePicker;
    }

    public void setAddressField(TextField addressField) {
        this.addressField = addressField;
    }

    public void setPasswordField(PasswordField passwordField) {
        this.passwordField = passwordField;
    }

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

        // Sprawdzenie czy wiek jest większy od 0
        int age = calculateAge(birthDatePicker.getValue());
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

    private void createPatient() throws PeselException, NullNameException, AgeException {
        // Obliczenie wieku na podstawie daty urodzenia
        int age = calculateAge(birthDatePicker.getValue());

        Patient patient = new Patient.Builder()
                .firstName(nameField.getText().trim())
                .lastName(surnameField.getText().trim())
                .pesel(Long.parseLong(peselField.getText()))
                .birthDate(birthDatePicker.getValue())
                .address(addressField.getText().trim())
                .age(age)  // Przekazanie obliczonego wieku
                .plainPassword(passwordField.getText())
                .diagnosis(Diagnosis.AWAITING)
                .build();

        patientRepository.createPatient(patient);
    }

    /**
     * Oblicza wiek na podstawie daty urodzenia.
     * @param birthDate data urodzenia
     * @return wiek w latach
     */
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}