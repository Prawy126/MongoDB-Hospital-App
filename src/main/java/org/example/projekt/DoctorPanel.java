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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Klasa odpowiedzialna za graficzny panel lekarza.
 * Zawiera menu nawigacyjne oraz dynamiczne przełączanie widoków.
 */
public class DoctorPanel {

    private BorderPane root;
    private Stage primaryStage;
    private DoctorPanelController controller;
    private final Doctor currentDoctor;

    /**
     * Konstruktor inicjalizujący panel lekarza.
     * @param stage główne okno aplikacji
     */
    public DoctorPanel(Stage stage, Doctor doctor) {
        this.primaryStage = stage;
        this.currentDoctor  = doctor;
        this.controller = new DoctorPanelController(this, doctor);

        primaryStage.setTitle("Panel lekarza");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f0f8ff;");

        VBox menu = createMenu();
        root.setLeft(menu);

        controller.showDashboard();

        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Tworzy menu nawigacyjne z przyciskami.
     * @return VBox zawierający menu
     */
    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #D0E0F0; -fx-border-radius: 10; -fx-background-radius: 10;");
        menu.setAlignment(Pos.TOP_LEFT);

        Button dashboardButton = createStyledButton("Dashboard");
        dashboardButton.setOnAction(e -> controller.showDashboard());

        Button proceduresButton = createStyledButton("Moje zabiegi");
        proceduresButton.setOnAction(e -> controller.showProcedureSchedule());

        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(
                dashboardButton,
                proceduresButton,
                logoutButton
        );

        return menu;
    }

    /**
     * Tworzy przycisk z domyślnym kolorem.
     */
    private Button createStyledButton(String text) {
        return createStyledButton(text, "#3498DB");
    }

    /**
     * Tworzy stylizowany przycisk z animacjami.
     */
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

    /**
     * Animacja pojawienia się (fade-in).
     */
    private void animateFadeIn(VBox element, int duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), element);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Animacja zsunięcia elementu z góry (slide down).
     */
    private void animateSlideDown(VBox element, int duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), element);
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    /**
     * Ustawia panel centralny na podany komponent.
     */
    public void setCenterPane(Pane pane) {
        root.setCenter(pane);
    }

    /**
     * Zwraca główny stage aplikacji.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}