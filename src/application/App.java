package application;



import com.jfoenix.controls.JFXDecorator;
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
import java.util.Arrays;
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
    //         Resolutions
        public static Stage window;
        public static double[] current_resolution;
        public static double[] login_resolution = {420 , 580};
        public static double[] app_default_resolution = {1200 , 800};

    //---------------------------------
    //         Scenes
        public static Scene login_scene;
        public static Scene home_scene;
        public static Scene consultation_scene;

        public static HashMap<String, Boolean> scenes;
        public static int scenes_size;
    //
    //---------------------------------

    //---------------------------------
    //         FXML Objects
        public static Object root_login;
        public static Object root_home;
        public static Object root_consultation;
    //
    //---------------------------------


    //---------------------------------
    //         Applications
        public static User current_user;
    //
    //---------------------------------

    public static void main(String[] args){
        launch(args);

        // close database if exit program
        if(connection_active)
            database.closeDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {

        // init static variables
        scenes_size = 2;
        current_resolution = app_default_resolution;

        scenes = new HashMap<>();
        scenes.put("login_scene", true);
        scenes.put("home_scene", false);


        // init FXML login file
        root_login = FXMLLoader.load(getClass().getResource("javafx/fxml/login.fxml"));


        // load login scene
        login_scene = new Scene((Parent) root_login, login_resolution[0], login_resolution[1]);

        // init window
        window = stage;
        window.setScene(login_scene);
        window.setResizable(false);
        window.setTitle("PsyMeeting - Login");
        window.show();
        centerWindow();
        /*final long startNanoTime = System.nanoTime();
        final int[] i = {0};
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                i[0]++;
                if(i[0] % 60 == 0) {
                    //System.out.println(stage.getX() + " : " + stage.getY());
                    //System.out.println("window" + window.getWidth() + " : " + window.getHeight());
                    //assert home_scene != null;
                    //System.out.println(home_scene.getWidth() + " : " + home_scene.getHeight());
                }
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;


            }
        }.start();*/

    }


    public static void sceneMapping(String origin_scene, String target_scene){

        try {
            if(!origin_scene.equals("login_scene") && !target_scene.equals("login_scene"))
                getCurrentResolution(getSceneByName(origin_scene));
            else // reset current resolution
                current_resolution = app_default_resolution;

            // change scene
            switch (target_scene) {
                case "home_scene":
                    root_home = FXMLLoader.load(App.class.getResource("javafx/fxml/home.fxml")); // launch initialize methods
                    home_scene = new Scene((Parent) root_home, current_resolution[0], current_resolution[1]);
                    window.setScene(home_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Home");
                    break;
                case "consultation_scene":
                    root_consultation = FXMLLoader.load(App.class.getResource("javafx/fxml/consultation.fxml")); // launch initialize methods
                    consultation_scene = new Scene((Parent) root_consultation, current_resolution[0], current_resolution[1]);
                    window.setScene(consultation_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Consultation");
                    break;
                case "login_scene":

                    root_login = FXMLLoader.load(App.class.getResource("javafx/fxml/login.fxml")); // launch initialize methods
                    login_scene = new Scene((Parent) root_login, login_resolution[0], login_resolution[1]);
                    window.close();
                    // open a fresh window
                    window.setMaximized(false);
                    window.setScene(login_scene);
                    window.setResizable(false);
                    window.setTitle("PsyMeeting - Login");
                    window.show();
                    centerWindow();
                    break;
            }
            scenes.put(origin_scene, false);
            scenes.put(target_scene, true);

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static Scene getSceneByName(String name){
        switch (name){
            case "login_scene":
                return login_scene;
            case "home_scene":
                return home_scene;
            case "consultation_scene":
                return consultation_scene;
            default:
                return null;
        }
    }

    // get current scene
    protected static String getCurrentScene(){
        for (HashMap.Entry hm: scenes.entrySet()
             ) {
            if(hm.getValue().equals(true))
                return hm.getKey().toString();
        }
        return null;
    }

    // get X and Y of window
    protected static void getCurrentResolution(Scene s){
        if(s != null) {
            current_resolution[0] = s.getWidth();
            current_resolution[1] = s.getHeight();
        }
    }

    protected static int getScreenMonitorIndex(){
        int screen_index = 0;
        for (int i = 0; i < Screen.getScreens().size(); i++) {
            if ((window.getX() + window.getWidth() / 2) > Screen.getScreens().get(i).getVisualBounds().getMinX())
                screen_index = i;
        }
        return screen_index;
    }

    protected static void centerWindow(){
        // center window
        Rectangle2D primScreenBounds = Screen.getScreens().get(getScreenMonitorIndex()).getVisualBounds();
        window.setX(primScreenBounds.getMinX() + (primScreenBounds.getWidth() - window.getWidth()) / 2);
        window.setY((primScreenBounds.getHeight() - window.getHeight()) / 2);
    }


}
