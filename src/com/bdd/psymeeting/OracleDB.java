/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.*;


/**
 * Database class
 */
public class OracleDB {


    //private static final OracleDB instance = new OracleDB();
    private ComboPooledDataSource comboPooledDataSource;

    public OracleDB() {

    }

//    public static final OracleDB getInstance() {
//        return instance;
//    }

    public Connection getConnection() throws SQLException {
        return this.comboPooledDataSource.getConnection();
    }

    public void close() {
        this.comboPooledDataSource.close();
    }

    /**
     * Connect to the DB.
     */
    public boolean connectionDatabase(String username, String password) {
        System.out.println("Connecting to DB...");
        try {
            this.comboPooledDataSource = new ComboPooledDataSource();
            this.comboPooledDataSource.setDriverClass("oracle.jdbc.driver.OracleDriver");
            this.comboPooledDataSource.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe");
            this.comboPooledDataSource.setUser(username);
            this.comboPooledDataSource.setPassword(password);

            this.comboPooledDataSource.setMinPoolSize(5);
            this.comboPooledDataSource.setAcquireIncrement(5);
            this.comboPooledDataSource.setMaxPoolSize(20);

            this.comboPooledDataSource.setAcquireRetryAttempts(0);

            this.comboPooledDataSource.setUnreturnedConnectionTimeout(10);
            this.comboPooledDataSource.setDebugUnreturnedConnectionStackTraces(true);

            this.comboPooledDataSource.setTestConnectionOnCheckout(true);

            System.out.println("Pooled data source set up: done!");

            return this.comboPooledDataSource.getUser().equals("admin") && this.comboPooledDataSource.getPassword().equals("adminpwd");

        } catch (PropertyVetoException e) {
            System.out.println("Unable to set up connection pool!");
            e.printStackTrace();
            // System.exit(1);
            return false;
        }
    }

    /**
     * Close the connection to the DB properly.
     */
    public void closeDatabase() {
        this.comboPooledDataSource.close();
    }


}
