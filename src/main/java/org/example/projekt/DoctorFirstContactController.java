package org.example.projekt;

import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import backend.status.Diagnosis;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
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
        dashboard.setPadding(new javafx.geometry.Insets(10));
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

        List<Patient> patients = patientRepository.findAll();
        ListView<String> listView = new ListView<>();
        patients.forEach(patient ->
                listView.getItems().add(patient.getFirstName() + " " + patient.getLastName())
        );

        patientsList.getChildren().addAll(
                new Label("Lista pacjentów"),
                listView
        );
        panel.setCenterPane(patientsList);
    }

    public void showDiagnosisManagement() {
        VBox diagnosisManagement = new VBox(10);
        diagnosisManagement.setPadding(new Insets(10));
        diagnosisManagement.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");

        List<Patient> awaitingPatients = patientRepository.findPatientsWithAwaitingDiagnosis();

        if (awaitingPatients.isEmpty()) {
            Label noPatientsLabel = new Label("Brak pacjentów oczekujących na diagnozę");
            diagnosisManagement.getChildren().add(noPatientsLabel);
        } else {
            ComboBox<Patient> patientComboBox = new ComboBox<>();
            patientComboBox.setItems(FXCollections.observableArrayList(awaitingPatients));
            patientComboBox.setPromptText("Wybierz pacjenta");

            ComboBox<Diagnosis> diagnosisComboBox = new ComboBox<>();
            diagnosisComboBox.setItems(FXCollections.observableArrayList(Diagnosis.values()));
            diagnosisComboBox.setPromptText("Wybierz diagnozę");

            TextArea diagnosisDescription = new TextArea();
            diagnosisDescription.setPromptText("Opis diagnozy");
            diagnosisDescription.setMaxHeight(100);

            Button saveDiagnosisButton = new Button("Zapisz diagnozę");
            saveDiagnosisButton.setOnAction(event -> {
                Patient selectedPatient = patientComboBox.getValue();
                Diagnosis selectedDiagnosis = diagnosisComboBox.getValue();

                if (selectedPatient != null && selectedDiagnosis != null) {
                    try {
                        selectedPatient.setDiagnosis(selectedDiagnosis);
                        // Ustawiamy tylko diagnozę, bez dodatkowych notatek
                        patientRepository.updatePatient(selectedPatient);

                        // Odświeżenie listy
                        patientComboBox.setItems(FXCollections.observableArrayList(
                                patientRepository.findPatientsWithAwaitingDiagnosis()
                        ));

                        showAlert(Alert.AlertType.INFORMATION, "Sukces", "Diagnoza została zapisana");

                        // Czyszczenie formularza
                        patientComboBox.setValue(null);
                        diagnosisComboBox.setValue(null);
                        diagnosisDescription.clear();
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd podczas zapisywania diagnozy");
                    }
                }
            });

            diagnosisManagement.getChildren().addAll(
                    new Label("Zarządzanie diagnozami pacjentów"),
                    patientComboBox,
                    diagnosisComboBox,
                    diagnosisDescription,
                    saveDiagnosisButton
            );
        }

        panel.setCenterPane(diagnosisManagement);
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
        // TODO: Zaimplementować pobieranie liczby dzisiejszych wizyt
        return 0;
    }
}