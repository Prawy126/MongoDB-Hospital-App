package org.example.projekt;

import backend.klasy.Doctor;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Klasa odpowiedzialna za panel doktora pierwszego kontaktu.
 * Zawiera specjalistyczne funkcje dla lekarzy pierwszego kontaktu.
 */
public class DoctorFirstContactPanel {
    private BorderPane root;
    private Stage primaryStage;
    private DoctorFirstContactController controller;
    private final Doctor currentDoctor;

    public DoctorFirstContactPanel(Stage stage, Doctor doctor) {
        this.primaryStage = stage;
        this.currentDoctor = doctor;
        this.controller = new DoctorFirstContactController(this, doctor);

        initializePanel();
    }

    private void initializePanel() {
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f0f8ff;");

        primaryStage.setTitle("Panel lekarza pierwszego kontaktu");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);

        VBox menu = createMenu();
        root.setLeft(menu);

        controller.showDashboard();

        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        Scene scene = new Scene(root, 800, 500);
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

        Button patientsButton = createStyledButton("Lista pacjentów");
        patientsButton.setOnAction(e -> controller.showPatientsList());

        Button availabilityButton = createStyledButton("Moja dostępność");
        availabilityButton.setOnAction(e -> controller.showAvailabilityCalendar());

        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(
                dashboardButton,
                patientsButton,
                availabilityButton,
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