package application;


import data.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
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

    public static Object root_login;
    public static Object root_home;


    //---------------------------------
    //         Applications
    public static User current_user;

    public static void main(String[] args){
        launch(args);

        // close database if exit program
        if(connection_active)
            database.closeDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {

        // init FXML login file
        root_login = FXMLLoader.load(getClass().getResource("javafx/fxml/login.fxml"));
        // init size
        scenes_size = 2;

        // load login scene
        login_scene = new Scene((Parent) root_login);

        // init scene HashMap
        scenes = new HashMap<>();
        scenes.put("login_scene", true);
        scenes.put("home_scene", false);

        // init windows
        windows = stage;
        windows.setScene(login_scene);
        windows.setResizable(false);
        windows.setTitle("PsyMeeting");
        windows.show();

        /*final long startNanoTime = System.nanoTime();
        final int[] i = {0};
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                i[0]++;
                if(i[0] % 60 == 0) {
                    System.out.println(stage.getX() + " : " + stage.getY());

                }
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;


            }
        }.start();*/

        windows = stage;
        windows.setResizable(false);
        windows.setTitle("PsyMeeting");
        windows.show();
    }


    public static void sceneMapping(String scene_open, String scene_close) throws IOException {

        // detect monitor
        int screen_index = 0;
        for (int i = 0; i < Screen.getScreens().size(); i++) {
            System.out.println(windows.getX());
            System.out.println(Screen.getScreens().get(i).getVisualBounds().getMinX());
            if((windows.getX() + windows.getWidth() / 2)  > Screen.getScreens().get(i).getVisualBounds().getMinX()){
                screen_index = i;
            }
        }

        // change scene
        switch(scene_close){
            case "home_scene":
                root_home = FXMLLoader.load(App.class.getResource("javafx/fxml/home.fxml")); // launch initialize methods
                home_scene = new Scene((Parent) root_home);
                windows.setScene(home_scene);
                windows.setResizable(true);
                break;
            case "login_scene":
                root_login = FXMLLoader.load(App.class.getResource("javafx/fxml/login.fxml")); // launch initialize methods
                login_scene = new Scene((Parent) root_login);
                windows.setScene(login_scene);
                windows.setResizable(false);
                break;
        }
        scenes.put(scene_open, false);
        scenes.put(scene_close, true);


        // center windows
        Rectangle2D primScreenBounds = Screen.getScreens().get(screen_index).getVisualBounds();
        windows.setX(primScreenBounds.getMinX() + (primScreenBounds.getWidth() - windows.getWidth()) / 2);
        windows.setY((primScreenBounds.getHeight() - windows.getHeight()) / 2);
        System.out.println(windows.getX());
    }


}
