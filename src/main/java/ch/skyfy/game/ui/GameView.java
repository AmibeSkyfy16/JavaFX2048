package ch.skyfy.game.ui;

import ch.skyfy.game.logic.Game;
import ch.skyfy.game.ui.utils.FXMLUtils;
import ch.skyfy.game.ui.utils.Utils;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameView extends StackPane implements Initializable {

    @FXML
    private GridPane root_GridPane, game_GridPane;

    private final Game game;

    /**
     * Each line will have a SequentialTransition transition
     * If the key is -10, It's for the latest animation (the newNumberAnimation)
     */
    public final Map<Integer, SequentialTransition> animations = new HashMap<>();

    private Transition generateNewNumberTransition = null;

    private final AtomicBoolean animationFinished = new AtomicBoolean(true);

    public GameView() {
        game = new Game(this::buildMergeTransition, this::buildNewNumberTransition);
        FXMLUtils.loadFXML(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildGameGridPane();
        registerEvents();
        registerOnStageRendered(this::startTheGame);
    }

    private void registerOnStageRendered(Runnable runnable) {
        this.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) {
                newValue.windowProperty().addListener((observable1, oldValue1, newValue1) -> {
                    if (oldValue1 == null && newValue1 != null) {
                        newValue1.setOnShown(event -> runnable.run());
                    }
                });
            }
        });
    }

    private void buildGameGridPane() {
        this.root_GridPane.setFocusTraversable(true);

        var percent = 100.0 / 4.0;

        // Adding row and col
        for (int i = 0; i < 4; i++) {
            game_GridPane.getColumnConstraints().add(new ColumnConstraints() {{
                setPercentWidth(percent);
                setHalignment(HPos.CENTER);
            }});
            game_GridPane.getRowConstraints().add(new RowConstraints() {{
                setPercentHeight(percent);
                setValignment(VPos.CENTER);
            }});
        }

        // Adding cell
        for (byte i = 0; i < game_GridPane.getColumnConstraints().size(); i++)
            for (byte j = 0; j < game_GridPane.getRowConstraints().size(); j++)
                game_GridPane.add(new CellView(), i, j);

        // Make our app responsive
        root_GridPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getWidth() == 0 || newValue.getHeight() == 0) return;

            // Getting rowConstraint and columnConstraint where our game_GridPane is placed
            var indexes = getRowAndColConstraints(game_GridPane, root_GridPane);
            if (indexes == null) return;

            var columnConstraints = indexes.getKey();
            var rowConstraints = indexes.getValue();

            // We calculate the new size of the cell in which our uniformGridPane is placed
            var cellWidth = newValue.getWidth() * (columnConstraints.getPercentWidth() / 100d);
            var cellHeight = newValue.getHeight() * (rowConstraints.getPercentHeight() / 100d);

            // To make our uniformGridPane square, we take the smallest size and apply it as width and height
            var size = Math.min(cellWidth, cellHeight);
            game_GridPane.setPrefSize(size, size);
        });

    }

    private void registerEvents() {
        this.setOnKeyPressed(event -> {
            if (!animationFinished.get()) return;
            animationFinished.set(false);
            animations.clear();
            switch (event.getCode()) {
                case DOWN -> game.move(Game.Direction.DOWN);
                case UP -> game.move(Game.Direction.UP);
                case RIGHT -> game.move(Game.Direction.RIGHT);
                case LEFT -> game.move(Game.Direction.LEFT);
            }
            playTransition();
        });
    }

    /**
     * Once all the graphic components have been built and placed with their default value, we start the game
     * TODO create a button called startTheGame to start the game
     */
    private void startTheGame() {
        game.generateNewNumber();
        update();
        playTransition();
    }

    private void update() {
        for (byte row = 0; row < game.terrain.length; row++) {
            for (byte col = 0; col < game.terrain.length; col++) {
                for (var child : game_GridPane.getChildren()) {
                    if (child instanceof CellView cellView) {
                        if (GridPane.getRowIndex(child) == row && GridPane.getColumnIndex(child) == col) {
                            // We add 3 spaces, so the animation of the new generated cell have the correct font size
                            cellView.innerCellView.number_Label.setText(String.valueOf(game.terrain[row][col] == 0 ? "   " : game.terrain[row][col]));
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void buildMergeTransition(int srcRow, int srcCol, int destRow, int destCol, int number, Game.Direction direction, int id) {
        var sourceCellView = getCellView(srcRow, srcCol);
        var destCellView = getCellView(destRow, destCol);

        var innerCellView = new InnerCellView();
        innerCellView.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        innerCellView.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        innerCellView.prefHeightProperty().bind(sourceCellView.innerCellView.heightProperty());
        innerCellView.prefWidthProperty().bind(sourceCellView.innerCellView.widthProperty());
        innerCellView.number_Label.setText(sourceCellView.innerCellView.number_Label.getText());

        var translate = new TranslateTransition(Duration.millis(800), innerCellView);
        translate.setRate(3);
        translate.setInterpolator(Interpolator.TANGENT(Duration.millis(200), 9));

        if (direction == Game.Direction.DOWN) {
            translate.setFromY(-sourceCellView.getHeight());
            translate.setToY(0);
        } else if (direction == Game.Direction.UP) {
            translate.setFromY(sourceCellView.getHeight());
            translate.setToY(0);
        } else if (direction == Game.Direction.RIGHT) {
            translate.setFromX(-sourceCellView.getWidth());
            translate.setToX(0);
        } else {
            translate.setFromX(sourceCellView.getWidth());
            translate.setToX(0);
        }

        translate.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Animation.Status.STOPPED)
                Platform.runLater(() -> {
                    destCellView.innerCellView.number_Label.setText(String.valueOf(number));
                    game_GridPane.getChildren().remove(innerCellView);
                });
            else if (newValue == Animation.Status.RUNNING) {
                Platform.runLater(() -> {
                    game_GridPane.add(innerCellView, destCol, destRow);
                    innerCellView.setViewOrder(-1);
                    innerCellView.number_Label.setText(String.valueOf(sourceCellView.innerCellView.number_Label.getText()));
                    sourceCellView.innerCellView.number_Label.setText("   ");
                });
            }
        });

        addTransition(id, translate);
    }

    private void buildNewNumberTransition(int row, int col, int newNumber) {
        var innerCellView = Objects.requireNonNull(getCellView(row, col), "This shouldn't happened").innerCellView;
        var number_Label = innerCellView.number_Label;

        var size = Utils.getCorrectSize(innerCellView.getHeight(), innerCellView.getWidth(), number_Label);
        var textSizeTransition = new TextSizeTransition(number_Label, 0, (int) size, Duration.millis(600));

        // make a binding between TextSizeTransition and endFontSize, so if the user resize the windows while an animation is playing
        // the end size updated and then correct
        innerCellView.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            var newSize = Utils.getCorrectSize(innerCellView.getHeight(), innerCellView.getWidth(), number_Label);
            textSizeTransition.setEnd((int) newSize);
        });

        var rotateTransition = new RotateTransition(Duration.millis(700), number_Label);
        rotateTransition.setByAngle(360);

        var parallelTransition = new ParallelTransition(rotateTransition, textSizeTransition);
        parallelTransition.setOnFinished(event -> animationFinished.set(true));
        parallelTransition.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Animation.Status.RUNNING) {
                number_Label.setText(String.valueOf(newNumber));
            } else if (newValue == Animation.Status.STOPPED)
                generateNewNumberTransition = null;
        });

        generateNewNumberTransition = parallelTransition;
    }

    private void addTransition(int id, Transition transition) {
        animations.compute(id, (integer, sequentialTransition) -> {
            if (sequentialTransition == null) sequentialTransition = new SequentialTransition();
            sequentialTransition.getChildren().add(transition);
            return sequentialTransition;
        });
    }

    private void playTransition() {
        // The first time animations are empty, there is just one animation to be played (generateNewNumberTransition)
        if (animations.values().isEmpty()) {
            if (generateNewNumberTransition != null)
                generateNewNumberTransition.play();
            else
                animationFinished.set(true);
            return;
        }
        var p = new ParallelTransition(animations.values().toArray(Animation[]::new));
        p.setOnFinished(event -> generateNewNumberTransition.play());
        p.play();
    }

    private @Nullable CellView getCellView(int targetRow, int targetCol) {
        for (var child : game_GridPane.getChildren())
            if (child instanceof CellView cellView)
                if (GridPane.getRowIndex(child) == targetRow && GridPane.getColumnIndex(child) == targetCol)
                    return cellView;
        return null;
    }

    private @Nullable Pair<ColumnConstraints, RowConstraints> getRowAndColConstraints(Node node, GridPane gridPane) {
        for (byte i = 0; i < gridPane.getColumnConstraints().size(); i++)
            for (byte j = 0; j < gridPane.getRowConstraints().size(); j++)
                for (Node ignored : gridPane.getChildren())
                    if (GridPane.getRowIndex(node) == j && GridPane.getColumnIndex(node) == i) {
                        return new Pair<>(gridPane.getColumnConstraints().get(i), gridPane.getRowConstraints().get(j));
                    }
        return null;
    }

}