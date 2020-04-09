package data;

import application.App;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Patient {

    // --------------------
    //   Attributes
    // --------------------
    private int patient_id;
    private String name;
    private String last_name;
    private boolean new_patient;

    // --------------------
    //   Constructors
    // --------------------
    public Patient(int patient_id){
        this.patient_id = patient_id;
    }

    public Patient(int patient_id, String name, String last_name, boolean new_patient){
        this.patient_id = patient_id;
        this.name = name;
        this.last_name = last_name;
        this.new_patient = new_patient;
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

    // --------------------
    //   Set methods
    // --------------------

    public void setNew_patient(boolean new_patient) {
        this.new_patient = new_patient;
    }

    // --------------------
    //   Statement methods
    // --------------------
    public static void getPatients(){
        try{
            Statement stmt = App.database.getConnection().createStatement();
            ResultSet rset = stmt.executeQuery("select * from PATIENT");
            while(rset.next()){
                App.patients.add(new Patient(rset.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getLastPatientId(){
        try {
            Statement stmt = App.database.getConnection().createStatement();
            ResultSet rset = stmt.executeQuery("select max(patient_id) from PATIENT");
            rset.next();
            return rset.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    public static Patient getPatientByEmail(String email){
        try {
            Statement stmt = App.database.getConnection().createStatement();
            ResultSet rset = stmt.executeQuery("select\n" +
                    "       u.PATIENT_ID,\n" +
                    "       p.NAME,\n" +
                    "       p.LAST_NAME\n" +
                    "from USER_APP u\n" +
                    "join PATIENT P on u.PATIENT_ID = P.PATIENT_ID\n" +
                    "where u.EMAIL = '"+email+"'");
            rset.next();
            return new Patient(rset.getInt(1), rset.getString(2), rset.getString(3), false);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
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
