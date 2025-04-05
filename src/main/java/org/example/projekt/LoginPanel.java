package org.example.projekt;

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

    // Sztywne dane logowania
    private final String ADMIN_LOGIN = "admin";
    private final String ADMIN_PASSWORD = "admin";

    private final String DOCTOR_LOGIN = "doktor";
    private final String DOCTOR_PASSWORD = "doktor";

    private final String PATIENT_LOGIN = "pacjent";
    private final String PATIENT_PASSWORD = "pacjent";

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: lightblue;");

        Label headerLabel = new Label("Szpital");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        GridPane.setConstraints(headerLabel, 0, 0, 3, 1);
        GridPane.setMargin(headerLabel, new Insets(0, 0, 20, 0));

        Label userLabel = new Label("Użytkownik:");
        GridPane.setConstraints(userLabel, 0, 1);

        TextField userField = new TextField();
        GridPane.setConstraints(userField, 1, 1, 2, 1);

        Label passLabel = new Label("Hasło:");
        GridPane.setConstraints(passLabel, 0, 2);

        PasswordField passField = new PasswordField();
        GridPane.setConstraints(passField, 1, 2, 2, 1);

        Label spacer = new Label();
        GridPane.setConstraints(spacer, 0, 3);
        GridPane.setMargin(spacer, new Insets(20, 0, 0, 0));

        loginBtn = new Button("Zaloguj się");
        loginBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        loginBtn.setPrefWidth(150);
        GridPane.setConstraints(loginBtn, 0, 4);

        Button exitBtn = new Button("Wyjście");
        exitBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white;");
        exitBtn.setPrefWidth(150);
        GridPane.setConstraints(exitBtn, 0, 5);

        Button registerBtn = new Button("Zarejestruj się");
        registerBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        registerBtn.setPrefWidth(150);
        GridPane.setConstraints(registerBtn, 1, 4);

        grid.getChildren().addAll(
                headerLabel,
                userLabel, userField,
                passLabel, passField,
                spacer,
                loginBtn, exitBtn, registerBtn
        );

        animateFadeIn(grid, 1000);

        loginBtn.setOnAction(e -> {
            String user = userField.getText().trim();
            String pass = passField.getText().trim();

            if (ADMIN_LOGIN.equals(user) && ADMIN_PASSWORD.equals(pass)) {
                openAdminPanel();
            } else if (DOCTOR_LOGIN.equals(user) && DOCTOR_PASSWORD.equals(pass)) {
                openDoctorPanel();
            } else if (PATIENT_LOGIN.equals(user) && PATIENT_PASSWORD.equals(pass)) {
                openPatientPanel();
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd logowania", "Nieprawidłowy login lub hasło!");
            }
        });

        exitBtn.setOnAction(e -> System.exit(0));

        registerBtn.setOnAction(e -> {
            Stage registerStage = new Stage();
            new RegisterPanel().start(registerStage);
        });

        Scene scene = new Scene(grid, 500, 350);
        primaryStage.setTitle("Panel Logowania");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(350);
        primaryStage.show();
    }

    private void openAdminPanel() {
        Stage adminStage = new Stage();
        new AdminPanel(adminStage);
        closeLoginWindow();
    }

    private void openDoctorPanel() {
        Stage doctorStage = new Stage();
        new DoctorPanel(doctorStage);
        closeLoginWindow();
    }

    private void openPatientPanel() {
        Stage patientStage = new Stage();
        new PatientPanel(patientStage);
        closeLoginWindow();
    }

    private void closeLoginWindow() {
        Stage currentStage = (Stage) loginBtn.getScene().getWindow();
        currentStage.close();
    }

    private void animateFadeIn(GridPane element, int duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), element);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
