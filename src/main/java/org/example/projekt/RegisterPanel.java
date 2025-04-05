package org.example.projekt;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RegisterPanel {

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
        GridPane.setConstraints(nameLabel, 0, 1);
        TextField nameField = new TextField();
        GridPane.setConstraints(nameField, 1, 1);

        Label surnameLabel = new Label("Nazwisko:");
        GridPane.setConstraints(surnameLabel, 0, 2);
        TextField surnameField = new TextField();
        GridPane.setConstraints(surnameField, 1, 2);

        Label emailLabel = new Label("Email:");
        GridPane.setConstraints(emailLabel, 0, 3);
        TextField emailField = new TextField();
        GridPane.setConstraints(emailField, 1, 3);

        Label userLabel = new Label("Login:");
        GridPane.setConstraints(userLabel, 0, 4);
        TextField userField = new TextField();
        GridPane.setConstraints(userField, 1, 4);

        Label passLabel = new Label("Hasło:");
        GridPane.setConstraints(passLabel, 0, 5);
        PasswordField passField = new PasswordField();
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
