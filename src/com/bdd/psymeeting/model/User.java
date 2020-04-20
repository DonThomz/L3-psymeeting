/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.model;

import com.bdd.psymeeting.Main;
import oracle.jdbc.proxy.annotation.Pre;

import java.sql.*;

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
        this.username = username;
        if (this.username.equals("admin")) {
            try (Connection connection = Main.database.getConnection()) {
                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery("select NAME, LAST_NAME from ADMINISTRATOR");
                resultSet.next();
                this.name = resultSet.getString(1);
                this.last_name = resultSet.getString(2);
                System.out.println("User: " + this.name + " " + this.last_name);

            } catch (SQLException ex) {
                System.out.println("Error add name or last name to the user (1)");
                System.out.println(ex.getErrorCode() + " : " + ex.getMessage());
            }
        }
    }


    // guest user without password => generate a random password
    public User(int user_id, String email, int patient_id, boolean new_user) {
        this.user_id = user_id;
        this.email = email;
        if(new_user) this.password = "tmp_password";
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
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("select max(USER_ID) from USER_APP");
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    public static int getPatientIdByEmail(String email) {
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("select\n" +
                    "       u.PATIENT_ID\n" +
                    "from USER_APP u\n" +
                    "where u.EMAIL = '" + email + "'");
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    public static String getUserFullName(String username) throws SQLException {
        // get name
        if (username.equals("admin")) {
            try (Connection connection = Main.database.getConnection()) {
                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery("select NAME, LAST_NAME from ADMINISTRATOR");
                resultSet.next();
                return resultSet.getString(1) + " " + resultSet.getString(2);


            } catch (SQLException ex) {
                System.out.println("Error add name or last name to the user (2)");
                System.out.println(ex.getErrorCode() + " : " + ex.getMessage());
                return null;
            }
        } else {
            return "Invité";
        }
    }

    /**
     * Check if a user exists with its email.
     * @param email: the mail for which you want to check if a user exist
     * @return true if exist or false if not
     */
    public static boolean userExist(String email) {
        try (Connection connection = Main.database.getConnection()) {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("select email from USER_APP");
            while (resultSet.next()) {
                if (resultSet.getString(1).equals(email)) {
                    return true;
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    public static User getUserByEmail(String email){
        try (Connection connection = Main.database.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select USER_ID, EMAIL, PATIENT_ID from USER_APP where EMAIL = ?");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            // return user who already exist in database
            return new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), false);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }




}
