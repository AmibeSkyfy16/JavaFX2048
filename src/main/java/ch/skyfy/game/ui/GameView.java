package ch.skyfy.game.ui;

import ch.skyfy.game.Main;
import ch.skyfy.game.logic.Game;
import ch.skyfy.game.ui.utils.FXMLUtils;
import com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.rotator.RotatorControl;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

public class GameView extends StackPane implements Initializable {

    @FXML
    private GridPane root_GridPane, game_GridPane;

    private final Game game;

    private final List<List<TranslateTransition>> animatedCellList = new ArrayList<>();

    public GameView() {
        game = new Game();
        game.cellsMergedEvent = this::buildTransition;
        game.newNumberEvent = (row, col, newNumber) -> {
            Objects.requireNonNull(getCellView(row, col)).number_Label.setText(String.valueOf(newNumber));
        };
        FXMLUtils.loadFXML(Main.class, "ui/fxml/Game.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildGameGridPane();
        registerEvents();
        update();
    }

    private void buildGameGridPane() {
        this.root_GridPane.setFocusTraversable(true);
        var percent = 100.0 / 4.0;
        for (int i = 0; i < game_GridPane.getColumnConstraints().size(); i++) {
            var columnConstraint = game_GridPane.getColumnConstraints().get(i);
            columnConstraint.setPercentWidth(percent);
            for (int i1 = 0; i1 < game_GridPane.getRowConstraints().size(); i1++) {
                var rowConstraint = game_GridPane.getRowConstraints().get(i1);
                rowConstraint.setPercentHeight(percent);
                var cell = new CellView();
                game_GridPane.add(cell, i, i1);
            }
        }
    }

    private void update() {
        for (byte row = 0; row < game.terrain.length; row++) {
            for (byte col = 0; col < game.terrain.length; col++) {
                for (var child : game_GridPane.getChildren()) {
                    if (child instanceof CellView cellView) {
                        if (GridPane.getRowIndex(child) == row && GridPane.getColumnIndex(child) == col) {
                            cellView.number_Label.setText(String.valueOf(game.terrain[row][col]));
                        }
                    }
                }
            }
        }
    }

    private void registerEvents() {
        this.setOnKeyPressed(event -> {

            animatedCellList.forEach(List::clear);
            animatedCellList.clear();

            switch (event.getCode()) {
                case DOWN -> game.move(Game.Direction.DOWN);
                case UP -> game.move(Game.Direction.UP);
                case RIGHT -> game.move(Game.Direction.RIGHT);
                case LEFT -> game.move(Game.Direction.LEFT);
            }
//            update();
            animateCells();
            RotatorControl r = new RotatorControl("text");
            game_GridPane.add(r, 0, 0);
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void buildTransition(int srcRow, int srcCol, int destRow, int destCol, int number, Game.Direction direction) {
        var sourceCellView = getCellView(srcRow, srcCol);
        var destCellView = getCellView(destRow, destCol);

        var translate = new TranslateTransition();
        translate.setDuration(Duration.millis(3000));

        if (direction == Game.Direction.DOWN) {
            translate.setByY(sourceCellView.getHeight());
        } else if (direction == Game.Direction.UP) {
            translate.setByY(-sourceCellView.getHeight());
        } else if (direction == Game.Direction.RIGHT) {
            translate.setByX(sourceCellView.getWidth());
        } else {
            translate.setByX(-sourceCellView.getWidth());
        }

        translate.setNode(sourceCellView);

        translate.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Animation.Status.STOPPED)
                Platform.runLater(() -> {
                    destCellView.number_Label.setText(String.valueOf(number));
                    // TODO delete sourceCellView
                    game_GridPane.getChildren().remove(sourceCellView);
                });
            else if (newValue == Animation.Status.RUNNING) {
                game_GridPane.add(new CellView(), srcCol, srcRow);
            }
        });


        // put transition in list, they will be executed later, when all of all col or row merged
        if (animatedCellList.size() == 0) {
            var translateTransitions = new ArrayList<TranslateTransition>();
            translateTransitions.add(translate);
            animatedCellList.add(translateTransitions);
            return;
        } else {
            if (animatedCellList.get(animatedCellList.size() - 1).size() == 3) {
                System.out.println("");
                var translateTransitions = new ArrayList<TranslateTransition>();
                translateTransitions.add(translate);
                animatedCellList.add(translateTransitions);
                return;
            }
        }
        for (List<TranslateTransition> translateTransitions : animatedCellList) {
            if (translateTransitions == null) translateTransitions = new ArrayList<>();
            if (translateTransitions.size() < 3) {
                translateTransitions.add(translate);
            }
        }

    }

    private void animateCells() {

        for (List<TranslateTransition> translateTransitions : animatedCellList) {
            new Thread(() -> {
                for (TranslateTransition translateTransition : translateTransitions) {
                    Semaphore semaphore = new Semaphore(0);
                    translateTransition.setOnFinished(event -> {
                        semaphore.release();
                    });
                    Platform.runLater(translateTransition::play);
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("unlocked");
                }
            }) {{
                setDaemon(true);
            }}.start();
        }


    }

    private CellView getCellView(int targetRow, int targetCol) {
        for (byte row = 0; row < game.terrain.length; row++) {
            for (byte col = 0; col < game.terrain.length; col++) {
                for (var child : game_GridPane.getChildren()) {
                    if (child instanceof CellView cellView) {
                        if (GridPane.getRowIndex(child) == targetRow && GridPane.getColumnIndex(child) == targetCol) {
                            return cellView;
                        }
                    }
                }
            }
        }
        return null;
    }

}