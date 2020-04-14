package application.controller;

import com.jfoenix.controls.JFXButton;
import data.Patient;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PatientsController implements Initializable {

    // Box
    public VBox patient_list_box;

    // Attributes
    public static ArrayList<Patient> list_patients;
    public static int current_patient_id;
    public AnchorPane profilePane;

    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        patient_list_box.setSpacing(20);
        setupListPatients();
    }

    public void setupListPatients(){
        list_patients = Patient.getAllPatientsProfiles();
        for (Patient p: list_patients
             ) {
            JFXButton patient_button = new JFXButton();
            patient_button.getStyleClass().add("patient_cell");
            patient_button.getStyleClass().add("patient_cell_list");

            patient_button.setText(p.getName() + " " + p.getLast_name());

            patient_button.setOnAction(event -> loadPatientInfo(p));

            patient_list_box.getChildren().add(patient_button);
        }
    }

    private void loadPatientInfo(Patient p) {
        try {
            profilePane.getChildren().clear();
            current_patient_id = p.getPatient_id();
            profilePane.getChildren().add(FXMLLoader.load(getClass().getResource("../fxml/profile_patient.fxml")));
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }


}
