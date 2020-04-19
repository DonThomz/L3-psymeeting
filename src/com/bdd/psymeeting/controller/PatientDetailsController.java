/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.controller;

import com.bdd.psymeeting.model.Consultation;
import com.bdd.psymeeting.model.Job;
import com.bdd.psymeeting.model.Patient;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class PatientDetailsController extends ConsultationHistoric implements Initializable {

    // Fields
    public JFXTextField name_field;
    public JFXTextField last_name_field;
    public JFXDatePicker birthday_field;
    public JFXComboBox<String> gender_field;
    public JFXComboBox<String> relation_field;
    public JFXTextField discovery_field;
    public JFXComboBox<Job> jobs_list_field;
    public JFXButton edit_button;
    public VBox box_consultations;

    // Attributes
    Patient patient;


    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.date_today = Calendar.getInstance();
        super.consultationArrayList = new ArrayList<>();

        patient = PatientsController.list_patients.get(PatientsController.current_patient_id);

        name_field.setText(patient.getName());

        last_name_field.setText(patient.getLast_name());

        if (patient.getBirthday() != null) {
            birthday_field.setDisable(true);
            birthday_field.setValue(patient.getBirthday().toLocalDate());
        }

        // TODO Improvement: add the value as choice if it's not in the choices.
        gender_field.getItems().addAll("Homme", "Femme", "Non binaire");
        gender_field.setValue(patient.getGender());

        relation_field.getItems().addAll("Célibataire", "Couple", "Autre");
        relation_field.setValue(patient.getRelationship());

        discovery_field.setText(patient.getDiscovery_way());

        // start loadConsultations service
        if (super.loadConsultations.getState() == Task.State.READY) {
            super.loadConsultations.start();
        }

        // Setup services
        super.loadConsultations.setOnSucceeded(evt -> {
            System.out.println("Task succeeded!");
            // run createBoxConsultations
            super.createBoxConsultations("patient_consultation_cell");
        });
        super.loadConsultations.setOnFailed(evt -> {
            evt.getSource();
            System.out.println("Task failed! Could not show consultations!");
        });

        /*
         * Update Patient, then send it to DB
         */
        edit_button.setOnAction(event -> {
            patient.setRelationship(relation_field.getValue());
            patient.setGender(gender_field.getValue());
            patient.setDiscovery_way(discovery_field.getText());
            if (birthday_field.getValue() != null) {
                patient.setBirthday(birthday_field.getValue());
            }
            if (true) {
                if (updateUserDetails.getState() == Task.State.READY) // loading update table in Service
                    updateUserDetails.start();
            }
        });

    }

    protected boolean setupBoxConsultations() {
        box_consultations.setSpacing(20);
        for (int i = 0; i < patient.getConsultationHistoric().size(); i++) {
            super.consultationArrayList.add(buildConsultationButton(patient.getConsultationHistoric().get(i)));
            if (consultationArrayList.get(i) == null) return false; // if error
        }

        // setup comparator
        super.consultationArrayList.sort(Comparator.comparing(Consultation::getDate));

        // order descending by default
        Collections.reverse(super.consultationArrayList);

        return true;
    }

    Service<Boolean> updateUserDetails = new Service<Boolean>() {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() {

                    return patient.updatePatient();
                }
            };
        }
    };

}
