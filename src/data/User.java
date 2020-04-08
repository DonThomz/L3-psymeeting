package data;

import application.App;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {

    private String username;
    private String name;
    private String last_name;

    public User(String username){
        // get name
        try{
            this.username = username;
            Statement stmt = App.database.getConnection().createStatement();
            if(this.username.equals("admin")) {
                ResultSet rset = stmt.executeQuery("select NAME, LAST_NAME from ADMINISTRATOR");
                rset.next();
                this.name = rset.getString(1);
                this.last_name = rset.getString(2);
            }

        }catch(SQLException ex){
            System.out.println("Error add name or last name to the user");
            System.out.println(ex.getErrorCode() + "\n" + ex.getMessage() + "\n" + ex.getSQLState());
        }
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getLast_name() {
        return last_name;
    }
}
