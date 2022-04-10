package ch.skyfy.game.ui;

import ch.skyfy.game.Main;
import ch.skyfy.game.ui.utils.FXMLUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class CellView extends StackPane implements Initializable {

    @FXML
    public Label number_Label;

    private static int increaseColor = 10;

    {
        increaseColor+=10;
        if(increaseColor <= 240)increaseColor = 20;
    }

    public CellView() {
        FXMLUtils.loadFXML(Main.class, "ui/fxml/cell.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        this.setBackground(new Background(new BackgroundFill(Color.rgb(increaseColor, 190, increaseColor), new CornerRadii(0), new Insets(10))));
        this.setBackground(new Background(new BackgroundFill(Color.valueOf("#FFCB47"), new CornerRadii(0), new Insets(0))));
    }
}
