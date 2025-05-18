package org.example.projekt;

import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.klasy.Room;
import backend.mongo.DoctorRepository;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import backend.mongo.RoomRepository;
import backend.status.Day;
import backend.status.Diagnosis;
import backend.status.TypeOfRoom;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Kontroler widoku dla lekarza pierwszego kontaktu.
 * Obsługuje panel główny, przypisywanie diagnoz, kalendarz dostępności i inne widoki interfejsu.
 */
public class DoctorFirstContactController implements Initializable {
    private final DoctorFirstContactPanel panel;
    private Doctor doctor;
    private final PatientRepository patientRepository;
    private final RoomRepository room;
    private final DoctorRepository doctorRepo;

    /**
     * Tworzy instancję kontrolera dla danego lekarza i panelu GUI.
     * @param panel panel GUI przypisany do kontrolera
     * @param doctor lekarz zalogowany do systemu
     */
    public DoctorFirstContactController(DoctorFirstContactPanel panel, Doctor doctor) {
        this.panel = panel;
        this.doctor = doctor;
        this.patientRepository = new PatientRepository(MongoDatabaseConnector.connectToDatabase());
        this.room = new RoomRepository(MongoDatabaseConnector.connectToDatabase());
        this.doctorRepo = new DoctorRepository(MongoDatabaseConnector.connectToDatabase());
    }

