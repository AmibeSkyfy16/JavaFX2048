package ch.skyfy.game.ui.utils;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

@SuppressWarnings("unused")
public class Utils {

    /**
     * Allows you to resize a text so that it fills its parent layout. Only test with a StackPane as parent layout.
     * For this to work, the following properties of the Label must be set
     *
     *         StackPane.setAlignment(label, Pos.CENTER);
     *         label.setContentDisplay(ContentDisplay.CENTER);
     *         label.setTextAlignment(TextAlignment.CENTER);
     *         label.setAlignment(Pos.CENTER);
     *
     *         label.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
     *         label.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
     *         label.setPrefSize(0, 0);
     *
     * @param newHeight the new height of the parent (StackPane)
     * @param label the label with the text to be resized
     */
    public static void resizeText(double newHeight, Label label) {
        if (newHeight == 0) return;

        var size = label.getFont().getSize();
        var margin = newHeight * (15d / 100d);

        var newText = new Text(label.getText());
        newText.setFont(Font.font(label.getFont().getFamily(), size));

        var decrease = newText.getLayoutBounds().getHeight() >= newHeight - margin;
        var oldSize = size;
        for (int i = 0; i < 10000; i++) {
            newText = new Text(label.getText());
            newText.setFont(Font.font(label.getFont().getFamily(), size));

            if (decrease) {
                size -= 1;
                if (newText.getLayoutBounds().getHeight() < newHeight - margin) {
                    label.setStyle("-fx-font-size: " + size);
                    break;
                }
            } else {
                if (newText.getLayoutBounds().getHeight() >= newHeight - margin) {
                    label.setStyle("-fx-font-size: " + oldSize);
                    break;
                }
                oldSize = size;
                size += 1;
            }
        }
    }

    /**
     * Allows you to resize a text so that it fills its parent layout. Only test with a StackPane as parent layout.
     * For this to work, the following properties of the Label must be set
     *
     *         StackPane.setAlignment(label, Pos.CENTER);
     *         label.setContentDisplay(ContentDisplay.CENTER);
     *         label.setTextAlignment(TextAlignment.CENTER);
     *         label.setAlignment(Pos.CENTER);
     *
     *         label.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
     *         label.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
     *         label.setPrefSize(0, 0);
     *
     * @param newHeight the new height of the parent (StackPane)
     * @param newWidth the new width of the parent (StackPane)
     * @param label the label with the text to be resized
     */
    public static void resizeText(double newHeight, double newWidth, Label label){
        var size = getCorrectSize(newHeight, newWidth, label);
        if(size == -1)return;
        label.setStyle("-fx-font-size: " + size);
    }

    public static double getCorrectSize(double newHeight, double newWidth, Label label){
        var sizeForHeight = getSizeForHeight(newHeight, label);
        var sizeForWidth = getSizeForWidth(newWidth, label);
        if(sizeForHeight == -1 || sizeForWidth == -1)return -1;
        return Math.min(sizeForHeight, sizeForWidth);
    }

    private static double getSizeForHeight(double newHeight, Label label){
        if (newHeight == 0) return -1;

        var size = label.getFont().getSize();
        var margin = newHeight * (25d / 100d);

        var newText = new Text(label.getText());
        newText.setFont(Font.font(label.getFont().getFamily(), size));

        var decrease = newText.getLayoutBounds().getHeight() >= newHeight - margin;
        var oldSize = size;
        for (int i = 0; i < 2000; i++) {
            newText = new Text(label.getText());
            newText.setFont(Font.font(label.getFont().getFamily(), size));

            if (decrease) {
                size -= 1;
                if (newText.getLayoutBounds().getHeight() < newHeight - margin) {
                    return size;
                }
            } else {
                if (newText.getLayoutBounds().getHeight() >= newHeight - margin) {
                    return oldSize;
                }
                oldSize = size;
                size += 1;
            }
        }
        return -1;
    }

    private static double getSizeForWidth(double newWidth, Label label){
        if (newWidth == 0) return -1;

        var size = label.getFont().getSize();
        var margin = newWidth * (25d / 100d);

        var newText = new Text(label.getText());
        newText.setFont(Font.font(label.getFont().getFamily(), size));

        var decrease = newText.getLayoutBounds().getWidth() >= newWidth - margin;
        var oldSize = size;
        for (int i = 0; i < 2000; i++) {
            newText = new Text(label.getText());
            newText.setFont(Font.font(label.getFont().getFamily(), size));

            if (decrease) {
                size -= 1;
                if (newText.getLayoutBounds().getWidth() < newWidth - margin) {
                    return size;
                }
            } else {
                if (newText.getLayoutBounds().getWidth() >= newWidth - margin) {
                    return oldSize;
                }
                oldSize = size;
                size += 1;
            }
        }
        return -1;
    }

}
