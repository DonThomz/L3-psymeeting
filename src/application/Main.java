package application;


import data.OracleDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    //---------------------------------

    public static OracleDB database;
    public static boolean connection_active;

    //---------------------------------

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Object root = FXMLLoader.load(getClass().getResource("javafx/fxml/app.fxml"));

        //stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene((Parent) root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

}
