package data;

import application.App;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Patient {

    // --------------------
    //   Attributes
    // --------------------

        private final int patient_id;
        private String name;
        private String last_name;
        private boolean new_patient;
        private ArrayList<Job> jobs;

    // --------------------
    //   Constructors
    // --------------------

        public Patient(int patient_id){
            this.patient_id = patient_id;
        }

        // patient without jobs
        public Patient(int patient_id, String name, String last_name, boolean new_patient) {
            this.patient_id = patient_id;
            this.name = name;
            this.last_name = last_name;
            this.new_patient = new_patient;
        }

        // patient with jobs history
        public Patient(int patient_id, String name, String last_name, boolean new_patient, ArrayList<Job> jobs) {
            this.patient_id = patient_id;
            this.name = name;
            this.last_name = last_name;
            this.new_patient = new_patient;
            this.jobs = jobs;
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

        public ArrayList<Job> getJobs() {
            return jobs;
        }

    // --------------------
    //   Set methods
    // --------------------

        public void setNew_patient(boolean new_patient) {
            this.new_patient = new_patient;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public void setJobs(ArrayList<Job> jobs) {
            this.jobs = jobs;
        }

    // --------------------
    //   Statement methods
    // --------------------

        public static int getLastPrimaryKeyId(){
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

        public static ResultSet getPatientFullNameByConsultationId(int consultation_id){
            try{
                // the insert statement
                String query = "select\n" +
                        "p.NAME, p.LAST_NAME\n" +
                        "from PATIENT p\n" +
                        "join CONSULTATION_CARRYOUT cc on p.PATIENT_ID = cc.PATIENT_ID\n" +
                        "where CONSULTATION_ID = ?";
                // create the insert preparedStatement
                PreparedStatement preparedStmt = App.database.getConnection().prepareStatement(query);
                preparedStmt.setInt(1, consultation_id);
                return preparedStmt.executeQuery();

            }catch(SQLException ex){
                ex.printStackTrace();
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
