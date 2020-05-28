/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.model;

import com.bdd.psymeeting.Main;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.scene.control.TextArea;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Consultation extends RecursiveTreeObject<Consultation> {

    // --------------------
    //   Attributes
    // --------------------

    // Consultation attributes from table CONSULTATION
    protected final int consultationID; //primary key
    protected Calendar date;
    protected float price;
    protected String payMode;
    protected boolean inRelation;
    /*
        map of patients infos
        - in ArrayList:
            0 - first name
            1 - last name
            2 - birthDay
            3 - relationship
            4 - category
     */
    protected HashMap<Integer, ArrayList<String>> patients;
    // Feedback
    protected Feedback feedback;

    // Graphic attributes
    protected TextArea full_infos;
    protected JFXButton consultation_button;

    // --------------------
    //   Constructors
    // --------------------
    public Consultation(int consultationID) {

        this.consultationID = consultationID;

        // get feedback
        this.feedback = new Feedback(this.consultationID);

        try (Connection connection = Main.database.getConnection()) {

            String query;
            PreparedStatement preparedStatement;
            ResultSet resultSet;

            // get consultation info (date, price, pay mode)
            query = "select CONSULTATION_DATE, PRICE, PAY_MODE, COUPLE from CONSULTATION where CONSULTATION_ID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, this.consultationID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            // assign attributes
            this.date = Main.Timestamp2Calendar(resultSet.getTimestamp(1));
            this.price = resultSet.getFloat(2);
            this.payMode = resultSet.getString(3);
            this.inRelation = resultSet.getBoolean(4);
            // get patients list from consultations
            query = "select P.PATIENT_ID, P.NAME, P.LAST_NAME, P.BIRTHDAY, P.RELATIONSHIP " +
                    "from PATIENT P " +
                    "join CONSULTATION_CARRYOUT CC on P.PATIENT_ID = CC.PATIENT_ID " +
                    "where CC.CONSULTATION_ID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, this.consultationID);
            resultSet = preparedStatement.executeQuery();

            patients = new HashMap<>();
            while (resultSet.next()) {

                ArrayList<String> infoPatient = new ArrayList<>(Arrays.asList(
                        resultSet.getString(2),
                        resultSet.getString(3),
                        "",
                        resultSet.getString(5),
                        ""
                ));
                String date;
                if (resultSet.getDate(4) != null) {
                    date = new SimpleDateFormat("yyyy-MM-dd").format(resultSet.getDate(4));
                    infoPatient.add(2, date);
                    infoPatient.add(3, getPatientCategory(this.date, resultSet.getDate(4)));
                }
                patients.put(resultSet.getInt(1), infoPatient);
            }

        } catch (SQLException ex) {
            System.out.println("error");
            ex.printStackTrace();
        }
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

    public String getDateString() {
        return this.date.getTime() + " " + this.getDate().get(Calendar.HOUR) + ":" + this.getDate().get(Calendar.MINUTE);
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

    public HashMap<Integer, ArrayList<String>> getPatients() {
        return patients;
    }

    public boolean isInRelation() {
        return inRelation;
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

    public void setPatients(HashMap<Integer, ArrayList<String>> patients) {
        this.patients = patients;
    }

    // --------------------
    //  Methods
    // --------------------
    public String getPatientCategory(Calendar dateConsultation, Date dateBirthDay) {
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTime(dateBirthDay);
        int years = this.getDate().get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        if (years <= Patient.KID_AGE_LIMIT) return "Enfant";
        else if (years <= Patient.TEEN_AGE_LIMIT) return "Adolescent";
        else return "Adulte";
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "consultationID=" + consultationID +
                ", date=" + date +
                ", price=" + price +
                ", payMode='" + payMode + '\'' +
                ", patients=" + patients +
                ", feedback=" + feedback +
                ", full_infos=" + full_infos +
                ", consultation_button=" + consultation_button +
                '}';
    }

    // --------------------
    //   Statement methods
    // --------------------

    public static int getLastPrimaryKeyId() {
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select max(CONSULTATION_ID) from CONSULTATION");
            result.next();
            return result.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    public static ArrayList<Integer> countConsultations() {
        try (Connection connection = Main.database.getConnection()) {
            ArrayList<Integer> IdConsultationsList = new ArrayList<>();
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select CONSULTATION_ID from CONSULTATION");
            while (result.next()) {
                IdConsultationsList.add(result.getInt(1));
            }
            return IdConsultationsList;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }

    }

    public static Calendar getDateById(int consultation_id) {
        // TODO Fix this, as the connection can't be checked out
        try (Connection connection = Main.database.getConnection()) {
            String query = "select\n" +
                    "     c.CONSULTATION_DATE\n" +
                    "from CONSULTATION c\n" +
                    "where c.CONSULTATION_ID = ?";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
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

    public static ArrayList<Consultation> getConsultationsByPatientID(int patientID) {

        try (Connection connection = Main.database.getConnection()) {

            ArrayList<Consultation> consultations = new ArrayList<>();

            String query;
            PreparedStatement preparedStatement;
            ResultSet resultSet;

            // CONSULTATIONS HISTORIC
            query = "select CC.CONSULTATION_ID from CONSULTATION\n" +
                    "join CONSULTATION_CARRYOUT CC on CONSULTATION.CONSULTATION_ID = CC.CONSULTATION_ID\n" +
                    "where PATIENT_ID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientID);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                consultations.add(new Consultation(resultSet.getInt(1)));
            }

            return consultations;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    /**
     * PreparedStatement to get all time slots free
     *
     * @param dateField date selected
     * @return HashMap with all time slots free ( key = TimeStamp, value = true / free or false / block )
     */
    public static Map<Timestamp, Boolean> getTimeSlots(LocalDate dateField) {

        try (Connection connection = Main.database.getConnection()) {

            Map<Timestamp, Boolean> timeSlots = initTimeSlotsByDateSelected(dateField);
            ArrayList<Timestamp> timeSlotsBlocked = new ArrayList<>();

            String[] dates = Main.getDatesOfDay(dateField);

            String query = "select CONSULTATION_DATE\n" +
                    "from CONSULTATION\n" +
                    "where CONSULTATION_DATE between TO_DATE('" + dates[0] + "', 'yyyy-mm-dd HH24:mi:ss') "
                    + "and TO_DATE('" + dates[1] + "', 'yyyy-mm-dd HH24:mi:ss')";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet result = preparedStatement.executeQuery();

            // Check all time slots
            while (result.next()) {
                timeSlotsBlocked.add(result.getTimestamp(1));
            }

            // 20 appointment by day
            for (Timestamp tmp : timeSlotsBlocked
            ) {
                timeSlots.put(tmp, true);
            }

            return timeSlots;
        } catch (SQLException ex) {
            System.out.println("Error loading date");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * get all time slots during a day
     */
    private static Map<Timestamp, Boolean> initTimeSlotsByDateSelected(LocalDate date) {
        Map<Timestamp, Boolean> timeSlots = new HashMap<>();
        String selectDate = new SimpleDateFormat("yyyy-MM-dd").format(Timestamp.valueOf(date.atTime(LocalTime.MIDNIGHT)));
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 08:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 08:30:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 09:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 09:30:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 10:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 10:30:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 11:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 11:30:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 14:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 14:30:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 15:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 15:30:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 16:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 16:30:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 17:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 17:30:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 18:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 18:30:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 19:00:00"), false);
        timeSlots.put(java.sql.Timestamp.valueOf(selectDate + " 19:30:00"), false);

        // order HashMap
        timeSlots = new TreeMap<>(timeSlots);
        return timeSlots;
    }


    /**
     * Get week consultations
     *
     * @return ArrayList<Consultation>
     */
    public static ArrayList<Consultation> getConsultationWeek(int indexWeek) {


        try (Connection connection = Main.database.getConnection()) {

            String[] dates = Main.getDatesOfWeek(indexWeek);
            ArrayList<Consultation> consultations = new ArrayList<>();
            System.out.println(dates[1]);
            String query = "select CONSULTATION_ID\n" +
                    "from CONSULTATION\n" +
                    "where CONSULTATION_DATE between TO_DATE('" + dates[0] + "', 'yyyy-mm-dd HH24:mi:ss') "
                    + "and TO_DATE('" + dates[1] + "', 'yyyy-mm-dd HH24:mi:ss')";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet result = preparedStatement.executeQuery();
            // Check all time slots
            while (result.next()) {
                consultations.add(new Consultation(result.getInt(1)));
            }
            System.out.println(consultations.size());
            return consultations;

        } catch (SQLException ex) {
            System.out.println("Error loading consultations of the week");
            System.out.println(ex.getMessage());
            return null;
        }

    }

    /**
     * Insert into consultation table new consultation
     *
     * @param consultationDate date of consultation
     * @param consultationID   consultation id
     * @return true if succeeded
     */
    public static boolean insertIntoConsultationTable(Timestamp consultationDate, int consultationID, boolean anxietyValue, boolean coupleValue) {
        try (Connection connection = Main.database.getConnection()) {
            // the insert statement
            String query = " insert into CONSULTATION (CONSULTATION_ID, CONSULTATION_DATE, ANXIETY, COUPLE)"
                    + " values (?, ?, ?, ?)";
            // create the insert preparedStatement
            PreparedStatement preparedStmt = connection.prepareStatement(query);

            // config parameters
            preparedStmt.setInt(1, consultationID + 1);
            preparedStmt.setTimestamp(2, consultationDate);

            if (anxietyValue) preparedStmt.setInt(3, 1);
            else preparedStmt.setInt(3, 0);

            if (coupleValue) preparedStmt.setInt(4, 1);
            else preparedStmt.setInt(4, 0);

            // execute the preparedStatement
            preparedStmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Insert into consultation_carryout table new consultation
     *
     * @param patients       list of patients to add
     * @param consultationID ID of current consultation
     * @param lastPatientID  last Patient ID
     * @return true if succeeded
     */
    public static boolean insertIntoConsultationCarryOutTable(ArrayList<Patient> patients, int consultationID, int lastPatientID) {

        try (Connection connection = Main.database.getConnection()) {
            int tmpLastPatientID = lastPatientID;
            // the insert statement
            String query = " insert into CONSULTATION_CARRYOUT (PATIENT_ID, CONSULTATION_ID)"
                    + " values (?, ?)";

            PreparedStatement preparedStmt = connection.prepareStatement(query);
            // create each carryout for each patients

            for (Patient p : patients
            ) {
                // config parameters
                // patient already exist
                if (p.getPatient_id() <= lastPatientID) preparedStmt.setInt(1, p.getPatient_id());
                else {
                    tmpLastPatientID++;
                    preparedStmt.setInt(1, tmpLastPatientID);
                }

                preparedStmt.setInt(2, consultationID + 1);

                // execute the preparedStatement
                preparedStmt.executeUpdate();
            }
            return true;
            // TODO Multiples connections in same method?

        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean removeConsultation(Consultation consultation) {
        try (Connection connection = Main.database.getConnection()) {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint("savePoint");
            try {
                PreparedStatement preparedStatement;
                String query;

                // Step 1 remove row in carryOut
                query = "delete CONSULTATION_CARRYOUT where CONSULTATION_ID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, consultation.getConsultationID());
                preparedStatement.executeUpdate();

                // Step 2 remove feedback
                query = "delete FEEDBACK where CONSULTATION_ID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, consultation.getConsultationID());
                preparedStatement.executeUpdate();

                // Step 3 remove consultation
                query = "delete CONSULTATION where CONSULTATION_ID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, consultation.getConsultationID());
                preparedStatement.executeUpdate();

                connection.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.print("SQL Exception, rollback executing...");
                connection.rollback(savepoint); // rollback if error
                return false;
            }
        } catch (SQLException thenables) {
            thenables.printStackTrace();
            return false;
        }
        return true;
    }


}
