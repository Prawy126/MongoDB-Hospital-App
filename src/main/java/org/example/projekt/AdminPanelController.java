package org.example.projekt;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminPanelController {

    private final AdminPanel adminPanel;
    private final Stage primaryStage;

    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.primaryStage = adminPanel.getPrimaryStage();
    }

    // Przeglądanie pacjentów
    public VBox showUserManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista pacjentów");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> tableView = createTableView();

        // HBox z przyciskami
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button addPatient = new Button("Dodaj pacjenta");
        Button deletePatient = new Button("Usuń pacjenta");
        buttonBox.getChildren().addAll(addPatient, deletePatient);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    // Przeglądanie lekarzy
    public VBox showConfigPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista lekarzy");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> tableView = createTableView();

        // HBox z przyciskami
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button addDoctor = new Button("Dodaj lekarza");
        Button editDoctor = new Button("Edytuj dane");
        buttonBox.getChildren().addAll(addDoctor, editDoctor);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    // Przeglądanie zabiegów
    public VBox showReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Harmonogram zabiegów");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> tableView = createTableView();

        // HBox z przyciskami
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button scheduleProcedure = new Button("Zaplanuj zabieg");
        Button cancelProcedure = new Button("Anuluj zabieg");
        buttonBox.getChildren().addAll(scheduleProcedure, cancelProcedure);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    // Przeglądanie sal
    public VBox showIssuesPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zarządzanie salami");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> tableView = createTableView();

        // HBox z przyciskami
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button addRoom = new Button("Dodaj salę");
        Button editRoom = new Button("Edytuj salę");
        buttonBox.getChildren().addAll(addRoom, editRoom);

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        adminPanel.setCenterPane(layout);
        return layout;
    }

    // Metoda pomocnicza do tworzenia tabeli
    private TableView<?> createTableView() {
        return new TableView<>();
    }

    // Wylogowanie
    public void logout() {
        primaryStage.close();
        Stage loginStage = new Stage();
        try {
            new LoginPanel().start(loginStage); // Uruchamiamy okno logowania
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
