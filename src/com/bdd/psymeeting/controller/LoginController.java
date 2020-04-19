/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.controller;

import com.bdd.psymeeting.Main;
import com.bdd.psymeeting.OracleDB;
import com.bdd.psymeeting.model.User;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;


public class LoginController implements Initializable {

    @FXML
    private AnchorPane login_pane;
    @FXML
    private Button login_button;
    @FXML
    private TextField username_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private VBox box_login;
    @FXML
    private CheckBox save_pwd_checkbox;
    private Label incorrect_text;
    private ExecutorService exec;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // init database
        Main.database = new OracleDB();
        Main.patients = new ArrayList<>();
        Main.connection_active = false;

        // fill field text and checkbox
        File tmpSaveFile = new File("save_pwd.txt");
        fillField(tmpSaveFile.exists());


        incorrect_text = new Label("Identifiant ou mot de passe incorrect !");
        incorrect_text.getStyleClass().add("warring_label");

        // Set actions for success and failed connection to the DB
        loginService.setOnSucceeded(evt -> {
            System.out.println("Task succeeded!");
            loginSucceeded();
            login_button.setDisable(false);
        });
        loginService.setOnFailed(evt -> {
            System.out.println("Task failed!");
            login_button.setDisable(false);
            loginFailed();
            loginService.reset();
        });
    }

    /**
     * Service that allows us to connect to DB in another Thread (≠ JavaFX UI Thread)
     * The reusable service allows the creation of multiple Tasks.
     * See https://fabrice-bouye.developpez.com/tutoriels/javafx/gui-service-tache-de-fond-thread-javafx/ for reference.
     */
    final Service<Boolean> loginService = new Service<Boolean>() {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    System.out.println("Task starting");
                    if (Main.database.connectionDatabase(username_field.getText(), password_field.getText()))
                        return true;
                    else
                        throw new Exception("Failed to connect. (This error should be silent and caught by 'loginService.setOnFailed')");
                }
            };
        }
    };

    public void login(ActionEvent actionEvent) {
        int[] i = {0};
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                i[0]++;
                if (i[0] % Main.time_transition == 0) {
                    this.stop();
                    // remove incorrect_text label
                    box_login.getChildren().remove(incorrect_text);

                    // if database already open
                    if (Main.connection_active) {
                        Main.database.closeDatabase();
                        login_button.setText("Login");
                        Main.connection_active = false;
                        // reset field
                        username_field.setText(null);
                        password_field.setText(null);
                    } else {
                        if (username_field.getText() != null && password_field.getText() != null) {
                            System.out.println("Starting login thread...");
                            if (loginService.getState() == Task.State.READY) {
                                loginService.start();
                                login_button.setDisable(true);
                            } else { // TODO Add loading state
                                System.out.println("Service is not ready for another try!");
                            }
                        }
                    }
                }
            }
        }.start();
    }

    public void loginSucceeded() {
        // Success
        if (save_pwd_checkbox.isSelected()) createSaveFile();
        else removeSaveFile();

        Main.connection_active = true;

        // load user
        Main.current_user = new User(username_field.getText());

        // load home scene
        Main.window.close();

        // remove incorrect_text label
        box_login.getChildren().remove(incorrect_text);

        // Load home scene
        Main.sceneMapping("login_scene", "home_scene");

        Main.window.centerOnScreen();
    }

    public void loginFailed() {
        // Add incorrect_text label
        box_login.getChildren().add(incorrect_text);
    }

    private void resetFields() {
        // After press login button --> reset field
        username_field.setText(null);
        password_field.setText(null);
    }

    private void createSaveFile() {
        // Create file
        try {
            File save_pwd = new File("save_pwd.txt");
            if (save_pwd.createNewFile()) {
                System.out.println("Successfully save_pwd.txt create !");
            } else System.out.println("save_pwd.txt already exist !");
        } catch (IOException e) {
            System.out.println("An error occurred. Creation of save_pwd.txt");
            e.printStackTrace();
        }
        // Write file
        try {
            FileWriter myWriter = new FileWriter("save_pwd.txt");
            myWriter.write(username_field.getText() + "\n");
            myWriter.write(password_field.getText());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void removeSaveFile() {
        File tmpFile = new File("save_pwd.txt");
        if (tmpFile.delete()) System.out.println("Successfully remove save_pwd.txt");
        else System.out.println("Error remove save_pwd.txt");
    }

    private void fillField(boolean exist) {
        if (exist) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(new File("save_pwd.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            assert scanner != null;
            if (scanner.hasNextLine())
                username_field.setText(scanner.next());
            if (scanner.hasNextLine())
                password_field.setText(scanner.next());
            save_pwd_checkbox.setSelected(true);
        }
    }
}
