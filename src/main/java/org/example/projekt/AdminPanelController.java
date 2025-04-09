package org.example.projekt;

import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.klasy.Room;
import backend.mongo.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import java.time.LocalDate;
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

    public VBox showUserManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Lista pacjentów");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Patient> tableView = new TableView<>();
        ObservableList<Patient> patientData = FXCollections.observableArrayList();
        PatientRepository patientRepo = new PatientRepository(MongoDatabaseConnector.connectToDatabase());

        // Kolumny
        TableColumn<Patient, String> firstNameCol = new TableColumn<>("Imię");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Patient, String> lastNameCol = new TableColumn<>("Nazwisko");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Patient, String> addressCol = new TableColumn<>("Adres");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Patient, LocalDate> birthDateCol = new TableColumn<>("Data urodzenia");
        birthDateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        tableView.getColumns().addAll(firstNameCol, lastNameCol, addressCol, birthDateCol);
        tableView.setItems(patientData);

        // Załaduj dane
        patientData.setAll(patientRepo.findAll());

        // Dodaj pacjenta
        Button addBtn = new Button("Dodaj pacjenta");
        addBtn.setOnAction(e -> {
            PatientForm.showForm(null, patient -> {
                patientRepo.createPatient(patient);
                patientData.setAll(patientRepo.findAll());
            });
        });

        // Edytuj pacjenta
        Button editBtn = new Button("Edytuj pacjenta");
        editBtn.setOnAction(e -> {
            Patient selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                PatientForm.showForm(selected, updated -> {
                    patientRepo.updatePatient(updated);
                    patientData.setAll(patientRepo.findAll());
                });
            }
        });

        // Usuń pacjenta
        Button deleteBtn = new Button("Usuń pacjenta");
        deleteBtn.setOnAction(e -> {
            Patient selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                patientRepo.deletePatient(selected.getId());
                patientData.setAll(patientRepo.findAll());
            }
        });

        HBox buttonBox = new HBox(10, addBtn, editBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);

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

        TableView<Room> tableView = new TableView<>();
        ObservableList<Room> roomData = FXCollections.observableArrayList();
        RoomRepository roomRepo = new RoomRepository(MongoDatabaseConnector.getClient(), "hospitalDB");

        TableColumn<Room, String> addressCol = new TableColumn<>("Adres");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Room, Integer> floorCol = new TableColumn<>("Piętro");
        floorCol.setCellValueFactory(new PropertyValueFactory<>("floor"));

        TableColumn<Room, Integer> numberCol = new TableColumn<>("Numer");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<Room, Integer> maxCol = new TableColumn<>("Max pacjenci");
        maxCol.setCellValueFactory(new PropertyValueFactory<>("maxPatients"));

        TableColumn<Room, Integer> currentCol = new TableColumn<>("Obecni pacjenci");
        currentCol.setCellValueFactory(new PropertyValueFactory<>("currentPatients"));

        tableView.getColumns().addAll(addressCol, floorCol, numberCol, maxCol, currentCol);
        roomData.setAll(roomRepo.getAllRooms());
        tableView.setItems(roomData);

        Button addRoom = new Button("Dodaj salę");
        addRoom.setOnAction(e -> RoomForm.showForm(null, room -> {
            roomRepo.createRoom(room);
            roomData.setAll(roomRepo.getAllRooms());
        }));

        Button editRoom = new Button("Edytuj salę");
        editRoom.setOnAction(e -> {
            Room selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                RoomForm.showForm(selected, updated -> {
                    roomRepo.updateRoom(updated);
                    roomData.setAll(roomRepo.getAllRooms());
                });
            }
        });

        Button deleteRoom = new Button("Usuń salę");
        deleteRoom.setOnAction(e -> {
            Room selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                roomRepo.deleteRoom(selected.getAddress(), selected.getFloor(), selected.getNumber());
                roomData.setAll(roomRepo.getAllRooms());
            }
        });

        HBox buttonBox = new HBox(10, addRoom, editRoom, deleteRoom);
        buttonBox.setAlignment(Pos.CENTER);

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
