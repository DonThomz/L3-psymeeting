/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.model;

import com.bdd.psymeeting.Main;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Patient {

    // --------------------
    //   Attributes
    // --------------------

    private int patient_id;
    private String name;
    private String last_name;
    private boolean new_patient;

    private Date birthday;
    private String gender;
    private String relationship;
    private String discovery_way;

    private User user;
    private ArrayList<Job> jobs;
    private ArrayList<Consultation> consultationHistoric;

    protected static int KID_AGE_LIMIT = 12;
    protected static int TEEN_AGE_LIMIT = 18;

    // --------------------
    //   Constructors
    // --------------------

    public Patient(int patient_id) {
        try (Connection connection = Main.database.getConnection()) {
            this.patient_id = patient_id;

            String query;
            PreparedStatement preparedStatement;
            ResultSet resultSet;

            // PATIENT INFO
            query = "select NAME, LAST_NAME, BIRTHDAY, GENDER, RELATIONSHIP, DISCOVERY_WAY from PATIENT " +
                    "where PATIENT_ID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, this.patient_id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            this.name = resultSet.getString(1);
            this.last_name = resultSet.getString(2);
            this.birthday = resultSet.getDate(3);
            this.gender = resultSet.getString(4);
            this.relationship = resultSet.getString(5);
            this.discovery_way = resultSet.getString(6);

            // CONSULTATIONS HISTORIC
            this.consultationHistoric = new ArrayList<>();
            query = "select CC.CONSULTATION_ID from CONSULTATION\n" +
                    "join CONSULTATION_CARRYOUT CC on CONSULTATION.CONSULTATION_ID = CC.CONSULTATION_ID\n" +
                    "where PATIENT_ID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patient_id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                this.consultationHistoric.add(new Consultation(resultSet.getInt(1)));
            }

            // JOBS
            this.jobs = new ArrayList<>();
            query = "select JOB_NAME, JOB_DATE\n" +
                    "from JOBS J\n" +
                    "join PATIENTJOB P on J.JOBS_ID = P.JOBS_ID\n" +
                    "where PATIENT_ID = ?";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, this.patient_id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                this.jobs.add(new Job(resultSet.getString(1), resultSet.getDate(2)));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    // patient without jobs
    public Patient(int patient_id, String name, String last_name, boolean new_patient) {
        this.patient_id = patient_id;
        this.name = name;
        this.last_name = last_name;
        this.new_patient = new_patient;
    }

    // patient (name and last name)
    public Patient(int patient_id, String name, String last_name) {
        this.patient_id = patient_id;
        this.name = name;
        this.last_name = last_name;
    }

    public Patient(int patient_id, String name, String last_name, String email) {
        this.patient_id = patient_id;
        this.name = name;
        this.last_name = last_name;
        this.user = User.getUserByEmail(email);
    }

    public Patient(int patient_id, String name, String last_name, Date birthday, String gender, String relationship, String discovery_way) {
        this.patient_id = patient_id;
        this.name = name;
        this.last_name = last_name;
        this.birthday = birthday;
        this.gender = gender;
        this.relationship = relationship;
        this.discovery_way = discovery_way;

        this.jobs = new ArrayList<>();
        this.consultationHistoric = new ArrayList<>();
    }

    // --------------------
    //   Get methods
    // --------------------

    public int getPatient_id() {
        return patient_id;
    }

    public String getName() {
        return name;
    }

    public String getLast_name() {
        return last_name;
    }

    public boolean isNew_patient() {
        return new_patient;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public String getGender() {
        return this.gender;
    }

    public String getDiscovery_way() {
        return this.discovery_way;
    }

    public String getRelationship() {
        return this.relationship;
    }

    public ArrayList<Consultation> getConsultationHistoric() {
        return consultationHistoric;
    }

    public User getUser() {
        return user;
    }


    // --------------------
    //   Set methods
    // --------------------

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public void setNew_patient(boolean new_patient) {
        this.new_patient = new_patient;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setJobs(ArrayList<Job> jobs) {
        this.jobs = jobs;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = Date.valueOf(birthday);
    }

    public void setRelationship(String relationshipStatus) {
        this.relationship = relationshipStatus;
    }

    public void setConsultationHistoric(ArrayList<Consultation> consultationHistoric) {
        this.consultationHistoric = consultationHistoric;
    }

    public void setDiscovery_way(String discovery_way) {
        this.discovery_way = discovery_way;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // --------------------
    //  Methods
    // --------------------


    // --------------------
    //   Statement methods
    // --------------------

    /**
     * Update the Patient in DB with local state.
     *
     * @return true if succeeded !
     */
    public boolean updatePatient() {
        System.out.println(this);
        try (Connection connection = Main.database.getConnection()) {
            connection.setAutoCommit(false);
            String request = "UPDATE PATIENT SET NAME = ?, LAST_NAME = ?, BIRTHDAY = ?, GENDER = ?, RELATIONSHIP = ?, DISCOVERY_WAY= ? WHERE PATIENT_ID = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setString(1, this.getName().toUpperCase());
            preparedStatement.setString(2, this.getLast_name().toUpperCase());
            preparedStatement.setDate(3, new java.sql.Date(this.getBirthday().getTime()));
            preparedStatement.setString(4, this.getGender() != null ? this.getGender() : "");
            preparedStatement.setString(5, this.getRelationship() != null ? this.getRelationship() : "");
            preparedStatement.setString(6, this.getDiscovery_way() != null ? this.getDiscovery_way() : "");
            preparedStatement.setInt(7, this.getPatient_id());

            preparedStatement.executeUpdate();


            preparedStatement.close();
            connection.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Insert new patient to Patient Table
     *
     * @return true if succeeded
     */
    public boolean insertPatient() {
        try (Connection connection = Main.database.getConnection()) {
            String request = "INSERT INTO PATIENT (PATIENT_ID, NAME, LAST_NAME, BIRTHDAY, GENDER, RELATIONSHIP, DISCOVERY_WAY) VALUES (?,?,?,?,?,?,?)";

            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setInt(1, this.getPatient_id());
            preparedStatement.setString(2, this.getName().toUpperCase());
            preparedStatement.setString(3, this.getLast_name().toUpperCase());
            preparedStatement.setDate(4, new java.sql.Date(this.getBirthday().getTime()));
            preparedStatement.setString(5, this.getGender().toUpperCase());
            preparedStatement.setString(6, this.getRelationship().toUpperCase());
            preparedStatement.setString(7, this.getDiscovery_way().toUpperCase());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean insertIntoPatientTable(ArrayList<Patient> patients, int lastPatientID) {
        try (Connection connection = Main.database.getConnection()) {
            // the insert statement
            String query = " insert into patient (patient_id, name, last_name)"
                    + " values (?, ?, ?)";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            // create the insert preparedStatement
            if (lastPatientID != -1) {
                for (Patient p : patients // for each patients saved in tmp_patients
                ) {
                    if (p.isNew_patient()) { // if patient does not exist in database

                        // config parameters//
                        // patient already exist
                        /*if (p.getPatient_id() <= lastPatientID) preparedStmt.setInt(1, p.getPatient_id());
                        else {
                            tmpLastPatientID++;
                            preparedStmt.setInt(1, tmpLastPatientID);
                        }*/
                        preparedStmt.setInt(1, p.getPatient_id());
                        preparedStmt.setString(2, p.getName().toUpperCase());
                        preparedStmt.setString(3, p.getLast_name().toUpperCase());
                        // execute the preparedStatement
                        preparedStmt.executeUpdate();
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

    public static int getLastPrimaryKeyId() {
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("select max(patient_id) from PATIENT");
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    public static Patient getPatientByEmail(String email) {
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("select\n" +
                    "       u.PATIENT_ID,\n" +
                    "       p.NAME,\n" +
                    "       p.LAST_NAME\n" +
                    "from USER_APP u\n" +
                    "join PATIENT P on u.PATIENT_ID = P.PATIENT_ID\n" +
                    "where u.EMAIL = '" + email + "'");
            resultSet.next();
            return new Patient(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), false);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Patient> getAllFullPatientsProfiles() {

        ArrayList<Patient> list_patients = new ArrayList<>();

        // adding patients to list_patients
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select PATIENT_ID, NAME, LAST_NAME, BIRTHDAY, GENDER, RELATIONSHIP, DISCOVERY_WAY from PATIENT");
            while (result.next()) {
                list_patients.add(new Patient(result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getDate(4),
                        result.getString(5),
                        result.getString(6),
                        result.getString(7)));
            }

            for (Patient patient : list_patients) {

                // adding jobs
                patient.setJobs(Job.getJobByPatientID(patient.getPatient_id()));

                // adding consultation historic
                patient.setConsultationHistoric(Consultation.getConsultationsByPatientID(patient.getPatient_id()));

                // adding user info
                patient.setUser(User.getUserByPatientID(patient.getPatient_id()));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list_patients;
    }

    public static ArrayList<Patient> getSimplyPatientsProfiles() {
        ArrayList<Patient> list_patients = new ArrayList<>();

        // adding patients to list_patients
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select PATIENT.PATIENT_ID, NAME, LAST_NAME, EMAIL from PATIENT join USER_APP UA on PATIENT.PATIENT_ID = UA.PATIENT_ID");
            while (result.next()) {
                list_patients.add(new Patient(result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4)));
            }


        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list_patients;
    }
    // --------------------
    //   Override methods
    // --------------------

    @Override
    public String toString() {
        return this.getName() + " " + this.getLast_name() + (this.user.getEmail() != null ? " : " + this.user.getEmail() : "");
    }
}
