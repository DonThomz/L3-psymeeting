package com.bdd.psymeeting.controller;

import com.bdd.psymeeting.TransitionEffect;
import com.bdd.psymeeting.model.Consultation;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

public class ConsultationController extends ParentController implements Initializable {

    // Fields
    public JFXTextField searchField;
    public VBox box_consultations;
    public JFXComboBox<Label> filter;

    // Attributes
    private ArrayList<Consultation> consultationArrayList;

    // --------------------
    //  Services
    // --------------------
    Service<Boolean> loadConsultations = new Service<Boolean>() {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return setupBoxConsultations(); // init consultation
                }
            };
        }
    };


    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // get current date
        super.date_today = Calendar.getInstance();

        consultationArrayList = new ArrayList<>();

        TransitionEffect.TranslateTransitionY(box_consultations, 600, 75);
        TransitionEffect.FadeTransition(box_consultations, 600, 0.2f, 5);

        // start loadConsultations service
        if (loadConsultations.getState() == Task.State.READY) {
            loadConsultations.start();
        }

        // Setup services
        loadConsultations.setOnSucceeded(evt -> {
            System.out.println("Task succeeded!");
            // run createBoxConsultations
            createBoxConsultations();
        });
        loadConsultations.setOnFailed(evt -> {
            System.out.println("Task failed!");
        });

        setupFilterBox();

    }

    @Override
    protected boolean setupBoxConsultations() {
        consultations_map = new HashMap<>();
        box_consultations.setSpacing(20);

        // request SQL
        consultation_size = Consultation.getLastPrimaryKeyId();
        if (consultation_size != 0) {
            for (int i = 1; i <= consultation_size; i++) {
                consultationArrayList.add(buildConsultationButton(i));
                if (consultationArrayList.get(i - 1) == null) return false; // if error
            }
        } else return false; // no consultation in DB

        // setup comparator
        consultationArrayList.sort(Comparator.comparing(Consultation::getDate));

        // order descending by default
        Collections.reverse(consultationArrayList);

        return true;
    }

    private void createBoxConsultations() {
        for (Consultation c : consultationArrayList
        ) {
            if (c.getDate().compareTo(date_today) < 0) {
                c.getConsultation_button().setStyle("-fx-background-color:  #eceff1;");
            }
            c.getConsultation_button().getStyleClass().add("consultation_cell");
            box_consultations.getChildren().add(c.getConsultation_button());
        }
    }


    // --------------------
    //  Filters methods
    // --------------------

    private void setupFilterBox() {
        filter.getItems().add(new Label("plus récent"));
        filter.getItems().add(new Label("moins récent"));
        filter.setStyle("-fx-font-size: 14");
        filter.valueProperty().addListener((observable, oldValue, newValue) -> {
            dateFilter();
        });
    }

    private void dateFilter() {
        // sort consultation ArrayList compared to the previous order
        Collections.reverse(consultationArrayList);
        box_consultations.getChildren().clear();
        for (Consultation c : consultationArrayList
        ) {
            box_consultations.getChildren().add(c.getConsultation_button());
        }
    }

}

