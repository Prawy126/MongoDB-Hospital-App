package org.example.projekt;

import backend.klasy.Appointment;
import backend.klasy.Patient;
import backend.mongo.AppointmentRepository;
import backend.mongo.DoctorRepository;
import backend.mongo.MongoDatabaseConnector;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Kontroler panelu pacjenta. */
public class PatientPanelController {

    private final PatientPanel view;
    private final Stage        primaryStage;
    private final Patient      patient;

    private final AppointmentRepository appointmentRepo =
            new AppointmentRepository(MongoDatabaseConnector.connectToDatabase());
    private final DoctorRepository doctorRepo =
            new DoctorRepository(MongoDatabaseConnector.connectToDatabase());



    public PatientPanelController(PatientPanel view, Patient patient) {
        this.view         = view;
        this.primaryStage = view.getPrimaryStage();
        this.patient      = patient;
    }

    /** Ekran powitalny. */
    public VBox showDashboard() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);

        Label hello = new Label("Witaj " + patient.getFirstName() + " " + patient.getLastName());
        hello.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        List<Appointment> allAppointments = appointmentRepo.findAppointmentsByPatient(patient);

        Optional<Appointment> nextAppointment = allAppointments.stream()
                .filter(app -> app.getDate().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Appointment::getDate));

        Label next;
        if (nextAppointment.isPresent()) {
            Appointment a = nextAppointment.get();
            next = new Label("Najbliższy zabieg: " +
                    a.getDate().toString() + " – " + a.getDescription());
        } else {
            next = new Label("Brak zbliżających się zabiegów");
        }

        next.setStyle("-fx-font-size: 14px;");
        box.getChildren().addAll(hello, next);

        view.setCenterPane(box);
        return box;
    }


    /** Historia leczenia (na razie pusta tabela). */
    public VBox showTreatmentHistory() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        Label title = new Label("Historia leczenia");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Appointment> table = new TableView<>();
        ObservableList<Appointment> data = FXCollections.observableArrayList(
                appointmentRepo.findAppointmentsByPatient(patient)
        );

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(a -> new ReadOnlyStringWrapper(
                a.getValue().getDate().toString()
        ));

        TableColumn<Appointment, String> descCol = new TableColumn<>("Opis");
        descCol.setCellValueFactory(a -> new ReadOnlyStringWrapper(
                a.getValue().getDescription()
        ));

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Lekarz");
        doctorCol.setCellValueFactory(a -> {
            var doc = doctorRepo.findDoctorById(a.getValue().getDoctorId());
            if (doc != null) {
                return new ReadOnlyStringWrapper(doc.getFirstName() + " " + doc.getLastName());
            } else {
                return new ReadOnlyStringWrapper("Nieznany");
            }
        });

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(a -> new ReadOnlyStringWrapper(
                a.getValue().getStatus().toString()
        ));

        table.getColumns().addAll(dateCol, descCol, doctorCol, statusCol);
        table.setItems(data);

        box.getChildren().addAll(title, table);
        view.setCenterPane(box);
        return box;
    }


    /** Wylogowanie. */
    public void logout() {
        primaryStage.close();
        try { new LoginPanel().start(new Stage()); }
        catch (Exception e) { e.printStackTrace(); }
    }
}
