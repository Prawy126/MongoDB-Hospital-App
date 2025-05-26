package org.example.projekt;

import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.status.Day;
import backend.status.Specialization;
import backend.wyjatki.AgeException;
import backend.wyjatki.NullNameException;
import backend.wyjatki.PeselException;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(doctorToEdit == null ? "Nowy lekarz" : "Edytuj lekarza");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Pola formularza
        TextField firstName = new TextField();
        firstName.setPromptText("Wprowadź imię");

        TextField lastName = new TextField();
        lastName.setPromptText("Wprowadź nazwisko");

        ComboBox<Specialization> specBox = new ComboBox<>();
        specBox.getItems().addAll(Specialization.values());
        specBox.setPromptText("Wybierz specjalizację");

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
        room.setPromptText("Wprowadź numer sali (liczba dodatnia)");

        TextField contact = new TextField();
        contact.setPromptText("Wprowadź numer telefonu (9 cyfr)");

        // Zamiana pola wieku na datę urodzenia
        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setPromptText("Wybierz datę urodzenia");

        Label ageValueLabel = new Label("");

        TextField pesel = new TextField();
        pesel.setPromptText("Wprowadź 11 cyfr PESEL");

        PasswordField password = new PasswordField();
        password.setPromptText("Wprowadź hasło");

        // Dni dostępności
        Label daysLabel = new Label("Dni dostępności:");
        VBox daysBox = new VBox(5);
        List<CheckBox> dayCheckboxes = new ArrayList<>();

        for (Day day : Day.values()) {
            CheckBox cb = new CheckBox(getDayName(day));
            dayCheckboxes.add(cb);
            daysBox.getChildren().add(cb);
        }

        // Aktualizuj wiek automatycznie po zmianie daty urodzenia
        birthDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int age = calculateAge(newValue);
                ageValueLabel.setText(String.valueOf(age) + " lat");
            } else {
                ageValueLabel.setText("");
            }
        });

        // Wypełnij pola, jeśli edytujemy istniejącego lekarza
        if (doctorToEdit != null) {
            firstName.setText(doctorToEdit.getFirstName());
            lastName.setText(doctorToEdit.getLastName());
            specBox.setValue(doctorToEdit.getSpecialization());
            room.setText(doctorToEdit.getRoom());
            contact.setText(doctorToEdit.getContactInformation());

            // Ustaw datę urodzenia na podstawie wieku (jeśli to możliwe)
            if (doctorToEdit.getBirthDate() != null) {
                birthDatePicker.setValue(doctorToEdit.getBirthDate());
                ageValueLabel.setText(String.valueOf(doctorToEdit.getAge()) + " lat");
            } else {
                // Jeśli nie ma daty urodzenia, możemy pokazać tylko wiek
                ageValueLabel.setText(String.valueOf(doctorToEdit.getAge()) + " lat (brak daty urodzenia)");
            }

            pesel.setText(String.valueOf(doctorToEdit.getPesel()));

            // Zaznacz dni dostępności
            List<Day> availableDays = doctorToEdit.getAvailableDays();
            if (availableDays != null) {
                for (int i = 0; i < Day.values().length; i++) {
                    dayCheckboxes.get(i).setSelected(availableDays.contains(Day.values()[i]));
                }
            }

            // Pole hasła pozostaje puste przy edycji
            password.setDisable(true);
            password.setPromptText("Hasło pozostaje bez zmian");
        } else {
            // Domyślnie zaznacz wszystkie dni dla nowego lekarza
            dayCheckboxes.forEach(cb -> cb.setSelected(true));
        }

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        HBox buttonBox = new HBox(10, saveButton, cancelButton);

        saveButton.setOnAction(e -> {
            // Walidacja pól
            StringBuilder errorMessage = new StringBuilder();

            if (firstName.getText() == null || firstName.getText().trim().isEmpty()) {
                errorMessage.append("• Imię nie może być puste\n");
            }

            if (lastName.getText() == null || lastName.getText().trim().isEmpty()) {
                errorMessage.append("• Nazwisko nie może być puste\n");
            }

            if (specBox.getValue() == null) {
                errorMessage.append("• Specjalizacja musi być wybrana\n");
            }

            // Walidacja numeru sali
            if (room.getText() == null || room.getText().trim().isEmpty()) {
                errorMessage.append("• Numer sali nie może być pusty\n");
            } else {
                try {
                    int roomNumber = Integer.parseInt(room.getText().trim());
                    if (roomNumber < 0) {
                        errorMessage.append("• Numer sali nie może być ujemny\n");
                    }
                } catch (NumberFormatException ex) {
                    errorMessage.append("• Numer sali musi być liczbą\n");
                }
            }

            // Walidacja numeru telefonu
            if (contact.getText() == null || contact.getText().trim().isEmpty()) {
                errorMessage.append("• Numer telefonu nie może być pusty\n");
            } else {
                String phoneNumber = contact.getText().trim();
                if (!phoneNumber.matches("\\d{9}")) {
                    errorMessage.append("• Numer telefonu musi składać się dokładnie z 9 cyfr\n");
                }
            }

            // Walidacja daty urodzenia
            if (birthDatePicker.getValue() == null) {
                errorMessage.append("• Data urodzenia jest wymagana\n");
            } else if (birthDatePicker.getValue().isAfter(LocalDate.now())) {
                errorMessage.append("• Data urodzenia nie może być w przyszłości\n");
            } else {
                int age = calculateAge(birthDatePicker.getValue());
                if (age < 25) {
                    errorMessage.append("• Lekarz musi mieć co najmniej 25 lat\n");
                }
            }

            // Walidacja PESEL
            String peselText = pesel.getText();
            if (peselText == null || peselText.trim().isEmpty()) {
                errorMessage.append("• PESEL nie może być pusty\n");
            } else {
                try {
                    long peselValue = Long.parseLong(peselText.trim());
                    if (peselValue < 10000000000L || peselValue > 99999999999L) {
                        errorMessage.append("• PESEL musi składać się dokładnie z 11 cyfr\n");
                    }
                } catch (NumberFormatException ex) {
                    errorMessage.append("• PESEL musi zawierać tylko cyfry\n");
                }
            }

            // Walidacja hasła dla nowego lekarza
            if (doctorToEdit == null && (password.getText() == null || password.getText().isEmpty())) {
                errorMessage.append("• Hasło jest wymagane dla nowego lekarza\n");
            }

            // Walidacja dni dostępności
            boolean anyDaySelected = dayCheckboxes.stream().anyMatch(CheckBox::isSelected);
            if (!anyDaySelected) {
                errorMessage.append("• Przynajmniej jeden dzień dostępności musi być wybrany\n");
            }

            // Jeśli są błędy, wyświetl je i przerwij zapisywanie
            if (errorMessage.length() > 0) {
                showValidationError("Błędy walidacji", errorMessage.toString());
                return;
            }

            try {
                // Zbierz wybrane dni
                List<Day> selectedDays = new ArrayList<>();
                for (int i = 0; i < Day.values().length; i++) {
                    if (dayCheckboxes.get(i).isSelected()) {
                        selectedDays.add(Day.values()[i]);
                    }
                }

                // Oblicz wiek na podstawie daty urodzenia
                int age = calculateAge(birthDatePicker.getValue());

                Doctor.Builder builder = new Doctor.Builder()
                        .firstName(firstName.getText().trim())
                        .lastName(lastName.getText().trim())
                        .specialization(specBox.getValue())
                        .room(room.getText().trim())
                        .contactInformation(contact.getText().trim())
                        .birthDate(birthDatePicker.getValue())
                        .age(age)
                        .pesel(Long.parseLong(pesel.getText().trim()))
                        .availableDays(selectedDays);

                if (doctorToEdit == null) {
                    // Nowy lekarz - użyj wprowadzonego hasła
                    builder.plainPassword(password.getText());
                } else {
                    // Edycja - zachowaj istniejące hasło
                    builder.withId(doctorToEdit.getId())
                            .passwordHash(doctorToEdit.getPasswordHash())
                            .passwordSalt(doctorToEdit.getPasswordSalt());
                }

                Doctor doctor = builder.build();
                onSave.accept(doctor);
                stage.close();

            } catch (NullNameException ex) {
                showValidationError("Błąd danych", "Imię lub nazwisko jest nieprawidłowe: " + ex.getMessage());
            } catch (AgeException ex) {
                showValidationError("Błąd wieku", "Wiek lekarza jest nieprawidłowy: " + ex.getMessage());
            } catch (PeselException ex) {
                showValidationError("Błąd PESEL", "Podany PESEL jest nieprawidłowy: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showValidationError("Błąd formatu", "Nieprawidłowy format liczby: " + ex.getMessage());
            } catch (Exception ex) {
                showValidationError("Nieoczekiwany błąd", "Wystąpił nieoczekiwany błąd: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> stage.close());

        // Układ formularza
        grid.addRow(0, new Label("Imię:"), firstName);
        grid.addRow(1, new Label("Nazwisko:"), lastName);
        grid.addRow(2, new Label("Specjalizacja:"), specBox);
        grid.addRow(3, new Label("Sala:"), room);
        grid.addRow(4, new Label("Numer telefonu:"), contact);
        grid.addRow(5, new Label("Data urodzenia:"), birthDatePicker);
        grid.addRow(6, new Label("Wiek:"), ageValueLabel);
        grid.addRow(7, new Label("PESEL:"), pesel);

        if (doctorToEdit == null) {
            grid.addRow(8, new Label("Hasło:"), password);
            grid.addRow(9, daysLabel);
            grid.add(daysBox, 1, 9);
            grid.addRow(10, buttonBox);
        } else {
            grid.addRow(8, daysLabel);
            grid.add(daysBox, 1, 8);
            grid.addRow(9, buttonBox);
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, 650);
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(650);
        stage.showAndWait();
    }

    /**
     * Oblicza wiek na podstawie daty urodzenia.
     */
    private static int calculateAge(LocalDate birthDate) {
        return Patient.calculateAge(birthDate);
    }

    /**
     * Zwraca polską nazwę dnia tygodnia.
     */
    private static String getDayName(Day day) {
        switch (day) {
            case MONDAY: return "Poniedziałek";
            case TUESDAY: return "Wtorek";
            case WEDNESDAY: return "Środa";
            case THURSDAY: return "Czwartek";
            case FRIDAY: return "Piątek";
            case SATURDAY: return "Sobota";
            case SUNDAY: return "Niedziela";
            default: return day.toString();
        }
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