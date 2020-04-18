package com.bdd.pj.data;

import com.bdd.pj.application.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {

    // --------------------
    //   Attributes
    // --------------------

    private int user_id;
    private int patient_id;
    private String username;
    private String name;
    private String last_name;
    private String email;
    private String password;

    private boolean new_user;


    // --------------------
    //   Constructors
    // --------------------
    public User(String username) {
        // get name
        try {
            this.username = username;

            // admin user
            if (this.username.equals("admin")) {
                Statement stmt = Main.database.getConnection().createStatement();
                ResultSet rset = stmt.executeQuery("select NAME, LAST_NAME from ADMINISTRATOR");
                rset.next();
                this.name = rset.getString(1);
                this.last_name = rset.getString(2);
            }


        } catch (SQLException ex) {
            System.out.println("Error add name or last name to the user");
            System.out.println(ex.getErrorCode() + " : " + ex.getMessage());
        }
    }


    // guest user without password => generate a random password
    public User(int user_id, String email, int patient_id, boolean new_user) {
        this.user_id = user_id;
        this.email = email;
        this.password = "tmp_password";
        this.patient_id = patient_id;
        this.new_user = new_user;
    }


    // --------------------
    //   Get methods
    // --------------------
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getLast_name() {
        return last_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public boolean isNew_user() {
        return new_user;
    }

    // --------------------
    //   Statement methods
    // --------------------
    public static int getLastUserId() {
        try {
            Statement stmt = Main.database.getConnection().createStatement();
            ResultSet rset = stmt.executeQuery("select max(USER_ID) from USER_APP");
            rset.next();
            return rset.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    public static int getPatientIdByEmail(String email) {
        try {
            Statement stmt = Main.database.getConnection().createStatement();
            ResultSet rset = stmt.executeQuery("select\n" +
                    "       u.PATIENT_ID\n" +
                    "from USER_APP u\n" +
                    "where u.EMAIL = '" + email + "'");
            rset.next();
            return rset.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }


}