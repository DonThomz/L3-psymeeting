package data;

import application.App;
import oracle.jdbc.proxy.annotation.Pre;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Patient {

    // --------------------
    //   Attributes
    // --------------------

    private final int patient_id;
    private String name;
    private String last_name;
    private boolean new_patient;
    private Date birthday;
    private String gender;
    private String relationship;
    private String discovery_way;

    private ArrayList<Job> jobs;

    // --------------------
    //   Constructors
    // --------------------

    public Patient(int patient_id) {
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

    public Patient(int patient_id, String name, String last_name, Date birthday, String gender, String relationship, String discovery_way) {
        this.patient_id = patient_id;
        this.name = name;
        this.last_name = last_name;
        this.birthday = birthday;
        this.gender = gender;
        this.relationship = relationship;
        this.discovery_way = discovery_way;
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

    public Date getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getDiscovery_way() {
        return discovery_way;
    }

    public String getRelationship() {
        return relationship;
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

    public static int getLastPrimaryKeyId() {
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

    public static Patient getPatientByEmail(String email) {
        try {
            Statement stmt = App.database.getConnection().createStatement();
            ResultSet rset = stmt.executeQuery("select\n" +
                    "       u.PATIENT_ID,\n" +
                    "       p.NAME,\n" +
                    "       p.LAST_NAME\n" +
                    "from USER_APP u\n" +
                    "join PATIENT P on u.PATIENT_ID = P.PATIENT_ID\n" +
                    "where u.EMAIL = '" + email + "'");
            rset.next();
            return new Patient(rset.getInt(1), rset.getString(2), rset.getString(3), false);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static ResultSet getPatientFullNameByConsultationId(int consultation_id) {
        try {
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

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Patient> getAllPatientsProfiles() {

        ArrayList<Patient> list_patients = new ArrayList<>();

        try {
            Statement stmt = App.database.getConnection().createStatement();
            ResultSet result = stmt.executeQuery("select PATIENT_ID, NAME, LAST_NAME, BIRTHDAY, GENDER, RELATIONSHIP, DISCOVERY_WAY from PATIENT");
            while (result.next()) {
                list_patients.add(new Patient(result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getDate(4),
                        result.getString(5),
                        result.getString(6),
                        result.getString(7)));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        for (Patient p : list_patients
        ) {
            ArrayList<Job> jobs = new ArrayList<>();
            try {
                PreparedStatement preparedStatement = App.database.getConnection().prepareStatement("select JOBS_ID, JOB_NAME, JOB_DATE " +
                        "from JOBS " +
                        "where PATIENT_ID = ?");
                preparedStatement.setInt(1, p.getPatient_id());
                ResultSet result = preparedStatement.executeQuery();
                while (result.next()) {
                    Calendar tmp_date_job = App.Date2Calendar(result.getDate(3));
                    jobs.add(new Job(result.getInt(1), result.getString(2), tmp_date_job));
                }
                p.setJobs(jobs);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return list_patients;
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
