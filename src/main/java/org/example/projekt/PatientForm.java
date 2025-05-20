package org.example.projekt;

import backend.klasy.Patient;
import backend.status.Diagnosis;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Formularz do tworzenia i edycji danych pacjenta.
 */
public class PatientForm {

    /**
     * Wyświetla formularz edycji/dodawania pacjenta.
     * @param existingPatient pacjent do edycji lub null w przypadku dodawania nowego
     * @param onSave akcja wykonywana po kliknięciu "Zapisz"
     */
    public static void showForm(Patient existingPatient, Consumer<Patient> onSave) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(existingPatient == null ? "Dodaj pacjenta" : "Edytuj pacjenta");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(15));

        // Pola formularza
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Wprowadź imię");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Wprowadź nazwisko");

        TextField peselField = new TextField();
        peselField.setPromptText("Wprowadź 11 cyfr PESEL");

        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setPromptText("Wybierz datę urodzenia");

        Label ageValueLabel = new Label("");

        TextField addressField = new TextField();
        addressField.setPromptText("Wprowadź adres zamieszkania");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Wprowadź hasło");

        // Wypełnij pola, jeśli edytujemy istniejącego pacjenta
        if (existingPatient != null) {
            firstNameField.setText(existingPatient.getFirstName());
            lastNameField.setText(existingPatient.getLastName());
            peselField.setText(String.valueOf(existingPatient.getPesel()));
            birthDatePicker.setValue(existingPatient.getBirthDate());
            ageValueLabel.setText(String.valueOf(existingPatient.getAge()) + " lat");
            addressField.setText(existingPatient.getAddress());

            // Pole hasła pozostaje puste przy edycji
            passwordField.setDisable(true);
            passwordField.setPromptText("Hasło pozostaje bez zmian");
        }

        // Aktualizuj wiek automatycznie po zmianie daty urodzenia
        birthDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int age = Patient.calculateAge(newValue);
                ageValueLabel.setText(String.valueOf(age) + " lat");
            } else {
                ageValueLabel.setText("");
            }
        });

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        HBox buttonBox = new HBox(10, saveButton, cancelButton);

        saveButton.setOnAction(e -> {
            // Walidacja pól
            StringBuilder errorMessage = new StringBuilder();

            if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
                errorMessage.append("• Imię nie może być puste\n");
            }

            if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
                errorMessage.append("• Nazwisko nie może być puste\n");
            }

            if (addressField.getText() == null || addressField.getText().trim().isEmpty()) {
                errorMessage.append("• Adres nie może być pusty\n");
            }

            if (birthDatePicker.getValue() == null) {
                errorMessage.append("• Data urodzenia jest wymagana\n");
            } else if (birthDatePicker.getValue().isAfter(LocalDate.now())) {
                errorMessage.append("• Data urodzenia nie może być w przyszłości\n");
            }

            String peselText = peselField.getText();
            if (peselText == null || peselText.trim().isEmpty()) {
                errorMessage.append("• PESEL nie może być pusty\n");
            } else {
                try {
                    long pesel = Long.parseLong(peselText);
                    if (pesel < 10000000000L || pesel > 99999999999L) {
                        errorMessage.append("• PESEL musi składać się dokładnie z 11 cyfr\n");
                    }
                } catch (NumberFormatException ex) {
                    errorMessage.append("• PESEL musi zawierać tylko cyfry\n");
                }
            }

            if (existingPatient == null && (passwordField.getText() == null || passwordField.getText().isEmpty())) {
                errorMessage.append("• Hasło jest wymagane dla nowego pacjenta\n");
            }

            // Jeśli są błędy, wyświetl je i przerwij zapisywanie
            if (errorMessage.length() > 0) {
                showValidationError("Błędy walidacji", errorMessage.toString());
                return;
            }

            try {
                int age = birthDatePicker.getValue() != null ?
                        Patient.calculateAge(birthDatePicker.getValue()) : 0;

                Patient.Builder builder = new Patient.Builder();

                if (existingPatient != null) {
                    builder.withId(existingPatient.getId())
                            .diagnosis(existingPatient.getDiagnosis()); // Zachowaj istniejącą diagnozę

                    builder.passwordHash(existingPatient.getPasswordHash())
                            .passwordSalt(existingPatient.getPasswordSalt());
                } else {
                    // Dla nowego pacjenta ustaw domyślną diagnozę AWAITING
                    builder.plainPassword(passwordField.getText())
                            .diagnosis(Diagnosis.AWAITING);
                }

                Patient patient = builder
                        .firstName(firstNameField.getText().trim())
                        .lastName(lastNameField.getText().trim())
                        .pesel(Long.parseLong(peselField.getText().trim()))
                        .birthDate(birthDatePicker.getValue())
                        .age(age)
                        .address(addressField.getText().trim())
                        .build();

                onSave.accept(patient);
                stage.close();

            } catch (NullNameException ex) {
                showValidationError("Błąd danych", "Imię lub nazwisko jest nieprawidłowe: " + ex.getMessage());
            } catch (AgeException ex) {
                showValidationError("Błąd wieku", "Wiek pacjenta jest nieprawidłowy: " + ex.getMessage());
            } catch (PeselException ex) {
                showValidationError("Błąd PESEL", "Podany PESEL jest nieprawidłowy: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showValidationError("Błąd formatu", "Nieprawidłowy format liczby: " + ex.getMessage());
            } catch (Exception ex) {
                showValidationError("Nieoczekiwany błąd", "Wystąpił nieoczekiwany błąd: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> stage.close());

        // Układ formularza
        grid.addRow(0, new Label("Imię:"), firstNameField);
        grid.addRow(1, new Label("Nazwisko:"), lastNameField);
        grid.addRow(2, new Label("PESEL:"), peselField);
        grid.addRow(3, new Label("Data urodzenia:"), birthDatePicker);
        grid.addRow(4, new Label("Wiek:"), ageValueLabel);
        grid.addRow(5, new Label("Adres:"), addressField);

        if (existingPatient == null) {
            grid.addRow(6, new Label("Hasło:"), passwordField);
            grid.addRow(7, buttonBox);
        } else {
            grid.addRow(6, buttonBox);
        }

        Scene scene = new Scene(grid, 500, 400);
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(400);
        stage.showAndWait();
    }

    /**
     * Wyświetla okno dialogowe z błędami walidacji.
     */
    private static void showValidationError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}