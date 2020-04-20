/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting;

import com.bdd.psymeeting.model.Consultation;
import com.bdd.psymeeting.model.Patient;
import com.bdd.psymeeting.model.User;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import sun.security.krb5.internal.PAData;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.ArrayList;


/**
 * Database class
 */
public class OracleDB {
//    private Connection connection;

    //private static final OracleDB instance = new OracleDB();
    private ComboPooledDataSource comboPooledDataSource;

    public OracleDB() {

    }

//    public static final OracleDB getInstance() {
//        return instance;
//    }

    public Connection getConnection() throws SQLException {
        return this.comboPooledDataSource.getConnection();
    }

    public void close() {
        this.comboPooledDataSource.close();
    }

    /**
     * Connect to the DB.
     */
    public boolean connectionDatabase(String username, String password) {
        System.out.println("Connecting to DB...");
        try {
            this.comboPooledDataSource = new ComboPooledDataSource();
            this.comboPooledDataSource.setDriverClass("oracle.jdbc.driver.OracleDriver");
            this.comboPooledDataSource.setJdbcUrl("jdbc:oracle:thin:@localhost:51521:xe");
            this.comboPooledDataSource.setUser(username);
            this.comboPooledDataSource.setPassword(password);

            this.comboPooledDataSource.setMinPoolSize(5);
            this.comboPooledDataSource.setAcquireIncrement(5);
            this.comboPooledDataSource.setMaxPoolSize(20);

            this.comboPooledDataSource.setAcquireRetryAttempts(0);

            this.comboPooledDataSource.setUnreturnedConnectionTimeout(10);
            this.comboPooledDataSource.setDebugUnreturnedConnectionStackTraces(true);

            this.comboPooledDataSource.setTestConnectionOnCheckout(true);

            System.out.println("Pooled data source set up: done!");

            try (Connection connection = this.getConnection()) {
                System.out.println("Checking connection....");
                Statement stmt = connection.createStatement();
                stmt.executeQuery("select NAME, LAST_NAME from ADMINISTRATOR");

                System.out.println("Connection succeeded!");
                return true;
            } catch (Exception e) {
                System.out.println("Connection failed! Maybe wrong password?");
                this.comboPooledDataSource.close();
                e.printStackTrace();
                return false;
            }

        } catch (PropertyVetoException e) {
            System.out.println("Unable to set up connection pool!");
            e.printStackTrace();
            // System.exit(1);
            return false;
        }
    }

    /**
     * Close the connection to the DB properly.
     */
    public void closeDatabase() {
        this.comboPooledDataSource.close();
    }

    // --------------------
    //  Update tables methods
    // --------------------

    public boolean updateNewConsultation(ArrayList<Patient> patients, ArrayList<User> users, Timestamp consultationDate) {
        System.out.println(users);
        int consultationID = Consultation.getLastPrimaryKeyId();
        int lastPatientID = Patient.getLastPrimaryKeyId();
        if (consultationID != -1) {
            return updatePatientTable(patients, lastPatientID) && updateUserTable(users) && updateConsultationTable(consultationDate, consultationID) && updateCarryOutTable(patients, consultationID, lastPatientID);
        } else return false;
    }

    public boolean updatePatientTable(ArrayList<Patient> patients, int lastPatientID) {
        try (Connection connection = this.getConnection()) {

            int tmpLastPatientID = lastPatientID;
            // the insert statement
            String query = " insert into patient (patient_id, name, last_name)"
                    + " values (?, ?, ?)";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            // create the insert preparedStatement
            if (tmpLastPatientID != -1) {
                for (Patient p : patients // for each patients saved in tmp_patients
                ) {
                    if (p.isNew_patient()) { // if patient does not exist in database

                        // config parameters//
                        // patient already exist
                        if (p.getPatient_id() <= lastPatientID) preparedStmt.setInt(1, p.getPatient_id());
                        else {
                            tmpLastPatientID++;
                            preparedStmt.setInt(1, tmpLastPatientID);
                        }
                        preparedStmt.setString(2, p.getName());
                        preparedStmt.setString(3, p.getLast_name());
                        // execute the preparedStatement
                        preparedStmt.execute();
                    }
                }
            }
            return true;
        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateUserTable(ArrayList<User> users) {

        try (Connection connection = this.getConnection()) {
            // the insert statement
            String query = " insert into USER_APP (USER_ID, EMAIL, PASSWORD, PATIENT_ID)"
                    + " values (?, ?, ?, ?)";

            PreparedStatement preparedStmt = connection.prepareStatement(query);

            int lastUserId = User.getLastUserId();
            // create the insert preparedStatement
            if (lastUserId != -1) {
                for (User u : users // for each patients saved in tmp_patients
                ) {
                    if (u.isNew_user()) { // if patient does not exist in database

                        // config parameters
                        lastUserId++;
                        preparedStmt.setInt(1, lastUserId);
                        preparedStmt.setString(2, u.getEmail());
                        preparedStmt.setString(3, u.getPassword());
                        preparedStmt.setInt(4, u.getPatient_id());
                        // execute the preparedStatement
                        preparedStmt.execute();
                    }
                }
                return true;
            } else return false;
        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateConsultationTable(Timestamp consultationDate, int consultationID) {
        try (Connection connection = this.getConnection()) {

            // the insert statement
            String query = " insert into CONSULTATION (CONSULTATION_ID, CONSULTATION_DATE)"
                    + " values (?, ?)";
            // create the insert preparedStatement
            PreparedStatement preparedStmt = connection.prepareStatement(query);

            // config parameters
            preparedStmt.setInt(1, consultationID + 1);
            preparedStmt.setTimestamp(2, consultationDate);

            // execute the preparedStatement
            preparedStmt.execute();
            return true;

        } catch (SQLException ex) {
            System.err.println("Got an exception!");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateCarryOutTable(ArrayList<Patient> patients, int consultationID, int lastPatientID) {

        try (Connection connection = this.getConnection()) {

            int tmpLastPatientID = lastPatientID;
            // the insert statement
            String query = " insert into CONSULTATION_CARRYOUT (PATIENT_ID, CONSULTATION_ID)"
                    + " values (?, ?)";

            PreparedStatement preparedStmt = connection.prepareStatement(query);
            // create each carryout for each patients

            for (Patient p : patients
            ) {
                // config parameters
                if (p.getPatient_id() <= lastPatientID) preparedStmt.setInt(1, p.getPatient_id());
                else {
                    tmpLastPatientID++;
                    preparedStmt.setInt(1, tmpLastPatientID);
                }

                preparedStmt.setInt(2, consultationID + 1);

                // execute the preparedStatement
                preparedStmt.execute();
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


}
