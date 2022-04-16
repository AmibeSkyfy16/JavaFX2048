package ch.skyfy.game.ui;

import ch.skyfy.game.ui.utils.FXMLUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import com.sun.javafx.scene.control.skin.Utils;
import javafx.scene.text.TextBoundsType;

import java.net.URL;
import java.util.ResourceBundle;

public class InnerCellView extends StackPane implements Initializable {

    @FXML
    public Label number_Label;

    public InnerCellView() {
        FXMLUtils.loadFXML(this);
    }

    static int count = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        StackPane.setAlignment(number_Label, Pos.CENTER);
        number_Label.setTextAlignment(TextAlignment.CENTER);
        number_Label.setAlignment(Pos.CENTER);


        number_Label.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        number_Label.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        number_Label.setPrefSize(0, 0);

        final double[] font = {40};
        var error = 5;

        var marginPercent = 10; // 10 percent


//        this.heightProperty().addListener((observable, oldValue, newValue) -> {
//
//            System.out.println("container height: " + newValue.doubleValue());
//            System.out.println("Label height: " + number_Label.boundsInParentProperty().get().getHeight());
//
//            var text1 = new Text(number_Label.getText());
//            text1.setFont(Font.font(number_Label.getFont().getFamily(), number_Label.getFont().getSize()));
//            var diff = number_Label.boundsInParentProperty().get().getHeight() - text1.boundsInParentProperty().get().getHeight();
//
//            var size = number_Label.getFont().getSize();
//
//            var newText = new Text(number_Label.getText());
//            newText.setFont(Font.font(number_Label.getFont().getFamily(), size));
//
//            while (newText.boundsInParentProperty().get().getHeight() >= number_Label.boundsInParentProperty().get().getHeight() + 10){
//                newText = new Text(number_Label.getText());
//                newText.setFont(Font.font(number_Label.getFont().getFamily(), size--));
//            }
//
//            number_Label.setFont(Font.font(number_Label.getFont().getFamily(), newText.getFont().getSize()));
//
//            System.out.println("GOOD SIZE SHOULD BE : " + newText.getFont().getSize());
//
//
////            System.out.println("text height: " + text1.boundsInParentProperty().get().getHeight());
//            System.out.println("text height: " + text1.layoutBoundsProperty().get().getHeight());
////
//            System.out.println("\n\n count : " + count++);
//        });

        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getHeight() == 0 || newValue.getWidth() == 0) return;

            System.out.println("New size is : " + number_Label.getFont().getSize());
            System.out.println("Label height: " + number_Label.boundsInParentProperty().get().getHeight());
            System.out.println("parent height: " + newValue.getHeight());

            var size = number_Label.getFont().getSize();
            var margin = newValue.getHeight() * (15d / 100d);

            var newText = new Text(number_Label.getText());
            newText.setFont(Font.font(number_Label.getFont().getFamily(), size));

            var decrease = newText.getLayoutBounds().getHeight() >= newValue.getHeight() - margin;
            var oldSize = size;
            for (int i = 0; i < 10000; i++) {
                newText = new Text(number_Label.getText());
                newText.setFont(Font.font(number_Label.getFont().getFamily(), size));

                if (decrease) {
                    size -= 1;
                    if (newText.getLayoutBounds().getHeight() < newValue.getHeight() - margin) {
                        number_Label.setStyle("-fx-font-size: " + size);
                        System.out.println("\tNew size is : " + size);
                        System.out.println("\tText height : " + newText.getLayoutBounds().getHeight());
                        System.out.println("\tLabel height: " + number_Label.boundsInParentProperty().get().getHeight());
                        System.out.println("\tparent height: " + newValue.getHeight());
                        break;
                    }
                }else{
                    if (newText.getLayoutBounds().getHeight() >= newValue.getHeight() - margin) {
                        number_Label.setStyle("-fx-font-size: " + oldSize);
                        System.out.println("\tNew size is : " + oldSize);
                        System.out.println("\tText height : " + newText.getLayoutBounds().getHeight());
                        System.out.println("\tLabel height: " + number_Label.boundsInParentProperty().get().getHeight());
                        System.out.println("\tparent height: " + newValue.getHeight());
                        break;
                    }
                    oldSize = size;
                    size += 1;
                }


            }

            System.out.println("\n");
        });

