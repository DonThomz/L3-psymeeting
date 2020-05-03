/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.model;

import com.bdd.psymeeting.Main;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Job {

    // --------------------
    //   Attributes
    // --------------------

    private int jobId;
    private String job_name;
    private Date job_date;
    private int patientID;

    // --------------------
    //   Constructors
    // --------------------

    public Job(int jobId, String job_name, Date job_date) {
        this.jobId = jobId;
        this.job_name = job_name;
        this.job_date = job_date;
    }

    public Job(String job_name, Date job_date) {
        this.job_name = job_name;
        this.job_date = job_date;
    }

    // --------------------
    //   Get methods
    // --------------------

    public int getJobId() {
        return jobId;
    }

    public Date getJob_date() {
        return job_date;
    }

    public String getJob_name() {
        return job_name;
    }

    public int getPatientID() {
        return patientID;
    }

    // --------------------
    //   Set methods
    // --------------------

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public void setJob_date(LocalDate job_date) {
        this.job_date = Date.valueOf(job_date);
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    // --------------------
    //   Statement methods
    // --------------------

    public boolean isExist() {
        try (Connection connection = Main.database.getConnection()) {


            String query = "select JOBS_ID from JOBS where JOB_NAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, this.getJob_name());

            return preparedStatement.execute();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * create the Job in DB with local state.
     *
     * @return true if succeeded
     */
    public static boolean insertJobFromArrayList(ArrayList<Job> jobs) {
        try (Connection connection = Main.database.getConnection()) {

            String request1 = "INSERT INTO JOBS (JOBS_ID, JOB_NAME) VALUES (?,?)";
            String request2 = "INSERT INTO PATIENTJOB (JOBS_ID, PATIENT_iD, JOB_DATE) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(request1);
            PreparedStatement preparedStatement1 = connection.prepareStatement(request2);

            for (Job job : jobs
            ) {
                if (!job.isExist()) { // if job name doesn't exist in database
                    preparedStatement.setInt(1, job.getJobId());
                    preparedStatement.setString(2, job.getJob_name());
                }

                // update patient-job
                preparedStatement1.setInt(1, job.getJobId());
                preparedStatement1.setInt(2, job.getPatientID());
                preparedStatement1.setDate(3, job.getJob_date());


                preparedStatement.executeUpdate();
                preparedStatement1.executeUpdate();
            }

            preparedStatement.close();
            preparedStatement1.close();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


}
