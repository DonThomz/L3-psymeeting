package application.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import data.Consultation;
import data.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ParentController {

    // FXML
    @FXML public StackPane stackPane;
    @FXML public VBox box_consultations;

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

    protected JFXButton buildConsultationButton(int consultation_id){

        try {

            // init button
            JFXButton consultation_button = new JFXButton();
            // setting button
            consultation_button.setId("consultation-button-id-" + consultation_id);


            // Setup content
            VBox box = new VBox();

            Calendar date_consultation = Consultation.getDateById(consultation_id);
            assert date_consultation != null;
            String timeStamp = new SimpleDateFormat("EEEE dd MMMM, yyyy à HH:mm",
                    Locale.FRANCE).format(date_consultation.getTime());
            Label title = new Label("Consultation : "
                    + "\n\t"+timeStamp);

            box.getChildren().add(title);

            // get patient and add in a javaFX TEXT
            ResultSet tmp_result_patients = Patient.getPatientFullNameByConsultationId(consultation_id);
            assert tmp_result_patients != null;

            // create label and add patients
            Label patient_list = new Label();
            patient_list.getStyleClass().add("content_text");
            StringBuilder content = new StringBuilder();
            while(tmp_result_patients.next()) {
                content.append(" | ").append(tmp_result_patients.getString(1)).append(" ").append(tmp_result_patients.getString(2)).append(" \n");
            }
            patient_list.setText(String.valueOf(content));
            box.getChildren().add(patient_list);


            // add action on button
            consultation_button.setOnAction(event -> loadConsultationInfo(consultation_id, date_consultation, content));

            // add to the button
            consultation_button.setGraphic(box);

            // add attributes to consultation instance
            return consultation_button;

        }catch (SQLException ex){
            System.out.println("SQL Error to get patients information");
            ex.printStackTrace();
        }
        return null;
    }

    protected void loadConsultationInfo(int consultation_id, Calendar date, StringBuilder patients_list){
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

    protected TextArea createBody(int consultation_id, StringBuilder patients_list){

        // get info
        StringBuilder info = new StringBuilder();
        try {
            ResultSet result = Consultation.getConsultationInfoById(consultation_id);

            assert result != null;
            result.next();
            // info price and pay mode
            info.append("Prix : ").append(result.getInt(1)).append(" €, payé avec : ").append(result.getString(2));

            // info feedback commentary, key words, postures
            info.append("\n\nRetour de séance").append("\n\n\tCommentaire : \n").append(result.getString(3));
            if(result.getString(4) != null)
                info.append("\n\n\tMots clés :").append(result.getString(4));
            if(result.getString(5) != null)
                info.append("\n\n\tPosture :").append(result.getString(5));

        }catch (SQLException ex){
            System.out.println("Error loading information...");
            ex.printStackTrace();
        }
        TextArea textArea = new TextArea("Patients :\n"
                + patients_list + "\n"
                + info + "\n"
        );
        textArea.setWrapText(true);
        return textArea;
    }

    protected Label createTitle(Calendar date){
        String format_date = new SimpleDateFormat("EEEE dd MMMM, yyyy à HH:mm",
                Locale.FRANCE).format(date.getTime());
        return new Label("Consultation du " + format_date);
    }



}
