package dev.sebm.noasis.guicomponent;

import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

public class FlipCard extends StackPane {
    private Node front;
    private Node back;

    @Getter
    private boolean frontShown;

    public FlipCard(Node front, Node back) {
        this.front = front;
        this.back = back;
        this.frontShown = true;

        getChildren().addAll(back, front);
        showFront();/* w   ww   .  d e  m    o2  s  .   c o   m */
        setOnMouseClicked(_ -> {
            flip();
        });
    }

    public void showFront() {
        front.setVisible(true);
        back.setVisible(false);
        frontShown = true;
    }

    public void showBack() {
        front.setVisible(false);
        back.setVisible(true);
        frontShown = false;
    }

    public void flip() {
        // Set up the flip animation
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.2), this);

        if (isFrontShown()) {
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    // Print the current angle of the rectangle
                    System.out.println("Current angle: " + rotateProperty().doubleValue());
                    if (rotateProperty().doubleValue() >= 90) {
                        showBack();
                        setScaleX(-1);
                    }
                }
            };
            rotateTransition.setAxis(Rotate.Y_AXIS);
            rotateTransition.setFromAngle(0);
            rotateTransition.setToAngle(180);
            rotateTransition.setOnFinished(e -> {
                timer.stop();
            });
            rotateTransition.play();
            timer.start();
        } else {
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    // Print the current angle of the rectangle
                    System.out.println("Current angle: " + rotateProperty().doubleValue());
                    if (rotateProperty().doubleValue() <= 90) {
                        showFront();
                        setScaleX(1);
                    }
                }
            };
            rotateTransition.setAxis(Rotate.Y_AXIS);
            rotateTransition.setFromAngle(180);
            rotateTransition.setToAngle(0);
            rotateTransition.setOnFinished(e -> {
                timer.stop();
            });
            rotateTransition.play();
            timer.start();
        }
    }
}