//        // Text will grow if the container size grown
//        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue.getHeight() == 0 || newValue.getWidth() == 0)return;
//
//            if(newValue.getHeight() < oldValue.getHeight())return;
//
//            var fivePercentHeight = newValue.getHeight() * 0.2;
//
//            var size = number_Label.getFont().getSize();
//            var oldSize = size;
//            do {
//
//                if(number_Label.getText().isEmpty()){
//                    System.out.println();
//                }
//
//                var text = new Text(number_Label.getText());
//                text.setFont(Font.font(number_Label.getFont().getFamily(), size));
//
//                var diff = number_Label.getHeight() - text.getLayoutBounds().getHeight();
//                diff = 0;
//
//                if(text.getLayoutBounds().getHeight() + diff >= newValue.getHeight() - fivePercentHeight){
//                    number_Label.setStyle("-fx-font-size: " + oldSize);
//                    break;
//                }else{
//                    oldSize = size;
//                }
//
//                size += 1;
//            }while (true);
//        });
//
//
//        // Text will decrease if the container size decreased
//        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue.getHeight() == 0 || newValue.getWidth() == 0)return;
//
//            if(newValue.getHeight() >= oldValue.getHeight())return;
//
//            var fivePercentHeight = newValue.getHeight() * 0.2;
//
//            var size = number_Label.getFont().getSize();
//            do {
//
//                var text = new Text(number_Label.getText());
//                text.setFont(Font.font(number_Label.getFont().getFamily(), size));
//
//                var diff = number_Label.getHeight() - text.getLayoutBounds().getHeight();
//                diff = 0;
//
//                if(text.getLayoutBounds().getHeight() + diff <= newValue.getHeight() - fivePercentHeight){
//                    number_Label.setStyle("-fx-font-size: " + size);
//                    break;
//                }
//
//                size -= 1;
//            }while (true);
//        });

//        number_Label.heightProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue.doubleValue() == 0)return;
////            if(newValue.doubleValue() >= oldValue.doubleValue())return;
//
//            var fivePercentHeight = this.getHeight() * 0.2;
//            var size = number_Label.getFont().getSize();
//            var oldSize = size;
//            do{
//
//                var text = new Text(number_Label.getText());
//                text.setFont(Font.font(number_Label.getFont().getFamily(), size));
//
//                var diff = newValue.doubleValue() - text.getLayoutBounds().getHeight();
//
//                if(newValue.doubleValue() >= this.getHeight() - fivePercentHeight){
////                    System.out.println("problème");
//                    number_Label.setStyle("-fx-font-size: " + oldSize);
//                    break;
//                }else{
//                    oldSize =  size;
//                }
//
//                size += 1;
//
//            }while (true);
//        });

//        number_Label.heightProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue.doubleValue() == 0)return;
////            if(newValue.doubleValue() >= oldValue.doubleValue())return;
//
//            var fivePercentHeight = this.getHeight() * 0.2;
//            var size = number_Label.getFont().getSize();
//            do{
//
//                var text = new Text(number_Label.getText());
//                text.setFont(Font.font(number_Label.getFont().getFamily(), size));
//
//                var diff = newValue.doubleValue() - text.getLayoutBounds().getHeight();
//
//                if(newValue.doubleValue() >= this.getHeight() - fivePercentHeight){
////                    System.out.println("problème");
//                    number_Label.setStyle("-fx-font-size: " + size);
//                }else{
//                    break;
//                }
//
//                size -= 1;
//
//            }while (true);
//        });


//
//        number_Label.heightProperty().addListener(new ChangeListener<Number>() {
//            //The changed(...) method is called every time a change in the height is detected
//            @Override
//            public void changed(ObservableValue<? extends Number> value, Number number, Number t1) {
//                double tentativeFont = font[0] * Math.sqrt(InnerCellView.this.getHeight()/ number_Label.getHeight());
//                if (tentativeFont < font[0] *(100-error)/100 || tentativeFont > font[0] *(100+error)/100) {
//                    font[0] = tentativeFont;
//                    number_Label.setStyle("-fx-font-size: " + font[0]);
//                }
//            }
//        });

//        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
//            number_Label.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
//            number_Label.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
//
//            if(newValue.getHeight() == 0 || newValue.getWidth() == 0)return;
//
//            var labelW = newValue.getWidth() * 0.95; // 80 percent of container width
//            var labelH = newValue.getHeight() * 0.95; // 80 percent of container width
//            System.out.println("w " + labelW);
//            number_Label.setPrefSize(labelW, labelH);
//        });

    }

}