    /**
     * Inicjalizuje komponenty kontrolera (obowiązkowe dla Initializable).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicjalizacja komponentów
    }

    /**
     * Wyświetla ekran powitalny dla lekarza z informacją o liczbie pacjentów oczekujących na diagnozę.
     */
    public void showDashboard() {
        VBox dashboard = new VBox(10);
        dashboard.setPadding(new Insets(10));
        dashboard.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label welcomeLabel = new Label("Witaj, Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label scheduleLabel = new Label("Dzisiaj masz " + patientRepository.findPatientsWithAwaitingDiagnosis().size() + " pacjentów.");
        scheduleLabel.setStyle("-fx-font-size: 14px;");

        dashboard.getChildren().addAll(welcomeLabel, scheduleLabel);
        panel.setCenterPane(dashboard);
    }

    /**
     * Wyświetla listę pacjentów z możliwością przypisania diagnozy.
     */
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

    /**
     * Pokazuje dialog umożliwiający przypisanie diagnozy pacjentowi.
     * @param patient pacjent, dla którego przypisywana jest diagnoza
     */
    private void showDiagnosisDialog(Patient patient) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Przypisz diagnozę");
        dialog.setHeaderText("Wybierz diagnozę dla pacjenta: " + patient.getFirstName() + " " + patient.getLastName());

        ComboBox<Diagnosis> diagnosisBox = new ComboBox<>();
        diagnosisBox.setItems(FXCollections.observableArrayList(Diagnosis.values()));
        diagnosisBox.setValue(patient.getDiagnosis());

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Dodatkowy opis diagnozy (opcjonalnie)");
        descriptionArea.setText(patient.getDiagnosis().toString());
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
                Diagnosis selectedDiagnosis = diagnosisBox.getValue();
                patient.setDiagnosis(selectedDiagnosis);

                // 1. Pobierz odpowiedni oddział z diagnozy
                TypeOfRoom department = selectedDiagnosis.getDepartment();
                System.out.println("Szukany oddział: " + department.name());

                // 2. Znajdź dostępne pokoje na oddziale
                List<Room> availableRooms = room.findRoomsByDepartment(department).stream()
                        .filter(r -> r.getPatientIds().size() < r.getMaxPatients())
                        .toList();

                // 3. Logowanie diagnostyczne
                System.out.println("Znalezione pokoje: " + availableRooms.size());
                availableRooms.forEach(r -> System.out.println(
                        r.getId() + " - " + r.getType() + " (" + r.getPatientIds().size() + "/" + r.getMaxPatients() + ")"
                ));

                if (!availableRooms.isEmpty()) {
                    Room selectedRoom = availableRooms.get(0);

                    // 4. Przypisz pacjenta do pokoju
                    selectedRoom.addPatientId(patient.getId());
                    room.updateRoom(selectedRoom.getId(), selectedRoom);


                    showAlert(Alert.AlertType.INFORMATION, "Sukces",
                            "Przypisano do pokoju: " + selectedRoom.getNumber());
                } else {
                    showAlert(Alert.AlertType.WARNING, "Brak miejsc",
                            "Brak wolnych łóżek na oddziale " + department.getDescription());
                }

                panel.setCenterPane(new VBox());
                showPatientsList();

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie można zapisać zmian: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Wyświetla kalendarz dostępności lekarza w formie interaktywnego formularza.
     * @return komponent VBox z kalendarzem dostępności
     */
    public VBox showAvailabilityCalendar() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Moja dostępność");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label instructionLabel = new Label("Zaznacz dni, w których jesteś dostępny/a do pracy:");
        instructionLabel.setStyle("-fx-font-size: 14px;");

        List<Day> availableDays = doctor.getAvailableDays();

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
                        .withId(doctor.getId())
                        .firstName(doctor.getFirstName())
                        .lastName(doctor.getLastName())
                        .specialization(doctor.getSpecialization())
                        .room(doctor.getRoom())
                        .contactInformation(doctor.getContactInformation())
                        .age(doctor.getAge())
                        .pesel(doctor.getPesel())
                        .passwordHash(doctor.getPasswordHash())
                        .passwordSalt(doctor.getPasswordSalt())
                        .availableDays(selectedDays)
                        .build();

                boolean success = doctorRepo.updateDoctor(updatedDoctor) != null;

                if (success) {
                    showSuccessAlert("Dostępność została zaktualizowana pomyślnie!");

                    Optional<Doctor> refreshedDoctorOpt = Optional.ofNullable(doctorRepo.findDoctorById(doctor.getId()));
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

        panel.setCenterPane(layout);
        return layout;
    }

    /**
     * Pokazuje alert informacyjny, ostrzegawczy lub błędu.
     * @param type typ alertu
     * @param title tytuł okna alertu
     * @param message treść wiadomości
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    /**
     * Wyświetla sekcję historii medycznej (widok placeholder).
     */
    public void showMedicalHistory() {
        VBox history = new VBox(10);
        history.setPadding(new Insets(10));
        history.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        history.getChildren().add(new Label("Historia medyczna"));
        panel.setCenterPane(history);
    }

    /**
     * Wyświetla sekcję skierowań (widok placeholder).
     */
    public void showReferrals() {
        VBox referrals = new VBox(10);
        referrals.setPadding(new Insets(10));
        referrals.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        referrals.getChildren().add(new Label("Skierowania"));
        panel.setCenterPane(referrals);
    }

    /**
     * Wylogowuje użytkownika i przełącza widok do panelu logowania.
     */
    public void logout() {
        panel.getPrimaryStage().close();
        Stage loginStage = new Stage();
        try {
            new LoginPanel().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pokazuje alert informujący o sukcesie.
     * @param message treść wiadomości
     */
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukces");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Pokazuje alert informujący o błędzie.
     * @param message treść wiadomości
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Odświeża obiekt lekarza w kontrolerze po aktualizacji danych.
     * @param updatedDoctor zaktualizowany obiekt lekarza
     */
    private void refreshDoctor(Doctor updatedDoctor) {
        this.doctor = updatedDoctor;
    }

    /**
     * Zwraca liczbę dzisiejszych wizyt (obecnie placeholder).
     * @return liczba wizyt (0)
     */
    //TODO: Sprawdzić tą metodę i poprawić
    private int getTodayAppointmentsCount() {
        return 0;
    }

    /**
     * Tworzy stylizowany przycisk z efektem powiększania po najechaniu.
     * @param text tekst na przycisku
     * @param color kolor tła przycisku
     * @return stylizowany przycisk
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
}