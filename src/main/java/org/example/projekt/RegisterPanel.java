package org.example.projekt;

import org.example.projekt.RegisterPanelController;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Formularz rejestracji nowego użytkownika.
 */
public class RegisterPanel {
    private final PatientRepository patientRepository;
    private RegisterPanelController controller;

    public RegisterPanel() {
        // Pobierz repozytorium pacjentów z połączenia MongoDB
        this.patientRepository = new PatientRepository(MongoDatabaseConnector.connectToDatabase());
    }

    /**
     * Uruchamia okno rejestracji.
     * @param stage okno JavaFX do wyświetlenia formularza
     */
    public void start(Stage stage) {
        // Tworzenie kontrolera
        controller = new RegisterPanelController(patientRepository);

        // Tworzenie interfejsu użytkownika
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: lightyellow;");

        Label headerLabel = new Label("Formularz rejestracji");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        GridPane.setConstraints(headerLabel, 0, 0, 2, 1);

        Label nameLabel = new Label("Imię:");
        TextField nameField = new TextField();
        GridPane.setConstraints(nameLabel, 0, 1);
        GridPane.setConstraints(nameField, 1, 1);

        Label surnameLabel = new Label("Nazwisko:");
        TextField surnameField = new TextField();
        GridPane.setConstraints(surnameLabel, 0, 2);
        GridPane.setConstraints(surnameField, 1, 2);

        Label peselLabel = new Label("PESEL:");
        TextField peselField = new TextField();
        GridPane.setConstraints(peselLabel, 0, 3);
        GridPane.setConstraints(peselField, 1, 3);

        Label birthDateLabel = new Label("Data urodzenia:");
        DatePicker birthDatePicker = new DatePicker();
        GridPane.setConstraints(birthDateLabel, 0, 4);
        GridPane.setConstraints(birthDatePicker, 1, 4);

        Label addressLabel = new Label("Adres zamieszkania:");
        TextField addressField = new TextField();
        GridPane.setConstraints(addressLabel, 0, 5);
        GridPane.setConstraints(addressField, 1, 5);

        Label passLabel = new Label("Hasło:");
        PasswordField passField = new PasswordField();
        GridPane.setConstraints(passLabel, 0, 6);
        GridPane.setConstraints(passField, 1, 6);

        Button submitBtn = new Button("Zarejestruj");
        submitBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white;");
        GridPane.setConstraints(submitBtn, 0, 7);

        Button cancelBtn = new Button("Anuluj");
        cancelBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        GridPane.setConstraints(cancelBtn, 1, 7);

        grid.getChildren().addAll(
                headerLabel,
                nameLabel, nameField,
                surnameLabel, surnameField,
                peselLabel, peselField,
                birthDateLabel, birthDatePicker,
                addressLabel, addressField,
                passLabel, passField,
                submitBtn, cancelBtn
        );

        // Powiązanie pól formularza z kontrolerem
        controller.setNameField(nameField);
        controller.setSurnameField(surnameField);
        controller.setPeselField(peselField);
        controller.setBirthDatePicker(birthDatePicker);
        controller.setAddressField(addressField);
        controller.setPasswordField(passField);

        // Powiązanie przycisków z kontrolerem
        submitBtn.setOnAction(e -> controller.handleSubmit(stage));
        cancelBtn.setOnAction(e -> stage.close());

        // Inicjalizacja walidacji formularza
        controller.setupFormValidation();

        Scene scene = new Scene(grid, 400, 450);
        stage.setTitle("Rejestracja nowego użytkownika");
        stage.setScene(scene);
        stage.show();
    }
}