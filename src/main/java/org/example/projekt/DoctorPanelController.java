package org.example.projekt;

import backend.klasy.Doctor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Kontroler panelu lekarza. Obsługuje widoki dashboardu i harmonogramu zabiegów.
 */
public class DoctorPanelController {

    private final DoctorPanel view;
    private final Stage primaryStage;
    private final Doctor doctor;


    public DoctorPanelController(DoctorPanel view, Doctor doctor) {
        this.view         = view;
        this.primaryStage = view.getPrimaryStage();
        this.doctor       = doctor;
    }

    /**
     * Wyświetla dashboard z informacjami ogólnymi.
     */
    public VBox showDashboard() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);

        Label hello = new Label(
                "Witaj " + doctor.getFirstName() + " " + doctor.getLastName()
        );                                               // ← personalizacja
        hello.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label stats = new Label(
                "Statystyki:\n- Liczba zabiegów dzisiaj: 3\n- Najbliższy zabieg: godz. 14:00"
        );
        stats.setStyle("-fx-font-size: 14px;");

        box.getChildren().addAll(hello, stats);
        view.setCenterPane(box);
        return box;
    }

    /**
     * Wyświetla harmonogram zabiegów lekarza.
     */
    public VBox showProcedureSchedule() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Moje zaplanowane zabiegi");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> procedureTable = createProcedureTable();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button refreshButton = new Button("Odśwież");
        buttonBox.getChildren().add(refreshButton);

        layout.getChildren().addAll(titleLabel, procedureTable, buttonBox);
        view.setCenterPane(layout);
        return layout;
    }

    /**
     * Tworzy pustą tabelę zabiegów (do uzupełnienia).
     */
    private TableView<?> createProcedureTable() {
        return new TableView<>();
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
