package application.controller;

import application.App;
import data.*;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;


public class LoginController implements Initializable {

    @FXML private Button login_button;
    @FXML private TextField username_field;
    @FXML private PasswordField password_field;
    @FXML private VBox box_login;
    @FXML private CheckBox save_pwd_checkbox;
    private Label incorrect_text;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // init database
        App.database = new OracleDB();
        App.patients = new ArrayList<>();
        App.connection_active = false;

        // fill field text and checkbox
        File tmpSaveFile = new File("save_pwd.txt");
        fillField(tmpSaveFile.exists());


        incorrect_text = new Label("identifiant ou mot de passe incorrect !");
        incorrect_text.getStyleClass().add("warring_label");

    }

    public void login(ActionEvent actionEvent){

        int[] i = {0};
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                i[0]++;
                if(i[0] % App.time_transition == 0) {
                    this.stop();
                    // remove incorrect_text label
                    box_login.getChildren().remove(incorrect_text);

                    // if database already open
                    if (App.connection_active) {
                        App.database.closeDatabase();
                        login_button.setText("Login");
                        App.connection_active = false;
                        // reset field
                        username_field.setText(null);
                        password_field.setText(null);
                    } else {
                        if (username_field.getText() != null && password_field.getText() != null) {
                            // if correct username and password --> connection to database
                            if (App.database.connectionDatabase(username_field.getText(), password_field.getText())) {

                                if(save_pwd_checkbox.isSelected()) createSaveFile();
                                else removeSaveFile();

                                App.connection_active = true;

                                // load user
                                App.current_user = new User(username_field.getText());

                                // load home scene
                                App.window.close();

                                // remove incorrect_text label
                                box_login.getChildren().remove(incorrect_text);

                                // load home scene
                                App.sceneMapping("login_scene", "home_scene");

                                App.centerWindow();

                            } else {
                                // add incorrect_text label
                                box_login.getChildren().add(incorrect_text);
                            }
                            // after press login button --> reset field
                            username_field.setText(null);
                            password_field.setText(null);
                        }
                    }
                }
            }
        }.start();
    }

    private void createSaveFile(){
        // create file
        try {
            File save_pwd = new File("save_pwd.txt");
            save_pwd.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred. Creation of save_pwd.txt");
            e.printStackTrace();
        }
        // write file
        try {
            FileWriter myWriter = new FileWriter("save_pwd.txt");
            myWriter.write(username_field.getText()+"\n");
            myWriter.write(password_field.getText());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void removeSaveFile(){
        File tmpFile = new File("save_pwd.txt");
        tmpFile.delete();
    }

    private void fillField(boolean exist){
        if(exist){
            Scanner scanner = null;
            try {
                scanner = new Scanner(new File("save_pwd.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            assert scanner != null;
            if(scanner.hasNextLine())
                username_field.setText(scanner.next());
            if(scanner.hasNextLine())
                password_field.setText(scanner.next());
            save_pwd_checkbox.setSelected(true);
        }
    }
}
