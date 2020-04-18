package com.bdd.pj.application;


import com.bdd.pj.data.OracleDB;
import com.bdd.pj.data.Patient;
import com.bdd.pj.data.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

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
    public static final double[] login_resolution = {420, 580};
    public static final double[] app_default_resolution = {1200, 800};
    public static final int time_transition = 10;

    //---------------------------------
    //         Scenes
    public static Scene login_scene;
    public static Scene home_scene;
    public static Scene consultation_scene;
    public static Scene patients_scene;
    public static Scene add_consultation_scene;

    public static HashMap<String, Boolean> scenes;
    public static int scenes_size;
    //
    //---------------------------------

    //---------------------------------
    //         FXML Objects
    public static Object root_login;
    public static Object root_home;
    public static Object root_consultation;
    public static Object root_patients;
    public static Object root_add_consultation;
    //
    //---------------------------------


    //---------------------------------
    //         Applications
    public static User current_user;
    //
    //---------------------------------

    public static void main(String[] args) {

        launch(args);

        // Close database if exit program
        if (connection_active)
            database.closeDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Init static variables
        scenes_size = 2;
        current_resolution = app_default_resolution;

        scenes = new HashMap<>();
        scenes.put("login_scene", true);
        scenes.put("home_scene", false);


        // Init FXML login file
        root_login = FXMLLoader.load(getClass().getResource("fxml/login.fxml"));


        // Load login scene
        login_scene = new Scene((Parent) root_login, login_resolution[0], login_resolution[1]);

        // Init window
        window = stage;
        window.setScene(login_scene);
        window.setResizable(false);
        window.setTitle("PsyMeeting - Login");
        window.show();
    }


    //---------------------------------
    //         App methods
    //---------------------------------

    // Mapping between scenes
    public static void sceneMapping(String origin_scene, String target_scene) {

        try {
            if (!origin_scene.equals("login_scene") && !target_scene.equals("login_scene"))
                getCurrentResolution(getSceneByName(origin_scene));
            else {
                // Reset current resolution
                current_resolution = app_default_resolution;
            }
            // Update HashMap
            scenes.put(origin_scene, false);
            scenes.put(target_scene, true);

            // Change scene
            switch (target_scene) {
                case "home_scene":
                    root_home = FXMLLoader.load(Main.class.getResource("fxml/home.fxml")); // launch initialize methods
                    home_scene = new Scene((Parent) root_home, current_resolution[0], current_resolution[1]);
                    window.setScene(home_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Home");
                    break;
                case "consultation_scene":
                    root_consultation = FXMLLoader.load(Main.class.getResource("fxml/consultation.fxml")); // launch initialize methods
                    consultation_scene = new Scene((Parent) root_consultation, current_resolution[0], current_resolution[1]);
                    window.setScene(consultation_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Consultation");
                    break;
                case "patients_scene":
                    root_patients = FXMLLoader.load(Main.class.getResource("fxml/patients.fxml")); // launch initialize methods
                    patients_scene = new Scene((Parent) root_patients, current_resolution[0], current_resolution[1]);
                    window.setScene(patients_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Patients");
                    break;
                case "add_consultation_scene":
                    root_add_consultation = FXMLLoader.load(Main.class.getResource("fxml/add_consultation.fxml")); // launch initialize methods
                    add_consultation_scene = new Scene((Parent) root_add_consultation, current_resolution[0], current_resolution[1]);
                    window.setScene(add_consultation_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Consultations");
                    break;
                case "login_scene":
                    root_login = FXMLLoader.load(Main.class.getResource("fxml/login.fxml")); // launch initialize methods
                    login_scene = new Scene((Parent) root_login, login_resolution[0], login_resolution[1]);
                    window.close();
                    // Open a fresh window
                    window.setMaximized(false);
                    window.setScene(login_scene);
                    window.setResizable(false);
                    window.setTitle("PsyMeeting - Login");
                    window.show();
                    // window.centerOnScreen(); // Not sure this is a great UX move?
                    break;
            }
            window.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Scene getSceneByName(String name) {
        switch (name) {
            case "login_scene":
                return login_scene;
            case "home_scene":
                return home_scene;
            case "consultation_scene":
                return consultation_scene;
            case "patients_scene":
                return patients_scene;
            case "add_consultation_scene":
                return add_consultation_scene;
            default:
                return null;
        }
    }

    /**
     * Get current scene
     */
    public static String getCurrentScene() {
        for (Map.Entry hm : scenes.entrySet()
        ) {
            if (hm.getValue().equals(true))
                return hm.getKey().toString();
        }
        return null;
    }

    /**
     * Get X and Y of window
     */
    public static void getCurrentResolution(Scene s) {
        if (s != null) {
            current_resolution[0] = s.getWidth();
            current_resolution[1] = s.getHeight();
        }
    }

    /**
     * Return monitor index
     */
    public static int getScreenMonitorIndex() {
        int screen_index = 0;
        for (int i = 0; i < Screen.getScreens().size(); i++) {
            if ((window.getX() + window.getWidth() / 2) > Screen.getScreens().get(i).getVisualBounds().getMinX())
                screen_index = i;
        }
        return screen_index;
    }

    /**
     * Reset HashMap
     */
    public static void resetHashMap() {
        scenes.put("login_scene", true);
        scenes.put(getCurrentScene(), false);
    }

    /**
     * Convert Timestamp to Calendar
     */
    public static Calendar Timestamp2Calendar(Timestamp t) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(t);
        return cal;
    }

    /**
     * Convert Date to Calendar
     */
    public static Calendar Date2Calendar(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal;
    }

    /**
     * Convert LocalDate to format string "yyyy-MM-dd HH:mm:ss"
     */
    public static String LocalDateFormat(LocalDate date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Timestamp.valueOf(date.atTime(LocalTime.MIDNIGHT)));
    }

    public static String[] getDatesOfDay(LocalDate date) {
        String[] dates = new String[2];
        dates[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Timestamp.valueOf(date.atTime(LocalTime.MIDNIGHT)));
        dates[1] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Timestamp.valueOf(date.atTime(LocalTime.MAX)));
        return dates;
    }


}
