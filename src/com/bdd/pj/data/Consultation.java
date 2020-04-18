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
    private final int consultation_id; //primary key
    private Calendar date;
    private float price;
    private String pay_mode;
    private ArrayList<String[]> patients_full_names; // String[0] = name | String[1] = last_name

    // Feedback attributes from table FEEDBACK
    private String commentary;
    private String keyword;
    private String posture;
    private int indicator;

    // Graphic attributes
    private TextArea full_infos;
    private JFXButton consultation_button;

    // --------------------
    //   Constructors
    // --------------------
    public Consultation(int consultation_id) {
        this.consultation_id = consultation_id;
    }

    public Consultation(int consultation_id, Calendar date, float price, String pay_mode, ArrayList<String[]> patients_full_names,
                        String commentary, String keyword, String posture, int indicator, TextArea full_infos, JFXButton consultation_button) {
        this.consultation_id = consultation_id;
        this.date = date;
        this.price = price;
        this.pay_mode = pay_mode;
        this.patients_full_names = patients_full_names;
        this.commentary = commentary;
        this.keyword = keyword;
        this.posture = posture;
        this.indicator = indicator;
        this.full_infos = full_infos;
        this.consultation_button = consultation_button;
    }


    // --------------------
    //   Get methods
    // --------------------
    public int getConsultation_id() {
        return consultation_id;
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

    public String getPay_mode() {
        return pay_mode;
    }

    public TextArea getFull_infos() {
        return full_infos;
    }

    public ArrayList<String[]> getPatients_full_names() {
        return patients_full_names;
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

    public void setPay_mode(String pay_mode) {
        this.pay_mode = pay_mode;
    }

    public void setFull_infos(TextArea full_infos) {
        this.full_infos = full_infos;
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

    public void setPatients_full_names(ArrayList<String[]> patients_full_names) {
        this.patients_full_names = patients_full_names;
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

    public static Calendar getDateById(int consultation_id) {
        // TODO Fix this, as the connection can't be checked out
        String query = "select\n" +
                "     c.CONSULTATION_DATE\n" +
                "from CONSULTATION c\n" +
                "where c.CONSULTATION_ID = ?";
        try (Connection connection = Main.database.getConnection()) {
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

    public static StringBuilder getConsultationInfoById(int consultation_id) {
        String query = "select c.PRICE, c.PAY_MODE, f.COMMENTARY, f.KEYWORD, f.POSTURE\n" +
                "from CONSULTATION c\n" +
                "join FEEDBACK f on c.CONSULTATION_ID = f.FEEDBACK_ID\n" +
                "where c.CONSULTATION_ID = ?";
        try (Connection connection = Main.database.getConnection()) {
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            // the insert statement

            // create the insert preparedStatement

            preparedStmt.setInt(1, consultation_id);

            ResultSet rs = preparedStmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.println("querying SELECT * FROM XXX");
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }
            assert rs != null;
            rs.next();
            // info price and pay mode
            StringBuilder info = new StringBuilder();
            info.append("Prix : ").append(rs.getInt(1)).append(" €, payé avec : ").append(rs.getString(2));

            // info feedback commentary, key words, postures
            info.append("\n\nRetour de séance").append("\n\n\tCommentaire : \n").append(rs.getString(3));
            if (rs.getString(4) != null)
                info.append("\n\n\tMots clés :").append(rs.getString(4));
            if (rs.getString(5) != null)
                info.append("\n\n\tPosture :").append(rs.getString(5));

            return info;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
