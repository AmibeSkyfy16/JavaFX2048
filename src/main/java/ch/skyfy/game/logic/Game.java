package ch.skyfy.game.logic;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

public class Game {

    public final int[][] terrain = new int[4][4];

    private boolean shouldGenerateNewNumber = false;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public Game() {
        populateTerrain();
//        terrain[0][0] = 2;
//        terrain[0][1] = 8;
////        terrain[0][2] = 2;
////        terrain[0][3] = 2;
//        terrain[0][0] = 2;
//        terrain[1][0] = 8;
//        terrain[2][0] = 0;
//        terrain[3][0] = 0;
        generateNewNumber();
        System.out.print("");
    }

//    private Integer[] deepCopy(Integer[] matrix) {
//        return java.util.Arrays.stream(matrix).toArray(Integer[]::new);
//    }

//    private boolean isSameArray(int[] array1, int[] array2) {
//        for (int i = 0; i < array1.length; i++) {
//            if (array1[i].intValue() != array2[i].intValue()) {
//                return false;
//            }
//        }
//        return true;
//    }

    public void move(Direction direction) {

        var terrainCopy = Arrays.stream(terrain).map(int[]::clone).toArray(value -> terrain.clone());
//        var terrainCopy = Arrays.copyOf(terrain, terrain.length);

        for (int i = 0; i < terrain.length; i++) {
            var cells = new int[4];

            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                System.arraycopy(terrain[i], 0, cells, 0, terrain.length);
                moveImpl(cells);

                // copying merged cells to terrain depending on the direction
                if (direction == Direction.RIGHT) {
                    var cellsWithRemoved0 = Arrays.stream(cells).filter(num -> num != 0).toArray();
                    var reorderedCells = new int[4];
                    for (int i1 = cells.length - 1; i1 >= 0; i1--)
                        reorderedCells[cells.length - 1 - i1] = (i1 > cellsWithRemoved0.length - 1) ? 0 : cellsWithRemoved0[cellsWithRemoved0.length - 1 - i1];
                    System.arraycopy(reorderedCells, 0, terrain[i], 0, terrain.length);
                } else {
                    var cellsWithRemoved0 = Arrays.stream(cells).filter(num -> num != 0).toArray();
                    var reorderedCells = new int[4];
                    for (int i1 = 0; i1 < cells.length; i1++)
                        reorderedCells[i1] = (i1 > cellsWithRemoved0.length - 1) ? 0 : cellsWithRemoved0[i1];
                    System.arraycopy(reorderedCells, 0, terrain[i], 0, reorderedCells.length);
                }
            } else {
                for (int i1 = 0; i1 < cells.length; i1++)
                    cells[i1] = terrain[i1][i];

//                var cellsCopy = deepCopy(cells);
                moveImpl(cells);
                // copying merged cells to terrain depending on the direction
                if (direction == Direction.DOWN) {
                    var reorderedCells = new int[4];
                    var cellsWithRemoved0 = Arrays.stream(cells).filter(num -> num != 0).toArray();
                    for (int i1 = cells.length - 1; i1 >= 0; i1--)
                        reorderedCells[cells.length - 1 - i1] = (i1 > cellsWithRemoved0.length - 1) ? 0 : cellsWithRemoved0[cellsWithRemoved0.length - 1- i1];


//                    if (!isSameArray(cellsCopy, cells)) {
                        for (int i1 = 0; i1 < cells.length; i1++) {
//                            terrain[i1][i] = cells[i1];
                            terrain[i1][i] = reorderedCells[i1];
                        }
//                    }
//                    for (int j = 0; j < cells.length; j++)
//                        terrain[j][i] = cells[j];
                } else {
                    var cellsWithRemoved0 = Arrays.stream(cells).filter(num -> num != 0).toArray();
                    var reorderedCells = new int[4];
                    for (int i1 = 0; i1 < cells.length; i1++)
                        reorderedCells[i1] = (i1 > cellsWithRemoved0.length - 1) ? 0 : cellsWithRemoved0[i1];

                    for (int i1 = 0; i1 < cells.length; i1++) {
                        terrain[i1][i] = reorderedCells[i1];
                    }
                }
            }
        }
        generateNewNumberIfRequired(terrainCopy);
    }

    public void generateNewNumberIfRequired(int[][] oldTerrain) {
        for (int row = 0; row < terrain.length; row++) {
            for (int col = 0; col < terrain.length; col++) {
                if (!Objects.equals(terrain[row][col], oldTerrain[row][col])) {
                    generateNewNumber();
                    return;
                }
            }
        }
    }

    public void moveImpl(int[] cells) {
        var alreadyMultiply = false;
        for (int j = 0; j < cells.length; j++) {
            for (int i = 0; i < cells.length; i++) {
                var cell = cells[i];
                if (i < cells.length - 1) {
                    var nextCell = cells[i + 1];
                    if (!Objects.equals(cell, nextCell) && cell != 0 && nextCell != 0) continue;

//                    if (cell != nextCell && (nextCell == 0 && cell == 0)){
//                        continue;
//                    }
//
//                        if(cell == 0 && nextCell == 0)continue;
//
//
//                    if (i + 1 == cells.length - 1) {
//                        if (nextCell != 0) continue;
//                    }

//                    if(cell != nextCell && nextCell != 0 && cell != 0)continue;

//                    if(nextCell != 0 && cell != 0){
//                        if(nextCell == cell){
//                            cells[i + 1] = (byte) (nextCell * 2);
//                            cells[i] = 0;
////                            shouldGenerateNewNumber = true;
//                        }
//                    }else{
//                        if(nextCell == 0 && cell == 0)continue;
//                        else{
//                            if(cell != 0) {
//                                cells[i + 1] = cell;
//                                cells[i] = 0;
////                                shouldGenerateNewNumber = true;
//                                continue;
//                            }
//                        }
//
//                    }

//                    if(nextCell == 0 && cell != 0){
//                        cells[i + 1] = cell;
//                    }
//
//                    if(nextCell == cell && nextCell != 0)
//                        cells[i + 1] = (byte) (nextCell * 2);
//                    else{
//                        continue;
//                    }

//                    cells[i + 1] = (cell != 0 && nextCell == 0)
//                            ? Stream.of(cell, nextCell).min((o1, o2) -> Integer.compare(o2, o1)).get()
//                            : (byte) (nextCell * 2);

                    if (cell == 0 || nextCell == 0) {
                        cells[i + 1] = Stream.of(cell, nextCell).min((o1, o2) -> Integer.compare(o2, o1)).get();
                        cells[i] = 0;
                    } else if (!alreadyMultiply) {
                        cells[i + 1] = (nextCell * 2);
                        cells[i] = 0;
                        alreadyMultiply = true;
                    }

//                    cells[i + 1] = (cell == 0 || nextCell == 0)
//                            ? Stream.of(cell, nextCell).min((o1, o2) -> Integer.compare(o2, o1)).get()
//                            : (byte) (nextCell * 2);
//                    cells[i] = 0;
                }
            }
        }
    }

    private void generateNewNumber() {
        var randomizer = new Random();
        var number = (new Random().nextInt(0, 2) == 0 ? 2 : 4);
        var shouldBreak = false;
        var found = false;
        var countIteration = 0;

        do {
            var randomRowIndex = randomizer.nextInt(0, 4);
            var randomColIndex = randomizer.nextInt(0, 4);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (randomRowIndex == i && randomColIndex == j) {
                        if (terrain[randomRowIndex][randomColIndex] == 0) {
                            System.out.println("new Number:" + number);
                            terrain[randomRowIndex][randomColIndex] = number;
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
        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain.length; j++) {
                terrain[i][j] = 0;
            }
        }
    }

}
