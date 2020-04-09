package application.controller;

import application.App;
import com.jfoenix.controls.*;
import data.Consultation;
import data.Patient;
import data.User;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddConsultationController implements Initializable {

    // Add patients feature
    public Label label_patients;
    public static int nb_patients;
    public JFXButton add_patient_button;

    // Fields
    public JFXDatePicker date_field;
    public JFXTimePicker hour_field;
    public JFXTextField name_field;
    public JFXTextField last_name_field;
    public JFXTextField email_field;
    public JFXCheckBox anxiety_checkbox;
    public VBox form_box;


    // --------------------
    //   Attributes
    // --------------------
    private ArrayList<Patient> tmp_patients;
    private ArrayList<User> tmp_users;
    private int max_patient_id;
    private int consultation_id;

    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // init patient label
        nb_patients = 1;
        max_patient_id = Patient.getLastPatientId();
        label_patients.setText(label_patients.getText() + " " +nb_patients);

        // init tmp_patients ArrayList
        tmp_patients = new ArrayList<>();

    }

    // --------------------
    //  Action methods
    // --------------------

    // button "Enregistrer"
    public void submit(ActionEvent actionEvent) throws SQLException {
        // disable during the thread
        add_patient_button.setDisable(true);

        if(tmp_patients != null && date_field.getValue() != null && hour_field.getValue() != null){

            // enable commit command
            App.database.getConnection().setAutoCommit(false);

            // update patient ArrayList
            addPatient2ArrayList();

            // update patient table with new patients
            updatePatientTable();

            // update consultation table
            updateConsultationTable();

            // update consultation_carryOut table
            updateCarryOutTable();

            App.database.getConnection().commit();
            System.out.println("successful");

            // reload scene
            App.sceneMapping("add_consultation_scene", "add_consultation_scene");

        }


    }

    // button "Ajouter patient"
    public void add(ActionEvent actionEvent) {

        // field not empty
        if(validField()) {

            // create and add patient to ArrayList patients
            if(addPatient2ArrayList()) {
                System.out.println(tmp_patients);
                // reset field
                resetField();

                // update patient label
                updatePatientLabel();
            }
        }
    }

    // --------------------
    //  Private methods
    // --------------------
    private boolean addPatient2ArrayList(){

        // check if user exist ==> email exit in database
        if(userExist()){
            // check if name and last name is correct
            Patient tmp_patient = Patient.getPatientByEmail(email_field.getText());
            assert tmp_patient != null;
            if(tmp_patient.getName().equals(name_field.getText()) && tmp_patient.getLast_name().equals(last_name_field.getText()))
                tmp_patients.add(tmp_patient);
            else{
                // error user email already exist in database
                // ex : ubabst1@zdnet.com
                Label warring = new Label("L'email renseigné est déjà pris");
                warring.getStyleClass().add("warring_label");
                form_box.getChildren().add(2, warring);

                return false;
            }
        }else{
            if(max_patient_id != -1){
                tmp_patients.add(new Patient(max_patient_id + 1, name_field.getText(), last_name_field.getText(), true));
                max_patient_id++;
            }
        }
        return true;
    }

    private boolean userExist(){
        try {
            Statement stmt = App.database.getConnection().createStatement();
            ResultSet rset = stmt.executeQuery("select email from USER_APP");
            while(rset.next()){
                if(rset.getString(1).equals(email_field.getText())){
                    return true;
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    private boolean validField(){
        return !name_field.getText().isEmpty()
                && !last_name_field.getText().isEmpty()
                && !email_field.getText().isEmpty();
    }

    private void resetField(){
        // reset field
        name_field.setText("");
        last_name_field.setText("");
        email_field.setText("");
        anxiety_checkbox.setSelected(false);
    }

    // --------------------
    //  Update table methods
    // --------------------
    private void updatePatientLabel(){
        // update label and button
        nb_patients++;
        if (nb_patients <= 3)
            label_patients.setText("Patient - " + nb_patients);
        else{
            add_patient_button.setDisable(true);
        }
    }

    private void updatePatientTable(){
        try {
            // the insert statement
            String query = " insert into patient (patient_id, name, last_name)"
                    + " values (?, ?, ?)";
            // create the insert preparedStatement
            PreparedStatement preparedStmt = App.database.getConnection().prepareStatement(query);
            //App.database.getConnection().setAutoCommit(false);
            for (Patient p: tmp_patients
            ) {
                if(p.isNew_patient()){

                    // config parameters
                    preparedStmt.setInt(1, p.getPatient_id());
                    preparedStmt.setString (2, p.getName());
                    preparedStmt.setString (3, p.getLast_name());
                    // execute the preparedStatement
                    preparedStmt.execute();
                }
            }
        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
        }
    }

    private void updateConsultationTable() {
        try {
            consultation_id = Consultation.getLastConsultationId();
            if(consultation_id != -1) {
                // the insert statement
                String query = " insert into CONSULTATION (CONSULTATION_ID, CONSULTATION_DATE)"
                        + " values (?, ?)";
                // create the insert preparedStatement
                PreparedStatement preparedStmt = App.database.getConnection().prepareStatement(query);

                // config parameters
                preparedStmt.setInt(1, consultation_id+1);
                preparedStmt.setTimestamp(2,Timestamp.valueOf(date_field.getValue()+" "+hour_field.getValue()+":00"));

                // execute the preparedStatement
                preparedStmt.execute();

                App.database.getConnection().commit();
            }

        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
        }
    }

    private void updateCarryOutTable(){
        try{
            // the insert statement
            String query = " insert into CONSULTATION_CARRYOUT (PATIENT_ID, CONSULTATION_ID)"
                    + " values (?, ?)";
            // create the insert preparedStatement
            PreparedStatement preparedStmt = App.database.getConnection().prepareStatement(query);

            // create each carryout for each patients
            for (Patient p: tmp_patients
                 ) {
                // config parameters
                preparedStmt.setInt(1, p.getPatient_id());
                preparedStmt.setInt(2, consultation_id+1);

                // execute the preparedStatement
                preparedStmt.execute();
            }
            App.database.getConnection().commit();

        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
        }
    }


}
