package org.example.projekt;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Utworzenie kontenera GridPane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Elementy interfejsu
        Label userLabel = new Label("Użytkownik:");
        GridPane.setConstraints(userLabel, 0, 0);

        TextField userField = new TextField();
        GridPane.setConstraints(userField, 1, 0);

        Label passLabel = new Label("Hasło:");
        GridPane.setConstraints(passLabel, 0, 1);

        PasswordField passField = new PasswordField();
        GridPane.setConstraints(passField, 1, 1);

        // Przycisk Zaloguj
        Button loginBtn = new Button("Zaloguj się");
        loginBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginBtn.setPrefWidth(120);
        GridPane.setConstraints(loginBtn, 0, 2);

        // Przycisk Wyjście
        Button exitBtn = new Button("Wyjście");
        exitBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white;");
        exitBtn.setPrefWidth(120);
        GridPane.setConstraints(exitBtn, 1, 2);

        // Dodanie elementów do kontenera
        grid.getChildren().addAll(userLabel, userField, passLabel, passField, loginBtn, exitBtn);

        // Obsługa zdarzeń
        loginBtn.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();

            // Prosta weryfikacja (przykład)
            if ("admin".equals(user) && "password".equals(pass)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zalogowano pomyślnie!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowe dane!");
            }
        });

        exitBtn.setOnAction(e -> {
            System.exit(0);
        });

        // Ustawienia sceny
        Scene scene = new Scene(grid, 350, 200);
        primaryStage.setTitle("Panel Logowania");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(350);
        primaryStage.setMinHeight(200);
        primaryStage.show();
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