package application.controller;

import application.App;
import application.TransitionEffect;
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import data.Consultation;
import data.Patient;
import data.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
    public JFXButton submit_button;
    RequiredFieldValidator validator_field;

    // Box
    public StackPane stackPane;
    public VBox form_box;
    public ScrollPane scroll_pane;

    // --------------------
    //   Attributes
    // --------------------
    private ArrayList<Patient> tmp_patients;
    private ArrayList<User> tmp_users;
    private int max_patient_id;
    private int consultation_id;
    private boolean confirmation;


    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // add transition effects to the form
        TransitionEffect.TranslateTransitionY(form_box, 600, 75);
        TransitionEffect.FadeTransition(form_box, 600, 0.2f, 5);

        // init patient label
        nb_patients = 1;
        max_patient_id = Patient.getLastPrimaryKeyId();
        label_patients.setText(label_patients.getText() + " " +nb_patients);

        // init tmp_patients ArrayList
        tmp_patients = new ArrayList<>();

        // validation settings
        validator_field = new RequiredFieldValidator();
        validator_field.setMessage("Le champs est obligatoire");

        // add validation for all fields
        addListenerValidationField(name_field);
        addListenerValidationField(last_name_field);
        addListenerValidationField(email_field);
        addListenerValidationField(date_field);
        addListenerValidationField(hour_field);

    }

    // --------------------
    //  Action methods
    // --------------------
    public void submit(ActionEvent actionEvent){ // button "Enregistrer"
        // disable during the thread
        add_patient_button.setDisable(true);

        // check if minimum 1 patient exist in tmp_patients or if fields are not empty
        if(date_field.getValue() != null && hour_field.getValue() != null && (!tmp_patients.isEmpty() || validField())){
            // open confirmation dialog
            loadDialog(actionEvent);
        }else{
            validateTextFieldAction();
            validateDateFieldAction();
        }
        add_patient_button.setDisable(false);

    }


    public void add(ActionEvent actionEvent) { // button "Ajouter patient"
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
        else validateTextFieldAction();
    }

    // --------------------
    //  Private methods
    // --------------------
    private boolean addPatient2ArrayList(){

        if (userExist()) { // check if user exist ==> email exit in database
            // check if name and last name is correct
            Patient tmp_patient = Patient.getPatientByEmail(email_field.getText());
            assert tmp_patient != null;
            if (tmp_patient.getName().equals(name_field.getText()) && tmp_patient.getLast_name().equals(last_name_field.getText())){
                tmp_patients.add(tmp_patient);
                return true;
            }
            else {
                // error user email already exist in database
                // ex : ubabst1@zdnet.com
                Label warring = new Label("L'email renseigné est déjà pris");
                warring.getStyleClass().add("warring_label");
                form_box.getChildren().add(2, warring);
                return false;
            }
        } else {
            if (max_patient_id != -1) {
                tmp_patients.add(new Patient(max_patient_id + 1, name_field.getText(), last_name_field.getText(), true));
                max_patient_id++;
                return true;
            }
        }
        return false;
    }

    private boolean userExist(){ // check if user exist in database with email_field
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

    private void loadDialog(ActionEvent event){
        JFXDialogLayout content = new JFXDialogLayout();
        confirmation = false;
        content.setHeading(new Text("La consultation a été enregistrée !"));
        // show information
        StringBuilder info = new StringBuilder();
        if(tmp_patients.size() != 0) {
            for (Patient p : tmp_patients
            ) {
                info.append("\n- ").append(p.getName()).append(" ").append(p.getLast_name());
            }
            if(validField())
                info.append("\n- ").append(name_field.getText()).append(" ").append(last_name_field.getText());
        }else
            info.append("\n- ").append(name_field.getText()).append(" ").append(last_name_field.getText());

        content.setBody(new Text("Date de consultation : " + date_field.getValue()
                + " à " + hour_field.getValue() + "\n"
                + "Les patients sont : \n"
                + info));

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton submit = new JFXButton("Confimer & Envoyer");
        JFXButton cancel = new JFXButton("Annuler");
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                confirmation = true;
                dialog.close();
                updateNewConsultation();
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                confirmation = false;
                dialog.close();
            }
        });
        content.setActions(cancel, submit);

        dialog.show();
    }


    // --------------------
    //  Field methods
    // --------------------
    private boolean validField(){ // check if fields are not empty
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

    private void validateTextFieldAction(){ // add require validation if fields are empty
        if (name_field.getText().isEmpty())
            name_field.validate();
        if (last_name_field.getText().isEmpty())
            last_name_field.validate();
        if (email_field.getText().isEmpty())
            email_field.validate();
    }

    private void validateDateFieldAction(){ // add require validation if date and hour fields are empty
        if (date_field.getValue() == null)
            date_field.validate();
        if (hour_field.getValue() == null)
            hour_field.validate();
    }

    private void addListenerValidationField(JFXTextField field){
        field.getValidators().add(validator_field);
        field.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue)
                    field.resetValidation();
            }
        });
    }
    private void addListenerValidationField(JFXDatePicker field){
        field.getValidators().add(validator_field);
        field.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue)
                    field.resetValidation();
            }
        });
    }
    private void addListenerValidationField(JFXTimePicker field){
        field.getValidators().add(validator_field);
        field.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue)
                    field.resetValidation();
            }
        });
    }

    // --------------------
    //  Update methods
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

            for (Patient p: tmp_patients // for each patients saved in tmp_patients
            ) {
                if(p.isNew_patient()){ // if patient does not exist in database

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
            consultation_id = Consultation.getLastPrimaryKeyId();
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

    void updateNewConsultation(){
        try {
            if (confirmation) {
                System.out.println("conf");
                // enable commit command
                App.database.getConnection().setAutoCommit(false);

                // update patient ArrayList
                if (validField())
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
        }catch (SQLException ex){
            System.out.println("Error creation consultation");
            ex.printStackTrace();
        }
    }


}
