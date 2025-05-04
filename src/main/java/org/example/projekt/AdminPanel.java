package org.example.projekt;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Klasa odpowiedzialna za graficzny panel administratora.
 * Zawiera menu nawigacyjne oraz dynamiczne ładowanie widoków.
 */
public class AdminPanel {
    //TODO: Dodać animacje do przycisków
    //TODO: Dodać animacje do przejść między widokami
    private BorderPane root;
    private Stage primaryStage;
    private AdminPanelController controller;

    /**
     * Konstruktor inicjalizujący panel administratora.
     * @param stage główne okno aplikacji
     */
    public AdminPanel(Stage stage) {
        this.primaryStage = stage;
        this.controller = new AdminPanelController(this);

        primaryStage.setTitle("Panel administratora");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        VBox menu = createMenu();
        root.setLeft(menu);

        controller.showPatientsManagement();

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
        menu.setStyle("-fx-background-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");
        menu.setAlignment(Pos.TOP_LEFT);

        Button patientsButton = createStyledButton("Pacjenci");
        patientsButton.setOnAction(e -> controller.showPatientsManagement());

        Button doctorsButton = createStyledButton("Lekarze");
        doctorsButton.setOnAction(e -> controller.showDoctorsManagement());

        Button proceduresButton = createStyledButton("Zabiegi");
        proceduresButton.setOnAction(e -> controller.showAppointmentsManagement());

        Button roomsButton = createStyledButton("Sale");
        roomsButton.setOnAction(e -> controller.showRoomsManagement());

        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(
                patientsButton,
                doctorsButton,
                proceduresButton,
                roomsButton,
                logoutButton
        );

        return menu;
    }

    /**
     * Tworzy przycisk z domyślnym kolorem.
     */
    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
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
     * Animacja pojawienia się (zanikanie).
     */
    private void animateFadeIn(VBox element, int duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), element);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Animacja zsunięcia w dół.
     */
    private void animateSlideDown(VBox element, int duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), element);
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    /**
     * Ustawia komponent centralny panelu.
     */
    public void setCenterPane(Pane pane) {
        root.setCenter(pane);
    }

    /**
     * Zwraca główne okno aplikacji.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
