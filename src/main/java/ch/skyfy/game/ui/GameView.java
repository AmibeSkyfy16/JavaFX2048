package ch.skyfy.game.ui;

import ch.skyfy.game.Main;
import ch.skyfy.game.logic.Game;
import ch.skyfy.game.ui.utils.FXMLUtils;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class GameView extends StackPane implements Initializable {

    @FXML
    private GridPane root_GridPane, game_GridPane;

    private final Game game;

    private final List<List<Transition>> animatedCellList = new ArrayList<>();

    public static final Map<Integer, List<Transition>> map = new HashMap<>();

    public enum EventType{
        MERGED,
        NEW_NUMBER
    }

    public GameView() {
        game = new Game();
        game.cellsMergedEvent = this::buildMergeTransition;
        game.newNumberEvent = this::buildNewNumberTransition;
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
            map.clear();
            switch (event.getCode()) {
                case DOWN -> game.move(Game.Direction.DOWN);
                case UP -> game.move(Game.Direction.UP);
                case RIGHT -> game.move(Game.Direction.RIGHT);
                case LEFT -> game.move(Game.Direction.LEFT);
            }
//            update();
            playTransition();
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void buildMergeTransition(int srcRow, int srcCol, int destRow, int destCol, int number, Game.Direction direction, int id) {
        var sourceCellView = getCellView(srcRow, srcCol);
        var destCellView = getCellView(destRow, destCol);

        var animatedCellView = new CellView();
        animatedCellView.number_Label.setText(sourceCellView.number_Label.getText());
        animatedCellView.setViewOrder(1);
        animatedCellView.setBackground(new Background(new BackgroundFill(Color.valueOf("#B88400"), new CornerRadii(0), new Insets(0))));
        game_GridPane.add(animatedCellView, destCol, destRow);

        var translate = new TranslateTransition();
        translate.setDuration(Duration.millis(1000));

        if (direction == Game.Direction.DOWN) {
            translate.setByY(sourceCellView.getHeight());
            animatedCellView.setTranslateY(-sourceCellView.getHeight());
        } else if (direction == Game.Direction.UP) {
            translate.setByY(-sourceCellView.getHeight());
            animatedCellView.setTranslateY(sourceCellView.getHeight());
        } else if (direction == Game.Direction.RIGHT) {
            translate.setByX(sourceCellView.getWidth());
            animatedCellView.setTranslateX(-sourceCellView.getWidth());
        } else {
            translate.setByX(-sourceCellView.getWidth());
            animatedCellView.setTranslateX(sourceCellView.getWidth());
        }

        translate.setNode(animatedCellView);

        translate.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Animation.Status.STOPPED)
                Platform.runLater(() -> {
                    destCellView.number_Label.setText(String.valueOf(number));
                    game_GridPane.getChildren().remove(animatedCellView);
                });
            else if (newValue == Animation.Status.RUNNING) {
                animatedCellView.number_Label.setText(String.valueOf(sourceCellView.number_Label.getText()));
                sourceCellView.number_Label.setText("0");
                animatedCellView.setViewOrder(-1);
            }
        });

        addTransition(id, translate);
    }

    private void buildNewNumberTransition(int row, int col, int newNumber){
        var text = Objects.requireNonNull(getCellView(row, col)).number_Label;
        var tr = new TextSizeTransition(text, 0, 40, Duration.millis(600));
        tr.statusProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == Animation.Status.RUNNING){
                text.setText(String.valueOf(newNumber));
            }
        });
        addTransition(-10, tr);
    }

    private void addTransition(int id, Transition transition){
        // Adding the newNumberAnimation
        // This animation will be the last animation
        if(id == -10){
            var greatestList = map.entrySet().stream().sorted(Comparator.comparing(integerListEntry -> integerListEntry.getValue().size(), Comparator.reverseOrder())).findFirst();
            greatestList.ifPresent(integerListEntry -> integerListEntry.getValue().add(transition));
            return;
        }
        map.compute(id, (integer, translateTransitions) -> {
            if(translateTransitions == null)translateTransitions = new ArrayList<>();
            translateTransitions.add(transition);
            return translateTransitions;
        });
    }

    private void playTransition() {
        for (var entry : map.entrySet()) {
            new Thread(() -> {
                for (Transition transition : entry.getValue()) {
                    Semaphore semaphore = new Semaphore(0);
                    transition.setOnFinished(event -> {
                        semaphore.release();
                    });
                    Platform.runLater(transition::play);
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