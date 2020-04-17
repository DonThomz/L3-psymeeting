package application.controller;

import application.App;
import application.TransitionEffect;
import com.jfoenix.controls.*;
import data.Consultation;
import data.Patient;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class ConsultationController extends ParentController implements Initializable {

    // Fields
    public JFXTextField searchField;
    public VBox box_consultations;
    public JFXComboBox<Label> filter;

    // Attributes


    // --------------------
    //   Initialize method
    // --------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // get current date
        super.date_today = Calendar.getInstance();

        TransitionEffect.TranslateTransitionY(box_consultations, 600, 75);
        TransitionEffect.FadeTransition(box_consultations, 600, 0.2f, 5);

        setupFilterBox();
        setupBoxConsultations();
        setupSearchBox();

    }

    @Override
    protected void setupBoxConsultations() {
        consultations_map = new HashMap<>();
        box_consultations.setSpacing(20);
        consultation_size = Consultation.getLastPrimaryKeyId();
        for (int i = 1; i <= consultation_size; i++) {
            consultations_map.put(Consultation.getDateById(i), buildConsultationButton(i));
        }
        // sort in descending order
        consultations_map = new TreeMap<>(consultations_map).descendingMap();
        // add all button
        consultations_map.forEach((k, v) -> {
            if (k.compareTo(date_today) < 0) {
                v.setStyle("-fx-background-color: #eceff1;");
            }
            v.getStyleClass().add("consultation_cell");
            box_consultations.getChildren().add(v);
        });
    }

    private void setupFilterBox() {
        filter.getItems().add(new Label("plus récent"));
        filter.getItems().add(new Label("moins récent"));
        filter.setStyle("-fx-font-size: 14");
        filter.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getText().equals("plus récent")) {
                mostRecentFirst();
            } else {
                mostOldestFirst();
            }
        });
    }

    private void setupSearchBox() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

        });
    }


    // --------------------
    //  Filters methods
    // --------------------

    private void mostRecentFirst() {
        // sort in ascending order
        consultations_map = new TreeMap<>(consultations_map).descendingMap();
        if (consultation_size > 0) {
            box_consultations.getChildren().subList(0, consultation_size).clear();
        }
        consultations_map.forEach((k, v) -> box_consultations.getChildren().add(v));
    }

    private void mostOldestFirst() {
        // sort in descending order
        consultations_map = new TreeMap<>(consultations_map);
        if (consultation_size > 0) {
            box_consultations.getChildren().subList(0, consultation_size).clear();
        }
        consultations_map.forEach((k, v) -> box_consultations.getChildren().add(v));
    }
}

