package org.example.projekt;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PatientPanelController {

    private final PatientPanel patientPanel;
    private final Stage primaryStage;

    public PatientPanelController(PatientPanel patientPanel) {
        this.patientPanel = patientPanel;
        this.primaryStage = patientPanel.getPrimaryStage();
    }

    // Dashboard pacjenta – najbliższy zabieg
    public VBox showDashboard() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Dashboard pacjenta");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label nextProcedureLabel = new Label("Najbliższy zabieg: 10 kwietnia 2025, godz. 10:30 – RTG klatki piersiowej");
        nextProcedureLabel.setStyle("-fx-font-size: 14px;");

        layout.getChildren().addAll(titleLabel, nextProcedureLabel);
        patientPanel.setCenterPane(layout);
        return layout;
    }

    // Historia leczenia w tabeli
    public VBox showTreatmentHistory() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Historia leczenia");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> historyTable = createHistoryTable();

        layout.getChildren().addAll(titleLabel, historyTable);
        patientPanel.setCenterPane(layout);
        return layout;
    }

    private TableView<?> createHistoryTable() {
        return new TableView<>(); // Możesz uzupełnić o kolumny np. nazwa zabiegu, data, lekarz
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
