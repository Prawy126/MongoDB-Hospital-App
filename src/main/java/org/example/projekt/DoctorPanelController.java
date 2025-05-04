package org.example.projekt;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.mongo.AppointmentRepository;
import backend.mongo.MongoDatabaseConnector;
import backend.mongo.PatientRepository;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;


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
    private final Doctor doctor;
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("pl", "PL"));
    private final AppointmentRepository appointmentRepo =
            new AppointmentRepository(MongoDatabaseConnector.connectToDatabase());



    public DoctorPanelController(DoctorPanel view, Doctor doctor) {
        this.view         = view;
        this.primaryStage = view.getPrimaryStage();
        this.doctor       = doctor;
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
            var patientOpt = new PatientRepository(MongoDatabaseConnector.connectToDatabase())
                    .findPatientById(a.getValue().getPatientId());
            return new ReadOnlyStringWrapper(patientOpt
                    .map(p -> p.getFirstName() + " " + p.getLastName())
                    .orElse("Nieznany pacjent"));
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
