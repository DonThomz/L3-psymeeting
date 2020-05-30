/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting;


import com.bdd.psymeeting.model.Patient;
import com.bdd.psymeeting.model.User;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Main extends Application {

    public static final double[] login_resolution = {420, 580};
    public static final double[] app_default_resolution = {1280, 920};
    public static final int time_transition = 10;
    //
    //---------------------------------
    //---------------------------------
    //         Database
    public static OracleDB database;
    public static boolean connection_active;
    public static ArrayList<Patient> patients;
    //---------------------------------
    //         Resolutions
    public static Stage window;
    public static double[] current_resolution;
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
    /**
     * Displays an alert modal before quitting the app.
     */
    private final EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
        Alert closeConfirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Êtes-vous sûr de vouloir quitter ?"
        );
        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(
                ButtonType.OK
        );
        exitButton.setText("Quitter");
        closeConfirmation.setHeaderText("Confirmer la fermeture");
        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
        closeConfirmation.initOwner(window);

        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        }
    };

    public static void main(String[] args) {

        launch(args);

        // Close database if exit program
        if (connection_active)
            database.closeDatabase();
    }

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
                    root_home = FXMLLoader.load(Main.class.getResource("views/home/home.fxml")); // launch initialize methods
                    home_scene = new Scene((Parent) root_home, current_resolution[0], current_resolution[1]);
                    window.setScene(home_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Home");
                    break;
                case "consultation_scene":
                    root_consultation = FXMLLoader.load(Main.class.getResource("views/consultations/consultation.fxml")); // launch initialize methods
                    consultation_scene = new Scene((Parent) root_consultation, current_resolution[0], current_resolution[1]);
                    window.setScene(consultation_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Consultation");
                    break;
                case "patients_scene":
                    root_patients = FXMLLoader.load(Main.class.getResource("views/patients/patients.fxml")); // launch initialize methods
                    patients_scene = new Scene((Parent) root_patients, current_resolution[0], current_resolution[1]);
                    window.setScene(patients_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Patients");
                    break;
                case "add_consultation_scene":
                    root_add_consultation = FXMLLoader.load(Main.class.getResource("views/consultations/add_consultation.fxml")); // launch initialize methods
                    add_consultation_scene = new Scene((Parent) root_add_consultation, current_resolution[0], current_resolution[1]);
                    window.setScene(add_consultation_scene);
                    window.setResizable(true);
                    window.setTitle("PsyMeeting - Consultations");
                    break;
                case "login_scene":
                    root_login = FXMLLoader.load(Main.class.getResource("views/login/login.fxml")); // launch initialize methods
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
    //---------------------------------
    //         App methods
    //---------------------------------

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

    public static String[] getDatesOfWeek(int indexWeek) {
        String[] dates = new String[2];
        // TODO: Consider time zones, calendars etc
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Monday 8h00 AM
        date1.set(Calendar.HOUR_OF_DAY, 8);
        date1.set(Calendar.MINUTE, 0);
        date1.set(Calendar.SECOND, 0);

        date2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // Saturday 8h00 PM
        date2.set(Calendar.HOUR, 20);
        date2.set(Calendar.MINUTE, 0);
        date2.set(Calendar.SECOND, 0);

        // Update date with the correct week index
        date1.add(Calendar.WEEK_OF_YEAR, indexWeek);
        date2.add(Calendar.WEEK_OF_YEAR, indexWeek);


        dates[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date1.getTime());
        dates[1] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date2.getTime());
        return dates;
    }

    public static Calendar[] getCalendarOfWeek(int indexWeek) {
        // TODO: Consider time zones, calendars etc
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Monday 8h00 AM
        date1.set(Calendar.HOUR_OF_DAY, 8);
        date1.set(Calendar.MINUTE, 0);
        date1.set(Calendar.SECOND, 0);

        date2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // Saturday 8h00 PM
        date2.set(Calendar.HOUR, 8);
        date2.set(Calendar.MINUTE, 0);
        date2.set(Calendar.SECOND, 0);

        // Update date with the correct week index
        date1.add(Calendar.WEEK_OF_YEAR, indexWeek);
        date2.add(Calendar.WEEK_OF_YEAR, indexWeek);

        return new Calendar[]{date1, date2};
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
        root_login = FXMLLoader.load(getClass().getResource("views/login/login.fxml"));


        // Load login scene
        login_scene = new Scene((Parent) root_login, login_resolution[0], login_resolution[1]);

        // Add a closing button (as requested)
        stage.setOnCloseRequest(confirmCloseEventHandler);

        // Init window
        window = stage;
        window.setScene(login_scene);
        window.setResizable(false);
        window.setTitle("PsyMeeting - Login");
        window.show();
    }


}
