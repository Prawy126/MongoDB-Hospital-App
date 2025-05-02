package org.example.projekt;

import backend.klasy.Patient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Kontroler panelu pacjenta. */
public class PatientPanelController {

    private final PatientPanel view;
    private final Stage        primaryStage;
    private final Patient      patient;

    public PatientPanelController(PatientPanel view, Patient patient) {
        this.view         = view;
        this.primaryStage = view.getPrimaryStage();
        this.patient      = patient;
    }

    /** Ekran powitalny. */
    public VBox showDashboard() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);

        Label hello = new Label(
                "Witaj " + patient.getFirstName() + " " + patient.getLastName()
        );
        hello.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label next = new Label("Najbliższy zabieg: 10 kwietnia 2025, godz. 10:30 – RTG klatki piersiowej");
        next.setStyle("-fx-font-size: 14px;");

        box.getChildren().addAll(hello, next);
        view.setCenterPane(box);
        return box;
    }

    /** Historia leczenia (na razie pusta tabela). */
    public VBox showTreatmentHistory() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        Label title = new Label("Historia leczenia");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<?> table = new TableView<>();   // TODO: uzupełnić kolumny

        box.getChildren().addAll(title, table);
        view.setCenterPane(box);
        return box;
    }

    /** Wylogowanie. */
    public void logout() {
        primaryStage.close();
        try { new LoginPanel().start(new Stage()); }
        catch (Exception e) { e.printStackTrace(); }
    }
}
