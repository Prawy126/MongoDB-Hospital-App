package org.example.projekt;

import backend.klasy.Room;
import backend.status.TypeOfRoom;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * Formularz do tworzenia i edycji danych sali szpitalnej.
 */
public class RoomForm {

    /**
     * Wyświetla formularz dodawania lub edycji sali.
     * @param existingRoom istniejący obiekt sali lub null przy tworzeniu nowej
     * @param onSave akcja wykonywana po zapisaniu formularza
     */
    public static void showForm(Room existingRoom, Consumer<Room> onSave) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(existingRoom == null ? "Dodaj salę" : "Edytuj salę");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(15));

        TextField addressField = new TextField();
        TextField floorField = new TextField();
        TextField numberField = new TextField();
        TextField maxPatientsField = new TextField();
        ComboBox<TypeOfRoom> typeOfRoomComboBox = new ComboBox<>();
        typeOfRoomComboBox.getItems().addAll(TypeOfRoom.values());

        if (existingRoom != null) {
            addressField.setText(existingRoom.getAddress());
            floorField.setText(String.valueOf(existingRoom.getFloor()));
            numberField.setText(String.valueOf(existingRoom.getNumber()));
            maxPatientsField.setText(String.valueOf(existingRoom.getMaxPatients()));
            typeOfRoomComboBox.setValue(existingRoom.getType());
        }

        Button saveButton = new Button("Zapisz");
        saveButton.setOnAction(e -> {
            try {
                String addr   = addressField.getText();
                int    floor  = Integer.parseInt(floorField.getText());
                int    number = Integer.parseInt(numberField.getText());
                int    max    = Integer.parseInt(maxPatientsField.getText());
                TypeOfRoom type = typeOfRoomComboBox.getValue();

                if (existingRoom == null) {
                    Room room = new Room(addr, floor, number, max, type);
                    onSave.accept(room);
                } else {
                    existingRoom.setAddress(addr);
                    existingRoom.setFloor(floor);
                    existingRoom.setNumber(number);
                    existingRoom.setMaxPatients(max);
                    existingRoom.setType(type);
                    onSave.accept(existingRoom);
                }

                stage.close();

            } catch (NumberFormatException | NullPointerException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Błąd");
                a.setHeaderText("Nieprawidłowe dane");
                a.setContentText("Wszystkie pola muszą być poprawnie wypełnione.");
                a.showAndWait();
            }
        });

        Button cancelButton = new Button("Anuluj");
        cancelButton.setOnAction(e -> stage.close());

        grid.addRow(0, new Label("Adres:"), addressField);
        grid.addRow(1, new Label("Piętro:"), floorField);
        grid.addRow(2, new Label("Numer:"), numberField);
        grid.addRow(3, new Label("Maks. pacjentów:"), maxPatientsField);
        grid.addRow(4, new Label("Typ sali:"), typeOfRoomComboBox);
        grid.addRow(5, saveButton, cancelButton);

        Scene scene = new Scene(grid, 400, 300);
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.showAndWait();
    }
}