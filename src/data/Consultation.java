package data;

import application.App;

import java.sql.*;

public class Consultation {

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
    public static int getLastConsultationId(){
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





}
