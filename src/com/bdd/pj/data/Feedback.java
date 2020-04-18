package com.bdd.pj.data;


import com.bdd.pj.application.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Feedback {

    // Feedback attributes from table FEEDBACK
    private int feedbackID;
    private String commentary;
    private String keyword;
    private String posture;
    private int indicator;

    private int consultation_id;

    // --------------------
    //   Constructors
    // --------------------
    public Feedback(int feedbackID, String commentary, String keyword, String posture, int indicator, int consultation_id) {
        this.feedbackID = feedbackID;
        this.commentary = commentary;
        this.keyword = keyword;
        this.posture = posture;
        this.indicator = indicator;
        this.consultation_id = consultation_id;
    }

    // Feedback from DB
    public Feedback(int consultationID){
        try (Connection connection = Main.database.getConnection()) {

            String query = "select f.FEEDBACK_ID, f.COMMENTARY, f.KEYWORD, f.POSTURE, f.INDICATOR, f.CONSULTATION_ID\n" +
                    "from CONSULTATION c\n" +
                    "join FEEDBACK f on c.CONSULTATION_ID = f.CONSULTATION_ID\n" +
                    "where c.CONSULTATION_ID = ?";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1, consultationID);
            ResultSet resultSet = preparedStmt.executeQuery();
            resultSet.next();
            this.feedbackID = resultSet.getInt(1);
            this.commentary = resultSet.getString(2);
            this.keyword = resultSet.getString(3);
            this.posture = resultSet.getString(4);
            this.indicator = resultSet.getInt(5);
            this.consultation_id = resultSet.getInt(6);

        } catch (SQLException ex) {
            System.out.println("Error add name or last name to the user");
            System.out.println(ex.getErrorCode() + " : " + ex.getMessage());
        }
    }

    // ----------------------
    //  Getters and Setters
    // ----------------------

    public int getFeedbackID() {
        return feedbackID;
    }

    public String getCommentary() {
        return commentary;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getPosture() {
        return posture;
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

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setPosture(String posture) {
        this.posture = posture;
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


}
