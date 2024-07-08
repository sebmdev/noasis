package dev.sebm.noasis.guicomponent;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
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
}
