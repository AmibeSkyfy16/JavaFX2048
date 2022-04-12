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

    public void moveOLD(Direction direction) {
        var terrainCopy = Arrays.stream(terrain).map(int[]::clone).toArray(value -> terrain.clone());
        for (byte i = 0; i < terrain.length; i++) {
            var cells = new int[terrain[0].length];
            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                System.arraycopy(terrain[i], 0, cells, 0, terrain.length);
                cells = mergeCellsOLD(cells, direction, i);
                for (byte j = 0; j < cells.length; j++)
                    terrain[i][j] = cells[j];

            } else {
                for (byte j = 0; j < cells.length; j++)
                    cells[j] = terrain[j][i];
                cells = mergeCellsOLD(cells, direction, i);
                for (byte j = 0; j < cells.length; j++)
                    terrain[j][i] = cells[j];
            }
        }
        generateNewNumberIfRequired(terrainCopy);
    }

    public int[] mergeCellsOLD(int[] cells, Direction direction, int colOrRow) {
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
                                terrain[i][colOrRow] = cell * 2;
                                terrain[i + 1][colOrRow] = 0;
                            } else {
                                cellsMergedEvent.merged(colOrRow, i + 1, colOrRow, i, cells[i], direction, colOrRow); // LEFT
                                terrain[colOrRow][i] = cell * 2;
                                terrain[colOrRow][i + 1] = 0;
                            }
                        } else {
                            if (cell == 0) {
                                cells[i] = nextCell;
                                cells[i + 1] = 0;
                                if (direction == Direction.UP) {
                                    cellsMergedEvent.merged(i + 1, colOrRow, i, colOrRow, cells[i], direction, colOrRow); //UP
                                    terrain[i][colOrRow] = nextCell;
                                    terrain[i + 1][colOrRow] = 0;
                                } else {
                                    cellsMergedEvent.merged(colOrRow, i + 1, colOrRow, i, cells[i], direction, colOrRow); // LEFT
                                    terrain[colOrRow][i] = nextCell;
                                    terrain[colOrRow][i + 1] = 0;
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
                                terrain[i][colOrRow] = cell * 2;
                                terrain[i - 1][colOrRow] = 0;
                            } else if (direction == Direction.RIGHT) {
                                cellsMergedEvent.merged(colOrRow, i - 1, colOrRow, i, cells[i], direction, colOrRow); // RIGHT
                                terrain[colOrRow][i] = cell * 2;
                                terrain[colOrRow][i - 1] = 0;
                            }
                        } else {
                            if (cell == 0) {
                                cells[i] = previousCell;
                                cells[i - 1] = 0;
                                if (direction == Direction.DOWN) {
                                    cellsMergedEvent.merged(i - 1, colOrRow, i, colOrRow, cells[i], direction, colOrRow); //DOWN
                                    terrain[i][colOrRow] = previousCell;
                                    terrain[i - 1][colOrRow] = 0;
                                } else if (direction == Direction.RIGHT) {
                                    cellsMergedEvent.merged(colOrRow, i - 1, colOrRow, i, cells[i], direction, colOrRow); // RIGHT
                                    terrain[colOrRow][i] = previousCell;
                                    terrain[colOrRow][i - 1] = 0;
                                }
                            }
                        }

                    }
                }
            }

        }
        return cells;
    }

    public void move(Direction direction) {
        var terrainCopy = Arrays.stream(terrain).map(int[]::clone).toArray(value -> terrain.clone());
        mergeCells(direction);
        generateNewNumberIfRequired(terrainCopy);
    }

    public void mergeCells(Direction direction) {

        for(byte i = 0; i < 4; i++) {

            var alreadyMultiply2 = new ArrayList<Integer>(2); // cell can merge (multiply) only one time

            for (int m = 0; m <= 4; m++) { // Perform several iterations to make sure all cells have merged

                if (direction == Direction.UP || direction == Direction.LEFT) {
                    for (var j = 0; j < 4; j++) {
                        if (j + 1 < 4) {
                            var cell = direction == Direction.UP ? terrain[j][i] : terrain[i][j];
                            var nextCell = direction == Direction.UP ? terrain[j+1][i] : terrain[i][j+1];

                            if (cell != nextCell && cell != 0 && nextCell != 0) continue;
                            if (cell == 0 && nextCell == 0) continue;

                            if (cell == nextCell) {

                                if (alreadyMultiply2.contains(j) || alreadyMultiply2.contains(j + 1)) continue;
                                alreadyMultiply2.add(j);

                                if (direction == Direction.UP) {
                                    cellsMergedEvent.merged(j + 1, i, j, i, cell*2, direction, i); //UP
                                    terrain[j][i] = cell * 2;
                                    terrain[j + 1][i] = 0;
                                } else {
                                    cellsMergedEvent.merged(i, j + 1, i, j, cell*2, direction, i); // LEFT
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
                            var previousCell = direction == Direction.DOWN ? terrain[j-1][i] : terrain[i][j-1];

                            if (cell != previousCell && cell != 0 && previousCell != 0) continue;
                            if (cell == 0 && previousCell == 0) continue;

                            if (cell == previousCell) {

                                if (alreadyMultiply2.contains(j) || alreadyMultiply2.contains(j - 1)) continue;
                                alreadyMultiply2.add(j);

                                if (direction == Direction.DOWN) {
                                    cellsMergedEvent.merged(j - 1, i, j, i, cell*2, direction, i); //DOWN
                                    terrain[j][i] = cell * 2;
                                    terrain[j - 1][i] = 0;
                                } else if (direction == Direction.RIGHT) {
                                    cellsMergedEvent.merged(i, j - 1, i, j, cell*2, direction, i); // RIGHT
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


    public void generateNewNumberIfRequired(int[][] oldTerrain) {
        for (byte row = 0; row < terrain.length; row++) {
            for (byte col = 0; col < terrain.length; col++) {
                if (terrain[row][col] != oldTerrain[row][col]) {
                    generateNewNumber();
                    return;
                }
            }
        }
        System.out.println("YOU LOST");
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
