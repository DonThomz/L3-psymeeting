package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TopBarController implements Initializable{

    @FXML public Hyperlink home_link;
    @FXML public Hyperlink consultation_link;
    @FXML public Hyperlink patients_link;
    @FXML private MenuButton user_menu;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user_menu.setText(App.current_user.getName() + " " + App.current_user.getLast_name());
    }


    public void logout(ActionEvent actionEvent) {

        // close database
        App.database.closeDatabase();
        App.connection_active = false;

        // reset user
        App.current_user = null;

        // return to login page
        App.sceneMapping("home_scene", "login_scene");

    }


    public void switchScene(ActionEvent actionEvent){
        String current_scene = App.getCurrentScene();
        String target_scene = ((Hyperlink)actionEvent.getSource()).getId();
        target_scene = target_scene.split("_")[0] + "_scene";

        assert current_scene != null;
        // only if the next scene is different
        if(!current_scene.equals(target_scene))
            App.sceneMapping(current_scene, target_scene);
    }

 }
