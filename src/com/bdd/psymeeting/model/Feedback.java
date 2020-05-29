/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.model;


import com.bdd.psymeeting.Main;

import java.sql.*;
import java.util.ArrayList;

public class Feedback {

    // Feedback attributes from table FEEDBACK
    private int feedbackID;
    private ArrayList<String> commentaries;
    private ArrayList<String> keywords;
    private ArrayList<String> postures;
    private int indicator;

    private int consultation_id;

    // --------------------
    //   Constructors
    // --------------------

    // Feedback from DB
    public Feedback(int consultationID) {
        try (Connection connection = Main.database.getConnection()) {

            this.commentaries = new ArrayList<>();
            this.keywords = new ArrayList<>();
            this.postures = new ArrayList<>();

            String query = "select C2.COMMENTARY, K.KEYWORD, P.POSTURE \n" +
                    "from FEEDBACK\n" +
                    "join COMMENTARY C2 on FEEDBACK.FEEDBACK_ID = C2.FEEDBACK_ID\n" +
                    "join KEYWORD K on FEEDBACK.FEEDBACK_ID = K.FEEDBACK_ID\n" +
                    "join POSTURE P on FEEDBACK.FEEDBACK_ID = P.FEEDBACK_ID\n" +
                    "where CONSULTATION_ID = ?";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1, consultationID);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                this.feedbackID = resultSet.getInt(1);
                this.commentaries.add(resultSet.getString(2));
                this.keywords.add(resultSet.getString(3));
                this.postures.add(resultSet.getString(4));
                this.indicator = resultSet.getInt(5);
                this.consultation_id = resultSet.getInt(6);
            }

        } catch (SQLException ex) {
            System.out.println("Error add name or last name to the user (3)");
            ex.printStackTrace();
            System.out.println(ex.getErrorCode() + " : " + ex.getMessage());
        }
    }


    // ----------------------
    //  Getters and Setters
    // ----------------------

    public int getFeedbackID() {
        return feedbackID;
    }

    public ArrayList<String> getCommentary() {
        return commentaries;
    }

    public ArrayList<String> getKeyword() {
        return keywords;
    }

    public ArrayList<String> getPosture() {
        return postures;
    }

    public int getIndicator() {
        return indicator;
    }

    public int getConsultation_id() {
        return consultation_id;
    }

    // ----------------------
    //  Setters
    // ----------------------

    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }

    public void setIndicator(int indicator) {
        this.indicator = indicator;
    }

    public void setConsultation_id(int consultation_id) {
        this.consultation_id = consultation_id;
    }


    // --------------------
    //   Statement methods
    // --------------------

    public static int getLastFeedbackID() {
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select max(FEEDBACK_ID) from FEEDBACK");
            result.next();
            return result.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return -1;
        }

    }

    public static boolean insertFeedback(int consultation_id) {
        try (Connection connection = Main.database.getConnection()) {
            String query = "insert into FEEDBACK (FEEDBACK_ID, CONSULTATION_ID) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            if (getLastFeedbackID() != -1) {
                preparedStatement.setInt(1, getLastFeedbackID() + 1);
                preparedStatement.setInt(2, consultation_id + 1);
                return true;
            } else return false;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
