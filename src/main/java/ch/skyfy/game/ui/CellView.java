package ch.skyfy.game.ui;

import ch.skyfy.game.ui.utils.FXMLUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class CellView extends StackPane implements Initializable {

    @FXML
    public InnerCellView innerCellView;

    public CellView() {
        FXMLUtils.loadFXML(CellView.this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
