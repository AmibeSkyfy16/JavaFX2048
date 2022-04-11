package ch.skyfy.game.ui;

import ch.skyfy.game.logic.Game;
import ch.skyfy.game.ui.utils.FXMLUtils;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameView extends StackPane implements Initializable {

    @FXML
    private GridPane root_GridPane, game_GridPane;

    private final Game game;

    public static final Map<Integer, List<Transition>> map = new HashMap<>();
    public static final Map<Integer, SequentialTransition> mapTest = new HashMap<>();

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
        for (int i = 0; i < game_GridPane.getColumnConstraints().size(); i++) {
            var columnConstraint = game_GridPane.getColumnConstraints().get(i);
            columnConstraint.setPercentWidth(percent);
            columnConstraint.setHalignment(HPos.CENTER);
            for (int i1 = 0; i1 < game_GridPane.getRowConstraints().size(); i1++) {
                var rowConstraint = game_GridPane.getRowConstraints().get(i1);
                rowConstraint.setPercentHeight(percent);
                rowConstraint.setValignment(VPos.CENTER);
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
                            cellView.innerCellView.number_Label.setText(String.valueOf(game.terrain[row][col]));
                        }
                    }
                }
            }
        }
    }

    private void registerEvents() {
        this.setOnKeyPressed(event -> {
//            if (!animationFinished()) return;
//            map.clear();
            if(!animationFinished.get()){
                System.out.println("NOT FINISHED");
                return;
            }
            animationFinished.set(false);
            mapTest.clear();
//            animationThread.clear();
            switch (event.getCode()) {
                case DOWN -> game.move(Game.Direction.DOWN);
                case UP -> game.move(Game.Direction.UP);
                case RIGHT -> game.move(Game.Direction.RIGHT);
                case LEFT -> game.move(Game.Direction.LEFT);
            }
//            update();
//            playTransition();
            playTransitionTest();
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void buildMergeTransition(int srcRow, int srcCol, int destRow, int destCol, int number, Game.Direction direction, int id) {
        var sourceCellView = getCellView(srcRow, srcCol);
        var destCellView = getCellView(destRow, destCol);


        System.out.println("sourceView WIDTH " + sourceCellView.innerCellView.getWidth());
        System.out.println("sourceView HEIGHT " + sourceCellView.innerCellView.getHeight());

        var innerCellView = new InnerCellView();
        System.out.println("innerCellView WIDTH " + innerCellView.getWidth());
        System.out.println("innerCellView HEIGHT " + innerCellView.getHeight());
//        innerCellView.getStylesheets().clear();
//        innerCellView.setBackground(new Background(new BackgroundFill(Color.valueOf("#B88400"), new CornerRadii(20), new Insets(0))));

        innerCellView.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        innerCellView.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        innerCellView.setPrefHeight(sourceCellView.innerCellView.getHeight());
        innerCellView.setPrefWidth(sourceCellView.innerCellView.getWidth());
        innerCellView.number_Label.setText(sourceCellView.innerCellView.number_Label.getText());


        var translate = new TranslateTransition();
        translate.setDuration(Duration.millis(200));

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
//            var greatestList = map.entrySet().stream().sorted(Comparator.comparing(integerListEntry -> integerListEntry.getValue().size(), Comparator.reverseOrder())).findFirst();
            var greatestList = map.entrySet().stream().max(Comparator.comparing(integerListEntry -> integerListEntry.getValue().size()));
            greatestList.ifPresent(integerListEntry -> integerListEntry.getValue().add(transition));

            generateNewNumberTransition = transition;
            generateNewNumberTransition.setOnFinished(event -> animationFinished.set(true));

            return;
        }
        map.compute(id, (integer, translateTransitions) -> {
            if (translateTransitions == null) translateTransitions = new ArrayList<>();
            translateTransitions.add(transition);
            return translateTransitions;
        });

        mapTest.compute(id, (integer, sequentialTransition) -> {
            if(sequentialTransition == null)sequentialTransition = new SequentialTransition();
            sequentialTransition.getChildren().add(transition);
            return sequentialTransition;
        });
    }

    private final List<Thread> animationThread = new ArrayList<>();

    private boolean animationFinished() {
        for (var thread : animationThread) {
            if (thread.isAlive()) return false;
        }
        return true;
    }

    private void playTransitionTest(){
        var p = new ParallelTransition();
        p.getChildren().addAll(mapTest.values());
        p.setOnFinished(event -> generateNewNumberTransition.play());
        p.play();
    }

    private void playTransition() {
        for (var entry : map.entrySet()) {
            var t = new Thread(() -> {
                for (Transition transition : entry.getValue()) {
                    var semaphore = new Semaphore(0);
                    transition.setOnFinished(event -> semaphore.release());
                    Platform.runLater(transition::play);
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }) {{
                setDaemon(true);
            }};
            t.start();
            animationThread.add(t);
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