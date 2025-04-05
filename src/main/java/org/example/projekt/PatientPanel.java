package org.example.projekt;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PatientPanel {

    private BorderPane root;
    private Stage primaryStage;
    private PatientPanelController controller;

    public PatientPanel(Stage stage) {
        this.primaryStage = stage;
        this.controller = new PatientPanelController(this);

        primaryStage.setTitle("Panel pacjenta");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f4f4f4;");

        VBox menu = createMenu();
        root.setLeft(menu);

        controller.showDashboard();

        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #D0E0F0; -fx-border-radius: 10; -fx-background-radius: 10;");
        menu.setAlignment(Pos.TOP_LEFT);

        Button dashboardButton = createStyledButton("Dashboard");
        dashboardButton.setOnAction(e -> controller.showDashboard());

        Button historyButton = createStyledButton("Historia leczenia");
        historyButton.setOnAction(e -> controller.showTreatmentHistory());

        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(
                dashboardButton,
                historyButton,
                logoutButton
        );

        return menu;
    }

    private Button createStyledButton(String text) {
        return createStyledButton(text, "#3498DB");
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");

        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        return button;
    }

    private void animateFadeIn(VBox element, int duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), element);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void animateSlideDown(VBox element, int duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), element);
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    public void setCenterPane(Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
