package com.bdd.pj.application;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class TransitionEffect {

    public static void TranslateTransitionY(Pane object, int time, int translate_y) {
        object.setTranslateY(translate_y);
        TranslateTransition tt = new TranslateTransition(Duration.millis(time), object);
        tt.setByY(-translate_y);
        tt.setCycleCount(1);
        tt.setAutoReverse(true);
        tt.play();
    }

    public static void FadeTransition(Object object, int time, float min_opacity, float max_opacity) {
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(time));
        fade.setFromValue(min_opacity);
        fade.setToValue(max_opacity);
        fade.setNode((Node) object);
        fade.play();

    }
}
