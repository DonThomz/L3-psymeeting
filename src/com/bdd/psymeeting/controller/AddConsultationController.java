package com.bdd.psymeeting.controller;

import com.bdd.psymeeting.Main;
import com.bdd.psymeeting.TransitionEffect;
import com.bdd.psymeeting.model.Consultation;
import com.bdd.psymeeting.model.Patient;
import com.bdd.psymeeting.model.User;
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class AddConsultationController implements Initializable {

    // Add patients feature
    public Label label_patients;
    public static int nb_patients;
    public JFXButton add_patient_button;

    // Fields
    public JFXDatePicker date_field;
    public JFXComboBox<String> hour_field;
    public JFXTextField name_field;
    public JFXTextField last_name_field;
    public JFXTextField email_field;
    public JFXCheckBox anxiety_checkbox;
    public JFXButton submit_button;
    public JFXButton test;

    RequiredFieldValidator validator_field;

    // Box
    public StackPane stackPane;
    public VBox form_box;
    public ScrollPane scroll_pane;
    public VBox patients_save;

    public Label warring;
    private boolean warring_check;

    // --------------------
    //   Attributes
    // --------------------
    private ArrayList<Patient> tmp_patients;
    private ArrayList<User> tmp_users;
    private int last_patient_id;
    private int consultation_id;
    private boolean confirmation;
    private Map<Timestamp, Boolean> appointment_map;


    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        appointment_map = new HashMap<>();
        // add transition effects to the form
        TransitionEffect.TranslateTransitionY(form_box, 600, 75);
        TransitionEffect.FadeTransition(form_box, 600, 0.2f, 5);

        // init patient label
        nb_patients = 1;
        last_patient_id = Patient.getLastPrimaryKeyId();
        label_patients.setText(label_patients.getText() + " " + nb_patients);

        // init ArrayList
        tmp_patients = new ArrayList<>();
        tmp_users = new ArrayList<>();

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
    public void submit(ActionEvent actionEvent) { // button "Enregistrer"
        // disable during the thread
        add_patient_button.setDisable(true);


        // check if minimum 1 patient exist in tmp_patients or if fields are not empty
        if (date_field.getValue() != null && hour_field.getValue() != null && (!tmp_patients.isEmpty() || validField())) {
            // open confirmation dialog
            boolean check = true;
            if (validField())
                check = addPatient2ArrayList();
            if (check) confirmationDialog();
        } else {
            validateTextFieldAction();
            validateDateFieldAction();
        }
        add_patient_button.setDisable(false);

    }

    public void add(ActionEvent actionEvent) { // button "Ajouter patient"
        // field not empty
        if (validField()) {
            // create and add patient to ArrayList patients
            if (addPatient2ArrayList()) {
                System.out.println(tmp_patients);
                // reset field
                resetField();

                // add patient to the side right bar
                attachPatients();
                // update patient label
                updatePatientLabel();

            }
        } else validateTextFieldAction();
    }


    // --------------------
    //  Private methods
    // --------------------
    private boolean addPatient2ArrayList() {

        if (userExist()) { // check if user exist ==> email exit in database
            // check if name and last name is correct
            Patient tmp_patient = Patient.getPatientByEmail(email_field.getText());
            assert tmp_patient != null;
            if (tmp_patient.getName().equals(name_field.getText()) && tmp_patient.getLast_name().equals(last_name_field.getText())) {
                tmp_patients.add(tmp_patient);
                return true;
            } else {
                // error user email already exist in database
                // ex : ubabst1@zdnet.com
                warring = new Label("L'email renseigné est déjà pris");
                warring.getStyleClass().add("warring_label");
                form_box.getChildren().add(2, warring);
                warring_check = true;
                return false;
            }
        } else { // create a patient and a user
            if (last_patient_id != -1) {
                Patient tmp_p = new Patient(last_patient_id + 1, name_field.getText(), last_name_field.getText(), true);
                tmp_patients.add(tmp_p); // add new patient
                tmp_users.add(new User(User.getLastUserId() + 1, email_field.getText(), tmp_p.getPatient_id(), true)); // add new user
                last_patient_id++;
                return true;
            }
        }
        return false;
    }


    private boolean userExist() { // check if user exist in database with email_field
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();

            ResultSet rset = stmt.executeQuery("select email from USER_APP");
            while (rset.next()) {
                if (rset.getString(1).equals(email_field.getText())) {
                    return true;
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    private void confirmationDialog() {
        JFXDialogLayout content = new JFXDialogLayout();
        confirmation = false;
        content.setHeading(new Text("La consultation a été enregistrée !"));
        // show information
        StringBuilder info = new StringBuilder();
        if (tmp_patients.size() != 0) {
            for (Patient p : tmp_patients
            ) {
                info.append("\n- ").append(p.getName()).append(" ").append(p.getLast_name());
            }
            if (validField())
                info.append("\n- ").append(name_field.getText()).append(" ").append(last_name_field.getText());
        } else
            info.append("\n- ").append(name_field.getText()).append(" ").append(last_name_field.getText());

        content.setBody(new Text("Date de consultation : " + date_field.getValue()
                + " à " + hour_field.getValue() + "\n"
                + "Les patients sont : \n"
                + info));

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton submit = new JFXButton("Confimer & Envoyer");
        JFXButton cancel = new JFXButton("Annuler");
        submit.setOnAction(event -> {
            confirmation = true;
            dialog.close();
            updateNewConsultation();
            // reload scene
            Main.sceneMapping("add_consultation_scene", "add_consultation_scene");
        });
        cancel.setOnAction(event -> {
            confirmation = false;
            dialog.close();
        });
        content.setActions(cancel, submit);

        dialog.show();
    }

    private void attachPatients() { // attach patient to the right side
        JFXButton patient_save = new JFXButton();
        patient_save.getStyleClass().add("patient_cell");
        patient_save.setMaxWidth(250);
        patient_save.setMaxHeight(100);

        String text = "Patient" + nb_patients
                + "\n " + tmp_patients.get(nb_patients - 1).getName()
                + " " + tmp_patients.get(nb_patients - 1).getName()
                + "\n\n Email :\n" + tmp_users.get(nb_patients - 1).getEmail();

        patient_save.setText(text);

        patient_save.setOnAction(event -> confirmationDialogPatient());

        patients_save.getChildren().add(patient_save);
        patients_save.setSpacing(20);

    }

    private void confirmationDialogPatient() {
        // add dialog layout to remove patient
        JFXDialogLayout confirmation_remove = new JFXDialogLayout();
        confirmation_remove.setHeading(new Text("Voulez-vous supprimer le patient ?"));

        JFXDialog dialog_confirmation = new JFXDialog(stackPane, confirmation_remove, JFXDialog.DialogTransition.CENTER);
        JFXButton remove = new JFXButton("Supprimer");
        JFXButton cancel = new JFXButton("Annuler");

        remove.setOnAction(event -> {
            confirmation = true;
            dialog_confirmation.close();
            System.out.println(nb_patients);
            // remove patient
            tmp_patients.remove(nb_patients - 2);
            patients_save.getChildren().remove(nb_patients - 2);
            nb_patients--;
            //updatePatientLabel();

        });
        cancel.setOnAction(event -> {
            confirmation = false;
            dialog_confirmation.close();
        });

        confirmation_remove.setActions(remove, cancel);
        dialog_confirmation.show();


    }


    // --------------------
    //  Field methods
    // --------------------
    private boolean validField() { // check if fields are not empty
        return !name_field.getText().isEmpty()
                && !last_name_field.getText().isEmpty()
                && !email_field.getText().isEmpty();
    }

    private void resetField() {
        // reset field
        name_field.setText("");
        last_name_field.setText("");
        email_field.setText("");
        anxiety_checkbox.setSelected(false);
    }

    private void validateTextFieldAction() { // add require validation if fields are empty
        if (name_field.getText().isEmpty())
            name_field.validate();
        if (last_name_field.getText().isEmpty())
            last_name_field.validate();
        if (email_field.getText().isEmpty())
            email_field.validate();
    }

    private void validateDateFieldAction() { // add require validation if date and hour fields are empty
        if (date_field.getValue() == null)
            date_field.validate();
        if (hour_field.getValue() == null)
            hour_field.validate();
    }

    private void addListenerValidationField(JFXTextField field) {
        field.getValidators().add(validator_field);
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (warring_check) { // remove warring if warring check
                    form_box.getChildren().remove(2);
                    warring_check = false;
                }
                field.resetValidation();
            }
        });
    }

    private void addListenerValidationField(JFXDatePicker field) {
        field.getValidators().add(validator_field);
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                appointment_map.clear();
                hour_field.getItems().clear();
                field.resetValidation();
            }
        });

    }

    private void addListenerValidationField(JFXComboBox<String> field) {
        //field.getValidators().add(validator_field);
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (date_field.getValue() != null) {
                if (date_field.getValue().compareTo(LocalDate.now()) >= 0)
                    initHourComboBox();
                if (newValue)
                    field.resetValidation();
            }
        });
    }

    private void initHourComboBox() {
        String[] dates = Main.getDatesOfDay(date_field.getValue());
        System.out.println(dates[0] + " " + dates[1]);

        String query = "select CONSULTATION_DATE\n" +
                "from CONSULTATION\n" +
                "where CONSULTATION_DATE between TO_DATE('" + dates[0] + "', 'yyyy-mm-dd HH24:mi:ss') "
                + "and TO_DATE('" + dates[1] + "', 'yyyy-mm-dd HH24:mi:ss')";
        try (Connection connection = Main.database.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet result = preparedStatement.executeQuery();

            // Check all hours
            ArrayList<Timestamp> hours_block = new ArrayList<>();

            while (result.next()) {
                hours_block.add(result.getTimestamp(1));
                //System.out.println(result.getTimestamp(1));
            }
            initAllAppointmentHours(date_field.getValue());
            // 20 appointment by day
            for (Timestamp tmp : hours_block
            ) {
                appointment_map.put(tmp, true);
            }
            appointment_map.forEach((k, v) -> {
                Timestamp tmp = new Timestamp(System.currentTimeMillis());
                if (!v && k.compareTo(tmp) >= 0) {
                    String hours_date = new SimpleDateFormat("HH:mm").format(k.getTime());
                    //System.out.println(hours_date);
                    hour_field.getItems().add(hours_date);
                }
            });


        } catch (SQLException ex) {
            System.out.println("Error loading date");
            ex.printStackTrace();
        }
    }

    /**
     * get all appointment hour during a day => key = hour and value = true / false if block or not
     */
    private void initAllAppointmentHours(LocalDate date) {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(Timestamp.valueOf(date.atTime(LocalTime.MIDNIGHT)));
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 08:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 08:30:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 09:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 09:30:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 10:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 10:30:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 11:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 11:30:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 14:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 14:30:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 15:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 15:30:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 16:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 16:30:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 17:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 17:30:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 18:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 18:30:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 19:00:00"), false);
        appointment_map.put(java.sql.Timestamp.valueOf(today + " 19:30:00"), false);

        appointment_map = new TreeMap<>(appointment_map);
    }


    // --------------------
    //  Update methods
    // --------------------
    private void updatePatientLabel() {
        // update label and button
        nb_patients++;
        if (nb_patients <= 3)
            label_patients.setText("Patient - " + nb_patients);
        else {
            add_patient_button.setDisable(true);
        }
    }

    private void updatePatientTable() {
        // the insert statement
        String query = " insert into patient (patient_id, name, last_name)"
                + " values (?, ?, ?)";
        try (Connection connection = Main.database.getConnection()) {
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            // create the insert preparedStatement
            for (Patient p : tmp_patients // for each patients saved in tmp_patients
            ) {
                if (p.isNew_patient()) { // if patient does not exist in database

                    // config parameters
                    preparedStmt.setInt(1, p.getPatient_id());
                    preparedStmt.setString(2, p.getName());
                    preparedStmt.setString(3, p.getLast_name());
                    // execute the preparedStatement
                    preparedStmt.execute();
                }
            }
        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
        }
    }

    private void updateUserTable() {
        // the insert statement
        String query = " insert into USER_APP (USER_ID, EMAIL, PASSWORD, PATIENT_ID)"
                + " values (?, ?, ?, ?)";
        try (Connection connection = Main.database.getConnection()) {
            PreparedStatement preparedStmt = connection.prepareStatement(query);

            // create the insert preparedStatement


            for (User u : tmp_users // for each patients saved in tmp_patients
            ) {
                if (u.isNew_user()) { // if patient does not exist in database

                    // config parameters
                    preparedStmt.setInt(1, u.getUser_id());
                    preparedStmt.setString(2, u.getEmail());
                    preparedStmt.setString(3, u.getPassword());
                    preparedStmt.setInt(4, u.getPatient_id());
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
        try (Connection connection = Main.database.getConnection()) {
            consultation_id = Consultation.getLastPrimaryKeyId();
            if (consultation_id != -1) {
                // the insert statement
                String query = " insert into CONSULTATION (CONSULTATION_ID, CONSULTATION_DATE)"
                        + " values (?, ?)";
                // create the insert preparedStatement
                PreparedStatement preparedStmt = connection.prepareStatement(query);

                // config parameters
                preparedStmt.setInt(1, consultation_id + 1);
                preparedStmt.setTimestamp(2, Timestamp.valueOf(date_field.getValue() + " " + hour_field.getValue() + ":00"));

                // execute the preparedStatement
                preparedStmt.execute();

                connection.commit();
            }

        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
        }
    }

    private void updateCarryOutTable() {
        // the insert statement
        String query = " insert into CONSULTATION_CARRYOUT (PATIENT_ID, CONSULTATION_ID)"
                + " values (?, ?)";
        try (Connection connection = Main.database.getConnection()) {
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            // create each carryout for each patients
            for (Patient p : tmp_patients
            ) {
                // config parameters
                preparedStmt.setInt(1, p.getPatient_id());
                preparedStmt.setInt(2, consultation_id + 1);

                // execute the preparedStatement
                preparedStmt.execute();
            }
            // TODO Multiples connections in same method?
            connection.commit();

        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
        }
    }

    void updateNewConsultation() {
        try (Connection connection = Main.database.getConnection()) {
            if (confirmation) {
                System.out.println("conf");
                // enable commit command
                connection.setAutoCommit(false);

                // update patient table with new patients
                updatePatientTable();

                // update user table with new users
                updateUserTable();

                // update consultation table
                updateConsultationTable();

                // update consultation_carryOut table
                updateCarryOutTable();

                connection.commit();
                System.out.println("successful");
            }
        } catch (SQLException ex) {
            System.out.println("Error creation consultation");
            ex.printStackTrace();
        }
    }
}
