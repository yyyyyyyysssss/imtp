package org.imtp.desktop.util;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/2 16:05
 */
public class EffectUtilities {

    public static void fadeOnClick(final Node node, final EventHandler<ActionEvent> onFinished) {
        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                node.setMouseTransparent(true);
                FadeTransition fade = new FadeTransition(Duration.seconds(1.2), node);
                fade.setOnFinished(onFinished);
                fade.setFromValue(1);
                fade.setToValue(0);
                fade.play();
            }
        });
    }

    public static void addGlowOnHover(final Node node) {
        final Glow glow = new Glow();
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                node.setEffect(glow);
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                node.setEffect(null);
            }
        });
    }

    public static void makeDraggable(final Stage stage, final Node byNode) {
        final Delta dragDelta = new Delta();
        byNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                dragDelta.y = stage.getY() - mouseEvent.getScreenY();
            }
        });
        byNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                stage.setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });
    }

    private static class Delta {
        double x, y;
    }

}
