package database;

import java.sql.*;


/**
 *  Database class
 */
public class OracleDB {

    private Connection connection;

    // Constructor

    public OracleDB(){}

    // Connexion methods

    public void connectionDatabase(String username, String password){
        try {
            // Connection to oracle database
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:51521:xe", username, password);

            System.out.println(" *** Connection successfully established ***");
            System.out.println("Welcome " + username);


        }catch(SQLException ex){
            System.out.println(" *** ERROR CONNECTION TO DATABASE ***");
            while (ex != null) {
                System.out.println ("SQL Etat: " + ex.getSQLState ());
                System.out.println ("Message: " + ex.getMessage ());
                System.out.println ("Error code: " +
                        ex.getErrorCode ());
                ex = ex.getNextException ();
            }
        }catch(ClassNotFoundException ex){
            System.out.println(" *** ERROR LOADING DRIVER *** ");
            while (ex != null) {
                System.out.println("Message: " + ex.getMessage());
                System.out.println("Error code: " + ex.getCause());
            }
        }
    }

    public void closeDatabase(){
        if(connection != null) {
            try {
                connection.close();
                System.out.println(" *** Connection close ***");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
