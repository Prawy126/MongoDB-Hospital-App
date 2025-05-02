package org.example.projekt;

import backend.klasy.Doctor;
import backend.klasy.Login;
import backend.klasy.Patient;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginPanel extends Application {

    private Button loginBtn;
    private final Login loginService = new Login();

    @Override
    public void start(Stage primaryStage) {
        
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: lightblue;");

        Label header = new Label("Szpital");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        GridPane.setConstraints(header, 0, 0, 3, 1);
        GridPane.setMargin(header, new Insets(0, 0, 20, 0));

        Label userLabel = new Label("PESEL:");
        TextField userField = new TextField();

        Label passLabel = new Label("Hasło:");
        PasswordField passField = new PasswordField();

        loginBtn = new Button("Zaloguj się");
        loginBtn.setPrefWidth(150);
        loginBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        Button exitBtn = new Button("Wyjście");
        exitBtn.setPrefWidth(150);
        exitBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white;");

        Button registerBtn = new Button("Zarejestruj się");
        registerBtn.setPrefWidth(150);
        registerBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        GridPane.setConstraints(userLabel, 0, 1); GridPane.setConstraints(userField, 1, 1, 2, 1);
        GridPane.setConstraints(passLabel, 0, 2); GridPane.setConstraints(passField, 1, 2, 2, 1);
        GridPane.setConstraints(loginBtn, 0, 4);
        GridPane.setConstraints(registerBtn, 1, 4);
        GridPane.setConstraints(exitBtn, 0, 5);

        grid.getChildren().addAll(header, userLabel, userField, passLabel, passField,
                loginBtn, registerBtn, exitBtn);
        animateFadeIn(grid, 1000);

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
                case ADMIN   -> openAdminPanel();
                case DOCTOR -> {
                    Doctor doctor = loginService.getAuthenticatedDoctor();
                    openDoctorPanel(doctor);
                }
                case PATIENT -> {
                    Patient patient = loginService.getAuthenticatedPatient();
                    openPatientPanel(patient);
                }
            }
        });

        exitBtn.setOnAction(e -> System.exit(0));
        registerBtn.setOnAction(e -> new RegisterPanel().start(new Stage()));

        primaryStage.setScene(new Scene(grid, 500, 350));
        primaryStage.setTitle("Panel Logowania");
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(350);
        primaryStage.show();
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
