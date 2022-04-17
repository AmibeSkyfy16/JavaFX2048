package ch.skyfy.game.ui;

import ch.skyfy.game.ui.utils.FXMLUtils;
import ch.skyfy.game.ui.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class InnerCellView extends StackPane implements Initializable {

    @FXML
    public Label number_Label;

    public InnerCellView() {
        FXMLUtils.loadFXML(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Update the font of the text depending on the window size
        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getHeight() == 0 || newValue.getWidth() == 0) return; // Sometimes, when the app. is launching, height or width == 0, so we can just skip
            Utils.resizeText(newValue.getHeight(), newValue.getWidth(), number_Label);
        });

        // if a 512 by 512 merge for example, the cell will be 1024 and 1024 have one more character. So we have to update the size
        number_Label.textProperty().addListener((observable, oldValue, newValue) -> Utils.resizeText(InnerCellView.this.getHeight(), InnerCellView.this.getWidth(), number_Label));
    }

}
