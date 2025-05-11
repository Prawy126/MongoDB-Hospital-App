package org.example.projekt;

import backend.klasy.Doctor;
import backend.status.Day;
import backend.status.Specialization;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Formularz do tworzenia i edytowania lekarza (Doctor).
 */
public class DoctorForm {

    /**
     * Wyświetla formularz dialogowy do utworzenia lub edycji lekarza.
     * @param doctorToEdit lekarz do edycji; gdy null – tryb tworzenia nowego
     * @param onSave       akcja do wykonania po zapisie
     */
    public static void showForm(Doctor doctorToEdit, Consumer<Doctor> onSave) {

        Dialog<Doctor> dialog = new Dialog<>();
        dialog.setTitle(doctorToEdit == null ? "Nowy lekarz" : "Edytuj lekarza");

        ButtonType saveBtn = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField firstName = new TextField();
        TextField lastName = new TextField();

        ComboBox<Specialization> specBox = new ComboBox<>();
        specBox.getItems().addAll(Specialization.values());
        specBox.setEditable(false);

        specBox.setConverter(new StringConverter<Specialization>() {
            @Override
            public String toString(Specialization spec) {
                return spec != null ? spec.getDescription() : "";
            }

            @Override
            public Specialization fromString(String string) {
                return Specialization.fromDescription(string);
            }
        });

        TextField room = new TextField();
        TextField contact = new TextField();
        TextField age = new TextField();
        TextField pesel = new TextField();

        grid.addRow(0, new Label("Imię:"), firstName);
        grid.addRow(1, new Label("Nazwisko:"), lastName);
        grid.addRow(2, new Label("Specjalizacja:"), specBox);
        grid.addRow(3, new Label("Sala:"), room);
        grid.addRow(4, new Label("Kontakt:"), contact);
        grid.addRow(5, new Label("Wiek:"), age);
        grid.addRow(6, new Label("Pesel:"), pesel);

        if (doctorToEdit != null) {
            firstName.setText(doctorToEdit.getFirstName());
            lastName.setText(doctorToEdit.getLastName());
            specBox.setValue(doctorToEdit.getSpecialization());
            room.setText(doctorToEdit.getRoom());
            contact.setText(doctorToEdit.getContactInformation());
            age.setText(String.valueOf(doctorToEdit.getAge()));
            pesel.setText(String.valueOf(doctorToEdit.getPesel()));
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;

            try {
                Specialization specialization = specBox.getValue();
                if (specialization == null) {
                    throw new IllegalArgumentException("Wybierz specjalizację z listy.");
                }

                Doctor.Builder builder = new Doctor.Builder()
                        .firstName(firstName.getText())
                        .lastName(lastName.getText())
                        .specialization(specialization)
                        .room(room.getText())
                        .contactInformation(contact.getText())
                        .age(Integer.parseInt(age.getText()))
                        .pesel(Long.parseLong(pesel.getText()));

                if (doctorToEdit == null) {
                    builder.plainPassword("haslo")
                            .availableDays(Arrays.asList(Day.values()));
                } else {
                    builder.withId(doctorToEdit.getId())
                            .passwordHash(doctorToEdit.getPasswordHash())
                            .passwordSalt(doctorToEdit.getPasswordSalt())
                            .availableDays(doctorToEdit.getAvailableDays());
                }
                return builder.build();

            } catch (Exception ex) {
                showError("Błąd formularza: " + ex.getMessage());
                return null;
            }
        });

        dialog.showAndWait().ifPresent(onSave);
    }

    private static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Błąd");
        a.setContentText(msg);
        a.showAndWait();
    }
}