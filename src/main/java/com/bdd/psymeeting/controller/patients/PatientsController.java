/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.controller.patients;

import com.bdd.psymeeting.Main;
import com.bdd.psymeeting.controller.InitController;
import com.bdd.psymeeting.model.Patient;
import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PatientsController implements Initializable, InitController {

    public static ArrayList<Patient> list_patients;
    public static int current_patient_id;
    // --------------------
    //  Services
    // --------------------
    final Service<ArrayList<Patient>> loadPatients = new Service<ArrayList<Patient>>() {
        @Override
        protected Task<ArrayList<Patient>> createTask() {
            return new Task<ArrayList<Patient>>() {
                @Override
                protected ArrayList<Patient> call() throws Exception {
                    return Patient.getAllFullPatientsProfiles(); // init patients
                }
            };
        }
    };
    // JavaFX
    public VBox patient_list_box;
    public AnchorPane profilePane;
    public JFXButton addPatientButton;
    // Attributes
    public HashMap<JFXButton, Boolean> profilesButtonsHashMap;

    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addPatientButton.setOnAction(event -> {
            loadAddPatientForm();
        });

        profilesButtonsHashMap = new HashMap<>();
        patient_list_box.setSpacing(10);

        initServices();

    }

    @Override
    public void initServices() {

        // execute sql request in another thread
        if (loadPatients.getState() == Task.State.READY)
            loadPatients.start();

        loadPatients.setOnSucceeded(event -> {
            System.out.println("Task succeeded -> loading patients informations\nCreation patients box...");
            setupListPatients(loadPatients.getValue());
        });

        loadPatients.setOnFailed(event -> System.out.println("Task failed -> error loading patients informations"));

        loadPatients.setOnRunning(event -> System.out.println("Loading..."));
    }

    @Override
    public void initListeners() {

    }

    public void setupListPatients(ArrayList<Patient> patientArrayList) {
        int ID = 0;
        list_patients = patientArrayList;
        for (Patient p : list_patients
        ) {
            if (p != null) {
                JFXButton patient_button = new JFXButton();
                patient_button.getStyleClass().add("patient_cell");
                patient_button.getStyleClass().add("patient_cell_list");

                patient_button.setText(p.getName() + " " + p.getLast_name());

                profilesButtonsHashMap.put(patient_button, false);

                int finalID = ID;
                patient_button.setOnAction(event -> {
                    loadPatientInfo(finalID);
                    updateButtonStyle(patient_button);
                });
                ID++;
                patient_list_box.getChildren().add(patient_button);
            }
        }

    }

    private void loadPatientInfo(int ID) {
        try {
            profilePane.getChildren().clear();
            current_patient_id = ID;
            profilePane.getChildren().add(FXMLLoader.load(Main.class.getResource("views/patients/profile_patient.fxml")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateButtonStyle(JFXButton b) {
        profilesButtonsHashMap.forEach((k, v) -> {
            if (k.getText().equals(b.getText())) {
                profilesButtonsHashMap.put(k, true);
                k.setStyle("-fx-background-color: #546e7a");
            } else {
                profilesButtonsHashMap.put(k, false);
                k.setStyle("-fx-background-color: #fafafa");
            }
        });
    }

    private void resetButtonStyle() {
        profilesButtonsHashMap.forEach((k, v) -> {
            profilesButtonsHashMap.put(k, false);
            k.setStyle("-fx-background-color: #fafafa");
        });
    }

    private void loadAddPatientForm() {
        try {
            resetButtonStyle();
            profilePane.getChildren().clear();
            profilePane.getChildren().add(FXMLLoader.load(Main.class.getResource("views/patients/patient_form.fxml")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
