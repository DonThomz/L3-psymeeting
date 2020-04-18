package com.bdd.psymeeting.controller;

import com.bdd.psymeeting.model.Consultation;
import com.bdd.psymeeting.model.Patient;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class ParentController {

    // FXML
    @FXML
    public StackPane stackPane;
    @FXML
    public VBox box_consultations;

    // Attributes
    protected Map<Calendar, JFXButton> consultations_map;
    protected int consultation_size;
    protected Calendar date_today;

    // --------------------
    //  Get methods
    // --------------------
    public StackPane getStackPane() {
        return stackPane;
    }


    // --------------------
    //  Set methods
    // --------------------
    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }


    // --------------------
    //  Consultation methods
    // --------------------

    protected void setupBoxConsultations() {
    }

    protected JFXButton buildConsultationButton(int consultation_id) {

        Consultation consultation = new Consultation(consultation_id);

        // init button
        JFXButton consultation_button = new JFXButton();
        // setting button
        consultation_button.setId("consultation-button-id-" + consultation_id);


        // Setup content
        VBox box = new VBox();

        Calendar date_consultation = Consultation.getDateById(consultation_id);
        assert date_consultation != null;
        @SuppressWarnings("SpellCheckingInspection") String timeStamp = new SimpleDateFormat("EEEE dd MMMM, yyyy à HH:mm",
                Locale.FRANCE).format(date_consultation.getTime());
        Label title = new Label("Consultation : "
                + "\n\t" + timeStamp);

        box.getChildren().add(title);

        // create label and add patients
        Label patient_list = new Label();
        patient_list.getStyleClass().add("content_text");
        StringBuilder content = new StringBuilder();
        for (Patient p : consultation.getPatients()
             ) {
            content.append(" | ").append(p.getName()).append(" ").append(p.getLast_name()).append(" \n");
        }
        patient_list.setText(String.valueOf(content));
        box.getChildren().add(patient_list);


        // add action on button
        consultation_button.setOnAction(event -> loadConsultationInfo(consultation_id, date_consultation, content));

        // add to the button
        consultation_button.setGraphic(box);

        // add attributes to consultation instance
        return consultation_button;

    }

    protected void loadConsultationInfo(int consultation_id, Calendar date, StringBuilder patients_list) {
        // create dialog layout
        JFXDialogLayout content = new JFXDialogLayout();

        // add heading
        content.setHeading(createTitle(date));

        // add body
        content.setBody(createBody(consultation_id, patients_list));

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton done = new JFXButton("Fermer");
        JFXButton modify = new JFXButton("Modifier");

        done.setOnAction(event -> dialog.close());

        modify.setOnAction(event -> dialog.close());

        content.setActions(modify, done);

        dialog.show();
    }

    protected TextArea createBody(int consultation_id, StringBuilder patients_list) {

        // get info
        StringBuilder info = new StringBuilder();
            Consultation consultation = new Consultation(consultation_id);

            // info price and pay mode
            info.append("Prix : ").append(consultation.getPrice()).append(" €, payé avec : ").append(consultation.getPayMode());

            // info feedback commentary, key words, postures, indicator
            info.append("\n\nRetour de séance").append("\n\n\tCommentaire : \n").append(consultation.getFeedback().getCommentary());
            if (consultation.getFeedback().getKeyword() != null)
                info.append("\n\n\tMots clés :").append(consultation.getFeedback().getKeyword());
            if (consultation.getFeedback().getPosture() != null)
                info.append("\n\n\tPosture :").append(consultation.getFeedback().getPosture());
            if (consultation.getFeedback().getIndicator() != 0)
                info.append("\n\n\tIndicateur :").append(consultation.getFeedback().getIndicator());

        TextArea textArea = new TextArea("Patients :\n"
                + patients_list + "\n"
                + info + "\n"
        );
        textArea.setWrapText(true);
        return textArea;
    }

    protected Label createTitle(Calendar date) {
        @SuppressWarnings("SpellCheckingInspection") String format_date = new SimpleDateFormat("EEEE dd MMMM, yyyy à HH:mm",
                Locale.FRANCE).format(date.getTime());
        return new Label("Consultation du " + format_date);
    }


}
