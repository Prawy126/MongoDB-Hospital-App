package org.example.projekt;

import backend.klasy.Appointment;
import backend.klasy.Doctor;
import backend.klasy.Patient;
import backend.klasy.Room;
import backend.mongo.AppointmentRepository;
import backend.mongo.DoctorRepository;
import backend.mongo.RoomRepository;
import backend.status.AppointmentStatus;
import backend.status.Day;
import backend.status.TypeOfRoom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Formularz dodawania lub edycji wizyty (Appointment).
 */
public class AppointmentForm {

    private final List<Doctor> wszyscyLekarze;
    private final List<Patient> pacjenci;
    private final List<Room> wszystkieSale;
    private final AppointmentRepository zabiegiRepo;
    private final RoomRepository saleRepo;
    private ObservableList<Doctor> dostepniLekarze;
    private ObservableList<Room> kompatybilneSale;

    /**
     * Konstruktor z repozytoriami do filtrowania.
     */
    public AppointmentForm(List<Doctor> lekarze, List<Patient> pacjenci, List<Room> sale,
                           AppointmentRepository zabiegiRepo, RoomRepository saleRepo) {
        this.wszyscyLekarze = lekarze;
        this.pacjenci = pacjenci;
        this.wszystkieSale = sale;
        this.zabiegiRepo = zabiegiRepo;
        this.saleRepo = saleRepo;
    }

    /**
     * Konstruktor bez repozytoriów (do kompatybilności wstecznej).
     */
    public AppointmentForm(List<Doctor> lekarze, List<Patient> pacjenci, List<Room> sale) {
        this.wszyscyLekarze = lekarze;
        this.pacjenci = pacjenci;
        this.wszystkieSale = sale;
        this.zabiegiRepo = null;
        this.saleRepo = null;
    }

