package com.bdd.pj.data;

import com.bdd.pj.application.Main;
import com.jfoenix.controls.JFXButton;
import javafx.scene.control.TextArea;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Consultation {

    // --------------------
    //   Attributes
    // --------------------

    // Consultation attributes from table CONSULTATION
    private final int consultationID; //primary key
    private Calendar date;
    private float price;
    private String payMode;
    private ArrayList<Patient> patients; // String[0] = name | String[1] = last_name

    // Feedback
    private Feedback feedback;


    // Graphic attributes
    private TextArea full_infos;
    private JFXButton consultation_button;

    // --------------------
    //   Constructors
    // --------------------
    public Consultation(int consultationID) {
        this.consultationID = consultationID;
        // get feedback
        this.feedback = new Feedback(this.consultationID);

        try(Connection connection = Main.database.getConnection()){

            String query;
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            // get consultation info (date, price, pay mode)
            query = "select CONSULTATION_DATE, PRICE, PAY_MODE from CONSULTATION where CONSULTATION_ID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, this.consultationID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            // assign attributes
            this.date = Main.Timestamp2Calendar(resultSet.getTimestamp(1));
            this.price = resultSet.getFloat(2);
            this.payMode = resultSet.getString(3);

            // get patients list from consultations
            query = "select P.PATIENT_ID, P.NAME, P.LAST_NAME " +
                    "from PATIENT P " +
                    "join CONSULTATION_CARRYOUT CC on P.PATIENT_ID = CC.PATIENT_ID " +
                    "where CC.CONSULTATION_ID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, this.consultationID);
            resultSet = preparedStatement.executeQuery();
            patients = new ArrayList<>();
            while(resultSet.next()){
                patients.add(new Patient(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
            }

        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public Consultation(int consultationID, Calendar date, float price, String payMode, ArrayList<Patient> patients, TextArea full_infos, JFXButton consultation_button) {
        this.consultationID = consultationID;
        this.date = date;
        this.price = price;
        this.payMode = payMode;
        this.patients = patients;
        this.full_infos = full_infos;
        this.consultation_button = consultation_button;
    }





    // --------------------
    //   Get methods
    // --------------------
    public int getConsultationID() {
        return consultationID;
    }

    public Calendar getDate() {
        return date;
    }

    public JFXButton getConsultation_button() {
        return consultation_button;
    }

    public float getPrice() {
        return price;
    }

    public String getPayMode() {
        return payMode;
    }

    public TextArea getFull_infos() {
        return full_infos;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    // --------------------
    //   Set methods
    // --------------------

    public void setConsultation_button(JFXButton consultation_button) {
        this.consultation_button = consultation_button;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public void setFull_infos(TextArea full_infos) {
        this.full_infos = full_infos;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public void setPatients(ArrayList<Patient> patients) {
        this.patients = patients;
    }

    // --------------------
    //   Statement methods
    // --------------------

    public static int getLastPrimaryKeyId() {
        try (Statement stmt = Main.database.getConnection().createStatement()) {

            ResultSet result = stmt.executeQuery("select max(CONSULTATION_ID) from CONSULTATION");
            result.next();
            return result.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    public static Calendar getDateById(int consultation_id) {
        // TODO Fix this, as the connection can't be checked out
        String query = "select\n" +
                "     c.CONSULTATION_DATE\n" +
                "from CONSULTATION c\n" +
                "where c.CONSULTATION_ID = ?";
        try (PreparedStatement preparedStmt = Main.database.getConnection().prepareStatement(query)) {
            preparedStmt.setInt(1, consultation_id);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                return Main.Timestamp2Calendar(result.getTimestamp(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
