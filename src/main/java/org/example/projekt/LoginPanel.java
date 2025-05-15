package org.example.projekt;

import backend.klasy.Doctor;
import backend.klasy.Login;
import backend.klasy.Patient;
import backend.mongo.MongoDatabaseConnector;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
// lekarz do testowania czyli pierwszego konatktu
//90030224046
public class LoginPanel extends Application {

    private Button loginBtn;
    private final Login loginService = new Login();
// Upewnij się, że robisz notatki w kodzie
    @Override
    public void start(Stage primaryStage) {

        // Tworzenie głównego gridu
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: lightblue;");
        grid.setAlignment(Pos.CENTER);  // Centrowanie całego gridu

        // Nagłówek
        Label header = new Label("Szpital");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        GridPane.setConstraints(header, 0, 0, 3, 1);
        GridPane.setMargin(header, new Insets(0, 0, 20, 0));
        GridPane.setHalignment(header, HPos.CENTER);  // Centrowanie nagłówka

        // Pola wprowadzania danych
        Label userLabel = new Label("PESEL:");
        GridPane.setHalignment(userLabel, HPos.RIGHT);  // Wyrównanie etykiety w prawo
        TextField userField = new TextField();
        GridPane.setHalignment(userField, HPos.CENTER);  // Centrowanie pola tekstowego

        Label passLabel = new Label("Hasło:");
        GridPane.setHalignment(passLabel, HPos.RIGHT);  // Wyrównanie etykiety w prawo
        PasswordField passField = new PasswordField();
        GridPane.setHalignment(passField, HPos.CENTER);  // Centrowanie pola hasła

        // Kontener na przyciski (główny)
        VBox buttonsContainer = new VBox(10);
        buttonsContainer.setAlignment(Pos.CENTER);

        // Kontener na górną linię przycisków
        HBox topButtonsBox = new HBox(10);
        topButtonsBox.setAlignment(Pos.CENTER);

        // Przyciski
        loginBtn = new Button("Zaloguj");
        loginBtn.setPrefWidth(100);
        loginBtn.setMinSize(100, 30);
        loginBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        Button registerBtn = new Button("Zarejestruj się");
        registerBtn.setPrefWidth(100);
        registerBtn.setMinSize(100, 30);
        registerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button exitBtn = new Button("Wyjście");
        exitBtn.setPrefWidth(100);
        exitBtn.setMinSize(100, 30);
        exitBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white;");

        // Organizacja przycisków
        topButtonsBox.getChildren().addAll(loginBtn, registerBtn);
        buttonsContainer.getChildren().addAll(topButtonsBox, exitBtn);

        // Dodanie elementów do gridu
        GridPane.setConstraints(userLabel, 0, 1);
        GridPane.setConstraints(userField, 1, 1, 2, 1);
        GridPane.setConstraints(passLabel, 0, 2);
        GridPane.setConstraints(passField, 1, 2, 2, 1);
        GridPane.setConstraints(buttonsContainer, 0, 4, 3, 1);

        grid.getChildren().addAll(header, userLabel, userField, passLabel, passField, buttonsContainer);

        loginBtn.setOnAction(e -> {
            String login = userField.getText().trim();
            String password = passField.getText().trim();

            Login.Role role = loginService.authenticate(login, password);

            if (role == null) {
                showAlert(Alert.AlertType.ERROR, "Błąd logowania",
                        "Niepoprawny PESEL / hasło lub użytkownik nie istnieje.");
                return;
            }

            switch (role) {
                case ADMIN   -> {
                    openAdminPanel();
                }
                case DOCTOR -> {
                    Doctor doctor = loginService.getAuthenticatedDoctor();
                    openDoctorPanel(doctor);
                }
                case DOCTOR_FIRST -> {
                    Doctor doctor = loginService.getAuthenticatedDoctor();
                    openDoctorFirstContactPanel(doctor);
                }
                case PATIENT -> {
                    Patient patient = loginService.getAuthenticatedPatient();
                    openPatientPanel(patient);
                }
            }
        });

        exitBtn.setOnAction(e -> {
            MongoDatabaseConnector.close();
            System.exit(0);
        });

        registerBtn.setOnAction(e -> new RegisterPanel().start(new Stage()));

        primaryStage.setScene(new Scene(grid, 500, 350));
        primaryStage.setTitle("Panel Logowania");
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(350);
        primaryStage.show();
    }

    private void openDoctorFirstContactPanel(Doctor doctor) {
        Stage doctorStage = new Stage();
        new DoctorFirstContactPanel(doctorStage, doctor);
        closeLoginWindow();
    }

    private void openAdminPanel()  { new AdminPanel(new Stage());  closeLoginWindow(); }
    private void openDoctorPanel(Doctor doctor) {
        Stage doctorStage = new Stage();
        new DoctorPanel(doctorStage, doctor);
        closeLoginWindow();
    }

    private void openPatientPanel(Patient patient) {
        Stage patientStage = new Stage();
        new PatientPanel(patientStage, patient);
        closeLoginWindow();
    }

    private void closeLoginWindow() {
        ((Stage) loginBtn.getScene().getWindow()).close();
    }

    private void animateFadeIn(GridPane pane, int ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), pane);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}