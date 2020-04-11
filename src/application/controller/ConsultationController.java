package application.controller;

import application.TransitionEffect;
import com.jfoenix.controls.*;
import data.Consultation;
import data.Patient;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConsultationController implements Initializable {

    // Fields
    public JFXTextField searchField;
    public VBox box_consultations;
    // Table view

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupBoxConsultations();
    }

    private void setupBoxConsultations() {
        try {
            int last_id = Consultation.getLastConsultationId();
            for (int i = 0; i < last_id; i++) {

                JFXButton consultation = new JFXButton();

                ResultSet tmp_result_patients = Patient.getPatientsByConsultationId(i);
                assert tmp_result_patients != null;
                StringBuilder info = new StringBuilder();
                info.append("Consultation : ").append(i).append(" \n ");
                while (tmp_result_patients.next()) {
                    info.append("- ").append(tmp_result_patients.getString(2)).append(" \n ");
                }
                consultation.setId("consultation-"+i);
                consultation.setText(String.valueOf(info));
                box_consultations.getChildren().add(consultation);
            }
        }catch (SQLException ex){
            System.out.println("SQL Error to get patients information");
            ex.printStackTrace();
        }
    }

}
