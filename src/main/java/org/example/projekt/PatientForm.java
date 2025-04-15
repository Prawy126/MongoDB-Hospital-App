package org.example.projekt;

import backend.klasy.Password;
import backend.klasy.Patient;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField peselField = new TextField();
        DatePicker birthDatePicker = new DatePicker();
        TextField ageField = new TextField();
        TextField addressField = new TextField();

        // Prefill danych pacjenta
        if (existingPatient != null) {
            firstNameField.setText(existingPatient.getFirstName());
            lastNameField.setText(existingPatient.getLastName());
            peselField.setText(String.valueOf(existingPatient.getPesel()));
            birthDatePicker.setValue(existingPatient.getBirthDate());
            ageField.setText(String.valueOf(existingPatient.getAge()));
            addressField.setText(existingPatient.getAddress());
        }

        Button saveButton = new Button("Zapisz");
        Password password = new Password("haslo");
        saveButton.setOnAction(e -> {
            try {
                Patient.Builder builder = existingPatient != null
                        ? new Patient.Builder().withId(existingPatient.getId())
                        : new Patient.Builder().plainPassword("haslo");

                Patient patient = builder
                        .firstName(firstNameField.getText())
                        .lastName(lastNameField.getText())
                        .pesel(Long.parseLong(peselField.getText()))
                        .birthDate(birthDatePicker.getValue())
                        .age(Integer.parseInt(ageField.getText()))
                        .address(addressField.getText())
                        .password(password.getHashedPassword())
                        .salt(password.getSalt())
                        .login(firstNameField.getText() + lastNameField.getText())
                        .build();

                onSave.accept(patient);
                stage.close();
            } catch (NullNameException | AgeException | PeselException | NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Niepoprawne dane");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });



        Button cancelButton = new Button("Anuluj");
        cancelButton.setOnAction(e -> stage.close());

        grid.addRow(0, new Label("Imię:"), firstNameField);
        grid.addRow(1, new Label("Nazwisko:"), lastNameField);
        grid.addRow(2, new Label("PESEL:"), peselField);
        grid.addRow(3, new Label("Data urodzenia:"), birthDatePicker);
        grid.addRow(4, new Label("Wiek:"), ageField);
        grid.addRow(5, new Label("Adres:"), addressField);
        grid.addRow(6, saveButton, cancelButton);

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.showAndWait();
    }
}