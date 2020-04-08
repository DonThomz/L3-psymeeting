package application;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TopBarController implements Initializable{

    @FXML public Hyperlink home_link;
    @FXML public Hyperlink consultation_link;
    @FXML public Hyperlink patients_link;
    @FXML private MenuButton user_menu;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user_menu.setText(App.current_user.getName() + " " + App.current_user.getLast_name());

        boldLink();
    }


    public void logout(ActionEvent actionEvent) {

        // close database
        App.database.closeDatabase();
        App.connection_active = false;

        // reset user
        App.current_user = null;

        App.resetHashMap();

        // return to login page
        App.sceneMapping(Objects.requireNonNull(App.getCurrentScene()), "login_scene");

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

    public void boldLink(){
        String s = App.getCurrentScene();
        System.out.println(s);
        switch (Objects.requireNonNull(s)){
            case "home_scene":
                home_link.setStyle("-fx-font-weight: bold; -fx-font-size: 18");
                break;
            case "consultation_scene":
                consultation_link.setStyle("-fx-font-weight: bold; -fx-font-size: 18");
                break;
            case "patients_scene":
                patients_link.setStyle("-fx-font-weight: bold; -fx-font-size: 18");
                break;
        }
    }
 }
