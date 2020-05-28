/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.model;

import com.bdd.psymeeting.Main;
import com.mchange.v2.lang.StringUtils;
import oracle.jdbc.proxy.annotation.Pre;

import java.sql.*;
import java.text.Normalizer;
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
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public int getJobDatabaseID() {
        try (Connection connection = Main.database.getConnection()) {

            String query = "select JOBS_ID from JOBS where JOB_NAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, this.getJob_name());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);

        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static int getLastPrimaryKeyID() {
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("select max(JOBS_ID) from JOBS");
            if (resultSet.next()) return resultSet.getInt(1);
            else return 0;

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
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
            int lastID = Job.getLastPrimaryKeyID();
            if (lastID != -1) {

                // insert each job
                for (Job job : jobs
                ) {
                    if (!job.isExist()) { // if job name doesn't exist in database
                        // update new id
                        job.setJobId(lastID + 1);
                        preparedStatement.setInt(1, job.getJobId());
                        preparedStatement.setString(2, job.getJob_name());
                        preparedStatement.executeUpdate();
                    } else {
                        int jobID = job.getJobDatabaseID(); // get the correct job ID
                        if (jobID != -1) {
                            job.setJobId(jobID);
                        } else return false; // error
                    }
                    // update patient-job
                    preparedStatement1.setInt(1, job.getJobId());
                    preparedStatement1.setInt(2, job.getPatientID());
                    preparedStatement1.setDate(3, job.getJob_date());
                    preparedStatement1.executeUpdate();

                    lastID++;
                }

                preparedStatement.close();
                preparedStatement1.close();
                return true;

            } else return false;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * get all jobs like %jobName%
     */
    public static ArrayList<String> getJobLike(String jobName) {
        try (Connection connection = Main.database.getConnection()) {
            if (!jobName.isEmpty()) {
                ArrayList<String> jobs = new ArrayList<>();
                String query = "select JOB_NAME from JOBS where JOB_NAME LIKE ? ";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, jobName.toUpperCase() + "%");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                    jobs.add(resultSet.getString(1));
                preparedStatement.close();
                return jobs;
            }
            return null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Job> getJobByPatientID(int patientID) {
        try (Connection connection = Main.database.getConnection()) {

            ArrayList<Job> listJob = new ArrayList<>();
            String query = "select JOBS.JOBS_ID, JOB_NAME, JOB_DATE from JOBS join PATIENTJOB P on JOBS.JOBS_ID = P.JOBS_ID where PATIENT_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                listJob.add(new Job(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDate(3)));
            }
            preparedStatement.close();
            return listJob;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
