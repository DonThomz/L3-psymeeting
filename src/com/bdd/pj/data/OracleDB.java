package com.bdd.pj.data;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Database class
 */
public class OracleDB {
//    private Connection connection;

    //private static final OracleDB instance = new OracleDB();
    private ComboPooledDataSource comboPooledDataSource;

    public OracleDB() {
        try {
            this.comboPooledDataSource = new ComboPooledDataSource();
            this.comboPooledDataSource.setDriverClass("oracle.jdbc.driver.OracleDriver");
            this.comboPooledDataSource.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe");
            this.comboPooledDataSource.setUser("pj2");
            this.comboPooledDataSource.setPassword("pass");

            this.comboPooledDataSource.setMinPoolSize(5);
            this.comboPooledDataSource.setAcquireIncrement(5);
            this.comboPooledDataSource.setMaxPoolSize(20);

            this.comboPooledDataSource.setUnreturnedConnectionTimeout(3);
            this.comboPooledDataSource.setDebugUnreturnedConnectionStackTraces(true);

            this.comboPooledDataSource.setTestConnectionOnCheckout(true);

            // TODO Set connection before loading
//            this.comboPooledDataSource.setPreferredTestQuery("SELECT 1");
//            this.comboPooledDataSource.setCheckoutTimeout(3000);
//            this.comboPooledDataSource.setAu

//            this.comboPooledDataSource.setTestConnectionOnCheckout(true);
//            this.comboPooledDataSource.setPreferredTestQuery("SELECT 1");

            System.out.println("Connected ? Idk");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            System.exit(1);
        }
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
    public boolean connectionDatabase(String username, String password) throws Exception {
        System.out.println("Connecting to DB...");
        return true;
        // load driver
//        try {
//            Class.forName("oracle.jdbc.driver.OracleDriver");
//        } catch (ClassNotFoundException ex) {
//            System.out.println(" *** ERROR LOADING DRIVER *** ");
//            System.out.println("Message: " + ex.getMessage());
//            System.out.println("Error code: " + ex.getCause());
//        }
//
//        // connection to oracle database
//        try {
//            this.connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", username, password);
//            this.connection.setAutoCommit(false);
//            System.out.println(" *** Connection successfully established ***");
//            System.out.println("Welcome " + username);
//            return true;
//        } catch (SQLException ex) {
//            // if docker oracle database port : 51521
//            try {
//                this.connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:51521:xe", username, password);
//                System.out.println(" *** Connection successfully established ***");
//                System.out.println("Welcome " + username);
//                return true;
//
//            } catch (SQLException e) {
//                System.out.println(" *** ERROR CONNECTION TO DATABASE ***");
//                while (ex != null) {
//                    System.out.println("SQL state: " + ex.getSQLState());
//                    System.out.println("Message: " + ex.getMessage());
//                    System.out.println("Error code: " + ex.getErrorCode());
//                    ex = ex.getNextException();
//                }
//                while (e != null) {
//                    System.out.println("SQL state: " + e.getSQLState());
//                    System.out.println("Message: " + e.getMessage());
//                    System.out.println("Error code: " + e.getErrorCode());
//                    e = e.getNextException();
//                }
//                //return false;
//                throw new Exception("Test");
//            }
//        }
    }

    /**
     * Close the connection to the DB properly.
     */
    public void closeDatabase() {
//        if (connection != null) {
//            try {
//                connection.close();
//                System.out.println(" *** Connection close ***");
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
        this.comboPooledDataSource.close();
    }


//    public Connection getConnection() {
//        return connection;
//    }
}
