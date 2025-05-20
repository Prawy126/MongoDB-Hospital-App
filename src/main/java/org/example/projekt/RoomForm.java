package org.example.projekt;

import backend.klasy.Room;
import backend.status.TypeOfRoom;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

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

        // Pola formularza
        TextField addressField = new TextField();
        addressField.setPromptText("Wprowadź adres budynku");

        TextField floorField = new TextField();
        floorField.setPromptText("Wprowadź numer piętra (liczba całkowita)");

        TextField numberField = new TextField();
        numberField.setPromptText("Wprowadź numer sali (liczba dodatnia)");

        TextField maxPatientsField = new TextField();
        maxPatientsField.setPromptText("Wprowadź maksymalną liczbę pacjentów (min. 1)");

        ComboBox<TypeOfRoom> typeOfRoomComboBox = new ComboBox<>();
        typeOfRoomComboBox.getItems().addAll(TypeOfRoom.values());
        typeOfRoomComboBox.setPromptText("Wybierz typ sali");

        // Konwerter dla ComboBox, aby wyświetlać opisy typów sal
        typeOfRoomComboBox.setConverter(new StringConverter<TypeOfRoom>() {
            @Override
            public String toString(TypeOfRoom type) {
                return type != null ? type.getDescription() : "";
            }

            @Override
            public TypeOfRoom fromString(String string) {
                for (TypeOfRoom type : TypeOfRoom.values()) {
                    if (type.getDescription().equals(string)) {
                        return type;
                    }
                }
                return null;
            }
        });

        // Wypełnij pola, jeśli edytujemy istniejącą salę
        if (existingRoom != null) {
            addressField.setText(existingRoom.getAddress());
            floorField.setText(String.valueOf(existingRoom.getFloor()));
            numberField.setText(String.valueOf(existingRoom.getNumber()));
            maxPatientsField.setText(String.valueOf(existingRoom.getMaxPatients()));
            typeOfRoomComboBox.setValue(existingRoom.getType());
        }

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        HBox buttonBox = new HBox(10, saveButton, cancelButton);

        saveButton.setOnAction(e -> {
            // Walidacja pól
            StringBuilder errorMessage = new StringBuilder();

            // Walidacja adresu
            if (addressField.getText() == null || addressField.getText().trim().isEmpty()) {
                errorMessage.append("• Adres budynku nie może być pusty\n");
            }

            // Walidacja piętra
            int floor = 0;
            try {
                floor = Integer.parseInt(floorField.getText().trim());
            } catch (NumberFormatException ex) {
                errorMessage.append("• Piętro musi być liczbą całkowitą\n");
            }

            // Walidacja numeru sali
            int number = 0;
            try {
                number = Integer.parseInt(numberField.getText().trim());
                if (number <= 0) {
                    errorMessage.append("• Numer sali musi być liczbą dodatnią\n");
                }
            } catch (NumberFormatException ex) {
                errorMessage.append("• Numer sali musi być liczbą całkowitą\n");
            }

            // Walidacja maksymalnej liczby pacjentów
            int maxPatients = 0;
            try {
                maxPatients = Integer.parseInt(maxPatientsField.getText().trim());
                if (maxPatients <= 0) {
                    errorMessage.append("• Maksymalna liczba pacjentów musi być większa od zera\n");
                }

                // Jeśli edytujemy istniejącą salę, sprawdź czy nowa wartość nie jest mniejsza niż aktualna liczba pacjentów
                if (existingRoom != null && maxPatients < existingRoom.getCurrentPatientCount()) {
                    errorMessage.append("• Maksymalna liczba pacjentów nie może być mniejsza niż aktualna liczba pacjentów ("
                            + existingRoom.getCurrentPatientCount() + ")\n");
                }
            } catch (NumberFormatException ex) {
                errorMessage.append("• Maksymalna liczba pacjentów musi być liczbą całkowitą\n");
            }

            // Walidacja typu sali
            if (typeOfRoomComboBox.getValue() == null) {
                errorMessage.append("• Typ sali musi być wybrany\n");
            }

            // Jeśli są błędy, wyświetl je i przerwij zapisywanie
            if (errorMessage.length() > 0) {
                showValidationError("Błędy walidacji", errorMessage.toString());
                return;
            }

            try {
                String addr = addressField.getText().trim();
                TypeOfRoom type = typeOfRoomComboBox.getValue();

                if (existingRoom == null) {
                    // Tworzenie nowej sali
                    Room room = new Room(addr, floor, number, maxPatients, type);
                    onSave.accept(room);
                } else {
                    // Aktualizacja istniejącej sali
                    existingRoom.setAddress(addr);
                    existingRoom.setFloor(floor);
                    existingRoom.setNumber(number);
                    existingRoom.setMaxPatients(maxPatients);
                    existingRoom.setType(type);
                    onSave.accept(existingRoom);
                }

                stage.close();

            } catch (IllegalArgumentException ex) {
                showValidationError("Błąd danych", ex.getMessage());
            } catch (Exception ex) {
                showValidationError("Nieoczekiwany błąd", "Wystąpił nieoczekiwany błąd: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> stage.close());

        // Układ formularza
        grid.addRow(0, new Label("Adres budynku:"), addressField);
        grid.addRow(1, new Label("Piętro:"), floorField);
        grid.addRow(2, new Label("Numer sali:"), numberField);
        grid.addRow(3, new Label("Maks. liczba pacjentów:"), maxPatientsField);
        grid.addRow(4, new Label("Typ sali:"), typeOfRoomComboBox);
        grid.addRow(5, buttonBox);

        Scene scene = new Scene(grid, 500, 300);
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(300);
        stage.showAndWait();
    }

    /**
     * Wyświetla okno dialogowe z błędami walidacji.
     */
    private static void showValidationError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}