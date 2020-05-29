/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.controller.home;


import com.bdd.psymeeting.Main;
import com.bdd.psymeeting.controller.InitController;
import com.bdd.psymeeting.controller.consultations.ConsultationHistoric;
import com.bdd.psymeeting.model.Consultation;
import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomeController extends ConsultationHistoric implements Initializable, InitController {

    public ScrollPane scrollPane;
    public GridPane gridColumnNames;

    public Button previousPagination;
    public Button nextPagination;

    public Label weekLabel;

    private int indexWeek;

    public GridPane scheduleGrid;
    Service<ArrayList<Consultation>> loadConsultationsWeek = new Service<ArrayList<Consultation>>() {
        @Override
        protected Task<ArrayList<Consultation>> createTask() {
            return new Task<ArrayList<Consultation>>() {
                @Override
                protected ArrayList<Consultation> call() {
                    return Consultation.getConsultationWeek(indexWeek);
                }
            };
        }
    };


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        indexWeek = 0;

        initServices();

        buildGripPane();


    }

    @Override
    public void initServices() {
        loadConsultationsWeek.setOnSucceeded(event -> {
            System.out.println("Loading consultations of the week succeeded !");
            fillGridPane(loadConsultationsWeek.getValue());
            loadConsultationsWeek.reset();
        });
        loadConsultationsWeek.setOnFailed(event -> {
            System.out.println("Loading consultation of the week failed !");
            loadConsultationsWeek.reset();
        });

        if (loadConsultationsWeek.getState() == Task.State.READY)
            loadConsultationsWeek.start();
    }

    @Override
    public void initListeners() {

    }

    @Override
    protected void refresh() {
        System.out.println(scheduleGrid.getChildren().removeIf(node -> node.getClass().getName().contains("AnchorPane")));

        //scheduleGrid.getChildren().clear();
        //buildGripPane();
        if (loadConsultationsWeek.getState() == Task.State.READY)
            loadConsultationsWeek.start();
    }


    protected void fillGridPane(ArrayList<Consultation> consultations) {

        // update week label
        Calendar[] calendars = Main.getCalendarOfWeek(indexWeek);
        String[] dates = new String[2];
        dates[0] = new SimpleDateFormat("dd-MM-yyyy").format(calendars[0].getTime());
        dates[1] = new SimpleDateFormat("dd-MM-yyyy").format(calendars[1].getTime());
        weekLabel.getStyleClass().add("labelWeek");
        weekLabel.setText("Semaine du " + dates[0] + " au " + dates[1]);

        for (Consultation c : consultations
        ) {
            int day = c.getDate().get(Calendar.DAY_OF_WEEK) - 1;
            int hours = c.getDate().get(Calendar.HOUR_OF_DAY);
            int minutes = c.getDate().get(Calendar.MINUTE);
            int row = getRowTimeSlotIndex(hours, minutes);
            JFXButton consultation = new JFXButton("Consultation");
            AnchorPane cell = new AnchorPane();
            cell.getStyleClass().add("consultationCell");
            cell.getChildren().add(consultation);
            AnchorPane.setLeftAnchor(consultation, 0.0);
            AnchorPane.setRightAnchor(consultation, 0.0);
            AnchorPane.setTopAnchor(consultation, 0.0);
            AnchorPane.setBottomAnchor(consultation, 0.0);
            scheduleGrid.add(cell, day, row);

            //add event to button
            consultation.setOnAction(event -> loadConsultationInfo(c));
        }

    }

    private void buildGripPane() {

        scheduleGrid.getStyleClass().add("scheduleGrid");

        // build columns
        int columns = 7;
        for (int i = 0; i < columns; i++) {

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.prefWidthProperty().bind(scrollPane.widthProperty().divide(columns));
            scheduleGrid.getColumnConstraints().add(columnConstraints);
            gridColumnNames.getColumnConstraints().add(columnConstraints);
            columnConstraints.setFillWidth(true);
        }

        // build rows
        int rows = 20;
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(30);
            scheduleGrid.getRowConstraints().add(rowConstraints);
            rowConstraints.setFillHeight(true);
        }


        // setup column label
        for (int col = 0; col < columns; col++) {
            StackPane pane = new StackPane();
            Label labelColumn;

            if (col == 0) {
                labelColumn = new Label("Heures");
                pane.setStyle("-fx-background-color: #b0bec5;");
            } else {
                labelColumn = new Label(Days.values()[col - 1].toString());
                pane.setStyle("-fx-background-color: #56c8d8;");
            }
            labelColumn.setStyle("-fx-padding: 10 10 10 10");

            pane.getChildren().add(labelColumn);
            gridColumnNames.add(pane, col, 0);
        }


        // setup times slots rows
        TimeSlots slots = new TimeSlots();
        for (int row = 0; row < rows; row++) {
            StackPane cell = new StackPane();
            Label time = new Label(slots.getTimeSlots()[row]);
            cell.getChildren().add(time);
            GridPane.setFillWidth(cell, true);
            GridPane.setFillHeight(cell, true);
            scheduleGrid.add(cell, 0, row);
        }
    }

    private int getRowTimeSlotIndex(int hours, int minutes) {
        TimeSlots timeSlots = new TimeSlots();
        for (int i = 0; i < timeSlots.getTimeSlots().length; i++) {
            if (timeSlots.getTimeSlots()[i].equals(hours + ":" + (minutes == 0 ? "00" : minutes)))
                return i;
        }
        return 0;
    }

    public void pagination(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(previousPagination)) indexWeek--;
        else indexWeek++;
        // refresh home page
        refresh();

    }


    protected enum Days {
        Lundi, Mardi, Mercredi, Jeudi, Vendredi, Samedi
    }

    private static class TimeSlots {
        String[] timeSlots;

        private TimeSlots() {
            timeSlots = new String[20];
            int t = 7;
            for (int i = 0; i < 8; i++) {
                if (i % 2 == 0) t++;
                timeSlots[i] = t + ":" + (i % 2 == 0 ? "00" : "30");
            }
            t = 13;
            for (int i = 0; i < 12; i++) {
                if (i % 2 == 0) t++;
                timeSlots[i + 8] = t + ":" + (i % 2 == 0 ? "00" : "30");
            }
        }

        public String[] getTimeSlots() {
            return timeSlots;
        }

    }

}

