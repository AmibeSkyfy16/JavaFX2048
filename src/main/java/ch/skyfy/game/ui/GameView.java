package ch.skyfy.game.ui;

import ch.skyfy.game.logic.Game;
import ch.skyfy.game.ui.utils.FXMLUtils;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameView extends StackPane implements Initializable {

    @FXML
    private GridPane root_GridPane, game_GridPane;

    private final Game game;

    public final Map<Integer, SequentialTransition> animations = new HashMap<>();

    private final AtomicBoolean animationFinished = new AtomicBoolean(true);

    public GameView() {
        game = new Game();
        game.cellsMergedEvent = this::buildMergeTransition;
        game.newNumberEvent = this::buildNewNumberTransition;
        FXMLUtils.loadFXML(this);
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

        // Create col and row
        for (int i = 0; i < 4; i++) {
            var c = new ColumnConstraints();
            c.setPercentWidth(percent);
            c.setMinWidth(USE_COMPUTED_SIZE);
            c.setMaxWidth(USE_COMPUTED_SIZE);
            c.setPrefWidth(USE_COMPUTED_SIZE);
            c.setHalignment(HPos.CENTER);
            c.setHgrow(Priority.SOMETIMES);
            game_GridPane.getColumnConstraints().add(c);
        }
        for (int i1 = 0; i1 < 4; i1++) {
            var r = new RowConstraints();
            r.setPercentHeight(percent);
            r.setMinHeight(USE_COMPUTED_SIZE);
            r.setMaxHeight(USE_COMPUTED_SIZE);
            r.setPrefHeight(USE_COMPUTED_SIZE);
            r.setValignment(VPos.CENTER);
            r.setVgrow(Priority.SOMETIMES);
            game_GridPane.getRowConstraints().add(r);
        }

        // Adding cell
        for (int i = 0; i < game_GridPane.getColumnConstraints().size(); i++)
            for (int i1 = 0; i1 < game_GridPane.getRowConstraints().size(); i1++)
                game_GridPane.add(new CellView(), i, i1);
    }

    private void update() {
        for (byte row = 0; row < game.terrain.length; row++) {
            for (byte col = 0; col < game.terrain.length; col++) {
                for (var child : game_GridPane.getChildren()) {
                    if (child instanceof CellView cellView) {
                        if (GridPane.getRowIndex(child) == row && GridPane.getColumnIndex(child) == col) {
                            cellView.innerCellView.number_Label.setText(String.valueOf(game.terrain[row][col]));
                        }
                    }
                }
            }
        }
    }

    private void registerEvents() {
        this.setOnKeyPressed(event -> {
            if (!animationFinished.getAndSet(true)) return;
            animations.clear();
            switch (event.getCode()) {
                case DOWN -> game.move(Game.Direction.DOWN);
                case UP -> game.move(Game.Direction.UP);
                case RIGHT -> game.move(Game.Direction.RIGHT);
                case LEFT -> game.move(Game.Direction.LEFT);
            }
//            update();
            playTransitionTest();
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void buildMergeTransition(int srcRow, int srcCol, int destRow, int destCol, int number, Game.Direction direction, int id) {
        var sourceCellView = getCellView(srcRow, srcCol);
        var destCellView = getCellView(destRow, destCol);

        var innerCellView = new InnerCellView();
//        innerCellView.getStylesheets().clear();
//        innerCellView.setBackground(new Background(new BackgroundFill(Color.valueOf("#B88400"), new CornerRadii(20), new Insets(0))));

        innerCellView.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        innerCellView.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        innerCellView.setPrefHeight(sourceCellView.innerCellView.getHeight());
        innerCellView.setPrefWidth(sourceCellView.innerCellView.getWidth());
        innerCellView.number_Label.setText(sourceCellView.innerCellView.number_Label.getText());

        var translate = new TranslateTransition();
        translate.setDuration(Duration.millis(150));

        if (direction == Game.Direction.DOWN) {
            translate.setByY(sourceCellView.getHeight());
            innerCellView.setTranslateY(-sourceCellView.getHeight());
        } else if (direction == Game.Direction.UP) {
            translate.setByY(-sourceCellView.getHeight());
            innerCellView.setTranslateY(sourceCellView.getHeight());
        } else if (direction == Game.Direction.RIGHT) {
            translate.setByX(sourceCellView.getWidth());
            innerCellView.setTranslateX(-sourceCellView.getWidth());
        } else {
            translate.setByX(-sourceCellView.getWidth());
            innerCellView.setTranslateX(sourceCellView.getWidth());
        }

        translate.setNode(innerCellView);

        translate.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Animation.Status.STOPPED)
                Platform.runLater(() -> {
                    destCellView.innerCellView.number_Label.setText(String.valueOf(number));
                    game_GridPane.getChildren().remove(innerCellView);
                });
            else if (newValue == Animation.Status.RUNNING) {
                Platform.runLater(() -> {
                    game_GridPane.add(innerCellView, destCol, destRow);
                    innerCellView.number_Label.setText(String.valueOf(sourceCellView.innerCellView.number_Label.getText()));
                    sourceCellView.innerCellView.number_Label.setText("0");
                    innerCellView.setViewOrder(-1);
                });
            }
        });

        addTransition(id, translate);
    }

    private void buildNewNumberTransition(int row, int col, int newNumber) {
        var text = Objects.requireNonNull(getCellView(row, col)).innerCellView.number_Label;
        var tr = new TextSizeTransition(text, 0, 40, Duration.millis(100));
        tr.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Animation.Status.RUNNING) {
                text.setText(String.valueOf(newNumber));
            }
        });
        addTransition(-10, tr);
    }

    private Transition generateNewNumberTransition = null;

    private void addTransition(int id, Transition transition) {
        // Adding the newNumberAnimation
        // This animation will be the last animation
        if (id == -10) {
            generateNewNumberTransition = transition;
            generateNewNumberTransition.setOnFinished(event -> animationFinished.set(true));
            return;
        }
        animations.compute(id, (integer, sequentialTransition) -> {
            if (sequentialTransition == null) sequentialTransition = new SequentialTransition();
            sequentialTransition.getChildren().add(transition);
            return sequentialTransition;
        });
    }

    private void playTransitionTest() {
        var p = new ParallelTransition();
        p.getChildren().addAll(animations.values());
        p.setOnFinished(event -> generateNewNumberTransition.play());
        p.play();
    }

//    private void playTransition() {
//        for (var entry : map.entrySet()) {
//            var t = new Thread(() -> {
//                for (Transition transition : entry.getValue()) {
//                    var semaphore = new Semaphore(0);
//                    transition.setOnFinished(event -> semaphore.release());
//                    Platform.runLater(transition::play);
//                    try {
//                        semaphore.acquire();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }) {{
//                setDaemon(true);
//            }};
//            t.start();
//            animationThread.add(t);
//        }
//
//    }

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