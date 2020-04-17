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
import java.util.HashMap;
import java.util.ResourceBundle;

public class PatientsController implements Initializable {

    // JavaFX
    public VBox patient_list_box;
    public HashMap<JFXButton, Boolean> profilesButtonsHashMap;

    // Attributes
    public static ArrayList<Patient> list_patients;
    public static int current_patient_id;
    public AnchorPane profilePane;

    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        profilesButtonsHashMap = new HashMap<>();
        patient_list_box.setSpacing(20);
        setupListPatients();
    }

    public void setupListPatients() {
        list_patients = Patient.getAllPatientsProfiles();
        int i = 0;
        for (Patient p : list_patients
        ) {
            JFXButton patient_button = new JFXButton();
            patient_button.getStyleClass().add("patient_cell");
            patient_button.getStyleClass().add("patient_cell_list");

            patient_button.setText(p.getName() + " " + p.getLast_name());

            profilesButtonsHashMap.put(patient_button, false);

            int finalI = i;
            patient_button.setOnAction(event -> {
                loadPatientInfo(p, finalI);
                updateButtonStyle(patient_button);
            });
            i++;
            patient_list_box.getChildren().add(patient_button);
        }
    }

    private void loadPatientInfo(Patient p, int i) {
        try {
            profilePane.getChildren().clear();
            current_patient_id = i;
            profilePane.getChildren().add(FXMLLoader.load(getClass().getResource("../fxml/profile_patient.fxml")));
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


}
