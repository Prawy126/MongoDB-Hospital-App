package org.example.projekt;

import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.klasy.Room;
import backend.mongo.*;
import backend.status.AppointmentStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import backend.klasy.Appointment;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AdminPanelController {

    private final AdminPanel adminPanel;
    private final Stage primaryStage;

    private AppointmentRepository appointmentRepo;
    private ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();

    private ObservableList<Doctor> doctorData = FXCollections.observableArrayList();
    private DoctorRepository doctorRepo = new DoctorRepository(MongoDatabaseConnector.connectToDatabase());


    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.primaryStage = adminPanel.getPrimaryStage();
        MongoDatabase db = MongoDatabaseConnector.connectToDatabase();
        this.appointmentRepo = new AppointmentRepository(db);

    }

    // Przeglądanie pacjentów
    public VBox showUserManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista pacjentów");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> tableView = createTableView();

        // HBox z przyciskami
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button addPatient = new Button("Dodaj pacjenta");
        Button deletePatient = new Button("Usuń pacjenta");
        buttonBox.getChildren().addAll(addPatient, deletePatient);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    // Przeglądanie lekarzy
    public VBox showConfigPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Lista lekarzy");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Doctor> tableView = new TableView<>();
        ObservableList<Doctor> doctorData = FXCollections.observableArrayList();
        DoctorRepository doctorRepo = new DoctorRepository(MongoDatabaseConnector.connectToDatabase());

        // Kolumny
        TableColumn<Doctor, String> nameCol = new TableColumn<>("Imię");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Doctor, String> lastNameCol = new TableColumn<>("Nazwisko");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Doctor, String> specializationCol = new TableColumn<>("Specjalizacja");
        specializationCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));

        TableColumn<Doctor, String> roomCol = new TableColumn<>("Sala");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        tableView.getColumns().addAll(nameCol, lastNameCol, specializationCol, roomCol);
        tableView.setItems(doctorData);

        // Załaduj dane
        doctorData.setAll(doctorRepo.findAll());

        // Przycisk dodawania
        Button addBtn = new Button("Dodaj lekarza");
        addBtn.setOnAction(e -> {
            DoctorForm.showForm(null, doctor -> {
                doctorRepo.createDoctor(doctor);
                doctorData.setAll(doctorRepo.findAll());
            });
        });

        // Przycisk edycji
        Button editBtn = new Button("Edytuj lekarza");
        editBtn.setOnAction(e -> {
            Doctor selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                DoctorForm.showForm(selected, updated -> {
                    doctorRepo.updateDoctor(updated);
                    doctorData.setAll(doctorRepo.findAll());
                });
            }
        });

        // Przycisk usuwania
        Button deleteBtn = new Button("Usuń lekarza");
        deleteBtn.setOnAction(e -> {
            Doctor selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                doctorRepo.deleteDoctor(selected.getId());
                doctorData.setAll(doctorRepo.findAll());
            }
        });

        HBox buttons = new HBox(10, addBtn, editBtn, deleteBtn);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, tableView, buttons);
        adminPanel.setCenterPane(layout);
        return layout;
    }



    // Przeglądanie zabiegów
    public VBox showReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Harmonogram zabiegów");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Appointment> tableView = new TableView<>();
        refreshAppointments(tableView);

        // Kolumny
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Appointment, String> roomCol = new TableColumn<>("Sala");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        TableColumn<Appointment, String> descCol = new TableColumn<>("Opis");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableView.getColumns().addAll(dateCol, roomCol, descCol);
        tableView.setItems(appointmentData);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button scheduleProcedure = new Button("Dodaj zabieg");
        Button cancelProcedure = new Button("Usuń zaznaczony");

        scheduleProcedure.setOnAction(e -> {
            List<Doctor> doctors = new DoctorRepository(MongoDatabaseConnector.connectToDatabase()).findAll();
            List<Patient> patients = new PatientRepository(MongoDatabaseConnector.connectToDatabase()).findAll();
            RoomRepository roomRepo = new RoomRepository(MongoDatabaseConnector.getClient(), "hospitalDB");
            List<Room> rooms = roomRepo.getAllRooms();

            AppointmentForm form = new AppointmentForm(doctors, patients, rooms);
            form.showForm(null, appointment -> {
                appointmentRepo.createAppointment(appointment);
                refreshAppointments(tableView);
            });
        });

        Button editProcedure = new Button("Edytuj zaznaczony");
        editProcedure.setOnAction(e -> {
            Appointment selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                List<Doctor> doctors = new DoctorRepository(MongoDatabaseConnector.connectToDatabase()).findAll();
                List<Patient> patients = new PatientRepository(MongoDatabaseConnector.connectToDatabase()).findAll();
                RoomRepository roomRepo = new RoomRepository(MongoDatabaseConnector.getClient(), "hospitalDB");
                List<Room> rooms = roomRepo.getAllRooms();

                AppointmentForm form = new AppointmentForm(doctors, patients, rooms);
                form.showForm(selected, appointment -> {
                    appointmentRepo.updateAppointment(appointment);
                    refreshAppointments(tableView);
                });
            }
        });



        cancelProcedure.setOnAction(e -> {
            Appointment selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                appointmentRepo.deleteAppointment(selected.getId());
                refreshAppointments(tableView);
            }
        });

        buttonBox.getChildren().addAll(scheduleProcedure, editProcedure, cancelProcedure);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    private void refreshAppointments(TableView<Appointment> tableView) {
        List<Appointment> appointments = appointmentRepo.findAll();
        appointmentData.setAll(appointments);
        tableView.refresh();
    }


    private void refreshDoctors(TableView<Doctor> tableView) {
        List<Doctor> doctors = doctorRepo.findAll();
        doctorData.setAll(doctors);
        tableView.refresh();
    }




    // Przeglądanie sal
    public VBox showIssuesPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zarządzanie salami");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> tableView = createTableView();

        // HBox z przyciskami
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button addRoom = new Button("Dodaj salę");
        Button editRoom = new Button("Edytuj salę");
        buttonBox.getChildren().addAll(addRoom, editRoom);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    // Metoda pomocnicza do tworzenia tabeli
    private TableView<?> createTableView() {
        return new TableView<>();
    }

    // Wylogowanie
    public void logout() {
        primaryStage.close();
        Stage loginStage = new Stage();
        try {
            new LoginPanel().start(loginStage); // Uruchamiamy okno logowania
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
