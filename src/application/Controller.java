package application;

import data.OracleDB;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button login_button;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.database = new OracleDB();
        Main.connection_active = false;
    }

    public void connection_to_database(ActionEvent actionEvent) {
        if(Main.connection_active){
            Main.database.closeDatabase();
            login_button.setText("Login");
            Main.connection_active = false;
        }
        else {
            Main.database.connectionDatabase("admin", "admin");
            login_button.setText("Logout");
            Main.connection_active = true;
        }

    }
}
