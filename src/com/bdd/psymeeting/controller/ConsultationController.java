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
        setupController();
    }

    private void setupController() {
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
            System.out.println("Task load consultation succeeded!");
            // run createBoxConsultations
            super.createBoxConsultations("consultation_cell");
            loadConsultations.reset();
        });
        super.loadConsultations.setOnFailed(evt -> {
            System.out.println("Task load consultation failed!");
            loadConsultations.reset();
        });

        setupFilterBox();
    }


    @Override
    public void refresh() {
        consultationArrayList.clear();
        box_consultations.getChildren().clear();
        setupController();
    }

    @Override
    protected boolean setupBoxConsultations() {
        box_consultations.setSpacing(20);

        // request SQL
        ArrayList<Integer> IdConsultationsList = Consultation.countConsultations();
        if (IdConsultationsList != null) {
            for (Integer consultationID : IdConsultationsList
            ) {
                // request SQL
                consultationArrayList.add(buildConsultationButton(new Consultation(consultationID)));
                if (consultationArrayList.get(consultationArrayList.size() - 1) == null) return false; // if error
            }
        } else return false; // no consultation in DB

        // setup comparator
        consultationArrayList.sort(Comparator.comparing(Consultation::getDate));

        // order descending by default
        Collections.reverse(consultationArrayList);

        return true;
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
        Collections.reverse(super.consultationArrayList);
        box_consultations.getChildren().clear();
        for (Consultation c : super.consultationArrayList
        ) {
            box_consultations.getChildren().add(c.getConsultation_button());
        }
    }


}

