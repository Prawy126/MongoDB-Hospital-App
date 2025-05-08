package org.example.projekt;

import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import backend.status.Diagnosis;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DoctorFirstContactController implements Initializable {
    private final DoctorFirstContactPanel panel;
    private final Doctor doctor;
    private final PatientRepository patientRepository;

    public DoctorFirstContactController(DoctorFirstContactPanel panel, Doctor doctor) {
        this.panel = panel;
        this.doctor = doctor;
        this.patientRepository = new PatientRepository(MongoDatabaseConnector.connectToDatabase());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicjalizacja komponentów
    }

    public void showDashboard() {
        VBox dashboard = new VBox(10);
        dashboard.setPadding(new Insets(10));
        dashboard.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label welcomeLabel = new Label("Witaj, Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label scheduleLabel = new Label("Dzisiaj masz " + getTodayAppointmentsCount() + " wizyt");
        scheduleLabel.setStyle("-fx-font-size: 14px;");

        dashboard.getChildren().addAll(welcomeLabel, scheduleLabel);
        panel.setCenterPane(dashboard);
    }

    public void showPatientsList() {
        VBox patientsList = new VBox(10);
        patientsList.setPadding(new Insets(10));
        patientsList.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableView<Patient> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Patient, String> nameCol = new TableColumn<>("Imię i Nazwisko");
        nameCol.setCellValueFactory(param ->
                new SimpleStringProperty(
                        param.getValue().getFirstName() + " " + param.getValue().getLastName()
                )
        );

        TableColumn<Patient, String> diagnosisCol = new TableColumn<>("Diagnoza");
        diagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        TableColumn<Patient, Void> actionCol = new TableColumn<>("Akcje");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button diagButton = createStyledButton("Przypisz diagnozę", "#2ECC71");

            {
                diagButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    showDiagnosisDialog(patient);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(diagButton);
                }
            }
        });

        tableView.getColumns().addAll(nameCol, diagnosisCol, actionCol);

        List<Patient> patients = patientRepository.findAll();
        tableView.setItems(FXCollections.observableArrayList(patients));

        patientsList.getChildren().addAll(
                new Label("Lista pacjentów"),
                tableView
        );

        panel.setCenterPane(patientsList);
    }

    private void showDiagnosisDialog(Patient patient) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Przypisz diagnozę");
        dialog.setHeaderText("Wybierz diagnozę dla pacjenta: " + patient.getFirstName() + " " + patient.getLastName());

        ComboBox<Diagnosis> diagnosisBox = new ComboBox<>();
        diagnosisBox.setItems(FXCollections.observableArrayList(Diagnosis.values()));
        diagnosisBox.setValue(patient.getDiagnosis());

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Dodatkowy opis diagnozy (opcjonalnie)");
        descriptionArea.setWrapText(true);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                new Label("Wybierz diagnozę:"),
                diagnosisBox,
                new Label("Opis (opcjonalnie):"),
                descriptionArea
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                patient.setDiagnosis(diagnosisBox.getValue());
                patientRepository.updatePatient(patient);
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Diagnoza została przypisana");
                panel.setCenterPane(new VBox());
                showPatientsList();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie można zapisać diagnozy");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public void showMedicalHistory() {
        VBox history = new VBox(10);
        history.setPadding(new Insets(10));
        history.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        history.getChildren().add(new Label("Historia medyczna"));
        panel.setCenterPane(history);
    }

    public void showReferrals() {
        VBox referrals = new VBox(10);
        referrals.setPadding(new Insets(10));
        referrals.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        referrals.getChildren().add(new Label("Skierowania"));
        panel.setCenterPane(referrals);
    }

    public void logout() {
        panel.getPrimaryStage().close();
    }

    private int getTodayAppointmentsCount() {
        return 0;
    }

    // Dodana metoda do tworzenia stylizowanego przycisku
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
}