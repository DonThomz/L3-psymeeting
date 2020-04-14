package application.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import data.Job;
import data.Patient;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class profileController implements Initializable {


    public JFXTextField name_field;
    public JFXTextField last_name_field;
    public JFXTextField birdthday_field;
    public JFXComboBox<String> gender_field;
    public JFXComboBox<String> relation_field;
    public JFXTextField discovery_field;
    public JFXComboBox<Job> jobs_list_field;
    public VBox box_consultations;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Patient tmp_p = PatientsController.list_patients.get(PatientsController.current_patient_id-1);

        name_field.setText(tmp_p.getName());

        last_name_field.setText(tmp_p.getLast_name());

        birdthday_field.setText(tmp_p.getBirthday().toString());

        gender_field.getItems().addAll( "homme", "femme", "non binaire");
        gender_field.setValue(tmp_p.getGender());

        relation_field.getItems().addAll("célibataire", "couple", "autre");
        relation_field.setValue(tmp_p.getRelationship());

        discovery_field.setText(tmp_p.getDiscovery_way());
    }
}
