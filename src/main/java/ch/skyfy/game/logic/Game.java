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
//        terrain[0][0] = 2;
//        terrain[0][1] = 2;
//        terrain[0][2] = 4;
//        terrain[0][3] = 8;
//        terrain[0][2] = 64;
//        terrain[0][3] = 64;

        terrain[0][3] = 2;
        terrain[1][3] = 2;
        terrain[2][3] = 2;
        terrain[3][3] = 2;

//        terrain[3][3] = 64;
//        terrain[2][3] = 20;
//        generateNewNumber();
    }

    public void move(Direction direction) {
        var terrainCopy = Arrays.stream(terrain).map(int[]::clone).toArray(value -> terrain.clone());

        for (byte i = 0; i < terrain.length; i++) {
            var cells = new int[terrain[0].length];
            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                System.arraycopy(terrain[i], 0, cells, 0, terrain.length);
                mergeCells(cells, direction, i);
                if (direction == Direction.RIGHT) {
                    var cellsWithRemoved0 = Arrays.stream(cells).filter(num -> num != 0).toArray();
                    for (byte j = (byte) (cells.length - 1); j >= 0; j--)
                        terrain[i][cells.length - 1 - j] = ((j > cellsWithRemoved0.length - 1) ? 0 : cellsWithRemoved0[cellsWithRemoved0.length - 1 - j]);
                } else {
                    var cellsWithRemoved0 = Arrays.stream(cells).filter(num -> num != 0).toArray();
                    for (byte j = 0; j < cells.length; j++)
                        terrain[i][j] = ((j > cellsWithRemoved0.length - 1) ? 0 : cellsWithRemoved0[j]);
                }
            } else {
                for (byte j = 0; j < cells.length; j++)
                    cells[j] = terrain[j][i];
                mergeCells(cells, direction, i);
                if (direction == Direction.DOWN) {
                    var cellsWithRemoved0 = Arrays.stream(cells).filter(num -> num != 0).toArray();
                    for (byte j = (byte) (cells.length - 1); j >= 0; j--)
                        terrain[cells.length - 1 - j][i] = ((j > cellsWithRemoved0.length - 1) ? 0 : cellsWithRemoved0[cellsWithRemoved0.length - 1 - j]);
                } else {
                    var cellsWithRemoved0 = Arrays.stream(cells).filter(num -> num != 0).toArray();
                    for (byte j = 0; j < cells.length; j++)
                        terrain[j][i] = ((j > cellsWithRemoved0.length - 1) ? 0 : cellsWithRemoved0[j]);
                }
            }
        }
        generateNewNumberIfRequired(terrainCopy);
    }

    private int[] reverseArray(int[] array) {
        var cellsCopy = new int[4];
        for (int i = array.length - 1; i >= 0; i--) {
            cellsCopy[array.length - 1 - i] = array[i];
        }
        return cellsCopy;
    }

    public void mergeCells(int[] cells, Direction direction, int colOrRow) {
        if (Arrays.stream(cells).allMatch(s -> s == 0)) return;
        var alreadyMultiply2 = new ArrayList<Integer>(2);

        if (direction == Direction.UP || direction == Direction.LEFT) {
            cells = reverseArray(cells);
        }

        for (int j = 0; j <= 4; j++) { // Double iteration to be sure all cells are merged
            for (var i = (cells.length - 1); i >= 0; i--) {
                if (i - 1 >= 0) {
                    var cell = cells[i];
                    var previousCell = cells[i - 1];

                    if (cell != previousCell && cell != 0 && previousCell != 0) continue;
                    if (cell == 0 && previousCell == 0) continue;

                    if (cell == previousCell) {

                        // Check for merged rule (a cell can merge only one time)
                        if (direction == Direction.DOWN || direction == Direction.RIGHT) {
                            if (alreadyMultiply2.contains(i - 1)) continue;
                            alreadyMultiply2.add(i);
                        } else {
                            if (alreadyMultiply2.contains(cells.length - i - 1)) continue;
                            alreadyMultiply2.add(cells.length - i - 1);
                        }

                        cells[i] = cell * 2;
                        cells[i - 1] = 0;
                        if (direction == Direction.UP) {
                            cellsMergedEvent.merged(cells.length - i, colOrRow, cells.length - i - 1, colOrRow, cells[i], direction, colOrRow); // UP,  ok
//                            terrain[j][cells.length - i - 1] = cell * 2;
//                            terrain[j][cells.length - i] = 0;
                            break;
                        } else if (direction == Direction.DOWN) {
                            cellsMergedEvent.merged(i - 1, colOrRow, i, colOrRow, cells[i], direction, colOrRow); //DOWN
                        } else if (direction == Direction.RIGHT) {
                            cellsMergedEvent.merged(colOrRow, i - 1, colOrRow, i, cells[i], direction, colOrRow); // RIGHT
                        } else if (direction == Direction.LEFT) {
                            cellsMergedEvent.merged(colOrRow, cells.length - i, colOrRow, cells.length - i - 1, cells[i], direction, colOrRow); // UP,  ok
                            break;
                        }
                    } else {
                        if (cell == 0) {
                            cells[i] = previousCell;
                            cells[i - 1] = 0;
                            if (direction == Direction.UP) {
                                cellsMergedEvent.merged(cells.length - i, colOrRow, cells.length - i - 1, colOrRow, cells[i], direction, colOrRow); // UP,  ok
//                                terrain[j][cells.length - i - 1] = previousCell;
//                                terrain[j][cells.length - i] = 0;
                                break;
                            } else if (direction == Direction.DOWN) {
                                cellsMergedEvent.merged(i - 1, colOrRow, i, colOrRow, cells[i], direction, colOrRow); //DOWN
                            } else if (direction == Direction.RIGHT) {
                                cellsMergedEvent.merged(colOrRow, i - 1, colOrRow, i, cells[i], direction, colOrRow); // RIGHT
                            } else if (direction == Direction.LEFT) {
                                cellsMergedEvent.merged(colOrRow, cells.length - i, colOrRow, cells.length - i - 1, cells[i], direction, colOrRow); // UP,  ok
                                break;
                            }
                        }
                    }

                }
            }
        }

//        if(0 == 0)return;
        // BROKEN CODE
//        for (byte j = 0; j < cells.length; j++) {
//            for (byte i = 0; i < cells.length; i++) {
//                var cell = cells[i];
//                if (i < cells.length - 1) {
//                    var nextCell = cells[i + 1];
//                    if (!Objects.equals(cell, nextCell) && cell != 0 && nextCell != 0) continue;
//
//                    if (cell == 0 || nextCell == 0) {
////                        if(cell == 0 && nextCell == 0)continue;
//                        cells[i + 1] = Stream.of(cell, nextCell).min((o1, o2) -> Integer.compare(o2, o1)).get();
//                        cells[i] = 0;
//
////                        cellsMergedEvent.merged(col,cells.length-1-i,col,cells.length-2-i,cells[cells.length-1],direction); // left // OK
////                        cellsMergedEvent.merged(col-i, col, col-i-1, col, cells[i + 1], direction); // UP,  ok
//                        if(cell != 0){
//                            cellsMergedEvent.merged(i, col, i+1, col, cells[i + 1], direction,col); // DOWN // OK
//                        }else{
//                            cellsMergedEvent.merged(i+1, col, i+2, col, cells[i + 1], direction, col); // DOWN // OK
//                        }
//
//                    } else if (!alreadyMultiply) {
//                        cells[i + 1] = (nextCell * 2);
//                        cells[i] = 0;
//                        alreadyMultiply = true;
//
////                        cellsMergedEvent.merged(col,cells.length-1-i,col,cells.length-2-i,cells[cells.length-1],direction); // left // seems OK
////                        cellsMergedEvent.merged(col-i, col, col-i-1, col, cells[i + 1], direction); // UP,  ok
//                        cellsMergedEvent.merged(i, col, i+1, col, cells[i + 1], direction, col);// DOWN // OK
//
//                    }
//
//
//                }
//            }
//        }
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
