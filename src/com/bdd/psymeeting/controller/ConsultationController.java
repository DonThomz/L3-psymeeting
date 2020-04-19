/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.controller;

import com.bdd.psymeeting.TransitionEffect;
import com.bdd.psymeeting.model.Consultation;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class ConsultationController extends ConsultationHistoric implements Initializable {

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

        super.consultationArrayList = new ArrayList<>();

        TransitionEffect.TranslateTransitionY(box_consultations, 600, 75);
        TransitionEffect.FadeTransition(box_consultations, 600, 0.2f, 5);

        // start loadConsultations service
        if (super.loadConsultations.getState() == Task.State.READY) {
            super.loadConsultations.start();
        }

        // Setup services
        super.loadConsultations.setOnSucceeded(evt -> {
            System.out.println("Task succeeded!");
            // run createBoxConsultations
            super.createBoxConsultations("consultation_cell");
        });
        super.loadConsultations.setOnFailed(evt -> {
            System.out.println("Task failed!");
        });

        setupFilterBox();

    }

    @Override
    protected boolean setupBoxConsultations() {
        box_consultations.setSpacing(20);

        // request SQL
        consultation_size = Consultation.getConsultationSize();
        if (consultation_size != 0) {
            for (int i = 1; i <= consultation_size; i++) {
                // request SQL
                consultationArrayList.add(buildConsultationButton(new Consultation(i)));
                if (consultationArrayList.get(i - 1) == null) return false; // if error
            }
        } else return false; // no consultation in DB

        // setup comparator
        consultationArrayList.sort(Comparator.comparing(Consultation::getDate));

        // order descending by default
        Collections.reverse(consultationArrayList);

        return true;
    }

    @Override
    public void clickConsultation(Consultation consultation) {
        // TODO: Show the new pane on the right
        System.out.println(consultation);
    }

    // --------------------
    //  Filters methods
    // --------------------
    private void setupFilterBox() {
        filter.getItems().add(new Label("Plus récent"));
        filter.getItems().add(new Label("Moins récent"));
        filter.setStyle("-fx-font-size: 14");
        filter.valueProperty().addListener((observable, oldValue, newValue) -> {
            dateFilter();
        });
    }

    private void dateFilter() {
        // sort consultation ArrayList compared to the previous order
        Collections.reverse(super.consultationArrayList);
        box_consultations.getChildren().clear();
        for (Consultation c : super.consultationArrayList
        ) {
            box_consultations.getChildren().add(c.getConsultation_button());
        }
    }


}

