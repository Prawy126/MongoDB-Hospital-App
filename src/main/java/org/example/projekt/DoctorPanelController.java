package org.example.projekt;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.mongo.AppointmentRepository;
import backend.mongo.DoctorRepository;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import backend.status.Day;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.lang.reflect.Field;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Kontroler panelu lekarza. Obsługuje widoki dashboardu i harmonogramu zabiegów.
 */
public class DoctorPanelController {

    private final DoctorPanel view;
    private final Stage primaryStage;
    private Doctor doctor;
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("pl", "PL"));
    private final AppointmentRepository appointmentRepo =
            new AppointmentRepository(MongoDatabaseConnector.connectToDatabase());
    private final DoctorRepository doctorRepo =
            new DoctorRepository(MongoDatabaseConnector.connectToDatabase());

    public DoctorPanelController(DoctorPanel view, Doctor doctor) {
        this.view = view;
        this.primaryStage = view.getPrimaryStage();
        this.doctor = doctor;
    }

    /**
     * Wyświetla dashboard z informacjami ogólnymi.
     */
    public VBox showDashboard() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);

        Label hello = new Label("Witaj " + doctor.getFirstName() + " " + doctor.getLastName());
        hello.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        List<Appointment> allAppointments = appointmentRepo.findAppointmentsByDoctor(doctor);

        LocalDateTime now = LocalDateTime.now();
        List<Appointment> todaysAppointments = allAppointments.stream()
                .filter(a -> a.getDate().toLocalDate().equals(now.toLocalDate()))
                .toList();

        Optional<Appointment> nextAppointment = allAppointments.stream()
                .filter(a -> a.getDate().isAfter(now))
                .min(Comparator.comparing(Appointment::getDate));

        String statsText = "- Liczba zabiegów dzisiaj: " + todaysAppointments.size() + "\n";

        statsText += nextAppointment
                .map(a -> "- Najbliższy zabieg: " + a.getDate().format(formatter) + " – " + a.getDescription())
                .orElse("- Brak nadchodzących zabiegów");

        Label stats = new Label("Statystyki:\n" + statsText);
        stats.setStyle("-fx-font-size: 14px;");

        box.getChildren().addAll(hello, stats);
        view.setCenterPane(box);
        return box;
    }


    /**
     * Wyświetla harmonogram zabiegów lekarza.
     */
    public VBox showProcedureSchedule() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Moje zaplanowane zabiegi");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> procedureTable = createProcedureTable();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button refreshButton = new Button("Odśwież");
        refreshButton.setOnAction(e -> showProcedureSchedule());

        buttonBox.getChildren().add(refreshButton);

        layout.getChildren().addAll(titleLabel, procedureTable, buttonBox);
        view.setCenterPane(layout);
        return layout;
    }

    /**
     * Tworzy pustą tabelę zabiegów (do uzupełnienia).
     */
    private TableView<Appointment> createProcedureTable() {
        TableView<Appointment> table = new TableView<>();
        ObservableList<Appointment> data = FXCollections.observableArrayList();

        List<Appointment> todayAppointments = appointmentRepo.findAppointmentsByDoctor(doctor).stream()
                .filter(a -> a.getDate().toLocalDate().equals(LocalDate.now()))
                .toList();

        data.addAll(todayAppointments);

        TableColumn<Appointment, String> timeCol = new TableColumn<>("Godzina");
        timeCol.setCellValueFactory(a -> new ReadOnlyStringWrapper(
                a.getValue().getDate().format(DateTimeFormatter.ofPattern("HH:mm"))
        ));

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Pacjent");
        patientCol.setCellValueFactory(a -> {
            List<Patient> patients = new PatientRepository(MongoDatabaseConnector.connectToDatabase())
                    .findPatientById(a.getValue().getPatientId());
            return new ReadOnlyStringWrapper(
                    patients.isEmpty() ? "Nieznany pacjent" : patients.get(0).getFirstName() + " " + patients.get(0).getLastName()
            );
        });

        TableColumn<Appointment, String> descCol = new TableColumn<>("Opis");
        descCol.setCellValueFactory(a -> new ReadOnlyStringWrapper(
                a.getValue().getDescription()
        ));

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(a -> new ReadOnlyStringWrapper(
                a.getValue().getStatus().toString()
        ));

        table.getColumns().addAll(timeCol, patientCol, descCol, statusCol);
        table.setItems(data);

        return table;
    }

    /**
     * Wyświetla kalendarz dostępności lekarza z możliwością edycji.
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

        view.setCenterPane(layout);
        return layout;
    }
    /**
     * Wyświetla alert sukcesu.
     */
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukces");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshDoctor(Doctor updatedDoctor) {
        this.doctor = updatedDoctor;
    }

    /**
     * Wyświetla alert błędu.
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Aktualizuje referencję do lekarza w kontrolerze.
     */
    private void updateDoctorReference(Doctor updatedDoctor) {
        Field doctorField;
        try {
            doctorField = this.getClass().getDeclaredField("doctor");
            doctorField.setAccessible(true);
            doctorField.set(this, updatedDoctor);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Wylogowuje użytkownika i otwiera panel logowania.
     */
    public void logout() {
        primaryStage.close();
        Stage loginStage = new Stage();
        try {
            new LoginPanel().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
