/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.model;

import com.bdd.psymeeting.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

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

    private ArrayList<Job> jobs;
    private ArrayList<Consultation> consultationHistoric;

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
            query = "select JOBS_ID, JOB_NAME, JOB_DATE " +
                    "from JOBS " +
                    "where PATIENT_ID = ?";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, this.patient_id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Calendar tmp_date_job = Main.Date2Calendar(resultSet.getDate(3));
                this.jobs.add(new Job(resultSet.getInt(1), resultSet.getString(2), tmp_date_job));
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
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getDiscovery_way() {
        return discovery_way;
    }

    public String getRelationship() {
        return relationship;
    }

    public ArrayList<Consultation> getConsultationHistoric() {
        return consultationHistoric;
    }

    // --------------------
    //   Set methods
    // --------------------

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

    public void setConsultationHistoric(ArrayList<Consultation> consultationHistoric) {
        this.consultationHistoric = consultationHistoric;
    }

    // --------------------
    //   Statement methods
    // --------------------

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


    public static ArrayList<Patient> getAllPatientsProfiles() {

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

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // adding jobs & consultations to each patients
        for (Patient p : list_patients
        ) {

            try (Connection connection = Main.database.getConnection()) {

                String query;
                PreparedStatement preparedStatement;
                ResultSet resultSet;

                // JOBS
                query = "select JOBS_ID, JOB_NAME, JOB_DATE " +
                        "from JOBS " +
                        "where PATIENT_ID = ?";
                preparedStatement = connection.prepareStatement(query);

                preparedStatement.setInt(1, p.getPatient_id());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Calendar tmp_date_job = Main.Date2Calendar(resultSet.getDate(3));
                    p.getJobs().add(new Job(resultSet.getInt(1), resultSet.getString(2), tmp_date_job));
                }

                // CONSULTATIONS HISTORIC
                query = "select CC.CONSULTATION_ID from CONSULTATION\n" +
                        "join CONSULTATION_CARRYOUT CC on CONSULTATION.CONSULTATION_ID = CC.CONSULTATION_ID\n" +
                        "where PATIENT_ID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, p.getPatient_id());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    p.getConsultationHistoric().add(new Consultation(resultSet.getInt(1)));
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return list_patients;
    }


    // --------------------
    //   Override methods
    // --------------------
    @Override
    public String toString() {
        return "Patient{" +
                "patient_id=" + patient_id +
                ", name='" + name + '\'' +
                ", last_name='" + last_name + '\'' +
                '}';
    }
}
