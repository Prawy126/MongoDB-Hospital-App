package org.example.projekt;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Formularz rejestracji nowego użytkownika.
 */
public class RegisterPanel {

    /**
     * Uruchamia okno rejestracji.
     * @param stage okno JavaFX do wyświetlenia formularza
     */
    public void start(Stage stage) {
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

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        GridPane.setConstraints(emailLabel, 0, 3);
        GridPane.setConstraints(emailField, 1, 3);

        Label userLabel = new Label("Login:");
        TextField userField = new TextField();
        GridPane.setConstraints(userLabel, 0, 4);
        GridPane.setConstraints(userField, 1, 4);

        Label passLabel = new Label("Hasło:");
        PasswordField passField = new PasswordField();
        GridPane.setConstraints(passLabel, 0, 5);
        GridPane.setConstraints(passField, 1, 5);

        Button submitBtn = new Button("Zarejestruj");
        submitBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white;");
        GridPane.setConstraints(submitBtn, 0, 6);

        Button cancelBtn = new Button("Anuluj");
        cancelBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        GridPane.setConstraints(cancelBtn, 1, 6);

        grid.getChildren().addAll(
                headerLabel,
                nameLabel, nameField,
                surnameLabel, surnameField,
                emailLabel, emailField,
                userLabel, userField,
                passLabel, passField,
                submitBtn, cancelBtn
        );

        submitBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rejestracja");
            alert.setHeaderText(null);
            alert.setContentText("Rejestracja zakończona sukcesem!");
            alert.showAndWait();
            stage.close();
        });

        cancelBtn.setOnAction(e -> stage.close());

        Scene scene = new Scene(grid, 400, 400);
        stage.setTitle("Rejestracja nowego użytkownika");
        stage.setScene(scene);
        stage.show();
    }
}