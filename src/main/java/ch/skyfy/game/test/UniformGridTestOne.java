package ch.skyfy.game.test;

import ch.skyfy.game.ui.utils.FXMLUtils;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Another way to make an UniformGridPane (see UniformGridTestTwo.java)
 */
public class UniformGridTestOne extends StackPane implements Initializable {

    public UniformGridTestOne() {
        FXMLUtils.loadFXML("ui/fxml/test/UniformGridTestOne.fxml",this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildUI();
    }

    private void buildUI(){
        var gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-pane");
        gridPane.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        gridPane.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);

        // create 3 cols and 3 rows
        // Our gridPane will contain 9 cells in total.
        var sizePercent = 100d / 3d;
        for(byte i = 0; i < 3; i++){
            var row = new RowConstraints(){{
                setPercentHeight(sizePercent);
            }};
            var col = new ColumnConstraints(){{
                setPercentWidth(sizePercent);
            }};
            gridPane.getRowConstraints().add(row);
            gridPane.getColumnConstraints().add(col);
        }

        // Adding cell to our gridPane
        var colored = 10;
        for(byte i = 0; i < 3; i++){
            for(byte j = 0; j < 3; j++){
                var cell = new StackPane();
                cell.setPadding(new Insets(10));
                cell.setBackground(new Background(new BackgroundFill(Color.rgb(40 + colored, 10 + (colored / 2), (int) (120d + (colored / 3)), 0.9), new CornerRadii(50), cell.getPadding())));
                gridPane.add(cell,i, j);
                colored+=15;
            }
        }

        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getHeight() == 0 || newValue.getWidth() == 0) return;
            double size = Math.min(newValue.getWidth(), newValue.getHeight());
            gridPane.setPrefSize(size, size);
        });

        this.getChildren().add(gridPane);
    }

}
