package org.example.projekt;

import backend.klasy.Doctor;
import backend.status.Day;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class DoctorForm {

    public static void showForm(Doctor doctorToEdit, Consumer<Doctor> onSave) {
        Dialog<Doctor> dialog = new Dialog<>();
        dialog.setTitle(doctorToEdit == null ? "Nowy lekarz" : "Edytuj lekarza");

        // Przyciski
        ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Formularz
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField spec = new TextField();
        TextField room = new TextField();
        TextField contact = new TextField();
        TextField age = new TextField();
        TextField pesel = new TextField();

        grid.add(new Label("Imię:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Nazwisko:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Specjalizacja:"), 0, 2);
        grid.add(spec, 1, 2);
        grid.add(new Label("Sala:"), 0, 3);
        grid.add(room, 1, 3);
        grid.add(new Label("Kontakt:"), 0, 4);
        grid.add(contact, 1, 4);
        grid.add(new Label("Wiek:"), 0, 5);
        grid.add(age, 1, 5);
        grid.add(new Label("Pesel:"), 0, 6);
        grid.add(pesel, 1, 6);

        // Prefill jeśli edycja
        if (doctorToEdit != null) {
            firstName.setText(doctorToEdit.getFirstName());
            lastName.setText(doctorToEdit.getLastName());
            spec.setText(doctorToEdit.getSpecialization());
            room.setText(doctorToEdit.getRoom());
            contact.setText(doctorToEdit.getContactInformation());
            age.setText(String.valueOf(doctorToEdit.getAge()));
            pesel.setText(String.valueOf(doctorToEdit.getPesel()));
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(new Callback<ButtonType, Doctor>() {
            @Override
            public Doctor call(ButtonType buttonType) {
                if (buttonType == saveButtonType) {
                    try {
                        Doctor.Builder builder = new Doctor.Builder()
                                .firstName(firstName.getText())
                                .lastName(lastName.getText())
                                .specialization(spec.getText())
                                .room(room.getText())
                                .contactInformation(contact.getText())
                                .age(Integer.parseInt(age.getText()))
                                .pesel(Long.parseLong(pesel.getText()))
                                .availableDays(Arrays.asList(Day.MONDAY, Day.WEDNESDAY)); // przykładowe dni

                        if (doctorToEdit != null) {
                            builder.withId(doctorToEdit.getId());
                        }

                        return builder.build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError("Błąd formularza: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result != null) {
                onSave.accept(result);
            }
        });
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Błąd");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
