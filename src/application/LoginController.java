package application;

import data.*;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
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
        System.out.println("test");
        App.database = new OracleDB();
        App.patients = new ArrayList<>();
        App.connection_active = false;

        // fill field text and checkbox
        File tmpSaveFile = new File("save_pwd.txt");
        fillField(tmpSaveFile.exists());


        incorrect_text = new Label("username or password are incorrect !");
        incorrect_text.setId("incorrect_text");

    }

    public void login(ActionEvent actionEvent) throws IOException {
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
                    App.sceneMapping("login_scene", "home_scene");

                    // remove incorrect_text label
                    box_login.getChildren().remove(incorrect_text);

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

    private void createSaveFile(){
        // create file
        try {
            File save_pwd = new File("save_pwd.txt");
            if (save_pwd.createNewFile()) {
                System.out.println("File created: " + save_pwd.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        // write file
        try {
            FileWriter myWriter = new FileWriter("save_pwd.txt");
            myWriter.write(username_field.getText()+"\n");
            myWriter.write(password_field.getText());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
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
