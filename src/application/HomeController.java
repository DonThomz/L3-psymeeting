package application;

import data.OracleDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HomeController implements Initializable{

    @FXML public MenuButton user_menu;
    @FXML public Text user_name;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user_name.setText(App.current_user.getName() + " " + App.current_user.getLast_name());
    }

    public void closeHome(ActionEvent actionEvent) throws IOException {

        // close database
        App.database.closeDatabase();
        App.connection_active = false;

        // reset user
        App.current_user = null;

        // return to login page
        App.sceneMapping("home_scene", "login_scene");


    }
}
