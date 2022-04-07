package ch.skyfy.game.ui;

import ch.skyfy.game.Main;
import ch.skyfy.game.logic.Game;
import ch.skyfy.game.ui.utils.FXMLUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameView extends StackPane implements Initializable {

    @FXML
    private GridPane root_GridPane, game_GridPane;

    private final Game game;

    ObservableList<ObservableList<Byte>> matrix = FXCollections.observableArrayList();

    public GameView() {
        game = new Game();
        FXMLUtils.loadFXML(Main.class, "ui/fxml/Game.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildGameGridPane();
        registerEvents();
        update();
    }

    private void buildGameGridPane(){
        this.root_GridPane.setFocusTraversable(true);

        var percent = 100.0 / 4.0;
        for (int i = 0; i < game_GridPane.getColumnConstraints().size(); i++) {
            var columnConstraint = game_GridPane.getColumnConstraints().get(i);
            columnConstraint.setPercentWidth(percent);
            for (int i1 = 0; i1 < game_GridPane.getRowConstraints().size(); i1++) {
                var rowConstraint = game_GridPane.getRowConstraints().get(i1);
                rowConstraint.setPercentHeight(percent);
                var cell = new CellView();

//                var rowArray = FXCollections.<Byte>observableArrayList();
//                for(int rowI = 0; rowI < 4; rowI++){
//                    rowArray.add(rowI, game.terrain[rowI][0]);
//                }
//                rowArray.addListener((ListChangeListener<? super Byte>) c -> {
//                    System.out.println("ds");
//                });

//                for (byte row = 0; row < 4; row++) {
//                    final ObservableList<Byte> columnArray = FXCollections.observableArrayList();
//                    matrix.add(row, columnArray);
//                    for (byte column = 0; column < 4; column++) {
//                        columnArray.add(game.terrain[row][column]);
//                    }
//                }

//                ObjectProperty<Byte> objectProperty = new SimpleObjectProperty<>(game.terrain[i][i1]);
//                cell.number_Label.textProperty().bind();
                game_GridPane.add(cell, i, i1);
            }
        }
    }

    private void update(){
        for (int row = 0; row < game.terrain.length; row++) {
            for (int col = 0; col < game.terrain.length; col++) {
                for (var child : game_GridPane.getChildren()) {
                    if(child instanceof CellView cellView){
                        if(GridPane.getRowIndex(child) == row && GridPane.getColumnIndex(child) == col){
                            cellView.number_Label.setText(String.valueOf(game.terrain[row][col]));
                        }
                    }
                }
            }
        }
    }

    private void registerEvents(){
        this.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case DOWN -> game.move(Game.Direction.DOWN);
                case UP -> game.move(Game.Direction.UP);
                case RIGHT -> game.move(Game.Direction.RIGHT);
                case LEFT -> game.move(Game.Direction.LEFT);
            }
            update();
            var c = game.terrain;
            System.out.println("");
        });
    }

    private void move(){

    }

}