package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.sql.*;

import database.OracleDB;

public class Main {

    public static void main(String[] args){
        //launch(args);

        OracleDB database = new OracleDB();

        database.connectionDatabase("thomas", "oracle");
        database.closeDatabase();

    }


    /*@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        StackPane root = new StackPane();
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }*/
}
