package data;

import application.App;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Patient {

    private int patient_id;

    public Patient(int patient_id){
        this.patient_id = patient_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    // --------------------
    //   get data info
    // --------------------
    public void getName(){
        try{
            Statement name_statement = App.database.getConnection().createStatement();
            ResultSet name_result = name_statement.executeQuery("select name from patient");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
