package org.example.projekt;

import backend.klasy.*;
import backend.mongo.*;
import backend.status.Specialization;
import backend.status.TypeOfRoom;
import backend.wyjatki.DoctorIsNotAvailableException;
import backend.wyjatki.InappropriateRoomException;
import backend.wyjatki.PatientIsNotAvailableException;
import com.mongodb.client.MongoDatabase;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


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
    private final DoctorRepository doctorRepo;
    private final RoomRepository roomRepo;
    private final PatientRepository patientRepo;
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("pl", "PL"));


    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.primaryStage = adminPanel.getPrimaryStage();

        MongoDatabase db = MongoDatabaseConnector.connectToDatabase();
        this.appointmentRepo = new AppointmentRepository(db);
        this.doctorRepo = new DoctorRepository(db);
        this.roomRepo = new RoomRepository(db);
        this.patientRepo = new PatientRepository(db);
    }

    /**
     * Wyświetla panel zarządzania pacjentami.
     */
    public VBox showPatientsManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPrefWidth(1000);
        layout.setPrefHeight(700);

        Label titleLabel = new Label("Lista pacjentów");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Patient> tableView = new TableView<>();
        ObservableList<Patient> patientData = FXCollections.observableArrayList();

        TableColumn<Patient, String> firstNameCol = new TableColumn<>("Imię");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Patient, String> lastNameCol = new TableColumn<>("Nazwisko");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Patient, String> addressCol = new TableColumn<>("Adres");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Patient, String> birthDateCol = new TableColumn<>("Data urodzenia");
        birthDateCol.setCellValueFactory(p -> new ReadOnlyStringWrapper(
                p.getValue().getBirthDate().format(DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("pl", "PL")))
        ));

        // Usunięto kolumnę diagnozy

        tableView.getColumns().addAll(firstNameCol, lastNameCol, addressCol, birthDateCol);
        tableView.setItems(patientData);
        refreshPatientData(patientData);

        Button addBtn = new Button("Dodaj pacjenta");
        addBtn.setOnAction(e -> PatientForm.showForm(null, patient -> {
            try {
                patientRepo.createPatient(patient);
                refreshPatientData(patientData);
                showSuccessMessage("Pacjent dodany", "Pacjent został pomyślnie dodany do bazy danych.");
            } catch (Exception ex) {
                showErrorMessage("Błąd dodawania", "Nie udało się dodać pacjenta: " + ex.getMessage());
            }
        }));

        Button editBtn = new Button("Edytuj pacjenta");
        editBtn.setOnAction(e -> {
            Patient selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                PatientForm.showForm(selected, updated -> {
                    try {
                        patientRepo.updatePatient(updated);
                        refreshPatientData(patientData);
                        showSuccessMessage("Pacjent zaktualizowany", "Dane pacjenta zostały pomyślnie zaktualizowane.");
                    } catch (Exception ex) {
                        showErrorMessage("Błąd aktualizacji", "Nie udało się zaktualizować danych pacjenta: " + ex.getMessage());
                    }
                });
            } else {
                showWarningMessage("Brak wyboru", "Proszę wybrać pacjenta do edycji.");
            }
        });

        Button deleteBtn = new Button("Usuń pacjenta");
        deleteBtn.setOnAction(e -> {
            Patient selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean confirmed = showConfirmationDialog("Potwierdzenie usunięcia",
                        "Czy na pewno chcesz usunąć pacjenta oraz wszystkie jego zabiegi?" + selected.getFirstName() + " " + selected.getLastName() + "?");

                if (confirmed) {
                    try {
                        patientRepo.deletePatient(selected.getId());
                        List<Appointment> lista = appointmentRepo.findAppointmentsByPatient(selected);
                        for(Appointment appointment : lista) {
                            appointmentRepo.deleteAppointment(appointment.getId());
                        }
                        refreshPatientData(patientData);
                        showSuccessMessage("Pacjent usunięty", "Pacjent został pomyślnie usunięty z bazy danych.");
                    } catch (Exception ex) {
                        showErrorMessage("Błąd usuwania", "Nie udało się usunąć pacjenta: " + ex.getMessage());
                    }
                }
            } else {
                showWarningMessage("Brak wyboru", "Proszę wybrać pacjenta do usunięcia.");
            }
        });

        HBox buttonBox = new HBox(10, addBtn, editBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    /**
     * Odświeża dane pacjentów w tabeli.
     */
    private void refreshPatientData(ObservableList<Patient> patientData) {
        patientData.setAll(patientRepo.findAll());
    }

    /**
     * Wyświetla komunikat o sukcesie.
     */
    private void showSuccessMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Wyświetla komunikat o błędzie.
     */
    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Wyświetla komunikat ostrzegawczy.
     */
    private void showWarningMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Wyświetla okno dialogowe z potwierdzeniem.
     *
     * @return true jeśli użytkownik potwierdził, false w przeciwnym razie
     */
    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType buttonTypeYes = new ButtonType("Tak");
        ButtonType buttonTypeNo = new ButtonType("Nie", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        return alert.showAndWait().orElse(buttonTypeNo) == buttonTypeYes;
    }

    /**
     * Wyświetla panel zarządzania lekarzami.
     */
    public VBox showDoctorsManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPrefWidth(1000);
        layout.setPrefHeight(700);

        Label titleLabel = new Label("Lista lekarzy");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Doctor> tableView = new TableView<>();
        ObservableList<Doctor> doctorList = FXCollections.observableArrayList();

        TableColumn<Doctor, String> nameCol = new TableColumn<>("Imię");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Doctor, String> lastNameCol = new TableColumn<>("Nazwisko");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Doctor, String> specializationCol = new TableColumn<>("Specjalizacja");
        specializationCol.setCellValueFactory(cellData -> {
            Specialization spec = cellData.getValue().getSpecialization();
            return new ReadOnlyStringWrapper(spec != null ? spec.getDescription() : "");
        });

        TableColumn<Doctor, String> roomCol = new TableColumn<>("Sala");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        TableColumn<Doctor, String> contactCol = new TableColumn<>("Kontakt");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactInformation"));

        tableView.getColumns().addAll(nameCol, lastNameCol, specializationCol, roomCol, contactCol);
        tableView.setItems(doctorList);
        refreshDoctorList(doctorList);

        Button addBtn = new Button("Dodaj lekarza");
        addBtn.setOnAction(e -> DoctorForm.showForm(null, doctor -> {
            try {
                doctorRepo.createDoctor(doctor);
                refreshDoctorList(doctorList);
                showSuccessMessage("Lekarz dodany", "Lekarz został pomyślnie dodany do bazy danych.");
            } catch (Exception ex) {
                showErrorMessage("Błąd dodawania", "Nie udało się dodać lekarza: " + ex.getMessage());
            }
        }));

        Button editBtn = new Button("Edytuj lekarza");
        editBtn.setOnAction(e -> {
            Doctor selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                DoctorForm.showForm(selected, updated -> {
                    try {
                        doctorRepo.updateDoctor(updated);
                        refreshDoctorList(doctorList);
                        showSuccessMessage("Lekarz zaktualizowany", "Dane lekarza zostały pomyślnie zaktualizowane.");
                    } catch (Exception ex) {
                        showErrorMessage("Błąd aktualizacji", "Nie udało się zaktualizować danych lekarza: " + ex.getMessage());
                    }
                });
            } else {
                showWarningMessage("Brak wyboru", "Proszę wybrać lekarza do edycji.");
            }
        });

        Button deleteBtn = new Button("Usuń lekarza");
        deleteBtn.setOnAction(e -> {
            Doctor selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean confirmed = showConfirmationDialog("Potwierdzenie usunięcia",
                        "Czy na pewno chcesz usunąć lekarza " + selected.getFirstName() + " " + selected.getLastName() + "?");

                if (confirmed) {
                    try {
                        if(appointmentRepo.findAppointmentsByDoctor(selected) != null && !appointmentRepo.findAppointmentsByDoctor(selected).isEmpty()) {
                            showWarningMessage("Uwaga!!","Lekarz posiada aktualnie zaplanowany zabieg nie można go usunąć.");
                        }else{
                            doctorRepo.deleteDoctor(selected.getId());
                            refreshDoctorList(doctorList);
                            showSuccessMessage("Lekarz usunięty", "Lekarz został pomyślnie usunięty z bazy danych.");
                        }

                    } catch (Exception ex) {
                        showErrorMessage("Błąd usuwania", "Nie udało się usunąć lekarza: " + ex.getMessage());
                    }
                }
            } else {
                showWarningMessage("Brak wyboru", "Proszę wybrać lekarza do usunięcia.");
            }
        });

        HBox buttons = new HBox(10, addBtn, editBtn, deleteBtn);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, tableView, buttons);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    /**
     * Odświeża listę lekarzy w tabeli.
     */
    private void refreshDoctorList(ObservableList<Doctor> doctorList) {
        doctorList.setAll(doctorRepo.findAll());
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
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refreshAppointments(tableView);

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getDate().format(formatter)
        ));

        TableColumn<Appointment, String> roomCol = new TableColumn<>("Sala");
        roomCol.setCellValueFactory(cellData -> {
            ObjectId roomId = cellData.getValue().getRoom();
            Room room = roomRepo.findRoomsById(roomId).getFirst();
            return new ReadOnlyStringWrapper(room != null ? room.toString2() : "Nieznana sala");
        });

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
            Patient pat = patientRepo.findPatientById(patId).getFirst();
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
                    patientRepo.findAll(),
                    roomRepo.getAllRooms()
            );
            form.showForm(null, appointment -> {
                try {
                    appointmentRepo.createAppointment(appointment);
                    refreshAppointments(tableView);
                } catch (DoctorIsNotAvailableException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd dostępności lekarza");
                    alert.setHeaderText("Lekarz jest niedostępny w wybranym terminie");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                } catch (PatientIsNotAvailableException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd dostępności pacjenta");
                    alert.setHeaderText("Pacjent jest niedostępny w wybranym terminie");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                } catch (InappropriateRoomException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd przypisania sali");
                    alert.setHeaderText("Sala nie jest odpowiednia dla specjalizacji lekarza");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            });
        });

        editProcedure.setOnAction(e -> {
            Appointment selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                AppointmentForm form = new AppointmentForm(
                        doctorRepo.findAll(),
                        patientRepo.findAll(),
                        roomRepo.getAllRooms()
                );
                form.showForm(selected, updated -> {
                    try {
                        ObjectId oldRoomId = selected.getRoom();
                        ObjectId newRoomId = updated.getRoom();

                        ObjectId oldPatientId = selected.getPatientId();
                        ObjectId newPatientId = updated.getPatientId();

                        // Aktualizacja zabiegu
                        appointmentRepo.updateAppointment(updated);

                        // Jeśli zmieniono pokój lub pacjenta
                        if (!oldRoomId.equals(newRoomId) || !oldPatientId.equals(newPatientId)) {
                            // Usuń pacjenta ze starego pokoju tylko jeśli tam był
                            roomRepo.updateRoomPatientAssignment(oldRoomId, oldPatientId, null);
                            // Dodaj pacjenta do nowego pokoju tylko jeśli go nie ma
                            roomRepo.updateRoomPatientAssignment(newRoomId, null, newPatientId);
                        }

                        refreshAppointments(tableView);
                    } catch (DoctorIsNotAvailableException | PatientIsNotAvailableException | InappropriateRoomException ex) {
                        showErrorMessage("Błąd", ex.getMessage());
                    }
                });
            }

        });

        cancelProcedure.setOnAction(e -> {
            Appointment selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Utwórz okno dialogowe potwierdzenia
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Potwierdzenie usunięcia");
                confirmDialog.setHeaderText("Czy na pewno chcesz usunąć wybrany zabieg?");

                // Dodaj szczegóły zabiegu do treści komunikatu
                Doctor lekarz = doctorRepo.findDoctorById(selected.getDoctorId());
                Patient pacjent = patientRepo.findPatientById(selected.getPatientId()).getFirst();
                Room sala = roomRepo.findRoomsById(selected.getRoom()).getFirst();

                String szczegoly = "Data: " + selected.getDate().format(formatter) + "\n" +
                        "Lekarz: " + lekarz.getFirstName() + " " + lekarz.getLastName() + "\n" +
                        "Pacjent: " + pacjent.getFirstName() + " " + pacjent.getLastName() + "\n" +
                        "Sala: " + sala.toString2() + "\n" +
                        "Opis: " + selected.getDescription();

                confirmDialog.setContentText(szczegoly);

                // Dodaj przyciski Tak/Nie
                ButtonType buttonTypeYes = new ButtonType("Tak");
                ButtonType buttonTypeNo = new ButtonType("Nie", ButtonBar.ButtonData.CANCEL_CLOSE);
                confirmDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                // Pokaż okno dialogowe i poczekaj na odpowiedź
                confirmDialog.showAndWait().ifPresent(response -> {
                    if (response == buttonTypeYes) {
                        // Jeśli użytkownik potwierdził, usuń zabieg
                        appointmentRepo.deleteAppointment(selected.getId());
                        refreshAppointments(tableView);
                    }
                });
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
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPrefWidth(1000);
        layout.setPrefHeight(700);

        Label titleLabel = new Label("Zarządzanie salami");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Room> tableView = new TableView<>();
        ObservableList<Room> roomData = FXCollections.observableArrayList();

        TableColumn<Room, String> addressCol = new TableColumn<>("Adres");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Room, Integer> floorCol = new TableColumn<>("Piętro");
        floorCol.setCellValueFactory(new PropertyValueFactory<>("floor"));

        TableColumn<Room, Integer> numberCol = new TableColumn<>("Numer");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<Room, Integer> maxCol = new TableColumn<>("Max pacjenci");
        maxCol.setCellValueFactory(new PropertyValueFactory<>("maxPatients"));

        TableColumn<Room, Integer> currentCol = new TableColumn<>("Obecni pacjenci");
        currentCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCurrentPatientCount()).asObject());

        TableColumn<Room, String> typeCol = new TableColumn<>("Typ sali");
        typeCol.setCellValueFactory(cellData -> {
            TypeOfRoom type = cellData.getValue().getType();
            return new ReadOnlyStringWrapper(type != null ? type.getDescription() : "");
        });

        tableView.getColumns().addAll(addressCol, floorCol, numberCol, maxCol, currentCol, typeCol);
        refreshRoomList(roomData);
        tableView.setItems(roomData);

        Button addRoom = new Button("Dodaj salę");
        addRoom.setOnAction(e -> RoomForm.showForm(null, room -> {
            try {
                roomRepo.createRoom(room);
                refreshRoomList(roomData);
                showSuccessMessage("Sala dodana", "Sala została pomyślnie dodana do bazy danych.");
            } catch (Exception ex) {
                showErrorMessage("Błąd dodawania", "Nie udało się dodać sali: " + ex.getMessage());
            }
        }));

        Button editRoom = new Button("Edytuj salę");
        editRoom.setOnAction(e -> {
            Room selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                RoomForm.showForm(selected, updated -> {
                    try {
                        roomRepo.updateRoom(updated.getId(), updated);
                        refreshRoomList(roomData);
                        showSuccessMessage("Sala zaktualizowana", "Dane sali zostały pomyślnie zaktualizowane.");
                    } catch (Exception ex) {
                        showErrorMessage("Błąd aktualizacji", "Nie udało się zaktualizować danych sali: " + ex.getMessage());
                    }
                });
            } else {
                showWarningMessage("Brak wyboru", "Proszę wybrać salę do edycji.");
            }
        });

        Button deleteRoom = new Button("Usuń salę");
        deleteRoom.setOnAction(e -> {
            Room selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Sprawdź, czy sala ma przypisanych pacjentów
                if (selected.getCurrentPatientCount() > 0) {
                    showWarningMessage("Nie można usunąć",
                            "Nie można usunąć sali, która ma przypisanych pacjentów. " +
                                    "Liczba przypisanych pacjentów: " + selected.getCurrentPatientCount());
                    return;
                }

                // Sprawdź, czy sala jest używana w zabiegach
                List<Appointment> appointmentsInRoom = appointmentRepo.findAppointmentsByRoom(selected.getId());
                if (appointmentsInRoom != null && !appointmentsInRoom.isEmpty()) {
                    showWarningMessage("Nie można usunąć",
                            "Nie można usunąć sali, która jest używana w zaplanowanych zabiegach. " +
                                    "Liczba zabiegów w tej sali: " + appointmentsInRoom.size());
                    return;
                }

                boolean confirmed = showConfirmationDialog("Potwierdzenie usunięcia",
                        "Czy na pewno chcesz usunąć salę nr " + selected.getNumber() +
                                " na piętrze " + selected.getFloor() + "?");

                if (confirmed) {
                    try {
                        boolean deleted = roomRepo.deleteRoom(selected.getId());
                        if (deleted) {
                            refreshRoomList(roomData);
                            showSuccessMessage("Sala usunięta", "Sala została pomyślnie usunięta z bazy danych.");
                        } else {
                            showErrorMessage("Błąd usuwania", "Nie udało się usunąć sali. Sala nie została znaleziona.");
                        }
                    } catch (Exception ex) {
                        showErrorMessage("Błąd usuwania", "Nie udało się usunąć sali: " + ex.getMessage());
                    }
                }
            } else {
                showWarningMessage("Brak wyboru", "Proszę wybrać salę do usunięcia.");
            }
        });

        HBox buttonBox = new HBox(10, addRoom, editRoom, deleteRoom);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    private void refreshRoomList(ObservableList<Room> roomData) {
        roomData.setAll(roomRepo.getAllRooms());
    }

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