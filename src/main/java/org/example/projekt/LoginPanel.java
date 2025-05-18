package org.example.projekt;

import backend.klasy.Doctor;
import backend.klasy.Login;
import backend.klasy.Patient;
import backend.mongo.DoctorRepository;
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

/**
 * Klasa reprezentująca panel logowania użytkownika do systemu szpitalnego.
 * Obsługuje logowanie, rejestrację oraz przekierowania do odpowiednich paneli.
 */
public class LoginPanel extends Application {

    private Button loginBtn;
    private final Login loginService = new Login();

    /**
     * Główna metoda inicjalizacji interfejsu logowania.
     * @param primaryStage główna scena aplikacji
     */
    @Override
    public void start(Stage primaryStage) {

        // Tworzenie głównego gridu
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: lightblue;");
        grid.setAlignment(Pos.CENTER);

        // Nagłówek
        Label header = new Label("Szpital");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        GridPane.setConstraints(header, 0, 0, 3, 1);
        GridPane.setMargin(header, new Insets(0, 0, 20, 0));
        GridPane.setHalignment(header, HPos.CENTER);

        // Pola wprowadzania danych
        Label userLabel = new Label("PESEL:");
        GridPane.setHalignment(userLabel, HPos.RIGHT);
        TextField userField = new TextField();
        GridPane.setHalignment(userField, HPos.CENTER);

        Label passLabel = new Label("Hasło:");
        GridPane.setHalignment(passLabel, HPos.RIGHT);
        PasswordField passField = new PasswordField();
        GridPane.setHalignment(passField, HPos.CENTER);

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

    /**
     * Otwiera panel lekarza pierwszego kontaktu po pomyślnym logowaniu.
     * @param doctor obiekt zalogowanego lekarza
     */
    private void openDoctorFirstContactPanel(Doctor doctor) {
        Stage doctorStage = new Stage();
        DoctorRepository doctorRepo = new DoctorRepository(MongoDatabaseConnector.connectToDatabase());
        new DoctorFirstContactPanel(doctorStage, doctor, doctorRepo);
        closeLoginWindow();
    }

    /**
     * Otwiera panel administratora.
     */
    private void openAdminPanel()  {
        new AdminPanel(new Stage());
        closeLoginWindow();
    }

    /**
     * Otwiera panel lekarza po zalogowaniu.
     * @param doctor obiekt lekarza
     */
    private void openDoctorPanel(Doctor doctor) {
        Stage doctorStage = new Stage();
        new DoctorPanel(doctorStage, doctor);
        closeLoginWindow();
    }

    /**
     * Otwiera panel pacjenta po zalogowaniu.
     * @param patient obiekt pacjenta
     */
    private void openPatientPanel(Patient patient) {
        Stage patientStage = new Stage();
        new PatientPanel(patientStage, patient);
        closeLoginWindow();
    }

    /**
     * Zamyka bieżące okno logowania.
     */
    private void closeLoginWindow() {
        ((Stage) loginBtn.getScene().getWindow()).close();
    }

    /**
     * Uruchamia animację stopniowego pojawienia się komponentu.
     * @param pane komponent do animacji
     * @param ms czas trwania animacji w milisekundach
     */
    private void animateFadeIn(GridPane pane, int ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), pane);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    /**
     * Wyświetla okno dialogowe z komunikatem.
     * @param type typ alertu
     * @param title tytuł okna
     * @param msg treść wiadomości
     */
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    /**
     * Główna metoda uruchamiająca aplikację.
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        launch(args);
    }
}