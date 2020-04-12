package data;

import application.App;

import java.sql.*;
import java.util.Calendar;

public class Consultation{

    // --------------------
    //   Attributes
    // --------------------
        private int consultation_id;
        private Date date;
        private float price;
        private String pay_mode;

    // --------------------
    //   Constructors
    // --------------------
        public Consultation(int consultation_id){
            this.consultation_id = consultation_id;
        }

    // --------------------
    //   Get methods
    // --------------------
    public int getConsultation_id() {
        return consultation_id;
    }


    // --------------------
    //   Statement methods
    // --------------------

    public static int getLastPrimaryKeyId() {
        try {
            Statement stmt = App.database.getConnection().createStatement();
            ResultSet result = stmt.executeQuery("select max(CONSULTATION_ID) from CONSULTATION");
            result.next();
            return result.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    public static Calendar getDateById(int consultation_id) {
        try{
            // the insert statement
            String query = "select\n" +
                    "     c.CONSULTATION_DATE\n" +
                    "from CONSULTATION c\n" +
                    "where c.CONSULTATION_ID = ?";
            // create the insert preparedStatement
            PreparedStatement preparedStmt = App.database.getConnection().prepareStatement(query);
            preparedStmt.setInt(1, consultation_id);
            ResultSet result = preparedStmt.executeQuery();
            if(result.next()){

                return App.Timestamp2Calendar(result.getTimestamp(1));
            }

        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static ResultSet getConsultationInfoById(int consultation_id){
        try{
            // the insert statement
            String query = "select price, PAY_MODE\n" +
                    "from CONSULTATION\n" +
                    "where CONSULTATION_ID = ?;";
            // create the insert preparedStatement
            PreparedStatement preparedStmt = App.database.getConnection().prepareStatement(query);
            preparedStmt.setInt(1, consultation_id);
            return preparedStmt.executeQuery();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }


    





}
