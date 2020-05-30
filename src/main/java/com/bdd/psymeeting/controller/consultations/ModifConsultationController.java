/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

package com.bdd.psymeeting.controller.consultations;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ModifConsultationController implements Initializable {

    protected static String commentaryAreaStatic;
    protected static String postureAreaStatic;
    protected static String keywordAreaStatic;
    protected static float price;
    protected static String paymentMode;
    // attributes
    private final List<String> paymentChoices = Arrays.asList("Carte bancaire", "Espèce", "Chèque");
    // FXML attributes
    public JFXComboBox<String> paymentComboBox;
    public JFXTextArea commentaryArea;
    public JFXTextArea postureArea;
    public JFXTextArea keywordArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paymentComboBox.setItems(FXCollections.observableArrayList(paymentChoices));

        // setup commentary
        StringBuilder commentary = new StringBuilder();
        ConsultationHistoric.consultationModified.getFeedback().getCommentary().forEach(line -> {
            commentary.append(line).append("\n");
        });
        commentaryArea.setText(commentary.toString());


        StringBuilder posture = new StringBuilder();
        ConsultationHistoric.consultationModified.getFeedback().getPosture().forEach(line -> {
            posture.append(line).append("\n");
        });
        postureArea.setText(posture.toString());
        postureAreaStatic = postureArea.getText();

        StringBuilder keyword = new StringBuilder();
        ConsultationHistoric.consultationModified.getFeedback().getKeyword().forEach(line -> {
            keyword.append(line).append("\n");
        });
        keywordArea.setText(keyword.toString());
        keywordAreaStatic = keywordArea.getText();

        initListeners();

    }

    public void initListeners() {

        commentaryArea.focusedProperty().addListener(event -> commentaryAreaStatic = commentaryArea.getText());

        postureArea.focusedProperty().addListener(event -> postureAreaStatic = postureArea.getText());

        keywordArea.focusedProperty().addListener(event -> keywordAreaStatic = keywordArea.getText());

    }
}
