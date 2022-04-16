package ch.skyfy.game.ui;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.control.Labeled;
import javafx.util.Duration;

/**
 * this code comes from https://stackoverflow.com/questions/51013522/how-to-animate-font-size-of-text-node-in-javafx
 */
public class TextSizeTransition extends Transition {

    private final Labeled UIcontrol;
    private final int start, end;

    public TextSizeTransition(Labeled UIcontrol, int start, int end, Duration duration) {
        this.UIcontrol = UIcontrol;
        this.start = start;
        this.end = end - start;
        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }

    @Override
    protected void interpolate(double frac) {
        int size = (int) ((end * frac) + start);
        if(size<=end) {
//            UIcontrol.setFont(Font.font(size));
            UIcontrol.setStyle("-fx-font-size: " + size);
        }
    }
}