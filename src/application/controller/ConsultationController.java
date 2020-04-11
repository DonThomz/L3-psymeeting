package application.controller;

import application.App;
import com.jfoenix.controls.*;
import data.Consultation;
import data.Patient;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConsultationController implements Initializable {

    // Fields
    public JFXTextField searchField;
    public VBox box_consultations;

    // Attributes
    private ArrayList<Calendar> list_date;



    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list_date = new ArrayList<>();
        setupBoxConsultations();
    }

    private void setupBoxConsultations() {
        box_consultations.setSpacing(20);
        int last_id = Consultation.getLastPrimaryKeyId();
        for (int i = 1; i <= last_id; i++) {
            box_consultations.getChildren().add(buildConsultationButton(i));
        }

    }




    // --------------------
    //  Action methods
    // --------------------



    // --------------------
    //  Private methods
    // --------------------

    private JFXButton buildConsultationButton(int consultation_id){

        try {
            // init button
            JFXButton consultation_button = new JFXButton();
            // setting button
            consultation_button.setId("consultation-button-id-" + consultation_id);
            consultation_button.getStyleClass().add("consultation_cell");


            // Setup content
            VBox box = new VBox();

            Calendar date_consultation = Consultation.getDateById(consultation_id);
            String timeStamp = new SimpleDateFormat("EEEE dd MMMM, yyyy à hh:mm",
                    Locale.FRANCE).format(date_consultation.getTime());
            Label title = new Label("Consultation : " + consultation_id
                + "\n\t"+timeStamp);

            box.getChildren().add(title);

            // get patient and add in a javaFX TEXT
            ResultSet tmp_result_patients = Patient.getPatientsByConsultationId(consultation_id);
            assert tmp_result_patients != null;
            Label patient_list = new Label();
            patient_list.getStyleClass().add("content_text");
            StringBuilder content = new StringBuilder();
            while(tmp_result_patients.next()) {
                content.append(" | ").append(tmp_result_patients.getString(1)).append(" ").append(tmp_result_patients.getString(2)).append(" \n");
            }
            patient_list.setText(String.valueOf(content));
            box.getChildren().add(patient_list);

            // add to the button
            consultation_button.setGraphic(box);

            return consultation_button;

        }catch (SQLException ex){
            System.out.println("SQL Error to get patients information");
            ex.printStackTrace();
        }
        return null;
    }

}