    /**
     * Pokazuje formularz edycji lub dodania wizyty.
     */
    public void showForm(Appointment istniejacyZabieg, Consumer<Appointment> poZapisie) {
        Stage okno = new Stage();
        okno.initModality(Modality.APPLICATION_MODAL);
        okno.setTitle(istniejacyZabieg == null ? "Dodaj zabieg" : "Edytuj zabieg");

        GridPane siatka = new GridPane();
        siatka.setVgap(10);
        siatka.setHgap(10);
        siatka.setPadding(new Insets(15));

        // Inicjalizacja pustych list - będą wypełnione na podstawie wyborów
        dostepniLekarze = FXCollections.observableArrayList();
        kompatybilneSale = FXCollections.observableArrayList();

        // Pole wyboru daty
        DatePicker wyborDaty = new DatePicker();
        wyborDaty.setPromptText("Wybierz datę");

        // Pole wyboru godziny
        TextField poleGodziny = new TextField();
        poleGodziny.setPromptText("gg:mm");

        // Pole wyboru lekarza
        ComboBox<Doctor> wyborLekarza = new ComboBox<>(dostepniLekarze);
        wyborLekarza.setPromptText("Wybierz lekarza");

        // Niestandardowy konwerter do wyświetlania informacji o lekarzu
        wyborLekarza.setConverter(new javafx.util.StringConverter<Doctor>() {
            @Override
            public String toString(Doctor lekarz) {
                if (lekarz == null) return null;
                return lekarz.getFirstName() + " " + lekarz.getLastName() +
                        " [" + formatujDostepneDni(lekarz) + "] " + lekarz.getSpecialization();
            }

            @Override
            public Doctor fromString(String tekst) {
                return null; // Niepotrzebne dla niemodyfikowalnego ComboBox
            }
        });

        // Pole wyboru pacjenta
        ComboBox<Patient> wyborPacjenta = new ComboBox<>(FXCollections.observableArrayList(pacjenci));
        wyborPacjenta.setPromptText("Wybierz pacjenta");

        // Pole wyboru sali
        ComboBox<Room> wyborSali = new ComboBox<>(kompatybilneSale);
        wyborSali.setPromptText("Wybierz salę");

        // Niestandardowy konwerter dla sal
        wyborSali.setConverter(new javafx.util.StringConverter<Room>() {
            @Override
            public String toString(Room sala) {
                if (sala == null) return null;
                return sala.toString2() + " - " + sala.getType().getDescription();
            }

            @Override
            public Room fromString(String tekst) {
                return null; // Niepotrzebne dla niemodyfikowalnego ComboBox
            }
        });

        // Pole opisu
        TextField poleOpisu = new TextField();
        poleOpisu.setPromptText("Opis zabiegu");

        // Pole statusu
        ComboBox<AppointmentStatus> wyborStatusu = new ComboBox<>(FXCollections.observableArrayList(AppointmentStatus.values()));
        wyborStatusu.setPromptText("Wybierz status");

        // Dodaj nasłuchiwacz do pola daty, aby aktualizować dostępnych lekarzy
        wyborDaty.valueProperty().addListener((obs, staraDat, nowaDat) -> {
            if (nowaDat != null) {
                aktualizujDostepnychLekarzy(nowaDat, poleGodziny.getText(), wyborLekarza, istniejacyZabieg);
            } else {
                // Wyczyść listę lekarzy, jeśli nie wybrano daty
                dostepniLekarze.clear();
                wyborLekarza.setValue(null);
            }
        });

        // Dodaj nasłuchiwacz do pola godziny, aby aktualizować dostępnych lekarzy
        poleGodziny.textProperty().addListener((obs, staraGodz, nowaGodz) -> {
            LocalDate wybranaDat = wyborDaty.getValue();
            if (wybranaDat != null && !nowaGodz.isEmpty()) {
                try {
                    // Spróbuj sparsować godzinę, aby sprawdzić, czy jest poprawna
                    LocalTime.parse(nowaGodz);
                    aktualizujDostepnychLekarzy(wybranaDat, nowaGodz, wyborLekarza, istniejacyZabieg);
                } catch (Exception e) {
                    // Ignoruj niepoprawny format godziny
                }
            }
        });

        // Dodaj nasłuchiwacz do wyboru lekarza, aby aktualizować kompatybilne sale
        wyborLekarza.valueProperty().addListener((obs, staryLekarz, nowyLekarz) -> {
            if (nowyLekarz != null) {
                aktualizujKompatybilneSale(nowyLekarz, wyborSali, istniejacyZabieg);
            } else {
                // Wyczyść listę sal, jeśli nie wybrano lekarza
                kompatybilneSale.clear();
                wyborSali.setValue(null);
            }
        });

        // Wypełnij pola, jeśli edytujemy istniejący zabieg
        if (istniejacyZabieg != null) {
            wyborDaty.setValue(istniejacyZabieg.getDate().toLocalDate());
            poleGodziny.setText(istniejacyZabieg.getDate().toLocalTime().toString());
            poleOpisu.setText(istniejacyZabieg.getDescription());
            wyborStatusu.setValue(istniejacyZabieg.getStatus());

            // Znajdź i ustaw wybranego lekarza
            Doctor wybranyLekarz = wszyscyLekarze.stream()
                    .filter(doc -> doc.getId().equals(istniejacyZabieg.getDoctorId()))
                    .findFirst()
                    .orElse(null);

            // Znajdź i ustaw wybranego pacjenta
            Patient wybranyPacjent = pacjenci.stream()
                    .filter(p -> p.getId().equals(istniejacyZabieg.getPatientId()))
                    .findFirst()
                    .orElse(null);

            // Aktualizuj listy dostępnych lekarzy i kompatybilnych sal
            aktualizujDostepnychLekarzy(wyborDaty.getValue(), poleGodziny.getText(), wyborLekarza, istniejacyZabieg);

            if (wybranyLekarz != null) {
                wyborLekarza.setValue(wybranyLekarz);
                // Lista sal zostanie zaktualizowana przez nasłuchiwacz
            }

            wyborPacjenta.setValue(wybranyPacjent);
        } else {
            // Dla nowych zabiegów, ustaw domyślnie dzisiejszą datę
            LocalDate dzisiaj = LocalDate.now();
            wyborDaty.setValue(dzisiaj);
            // Aktualizuj listę dostępnych lekarzy
            aktualizujDostepnychLekarzy(dzisiaj, "", wyborLekarza, null);
        }

        Button przyciskZapisz = new Button("Zapisz");
        przyciskZapisz.setOnAction(e -> {
            try {
                LocalDate data = wyborDaty.getValue();
                LocalTime godzina = LocalTime.parse(poleGodziny.getText());

                if (wyborLekarza.getValue() == null || wyborPacjenta.getValue() == null ||
                        wyborSali.getValue() == null || data == null ||
                        poleGodziny.getText().isEmpty() || wyborStatusu.getValue() == null) {

                    pokazBlad("Błąd", "Wszystkie pola muszą być wypełnione");
                    return;
                }

                Appointment.Builder builder = istniejacyZabieg != null
                        ? new Appointment.Builder().withId(istniejacyZabieg.getId())
                        : new Appointment.Builder();

                Appointment zabieg = builder
                        .doctorId(wyborLekarza.getValue())
                        .patientId(wyborPacjenta.getValue())
                        .room(wyborSali.getValue().getId())
                        .date(LocalDateTime.of(data, godzina))
                        .description(poleOpisu.getText())
                        .status(wyborStatusu.getValue())
                        .build();

                poZapisie.accept(zabieg);
                okno.close();

            } catch (Exception ex) {
                pokazBlad("Błąd", "Niepoprawny format godziny. Użyj formatu gg:mm");
            }
        });

        Button przyciskAnuluj = new Button("Anuluj");
        przyciskAnuluj.setOnAction(e -> okno.close());

        // Układ formularza
        siatka.add(new Label("Data:"), 0, 0);
        siatka.add(wyborDaty, 1, 0);

        siatka.add(new Label("Godzina:"), 2, 0);
        siatka.add(poleGodziny, 3, 0);

        siatka.add(new Label("Lekarz:"), 0, 1);
        siatka.add(wyborLekarza, 1, 1);

        siatka.add(new Label("Pacjent:"), 0, 2);
        siatka.add(wyborPacjenta, 1, 2);

        siatka.add(new Label("Sala:"), 0, 3);
        siatka.add(wyborSali, 1, 3);

        siatka.add(new Label("Opis:"), 0, 4);
        siatka.add(poleOpisu, 1, 4, 3, 1);

        siatka.add(new Label("Status:"), 0, 5);
        siatka.add(wyborStatusu, 1, 5);

        HBox przyciski = new HBox(10, przyciskZapisz, przyciskAnuluj);
        siatka.add(przyciski, 1, 6);

        // Rozkład kolumn
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            siatka.getColumnConstraints().add(col);
        }

