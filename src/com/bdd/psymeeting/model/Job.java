/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.model;

import com.bdd.psymeeting.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class Job {

    // --------------------
    //   Attributes
    // --------------------

    private int jobId;
    private String job_name;
    private Calendar job_date;
    private int patientID;

    // --------------------
    //   Constructors
    // --------------------

    public Job(int jobId, String job_name, Calendar job_date) {
        this.jobId = jobId;
        this.job_name = job_name;
        this.job_date = job_date;
    }

    public Job(String job_name, Calendar job_date) {
        this.job_name = job_name;
        this.job_date = job_date;
    }

    // --------------------
    //   Get methods
    // --------------------

    public int getJobId() {
        return jobId;
    }

    public Calendar getJob_date() {
        return job_date;
    }

    public String getJob_name() {
        return job_name;
    }

    // --------------------
    //   Set methods
    // --------------------

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public void setJob_date(Calendar job_date) {
        this.job_date = job_date;
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

    /**
     * create the Job in DB with local state.
     *
     * @return true if succeeded
     */
    public static boolean insertJobFromArrayList(ArrayList<Job> jobs) {
        try (Connection connection = Main.database.getConnection()) {

            String request = "INSERT INTO JOBS (JOBS_ID, JOB_NAME, JOB_DATE, PATIENT_ID) VALUES (?,?,?,?)";

            PreparedStatement preparedStatement = connection.prepareStatement(request);
            for (Job job : jobs
            ) {
                preparedStatement.setInt(1, job.getJobId());
                preparedStatement.setString(2, job.getJob_name());
                preparedStatement.setDate(3, new java.sql.Date(job.getJob_date().getTime().getTime()));
                preparedStatement.setInt(4, job.getJobId());

                preparedStatement.executeUpdate();
            }

            preparedStatement.close();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
