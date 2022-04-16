package ch.skyfy.game.logic;

import ch.skyfy.game.CellsMergedEvent;
import ch.skyfy.game.NewNumberEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    public final int[][] terrain;

    private final CellsMergedEvent cellsMergedEvent;
    private final NewNumberEvent newNumberEvent;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UPWARD,
        DOWNWARD
    }

    {
        terrain = new int[4][4];
    }

    public Game(CellsMergedEvent cellsMergedEvent, NewNumberEvent newNumberEvent) {
        this.cellsMergedEvent = cellsMergedEvent;
        this.newNumberEvent = newNumberEvent;

        populateTerrain();

        terrain[0][2] = 4;
        terrain[1][2] = 4;
//        terrain[2][2] = 4;
//        terrain[3][2] = 4;
    }


    public void move(Direction direction) {
        mergeCells3(direction);
        generateNewNumber();
    }

    public void mergeCells(Direction direction) {

        for (byte i = 0; i < 4; i++) {

            var alreadyMultiply2 = new ArrayList<Integer>(2); // cell can merge (multiply) only one time

            for (int m = 0; m <= 4; m++) { // Perform several iterations to make sure all cells have merged

                if (direction == Direction.UP || direction == Direction.LEFT) {
                    for (var j = 0; j < 4; j++) {
                        if (j + 1 < 4) {
                            var cell = direction == Direction.UP ? terrain[j][i] : terrain[i][j];
                            var nextCell = direction == Direction.UP ? terrain[j + 1][i] : terrain[i][j + 1];

                            if (cell != nextCell && cell != 0 && nextCell != 0) continue;
                            if (cell == 0 && nextCell == 0) continue;

                            if (cell == nextCell) {

                                if (alreadyMultiply2.contains(j) || alreadyMultiply2.contains(j + 1)) continue;
                                alreadyMultiply2.add(j);

                                if (direction == Direction.UP) {
                                    cellsMergedEvent.merged(j + 1, i, j, i, cell * 2, direction, i); //UP
                                    terrain[j][i] = cell * 2;
                                    terrain[j + 1][i] = 0;
                                } else {
                                    cellsMergedEvent.merged(i, j + 1, i, j, cell * 2, direction, i); // LEFT
                                    terrain[i][j] = cell * 2;
                                    terrain[i][j + 1] = 0;
                                }
                            } else {
                                if (cell == 0) {
                                    if (direction == Direction.UP) {
                                        cellsMergedEvent.merged(j + 1, i, j, i, nextCell, direction, i); //UP
                                        terrain[j][i] = nextCell;
                                        terrain[j + 1][i] = 0;
                                    } else {
                                        cellsMergedEvent.merged(i, j + 1, i, j, nextCell, direction, i); // LEFT
                                        terrain[i][j] = nextCell;
                                        terrain[i][j + 1] = 0;
                                    }
                                }
                            }

                        }
                    }
                } else {
                    for (var j = (4 - 1); j >= 0; j--) {
                        if (j - 1 >= 0) {

                            var cell = direction == Direction.DOWN ? terrain[j][i] : terrain[i][j];
                            var previousCell = direction == Direction.DOWN ? terrain[j - 1][i] : terrain[i][j - 1];

                            if (cell != previousCell && cell != 0 && previousCell != 0) continue;
                            if (cell == 0 && previousCell == 0) continue;

                            if (cell == previousCell) {

                                if (alreadyMultiply2.contains(j) || alreadyMultiply2.contains(j - 1)) continue;
                                alreadyMultiply2.add(j);

                                if (direction == Direction.DOWN) {
                                    cellsMergedEvent.merged(j - 1, i, j, i, cell * 2, direction, i); //DOWN
                                    terrain[j][i] = cell * 2;
                                    terrain[j - 1][i] = 0;
                                } else if (direction == Direction.RIGHT) {
                                    cellsMergedEvent.merged(i, j - 1, i, j, cell * 2, direction, i); // RIGHT
                                    terrain[i][j] = cell * 2;
                                    terrain[i][j - 1] = 0;
                                }
                            } else {
                                if (cell == 0) {
                                    if (direction == Direction.DOWN) {
                                        cellsMergedEvent.merged(j - 1, i, j, i, previousCell, direction, i); //DOWN
                                        terrain[j][i] = previousCell;
                                        terrain[j - 1][i] = 0;
                                    } else if (direction == Direction.RIGHT) {
                                        cellsMergedEvent.merged(i, j - 1, i, j, previousCell, direction, i); // RIGHT
                                        terrain[i][j] = previousCell;
                                        terrain[i][j - 1] = 0;
                                    }
                                }
                            }

                        }
                    }
                }

            }
        }

    }

    public void mergeCells2(Direction direction) {

        for (byte i = 0; i < 4; i++) {

            var alreadyMultiply2 = new ArrayList<Integer>(2); // cell can merge (multiply) only one time

            for (int m = 0; m <= 4; m++) { // Perform several iterations to make sure all cells have merged

                if (direction == Direction.UP || direction == Direction.LEFT) {
                    for (var j = 0; j < 4; j++) {
                        if (j + 1 < 4) {
                            var cell = direction == Direction.UP ? terrain[j][i] : terrain[i][j];
                            var nextCell = direction == Direction.UP ? terrain[j + 1][i] : terrain[i][j + 1];

                            if (cell != nextCell && cell != 0 && nextCell != 0) continue;
                            if (cell == 0 && nextCell == 0) continue;
                            if (nextCell == 0) continue;

                            var classicMerge = cell != nextCell;

                            if (!classicMerge) {
                                // A cell can be multiplied only one time
                                if (alreadyMultiply2.contains(j) || alreadyMultiply2.contains(j + 1)) continue;
                                alreadyMultiply2.add(j);
                            }

                            var number = classicMerge ? nextCell : cell * 2;

                            if (direction == Direction.UP) {
                                cellsMergedEvent.merged(j + 1, i, j, i, number, direction, i); //UP
                                terrain[j][i] = number;
                                terrain[j + 1][i] = 0;
                            } else {
                                cellsMergedEvent.merged(i, j + 1, i, j, number, direction, i); // LEFT
                                terrain[i][j] = number;
                                terrain[i][j + 1] = 0;
                            }
                        }
                    }
                } else {
                    for (var j = (4 - 1); j >= 0; j--) {
                        if (j - 1 >= 0) {

                            var cell = direction == Direction.DOWN ? terrain[j][i] : terrain[i][j];
                            var previousCell = direction == Direction.DOWN ? terrain[j - 1][i] : terrain[i][j - 1];

                            if (cell != previousCell && cell != 0 && previousCell != 0) continue;
                            if (cell == 0 && previousCell == 0) continue;
                            if (previousCell == 0) continue;

                            var classicMerge = cell != previousCell;

                            if (!classicMerge) {
                                if (alreadyMultiply2.contains(j) || alreadyMultiply2.contains(j - 1)) continue;
                                alreadyMultiply2.add(j);
                            }

                            var number = classicMerge ? previousCell : cell * 2;

                            if (direction == Direction.DOWN) {
                                cellsMergedEvent.merged(j - 1, i, j, i, number, direction, i); //DOWN
                                terrain[j][i] = number;
                                terrain[j - 1][i] = 0;
                            } else if (direction == Direction.RIGHT) {
                                cellsMergedEvent.merged(i, j - 1, i, j, number, direction, i); // RIGHT
                                terrain[i][j] = number;
                                terrain[i][j - 1] = 0;
                            }

                        }
                    }
                }

            }
        }

    }

    public void mergeCells3(Direction direction) {
        for (byte i = 0; i < 4; i++) {

            var alreadyMultiply2 = new ArrayList<Integer>(2); // cell can merge (multiply) only one time

            for (int m = 0; m <= 4; m++) { // Perform several iterations to make sure all cells have merged

                var towards = (direction == Direction.UP || direction == Direction.LEFT) ? Direction.UPWARD : Direction.DOWNWARD;

                var oldJ = 0;
                for (var j = 0; j < 4; j = oldJ++) {

                    j = towards == Direction.DOWNWARD ? 4 - 1 - j : j;

                    var limit = towards == Direction.UPWARD ? (j + 1 < 4) : (j - 1 >= 0);

                    if (limit) {
                        var index = towards == Direction.UPWARD ? j + 1 : j - 1;
                        var cell = direction == Direction.UP || direction == Direction.DOWN ? terrain[j][i] : terrain[i][j];
                        var otherCell = direction == Direction.UP || direction == Direction.DOWN ? terrain[index][i] : terrain[i][index];

                        if (cell != otherCell && cell != 0 && otherCell != 0) continue;
                        if (cell == 0 && otherCell == 0) continue;
                        if (otherCell == 0) continue;

                        var classicMerge = cell != otherCell; // classicMerge is a merge without multiplying

                        if (!classicMerge) { // A cell can be multiplied only one time
                            if (alreadyMultiply2.contains(j) || alreadyMultiply2.contains(index)) continue;
                            alreadyMultiply2.add(j);
                        }

                        var number = classicMerge ? otherCell : cell * 2;

                        if (direction == Direction.UP || direction == Direction.DOWN) {
                            cellsMergedEvent.merged(index, i, j, i, number, direction, i); //UP
                            terrain[j][i] = number;
                            terrain[index][i] = 0;
                        } else {
                            cellsMergedEvent.merged(i, index, i, j, number, direction, i); // LEFT
                            terrain[i][j] = number;
                            terrain[i][index] = 0;
                        }
                    }
                }
            }
        }

    }

    public void generateNewNumber() {
        var indices = new ArrayList<int[]>();
        for (byte i = 0; i < terrain.length; i++)
            for (byte j = 0; j < terrain[i].length; j++)
                if (terrain[i][j] == 0)
                    indices.add(new int[]{i, j});

        var randomNumberIndexes = indices.get(ThreadLocalRandom.current().nextInt(indices.size()));
        var newNumber = ThreadLocalRandom.current().nextInt(0, 2) == 0 ? 2 : 4;
        newNumberEvent.newNumber(randomNumberIndexes[0], randomNumberIndexes[1], newNumber);
        terrain[randomNumberIndexes[0]][randomNumberIndexes[1]] = newNumber;
    }

    private void populateTerrain() {
        Arrays.stream(terrain).forEach(row -> Arrays.fill(row, 0));
    }

}
