package ch.skyfy.game.logic;

import ch.skyfy.game.CellsMergedEvent;
import ch.skyfy.game.NewNumberEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game {

    public final int[][] terrain = new int[4][4];

    public CellsMergedEvent cellsMergedEvent;
    public NewNumberEvent newNumberEvent;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public Game() {
        populateTerrain();

        // work with contain(i)
//        terrain[0][0] = 4;
//        terrain[0][1] = 8;
//        terrain[0][2] = 8;
//        terrain[0][3] = 16;



//        terrain[0][2] = 64;
//        terrain[0][3] = 64;

        terrain[0][2] = 4;
        terrain[1][2] = 4;
        terrain[2][2] = 4;
        terrain[3][2] = 4;

//        terrain[3][0] = 4;
//        terrain[3][1] = 4;
//        generateNewNumber();
    }

    public void move(Direction direction) {
        var terrainCopy = Arrays.stream(terrain).map(int[]::clone).toArray(value -> terrain.clone());

        for (byte i = 0; i < terrain.length; i++) {
            var cells = new int[terrain[0].length];
            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                System.arraycopy(terrain[i], 0, cells, 0, terrain.length);
                cells = mergeCells(cells, direction, i);
                for (byte j = 0; j < cells.length; j++)
                    terrain[i][j] = cells[j];
            } else {
                for (byte j = 0; j < cells.length; j++)
                    cells[j] = terrain[j][i];
                cells = mergeCells(cells, direction, i);
                for (byte j = 0; j < cells.length; j++)
                    terrain[j][i] = cells[j];
            }
        }
        generateNewNumberIfRequired(terrainCopy);
    }

    public int[] mergeCells(int[] cells, Direction direction, int colOrRow) {
        if (Arrays.stream(cells).allMatch(s -> s == 0)) return cells;
        var alreadyMultiply2 = new ArrayList<Integer>(2);


        for (int j = 0; j <= 4; j++) {

            if (direction == Direction.UP || direction == Direction.LEFT) {
                for (var i = 0; i < cells.length; i++) {
                    if (i + 1 < cells.length) {
                        var cell = cells[i];
                        var nextCell = cells[i + 1];

                        if (cell != nextCell && cell != 0 && nextCell != 0) continue;
                        if (cell == 0 && nextCell == 0) continue;

                        if (cell == nextCell) {

                            if (alreadyMultiply2.contains(i) || alreadyMultiply2.contains(i+1)) continue;
                            alreadyMultiply2.add(i);

                            cells[i] = cell * 2;
                            cells[i + 1] = 0;
                            if (direction == Direction.UP) {
                                cellsMergedEvent.merged(i + 1, colOrRow, i, colOrRow, cells[i], direction, colOrRow); //UP
                            } else {
                                cellsMergedEvent.merged(colOrRow, i + 1, colOrRow, i, cells[i], direction, colOrRow); // LEFT
                            }
                        } else {
                            if (cell == 0) {
                                cells[i] = nextCell;
                                cells[i + 1] = 0;
                                if (direction == Direction.UP) {
                                    cellsMergedEvent.merged(i + 1, colOrRow, i, colOrRow, cells[i], direction, colOrRow); //UP
                                } else {
                                    cellsMergedEvent.merged(colOrRow, i + 1, colOrRow, i, cells[i], direction, colOrRow); // LEFT
                                }
                            }
                        }

                    }
                }
            } else {
                for (var i = (cells.length - 1); i >= 0; i--) {
                    if (i - 1 >= 0) {
                        var cell = cells[i];
                        var previousCell = cells[i - 1];

                        if (cell != previousCell && cell != 0 && previousCell != 0) continue;
                        if (cell == 0 && previousCell == 0) continue;

                        if (cell == previousCell) {

                            if (alreadyMultiply2.contains(i) || alreadyMultiply2.contains(i - 1)) continue;
                            alreadyMultiply2.add(i);

                            cells[i] = cell * 2;
                            cells[i - 1] = 0;
                            if (direction == Direction.DOWN) {
                                cellsMergedEvent.merged(i - 1, colOrRow, i, colOrRow, cells[i], direction, colOrRow); //DOWN
                            } else if (direction == Direction.RIGHT) {
                                cellsMergedEvent.merged(colOrRow, i - 1, colOrRow, i, cells[i], direction, colOrRow); // RIGHT
                            }
                        } else {
                            if (cell == 0) {
                                cells[i] = previousCell;
                                cells[i - 1] = 0;
                                if (direction == Direction.DOWN) {
                                    cellsMergedEvent.merged(i - 1, colOrRow, i, colOrRow, cells[i], direction, colOrRow); //DOWN
                                } else if (direction == Direction.RIGHT) {
                                    cellsMergedEvent.merged(colOrRow, i - 1, colOrRow, i, cells[i], direction, colOrRow); // RIGHT
                                }
                            }
                        }

                    }
                }
            }

        }
        return cells;
    }

    public void generateNewNumberIfRequired(int[][] oldTerrain) {
        for (byte row = 0; row < terrain.length; row++) {
            for (byte col = 0; col < terrain.length; col++) {
                if (terrain[row][col] != oldTerrain[row][col]) {
                    generateNewNumber();
                    return;
                }
            }
        }
    }

    private void generateNewNumber() {
        var randomizer = new Random();
        var number = (byte) (new Random().nextInt(0, 2) == 0 ? 2 : 4);
        var shouldBreak = false;
        var found = false;
        var countIteration = 0;

        do {
            var randomRowIndex = randomizer.nextInt(0, terrain.length);
            var randomColIndex = randomizer.nextInt(0, terrain.length);
            for (byte i = 0; i < 4; i++) {
                for (byte j = 0; j < 4; j++) {
                    if (randomRowIndex == i && randomColIndex == j) {
                        if (terrain[randomRowIndex][randomColIndex] == 0) {
                            terrain[randomRowIndex][randomColIndex] = number;
                            newNumberEvent.newNumber(randomRowIndex, randomColIndex, number);
                            found = true;
                        } else shouldBreak = true;
                        break;
                    }
                }
                if (shouldBreak) {
                    shouldBreak = false;
                    break;
                }
                if (found) break;
            }
            countIteration++;
            if (countIteration >= 10_000) break;
        } while (!found);
    }

    private void populateTerrain() {
        for (int i = 0; i < terrain.length; i++)
            for (int j = 0; j < terrain.length; j++)
                terrain[i][j] = 0;
    }

}
