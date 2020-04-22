/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.controller;


import com.bdd.psymeeting.model.Consultation;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;

public class HomeController implements Initializable {

    public ScrollPane scrollPane;
    public GridPane gridColumnNames;
    private int rows = 20;
    private int columns = 7;


    public GridPane scheduleGrid;
    Service<ArrayList<Consultation>> loadConsultationsWeek = new Service<ArrayList<Consultation>>() {
        @Override
        protected Task<ArrayList<Consultation>> createTask() {
            return new Task<ArrayList<Consultation>>() {
                @Override
                protected ArrayList<Consultation> call() throws Exception {
                    return Consultation.getConsultationWeek();
                }
            };
        }
    };


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        buildGripPane();

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


    protected void fillGridPane(ArrayList<Consultation> consultations) {

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < columns; i++) {
                StackPane cell = new StackPane();
            }
        }

    }

    private void buildGripPane() {

        scheduleGrid.getStyleClass().add("scheduleGrid");

        // build columns
        for (int i = 0; i < columns; i++) {

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.prefWidthProperty().bind(scrollPane.widthProperty().divide(columns));
            //columnConstraints.setPercentWidth(20);
            scheduleGrid.getColumnConstraints().add(columnConstraints);
            gridColumnNames.getColumnConstraints().add(columnConstraints);
        }

        // build rows
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(30);
            //rowConstraints.prefHeightProperty().bind(scrollPane.heightProperty().divide(rows));
            scheduleGrid.getRowConstraints().add(rowConstraints);
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

    protected enum Days {
        Lundi, Mardi, Mercredi, Jeudi, Vendredi, Samedi
    }

    private class TimeSlots {
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

        public void display() {
            for (int i = 0; i < 20; i++) {
                System.out.println(timeSlots[i]);
            }
        }
    }

}

