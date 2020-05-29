/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.controller.patients;

import com.bdd.psymeeting.controller.InitController;
import com.bdd.psymeeting.controller.consultations.ConsultationHistoric;
import com.bdd.psymeeting.model.Consultation;
import com.bdd.psymeeting.model.Job;
import com.bdd.psymeeting.model.Patient;
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;


import java.net.URL;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class PatientDetailsController extends ConsultationHistoric implements Initializable, InitController {

    // Fields
    public JFXTextField name_field;
    public JFXTextField last_name_field;
    public JFXTextField email_field;
    public JFXDatePicker birthday_field;
    public JFXComboBox<String> gender_field;
    public JFXComboBox<String> relation_field;
    public JFXComboBox<String> discovery_field;
    public JFXComboBox<String> jobs_list_field;
    public JFXButton jobs_button;
    public JFXButton edit_button;
    public VBox box_consultations;
    public Label infoEditLabel;

    private RequiredFieldValidator validator_field;
    // Attributes
    protected Patient patient;
    private ArrayList<Job> jobs;

    Service<Boolean> updateUserDetails = new Service<Boolean>() {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() {
                    return patient.updatePatient() && Job.insertJobFromArrayList(jobs);
                }
            };
        }
    };

    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        infoEditLabel.setTextAlignment(TextAlignment.CENTER);

        super.date_today = Calendar.getInstance();
        super.consultationArrayList = new ArrayList<>();

        patient = PatientsController.list_patients.get(PatientsController.current_patient_id);

        name_field.setText(patient.getName());

        last_name_field.setText(patient.getLast_name());

        email_field.setText(patient.getUser().getEmail());

        if (patient.getBirthday() != null) {
            birthday_field.setDisable(true);
            birthday_field.setValue(patient.getBirthday().toLocalDate());
        }

        // TODO Improvement: add the value as choice if it's not in the choices.
        gender_field.setValue(patient.getGender());
        gender_field.getItems().addAll("Homme", "Femme", "Non défini", "Autre");

        relation_field.setValue(patient.getRelationship());
        relation_field.getItems().addAll("Célibataire", "Couple", "Autre");

        discovery_field.setPromptText(patient.getDiscovery_way());
        discovery_field.getItems().addAll("Autre patient", "Pages jaunes", "Internet", "Autres");

        // jobs list
        ObservableList<String> jobsList = patient.getJobs().stream().map(Job::getJob_name).collect(Collectors.toCollection(FXCollections::observableArrayList));
        jobs_list_field.setItems(jobsList);


        // init Services
        initServices();

        // init Listeners
        initListeners();
    }

    @Override
    public void initServices() {

        jobs = new ArrayList<>();

        // start loadConsultations service
        if (super.loadConsultations.getState() == Task.State.READY) {
            super.loadConsultations.start();
        }

        // Setup services
        super.loadConsultations.setOnSucceeded(evt -> {
            System.out.println("Task load consultation succeeded!");
            // run createBoxConsultations
            super.createBoxConsultations("patient_consultation_cell");
        });
        super.loadConsultations.setOnFailed(evt -> System.out.println("Task failed! Could not show consultations!"));

        /*
         * Update Patient, then send it to DB
         */

        updateUserDetails.setOnSucceeded(event -> {
            System.out.println("Editing patient succeeded !");
            if (updateUserDetails.getValue()) {
                infoEditLabel.setStyle("-fx-text-fill: #28a528");
                infoEditLabel.setText("Modification du profil réussi ! ");
            } else {
                infoEditLabel.setStyle("-fx-text-fill: #b42727");
                infoEditLabel.setText("Échec de la modification du profil ");
            }
            updateUserDetails.reset();
        });

        updateUserDetails.setOnFailed(event -> {
            System.out.println("Task editing profil failed");
            infoEditLabel.setStyle("-fx-text-fill: #b42727");

            infoEditLabel.setText("Échec de la modification du profil,\nvérifier si la date de naissance est attribuée");
            updateUserDetails.reset();
        });
    }

    @Override
    public void initListeners() {

        jobs = new ArrayList<>();

        // validation settings
        validator_field = new RequiredFieldValidator();
        validator_field.setMessage("Le champs est obligatoire");

        addListenerValidationField(name_field);
        addListenerValidationField(last_name_field);
        addListenerValidationField(email_field);
        addListenerValidationField(birthday_field);

        edit_button.setOnAction(event -> {
            patient.setRelationship(relation_field.getValue());
            patient.setGender(gender_field.getValue());
            patient.setDiscovery_way(discovery_field.getValue());
            if (birthday_field.getValue() != null) {
                patient.setBirthday(birthday_field.getValue());
            }
            if (jobs.size() > 0) patient.setJobs(jobs);
            if (updateUserDetails.getState() == Task.State.READY) // loading update table in Service
                updateUserDetails.start();
        });

        jobs_button.setOnAction(event -> loadJobForm());
    }

    private void loadJobForm() {
        // create dialog layout
        JFXDialogLayout content = new JFXDialogLayout();
        // add heading
        content.setHeading(new Label("Ajout d'un métier"));

        // add body
        VBox jobForm = new VBox();
        jobForm.setSpacing(20);

        HBox jobNameBox = new HBox();
        jobNameBox.setSpacing(20);
        Label jobName = new Label("Nom du métier");

        Region space = new Region();
        space.setMinWidth(20);
        JFXTextField jobNameField = new JFXTextField();

        jobNameBox.getChildren().addAll(jobName, jobNameField);

        HBox jobDateBox = new HBox();
        jobDateBox.setSpacing(20);
        Label jobDate = new Label("Date");
        Region space2 = new Region();
        space.setMinWidth(20);
        JFXDatePicker jobDateField = new JFXDatePicker();
        jobDateBox.getChildren().addAll(jobDate, space2, jobDateField);

        addListenerValidationField(jobDateField);
        addListenerValidationField(jobNameField);

        jobForm.getChildren().addAll(jobNameBox, jobDateBox);
        content.setBody(jobForm);

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton done = new JFXButton("Fermer");
        JFXButton submit = new JFXButton("Sauvegarder");

        submit.setOnAction(event -> {
            if (validJobField(jobNameField, jobDateField)) {
                jobs.add(new Job(jobNameField.getText().toUpperCase(), Date.valueOf(jobDateField.getValue())));
                jobs.get(jobs.size() - 1).setPatientID(patient.getPatient_id());
                jobs_list_field.getItems().add(jobs.get(jobs.size() - 1).getJob_name());
                dialog.close();
            } else validatePriorityJobField(jobNameField, jobDateField);

        });

        done.setOnAction(event -> dialog.close());

        content.setActions(submit, done);

        dialog.show();
    }

    private boolean validField() { // check if fields are not empty
        return !name_field.getText().isEmpty()
                && !last_name_field.getText().isEmpty()
                && !email_field.getText().isEmpty()
                && birthday_field.getValue() != null;
    }

    private boolean validJobField(JFXTextField jobNameField, JFXDatePicker jobDateField) {
        return !jobNameField.getText().isEmpty() && jobDateField.getValue() != null;
    }

    private void validatePriorityJobField(JFXTextField jobNameField, JFXDatePicker jobDateField) { // add require validation if priority fields are empty
        if (jobNameField.getText().isEmpty())
            jobNameField.validate();
        if (jobDateField.getValue() == null)
            jobDateField.validate();
    }

    private void validatePriorityField() { // add require validation if priority fields are empty
        if (name_field.getText().isEmpty())
            name_field.validate();
        if (last_name_field.getText().isEmpty())
            last_name_field.validate();
        if (email_field.getText().isEmpty())
            email_field.validate();
        if (birthday_field.getValue() == null)
            birthday_field.validate();
    }

    private void addListenerValidationField(JFXTextField field) {
        field.getValidators().add(validator_field);
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                field.resetValidation();
            }
        });
    }

    private void addListenerValidationField(JFXDatePicker field) {
        field.getValidators().add(validator_field);
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                field.resetValidation();
            }
        });

    }

    @Override
    public void refresh() {
        patient.getConsultationHistoric().remove(consultationToBeRemove);
        consultationArrayList.clear();
        box_consultations.getChildren().clear();
        setupBoxConsultations();
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


}
