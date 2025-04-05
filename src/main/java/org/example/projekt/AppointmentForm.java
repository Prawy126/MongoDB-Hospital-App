package org.example.projekt;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.klasy.Room;
import backend.status.AppointmentStatus;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

public class AppointmentForm {

    private final List<Doctor> doctors;
    private final List<Patient> patients;
    private final List<Room> rooms;

    public AppointmentForm(List<Doctor> doctors, List<Patient> patients, List<Room> rooms) {
        this.doctors = doctors;
        this.patients = patients;
        this.rooms = rooms;
    }

    public void showForm(Appointment existingAppointment, Consumer<Appointment> onSave) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(existingAppointment == null ? "Dodaj zabieg" : "Edytuj zabieg");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(15));

        ComboBox<Doctor> doctorBox = new ComboBox<>(FXCollections.observableArrayList(doctors));
        doctorBox.setPromptText("Wybierz lekarza");

        ComboBox<Patient> patientBox = new ComboBox<>(FXCollections.observableArrayList(patients));
        patientBox.setPromptText("Wybierz pacjenta");

        ComboBox<Room> roomBox = new ComboBox<>(FXCollections.observableArrayList(rooms));
        roomBox.setPromptText("Wybierz salę");

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        timeField.setPromptText("hh:mm");

        TextField descriptionField = new TextField();
        ComboBox<AppointmentStatus> statusBox = new ComboBox<>(FXCollections.observableArrayList(AppointmentStatus.values()));

        // Prefill jeśli edycja
        if (existingAppointment != null) {
            datePicker.setValue(existingAppointment.getDate().toLocalDate());
            timeField.setText(existingAppointment.getDate().toLocalTime().toString());
            descriptionField.setText(existingAppointment.getDescription());
            statusBox.setValue(existingAppointment.getStatus());
            // lekarza/pacjenta/sali nie ustawiamy (zależnie od tego, czy kolekcje są zmapowane)
        }

        Button saveButton = new Button("Zapisz");
        saveButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            LocalTime time = LocalTime.parse(timeField.getText());

            Appointment.Builder builder = existingAppointment != null
                    ? new Appointment.Builder().withId(existingAppointment.getId())
                    : new Appointment.Builder();

            Appointment appointment = builder
                    .doctorId(doctorBox.getValue())
                    .patientId(patientBox.getValue())
                    .room(roomBox.getValue().getAdress() + " - " + roomBox.getValue().getNumber())
                    .date(LocalDateTime.of(date, time))
                    .description(descriptionField.getText())
                    .status(statusBox.getValue())
                    .build();

            onSave.accept(appointment);
            stage.close();
        });

        Button cancelButton = new Button("Anuluj");
        cancelButton.setOnAction(e -> stage.close());


        // Układ
        // Układ
        grid.add(new Label("Lekarz:"), 0, 0);
        grid.add(doctorBox, 1, 0);

        grid.add(new Label("Pacjent:"), 0, 1);
        grid.add(patientBox, 1, 1);

        grid.add(new Label("Sala:"), 0, 2);
        grid.add(roomBox, 1, 2);

        grid.add(new Label("Data:"), 0, 3);
        grid.add(datePicker, 1, 3);

        grid.add(new Label("Godzina:"), 2, 3);
        grid.add(timeField, 3, 3);

        grid.add(new Label("Opis:"), 0, 4);
        grid.add(descriptionField, 1, 4, 3, 1); // rozciągnięcie na 3 kolumny

        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusBox, 1, 5);

        HBox buttons = new HBox(10, saveButton, cancelButton);
        grid.add(buttons, 1, 6);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);



        Scene scene = new Scene(grid, 600, 350);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
