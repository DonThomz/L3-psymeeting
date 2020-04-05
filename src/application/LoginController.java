package application;

import data.OracleDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public Button login_button;

    public TextField username_field;
    public PasswordField password_field;
    public VBox vbox_login;
    Label incorrect_text;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        App.database = new OracleDB();
        App.patients = new ArrayList<>();
        App.connection_active = false;

        incorrect_text = new Label("username or password are incorrect !");
        incorrect_text.setId("incorrect_text");

    }

    public void login(ActionEvent actionEvent) {
        if (App.connection_active) {
            App.database.closeDatabase();
            login_button.setText("Login");
            App.connection_active = false;

            // reset field
            username_field.setText(null);
            password_field.setText(null);
        } else {
            if (username_field.getText() != null && password_field.getText() != null) {
                // if correct username and password
                if (App.database.connectionDatabase(username_field.getText(), password_field.getText())) {
                    App.database.getPatients();
                    login_button.setText("Logout");
                    App.connection_active = true;

                    // reset field
                    username_field.setText(null);
                    password_field.setText(null);
                    vbox_login.getChildren().remove(incorrect_text);

                    // load home scene
                    App.sceneMapping("login_scene", "home_scene", App.home_scene);


                } else {

                    vbox_login.getChildren().add(incorrect_text);
                    System.out.println("Incorrect username or password");

                    // reset field
                    username_field.setText(null);
                    password_field.setText(null);
                }
            }
        }
    }
}
