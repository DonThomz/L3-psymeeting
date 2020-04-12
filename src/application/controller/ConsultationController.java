package application.controller;

import application.App;
import com.jfoenix.controls.*;
import data.Consultation;
import data.Patient;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConsultationController implements Initializable {

    // Fields
    public JFXTextField searchField;
    public VBox box_consultations;
    public JFXComboBox<Label> filter;
    public StackPane stackPane;

    // Attributes
    private Map<Calendar, JFXButton> consultations_map;
    private int consultation_size;
    // --------------------
    //   Initialize method
    // --------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupFilterBox();
        setupBoxConsultations();

    }

    private void setupBoxConsultations() {
        consultations_map = new HashMap<>();
        box_consultations.setSpacing(20);
        consultation_size = Consultation.getLastPrimaryKeyId();
        for (int i = 1; i <= consultation_size; i++) {
            consultations_map.put(Consultation.getDateById(i), buildConsultationButton(i));
        }
        // sort in descending order
        consultations_map = new TreeMap<>(consultations_map).descendingMap();
        consultations_map.forEach((k,v)->box_consultations.getChildren().add(v));
    }

    private void setupFilterBox(){
        filter.getItems().add(new Label("plus récent"));
        filter.getItems().add(new Label("moins récent"));
        filter.setStyle("-fx-font-size: 14");
        filter.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.getText().equals("plus récent")){
                mostRecentFirst();
            }
            else{
                mostOldestFirst();
            }
        });
    }

    private JFXButton buildConsultationButton(int consultation_id){

        try {
            // init button
            JFXButton consultation_button = new JFXButton();
            // setting button
            consultation_button.setId("consultation-button-id-" + consultation_id);
            consultation_button.getStyleClass().add("consultation_cell");


            // Setup content
            VBox box = new VBox();

            Calendar date_consultation = Consultation.getDateById(consultation_id);
            assert date_consultation != null;
            String timeStamp = new SimpleDateFormat("EEEE dd MMMM, yyyy à hh:mm",
                    Locale.FRANCE).format(date_consultation.getTime());
            Label title = new Label("Consultation : "
                    + "\n\t"+timeStamp);

            box.getChildren().add(title);

            // get patient and add in a javaFX TEXT
            ResultSet tmp_result_patients = Patient.getPatientsByConsultationId(consultation_id);
            assert tmp_result_patients != null;
            Label patient_list = new Label();
            patient_list.getStyleClass().add("content_text");
            StringBuilder content = new StringBuilder();
            while(tmp_result_patients.next()) {
                content.append(" | ").append(tmp_result_patients.getString(1)).append(" ").append(tmp_result_patients.getString(2)).append(" \n");
            }
            patient_list.setText(String.valueOf(content));
            box.getChildren().add(patient_list);

            // add action on button
            consultation_button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    loadConsultationInfo(consultation_id, date_consultation, content);
                }
            });

            // add to the button
            consultation_button.setGraphic(box);


            return consultation_button;

        }catch (SQLException ex){
            System.out.println("SQL Error to get patients information");
            ex.printStackTrace();
        }
        return null;
    }

    // --------------------
    //  Action methods
    // --------------------

    private void mostRecentFirst(){
        // sort in ascending order
        consultations_map = new TreeMap<>(consultations_map).descendingMap();
        for (int i = 0; i < consultation_size; i++) {
            box_consultations.getChildren().remove(0);
        }
        consultations_map.forEach((k,v)->box_consultations.getChildren().add(v));
    }

    private void mostOldestFirst(){
        // sort in descending order
        consultations_map = new TreeMap<>(consultations_map);
        for (int i = 0; i < consultation_size; i++) {
            box_consultations.getChildren().remove(0);
        }
        consultations_map.forEach((k,v)->box_consultations.getChildren().add(v));
    }

    private void loadConsultationInfo(int consultation_id, Calendar date, StringBuilder patients_list){
        JFXDialogLayout content = new JFXDialogLayout();
        String title = new SimpleDateFormat("EEEE dd MMMM, yyyy à hh:mm",
                Locale.FRANCE).format(date.getTime());
        content.setHeading(new Label("Consultation du " + title));
        // show information
        content.setBody(new Text("Patients :\n"
                + patients_list
                + "\nInfo payement : \n "
        ));

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton done = new JFXButton("Fermer");
        JFXButton modify = new JFXButton("Modifier");
        done.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }
        });
        modify.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }
        });
        content.setActions(modify, done);

        dialog.show();
    }

    // --------------------
    //  Private methods
    // --------------------


}

