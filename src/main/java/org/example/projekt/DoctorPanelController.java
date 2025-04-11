package org.example.projekt;

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

    private final DoctorPanel doctorPanel;
    private final Stage primaryStage;

    /**
     * Konstruktor kontrolera.
     * @param doctorPanel powiązany panel graficzny
     */
    public DoctorPanelController(DoctorPanel doctorPanel) {
        this.doctorPanel = doctorPanel;
        this.primaryStage = doctorPanel.getPrimaryStage();
    }

    /**
     * Wyświetla dashboard z informacjami ogólnymi.
     */
    public VBox showDashboard() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Dashboard lekarza");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label statsLabel = new Label("Statystyki:\n- Liczba zabiegów dzisiaj: 3\n- Najbliższy zabieg: godz. 14:00");
        statsLabel.setStyle("-fx-font-size: 14px;");

        layout.getChildren().addAll(titleLabel, statsLabel);
        doctorPanel.setCenterPane(layout);
        return layout;
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
        doctorPanel.setCenterPane(layout);
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
