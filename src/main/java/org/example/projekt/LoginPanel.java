package org.example.projekt;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginPanel extends Application {
    private Button loginBtn;
    String login = "admin";
    String password = "admin";

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15); // Zwiększony ogólny odstęp między rzędami
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: lightblue;"); // Ustawienie tła na jasnoniebieski, jak w panelu admina

        // Nagłówek
        Label headerLabel = new Label("Szpital");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        GridPane.setConstraints(headerLabel, 0, 0, 3, 1);
        GridPane.setMargin(headerLabel, new Insets(0, 0, 20, 0));

        // Pola logowania
        Label userLabel = new Label("Użytkownik:");
        GridPane.setConstraints(userLabel, 0, 1);

        TextField userField = new TextField();
        GridPane.setConstraints(userField, 1, 1, 2, 1);

        Label passLabel = new Label("Hasło:");
        GridPane.setConstraints(passLabel, 0, 2);

        PasswordField passField = new PasswordField();
        GridPane.setConstraints(passField, 1, 2, 2, 1);

        // Dodanie pustego rzędu dla większego odstępu
        Label spacer = new Label();
        GridPane.setConstraints(spacer, 0, 3);
        GridPane.setMargin(spacer, new Insets(20, 0, 0, 0)); // Dodatkowy margines

        // Przyciski
        loginBtn = new Button("Zaloguj się");
        loginBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        loginBtn.setPrefWidth(150);
        GridPane.setConstraints(loginBtn, 0, 4); // Przesunięcie do nowego rzędu

        Button exitBtn = new Button("Wyjście");
        exitBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white;");
        exitBtn.setPrefWidth(150);
        GridPane.setConstraints(exitBtn, 0, 5); // Pod przyciskiem Zaloguj się

        Button registerBtn = new Button("Zarejestruj się");
        registerBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        registerBtn.setPrefWidth(150);
        GridPane.setConstraints(registerBtn, 1, 4); // Obok Zaloguj się

        grid.getChildren().addAll(
                headerLabel,
                userLabel, userField,
                passLabel, passField,
                spacer, // Dodany element spacer
                loginBtn, exitBtn, registerBtn
        );

        // Animacja fade-in dla całego formularza
        animateFadeIn(grid, 1000);

        // Obsługa zdarzeń
        loginBtn.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();

            // Sztywne dane logowania
            if (login.equals(user) && password.equals(pass)) {
                // Logowanie pomyślne – otwieramy AdminPanel
                openAdminPanel();
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowe dane!");
            }
        });

        exitBtn.setOnAction(e -> System.exit(0));

        registerBtn.setOnAction(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Rejestracja", "Przejdź do formularza rejestracji");
        });

        Scene scene = new Scene(grid, 500, 350); // Zwiększona wysokość okna
        primaryStage.setTitle("Panel Logowania");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(350);
        primaryStage.show();
    }

    // Funkcja do otwierania panelu administratora
    private void openAdminPanel() {
        Stage adminStage = new Stage();
        new AdminPanel(adminStage);  // Tworzymy obiekt AdminPanel, który otworzy okno administracyjne
        Stage currentStage = (Stage) loginBtn.getScene().getWindow();
        currentStage.close(); // Zamykamy okno logowania
    }

    // Funkcja animacji fade-in
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
