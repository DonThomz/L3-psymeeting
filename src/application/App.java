package application;


import data.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class App extends Application {

    //---------------------------------
    //         Database
    public static OracleDB database;
    public static boolean connection_active;
    public static ArrayList<Patient> patients;
    //
    //---------------------------------

    //---------------------------------
    //         javaFX GUI
    public static Stage windows;
    public static Scene login_scene;
    public static Scene home_scene;
    public static HashMap<String, Boolean> scenes;
    public static int scenes_size;

    public static void main(String[] args){
        launch(args);

        // close database if exit program
        if(connection_active)
            database.closeDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {

        // init FXML files
        Object root_login = FXMLLoader.load(getClass().getResource("javafx/fxml/login.fxml"));
        Object root_home = FXMLLoader.load(getClass().getResource("javafx/fxml/home.fxml"));

        // init scenes
        scenes_size = 2;

        login_scene = new Scene((Parent) root_login);
        home_scene = new Scene((Parent) root_home);

        scenes = new HashMap<>();
        scenes.put("login_scene", true);
        scenes.put("home_scene", false);


        // init windows
        windows = stage;
        windows.setScene(login_scene);
        windows.setResizable(false);
        windows.setTitle("PsyMeeting");
        windows.show();

    }


    public static void sceneMapping(String scene_open, String scene_close, Scene new_scene){
        // Warning
        windows.setScene(new_scene);
        scenes.put(scene_open, false);
        scenes.put(scene_close, true);
    }

}
