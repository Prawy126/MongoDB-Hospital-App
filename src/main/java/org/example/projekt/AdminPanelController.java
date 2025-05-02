package org.example.projekt;

import backend.klasy.*;
import backend.mongo.*;
import com.mongodb.client.MongoDatabase;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Kontroler panelu administratora. Odpowiada za wyświetlanie i obsługę zarządzania pacjentami, lekarzami, zabiegami i salami.
 */
public class AdminPanelController {

    private final AdminPanel adminPanel;
    private final Stage primaryStage;

    private final AppointmentRepository appointmentRepo;
    private final ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();
    private final ObservableList<Doctor> doctorData = FXCollections.observableArrayList();
    private final DoctorRepository doctorRepo = new DoctorRepository(MongoDatabaseConnector.connectToDatabase());
    private final RoomRepository roomRepo = new RoomRepository(MongoDatabaseConnector.connectToDatabase());

    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.primaryStage = adminPanel.getPrimaryStage();
        MongoDatabase db = MongoDatabaseConnector.connectToDatabase();
        this.appointmentRepo = new AppointmentRepository(db);
    }

    /**
     * Wyświetla panel zarządzania pacjentami.
     */
    public VBox showPatientsManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Lista pacjentów");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Patient> tableView = new TableView<>();
        ObservableList<Patient> patientData = FXCollections.observableArrayList();
        PatientRepository patientRepo = new PatientRepository(MongoDatabaseConnector.connectToDatabase());

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
        patientData.setAll(patientRepo.findAll());

        Button addBtn = new Button("Dodaj pacjenta");
        addBtn.setOnAction(e -> PatientForm.showForm(null, patient -> {
            patientRepo.createPatient(patient);
            patientData.setAll(patientRepo.findAll());
        }));

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

    /**
     * Wyświetla panel zarządzania lekarzami.
     */
    public VBox showDoctorsManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Lista lekarzy");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Doctor> tableView = new TableView<>();
        ObservableList<Doctor> doctorData = FXCollections.observableArrayList();
        DoctorRepository doctorRepo = new DoctorRepository(MongoDatabaseConnector.connectToDatabase());

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
        doctorData.setAll(doctorRepo.findAll());

        Button addBtn = new Button("Dodaj lekarza");
        addBtn.setOnAction(e -> DoctorForm.showForm(null, doctor -> {
            doctorRepo.createDoctor(doctor);
            doctorData.setAll(doctorRepo.findAll());
        }));

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

    /**
     * Wyświetla panel harmonogramu zabiegów.
     */
    public VBox showAppointmentsManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Harmonogram zabiegów");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Appointment> tableView = new TableView<>();
        refreshAppointments(tableView);

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Appointment, String> roomCol = new TableColumn<>("Sala");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        TableColumn<Appointment, String> descCol = new TableColumn<>("Opis");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Lekarz");
        doctorCol.setCellValueFactory(cellData -> {
            ObjectId docId = cellData.getValue().getDoctorId();
            Doctor doc = doctorRepo.findDoctorById(docId);
            if (doc == null) {
                throw new RuntimeException("Nie znaleziono lekarza");
            }
            return new ReadOnlyStringWrapper(doc.getFirstName() + " " + doc.getLastName());
        });

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Pacjent");
        patientCol.setCellValueFactory(cellData -> {
            ObjectId patId = cellData.getValue().getPatientId();
            Patient pat = new PatientRepository(MongoDatabaseConnector.connectToDatabase())
                    .findPatientById(patId).orElse(new Patient());
            return new ReadOnlyStringWrapper(pat.getFirstName() + " " + pat.getLastName());
        });

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStatus().toString()));

        tableView.getColumns().addAll(dateCol, roomCol, descCol, doctorCol, patientCol, statusCol);
        tableView.setItems(appointmentData);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button scheduleProcedure = new Button("Dodaj zabieg");
        Button editProcedure = new Button("Edytuj zaznaczony");
        Button cancelProcedure = new Button("Usuń zaznaczony");

        scheduleProcedure.setOnAction(e -> {
            AppointmentForm form = new AppointmentForm(
                    doctorRepo.findAll(),
                    new PatientRepository(MongoDatabaseConnector.connectToDatabase()).findAll(),
                    new RoomRepository(MongoDatabaseConnector.connectToDatabase()).getAllRooms()
            );
            form.showForm(null, appointment -> {
                appointmentRepo.createAppointment(appointment);
                refreshAppointments(tableView);
            });
        });

        editProcedure.setOnAction(e -> {
            Appointment selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                AppointmentForm form = new AppointmentForm(
                        doctorRepo.findAll(),
                        new PatientRepository(MongoDatabaseConnector.connectToDatabase()).findAll(),
                        new RoomRepository(MongoDatabaseConnector.connectToDatabase()).getAllRooms()
                );
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
        try {
            List<Appointment> appointments = appointmentRepo.findAll();
            appointmentData.clear();
            appointmentData.addAll(appointments);
            tableView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            // Optional: Show error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Nie udało się załadować zabiegów");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Wyświetla panel zarządzania salami.
     */
    public VBox showRoomsManagement() {

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zarządzanie salami");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Room> tableView = new TableView<>();
        ObservableList<Room> roomData = FXCollections.observableArrayList();

        /* -------- kolumny -------- */
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

        /* -------- przyciski -------- */
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
                    roomRepo.updateRoom(updated.getId(), updated);
                    roomData.setAll(roomRepo.getAllRooms());
                });
            }
        });

        Button deleteRoom = new Button("Usuń salę");
        deleteRoom.setOnAction(e -> {
            Room selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                roomRepo.deleteRoom(selected.getId());
                roomData.setAll(roomRepo.getAllRooms());
            }
        });

        HBox buttonBox = new HBox(10, addRoom, editRoom, deleteRoom);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
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