        Scene scena = new Scene(siatka, 800, 550);
        okno.setScene(scena);
        okno.setMinWidth(700);
        okno.setMinHeight(500);
        okno.showAndWait();
    }

    /**
     * Aktualizuje listę dostępnych lekarzy na podstawie wybranej daty i godziny.
     * Wyświetla komunikat o błędzie, jeśli format godziny jest niepoprawny.
     */
    private void aktualizujDostepnychLekarzy(LocalDate data, String godzinaText, ComboBox<Doctor> wyborLekarza, Appointment istniejacyZabieg) {
        // Wyczyść aktualną listę
        dostepniLekarze.clear();

        if (data == null) {
            // Jeśli nie wybrano daty, nie aktualizuj listy
            return;
        }

        // Pobierz dzień tygodnia z wybranej daty
        java.time.DayOfWeek dzienTygodnia = data.getDayOfWeek();
        Day wybranyDzien = konwertujNaDzienEnum(dzienTygodnia);

        // Filtruj lekarzy, którzy są dostępni w wybranym dniu
        List<Doctor> lekarzeNaDzien = wszyscyLekarze.stream()
                .filter(lekarz -> lekarz.getAvailableDays().contains(wybranyDzien))
                .collect(Collectors.toList());

        // Jeśli podano godzinę, sprawdź również dostępność w danym terminie
        if (!godzinaText.isEmpty() && zabiegiRepo != null) {
            try {
                LocalTime godzina = LocalTime.parse(godzinaText);
                LocalDateTime termin = LocalDateTime.of(data, godzina);

                // Filtruj lekarzy, którzy nie mają już zaplanowanych zabiegów w tym terminie
                lekarzeNaDzien = lekarzeNaDzien.stream()
                        .filter(lekarz -> {
                            // Jeśli edytujemy istniejący zabieg, wykluczamy go z porównania
                            ObjectId wykluczonId = istniejacyZabieg != null ? istniejacyZabieg.getId() : null;
                            return czyLekarzDostepny(lekarz.getId(), termin, wykluczonId);
                        })
                        .collect(Collectors.toList());
            } catch (Exception e) {
                // Wyświetl komunikat o błędnym formacie godziny
                pokazBlad("Niepoprawny format godziny",
                        "Wprowadzona godzina ma niepoprawny format. Użyj formatu gg:mm (np. 14:30).");
            }
        }

        // Dodaj wszystkich dostępnych lekarzy do listy
        dostepniLekarze.addAll(lekarzeNaDzien);

        // Jeśli edytujemy istniejący zabieg, upewnij się, że aktualny lekarz jest na liście
        if (istniejacyZabieg != null) {
            Doctor aktualnyLekarz = wszyscyLekarze.stream()
                    .filter(d -> d.getId().equals(istniejacyZabieg.getDoctorId()))
                    .findFirst()
                    .orElse(null);

            if (aktualnyLekarz != null && !dostepniLekarze.contains(aktualnyLekarz)) {
                dostepniLekarze.add(aktualnyLekarz);
            }

            // Ustaw wybranego lekarza
            wyborLekarza.setValue(aktualnyLekarz);
        } else if (!dostepniLekarze.isEmpty()) {
            // Dla nowych zabiegów, wybierz pierwszego dostępnego lekarza
            wyborLekarza.setValue(dostepniLekarze.get(0));
        } else {
            wyborLekarza.setValue(null);
        }
    }

    /**
     * Sprawdza, czy lekarz jest dostępny w danym terminie.
     */
    private boolean czyLekarzDostepny(ObjectId lekarzId, LocalDateTime termin, ObjectId wykluczonZabiegId) {
        if (zabiegiRepo == null) {
            // Jeśli nie mamy repozytorium, zakładamy, że lekarz jest dostępny
            return true;
        }

        // Pobierz wszystkie zabiegi dla danego lekarza
        Doctor lekarz = wszyscyLekarze.stream()
                .filter(d -> d.getId().equals(lekarzId))
                .findFirst()
                .orElse(null);

        if (lekarz == null) {
            return false;
        }

        List<Appointment> zabiegiLekarza = zabiegiRepo.findAppointmentsByDoctor(lekarz);

        // Sprawdź, czy lekarz ma już zaplanowany zabieg w tym terminie (z 30-minutowym marginesem)
        return zabiegiLekarza.stream()
                .filter(zabieg -> {
                    // Pomiń wykluczony zabieg (przy edycji)
                    if (wykluczonZabiegId != null && zabieg.getId().equals(wykluczonZabiegId)) {
                        return false;
                    }

                    // Pomiń zakończone zabiegi
                    if (zabieg.getStatus() == AppointmentStatus.COMPLETED) {
                        return false;
                    }

                    // Sprawdź, czy termin zabiegu koliduje z wybranym terminem
                    LocalDateTime czasZabiegu = zabieg.getDate();
                    long roznicaMinut = Math.abs(java.time.Duration.between(termin, czasZabiegu).toMinutes());
                    return roznicaMinut < 30; // Zakładamy, że zabieg trwa 30 minut
                })
                .findAny()
                .isEmpty(); // Zwróć true, jeśli nie znaleziono kolidujących zabiegów
    }

    /**
     * Aktualizuje listę kompatybilnych sal na podstawie wybranego lekarza.
     */
    private void aktualizujKompatybilneSale(Doctor lekarz, ComboBox<Room> wyborSali, Appointment istniejacyZabieg) {
        // Wyczyść aktualną listę
        kompatybilneSale.clear();

        // Pobierz kompatybilny typ sali dla specjalizacji lekarza
        TypeOfRoom kompatybilnyTyp = lekarz.getSpecialization().getCompatibleRoomType();

        // Filtruj sale według kompatybilnego typu
        List<Room> filtrowaneSale;
        if (saleRepo != null) {
            // Użyj repozytorium do pobrania sal według typu
            filtrowaneSale = saleRepo.findRoomByType(kompatybilnyTyp);
        } else {
            // Filtruj w pamięci, jeśli repozytorium nie jest dostępne
            filtrowaneSale = wszystkieSale.stream()
                    .filter(sala -> sala.getType() == kompatybilnyTyp)
                    .collect(Collectors.toList());
        }

        // Dodaj wszystkie kompatybilne sale do listy
        kompatybilneSale.addAll(filtrowaneSale);

        // Jeśli edytujemy istniejący zabieg, upewnij się, że aktualna sala jest na liście
        if (istniejacyZabieg != null) {
            Room aktualnaSala = wszystkieSale.stream()
                    .filter(r -> r.getId().equals(istniejacyZabieg.getRoom()))
                    .findFirst()
                    .orElse(null);

            if (aktualnaSala != null && !kompatybilneSale.contains(aktualnaSala)) {
                kompatybilneSale.add(aktualnaSala);
            }

            // Ustaw wybraną salę
            wyborSali.setValue(aktualnaSala);
        } else if (!kompatybilneSale.isEmpty()) {
            // Dla nowych zabiegów, wybierz pierwszą kompatybilną salę
            wyborSali.setValue(kompatybilneSale.get(0));
        } else {
            wyborSali.setValue(null);
        }
    }

    /**
     * Formatuje dostępne dni lekarza jako ciąg znaków.
     */
    private String formatujDostepneDni(Doctor lekarz) {
        StringBuilder sb = new StringBuilder();
        List<Day> dostepneDni = lekarz.getAvailableDays();

        for (int i = 0; i < dostepneDni.size(); i++) {
            sb.append(pobierzSkrotDnia(dostepneDni.get(i)));
            if (i < dostepneDni.size() - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    /**
     * Zwraca polski skrót dla dnia.
     */
    private String pobierzSkrotDnia(Day dzien) {
        return switch (dzien) {
            case MONDAY -> "pon";
            case TUESDAY -> "wt";
            case WEDNESDAY -> "śr";
            case THURSDAY -> "czw";
            case FRIDAY -> "pt";
            case SATURDAY -> "sob";
            case SUNDAY -> "nd";
        };
    }

    /**
     * Konwertuje java.time.DayOfWeek na enum Day.
     */
    private Day konwertujNaDzienEnum(java.time.DayOfWeek dzienTygodnia) {
        return switch (dzienTygodnia) {
            case MONDAY -> Day.MONDAY;
            case TUESDAY -> Day.TUESDAY;
            case WEDNESDAY -> Day.WEDNESDAY;
            case THURSDAY -> Day.THURSDAY;
            case FRIDAY -> Day.FRIDAY;
            case SATURDAY -> Day.SATURDAY;
            case SUNDAY -> Day.SUNDAY;
        };
    }

    /**
     * Wyświetla alert o błędzie.
     */
    private void pokazBlad(String tytul, String wiadomosc) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(tytul);
        alert.setHeaderText(null);
        alert.setContentText(wiadomosc);
        alert.showAndWait();
    }
}

