package org.example.projekt;

import backend.klasy.Doctor;
import backend.mongo.DoctorRepository;
import backend.status.Day;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Klasa odpowiedzialna za panel doktora pierwszego kontaktu.
 * Zawiera specjalistyczne funkcje dla lekarzy pierwszego kontaktu.
 */
public class DoctorFirstContactPanel {
    private BorderPane root;
    private Stage primaryStage;
    private DoctorFirstContactController controller;
    private final Doctor currentDoctor;
    private DoctorRepository doctorRepo;

    public DoctorFirstContactPanel(Stage stage, Doctor doctor, DoctorRepository doctorRepository) {
        this.primaryStage = stage;
        this.currentDoctor = doctor;
        this.doctorRepo = doctorRepository;
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

    public VBox showAvailabilityCalendar() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Moja dostępność");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label instructionLabel = new Label("Zaznacz dni, w których jesteś dostępny/a do pracy:");
        instructionLabel.setStyle("-fx-font-size: 14px;");

        List<Day> availableDays = currentDoctor.getAvailableDays();

        GridPane daysGrid = new GridPane();
        daysGrid.setHgap(10);
        daysGrid.setVgap(10);
        daysGrid.setPadding(new Insets(20));
        daysGrid.setAlignment(Pos.CENTER);

        List<CheckBox> dayCheckboxes = new ArrayList<>();

        int row = 0;
        for (Day day : Day.values()) {
            CheckBox checkbox = new CheckBox(day.getDescription());
            checkbox.setSelected(availableDays.contains(day));
            checkbox.setUserData(day);
            checkbox.setStyle("-fx-font-size: 14px;");

            dayCheckboxes.add(checkbox);
            daysGrid.add(checkbox, 0, row++);
        }

        Button saveButton = new Button("Zapisz zmiany");
        saveButton.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold;");

        saveButton.setOnAction(e -> {
            List<Day> selectedDays = new ArrayList<>();
            for (CheckBox cb : dayCheckboxes) {
                if (cb.isSelected()) {
                    selectedDays.add((Day) cb.getUserData());
                }
            }

            try {
                Doctor updatedDoctor = new Doctor.Builder()
                        .withId(currentDoctor.getId())
                        .firstName(currentDoctor.getFirstName())
                        .lastName(currentDoctor.getLastName())
                        .specialization(currentDoctor.getSpecialization())
                        .room(currentDoctor.getRoom())
                        .contactInformation(currentDoctor.getContactInformation())
                        .age(currentDoctor.getAge())
                        .pesel(currentDoctor.getPesel())
                        .passwordHash(currentDoctor.getPasswordHash())
                        .passwordSalt(currentDoctor.getPasswordSalt())
                        .availableDays(selectedDays)
                        .build();

                boolean success = doctorRepo.updateDoctor(updatedDoctor) != null;

                if (success) {
                    showSuccessAlert("Dostępność została zaktualizowana pomyślnie!");

                    Optional<Doctor> refreshedDoctorOpt = Optional.ofNullable(doctorRepo.findDoctorById(currentDoctor.getId()));
                    if (refreshedDoctorOpt.isPresent()) {
                        refreshDoctor(refreshedDoctorOpt.get());
                    }
                } else {
                    showErrorAlert("Nie udało się zaktualizować dostępności. Spróbuj ponownie.");
                }
            } catch (Exception ex) {
                showErrorAlert("Błąd podczas aktualizacji: " + ex.getMessage());
            }
        });

        Button selectAllButton = new Button("Zaznacz wszystkie");
        selectAllButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        selectAllButton.setOnAction(e -> {
            dayCheckboxes.forEach(cb -> cb.setSelected(true));
        });

        Button deselectAllButton = new Button("Odznacz wszystkie");
        deselectAllButton.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white;");
        deselectAllButton.setOnAction(e -> {
            dayCheckboxes.forEach(cb -> cb.setSelected(false));
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(selectAllButton, deselectAllButton, saveButton);
        layout.getChildren().addAll(titleLabel, instructionLabel, daysGrid, buttonBox);

        setCenterPane(layout);
        return layout;
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukces");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshDoctor(Doctor updatedDoctor) {
        // Update the controller with the refreshed doctor data
        controller = new DoctorFirstContactController(this, updatedDoctor);
        controller.showAvailabilityCalendar();
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