package com.bdd.pj.application.controller;

import com.bdd.pj.application.Main;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuButton;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TopBarController implements Initializable {

    @FXML
    public Hyperlink home_link;
    @FXML
    public Hyperlink consultation_link;
    @FXML
    public Hyperlink patients_link;
    @FXML
    private MenuButton user_menu;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user_menu.setText(Main.current_user.getName() + " " + Main.current_user.getLast_name());

        boldLink();
    }


    public void logout(ActionEvent actionEvent) {

        // close database
        Main.database.closeDatabase();
        Main.connection_active = false;

        // reset user
        Main.current_user = null;

        Main.resetHashMap();

        // return to login page
        Main.sceneMapping(Objects.requireNonNull(Main.getCurrentScene()), "login_scene");

    }

    public void switchScene(ActionEvent actionEvent) {
        String current_scene = Main.getCurrentScene();
        String target_scene = ((Hyperlink) actionEvent.getSource()).getId();
        target_scene = target_scene.split("_")[0] + "_scene";

        assert current_scene != null;
        // only if the next scene is different
        if (!current_scene.equals(target_scene))
            Main.sceneMapping(current_scene, target_scene);
    }


    public void boldLink() {
        String s = Main.getCurrentScene();
        switch (Objects.requireNonNull(s)) {
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

    public void add_consultation(ActionEvent actionEvent) throws InterruptedException {
        int[] i = {0};
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                i[0]++;
                if (i[0] % Main.time_transition == 0) {
                    this.stop();
                    Main.sceneMapping(Objects.requireNonNull(Main.getCurrentScene()), "add_consultation_scene");
                }
            }
        }.start();

    }
}
