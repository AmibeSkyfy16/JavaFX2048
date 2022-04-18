package ch.skyfy.game.logic;

import ch.skyfy.game.events.CellsMergedEvent;
import ch.skyfy.game.events.NewNumberEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    public final int[][] terrain = new int[4][4];

    private final CellsMergedEvent cellsMergedEvent;
    private final NewNumberEvent newNumberEvent;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UPWARDS,
        DOWNWARDS
    }

    public Game(CellsMergedEvent cellsMergedEvent, NewNumberEvent newNumberEvent) {
        this.cellsMergedEvent = cellsMergedEvent;
        this.newNumberEvent = newNumberEvent;
        populateTerrain();
    }

    public void move(Direction direction) {
        var terrainCopy = Arrays.stream(terrain).map(int[]::clone).toArray(value -> terrain.clone());

        moveImpl(direction);

        if (Arrays.deepEquals(terrainCopy, terrain)) {
            if (getAvailableCells().isEmpty())
                System.out.println("YOU LOST");
        } else
            generateNewNumber();
    }

    public void moveImpl(Direction direction) {
        for (byte i = 0; i < 4; i++) {

            var alreadyMultiply2 = new ArrayList<Integer>(2); // cell can merge (multiply) only one time

            for (int m = 0; m <= 4; m++) { // Perform several iterations to make sure all cells have merged

                var towards = (direction == Direction.UP || direction == Direction.LEFT) ? Direction.UPWARDS : Direction.DOWNWARDS;

                var oldJ = 0;
                for (var j = 0; j < 4; j = oldJ++) {

                    j = towards == Direction.DOWNWARDS ? 4 - 1 - j : j;

                    var limit = towards == Direction.UPWARDS ? (j + 1 < 4) : (j - 1 >= 0);

                    if (limit) {
                        var index = towards == Direction.UPWARDS ? j + 1 : j - 1;
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
        var availableCells = getAvailableCells();
        var randomNumberIndexes = availableCells.get(ThreadLocalRandom.current().nextInt(availableCells.size()));
        var newNumber = ThreadLocalRandom.current().nextInt(0, 2) == 0 ? 2 : 4;
        newNumberEvent.newNumber(randomNumberIndexes[0], randomNumberIndexes[1], newNumber);
        terrain[randomNumberIndexes[0]][randomNumberIndexes[1]] = newNumber;
    }

    private List<int[]> getAvailableCells() {
        var indices = new ArrayList<int[]>();
        for (byte i = 0; i < terrain.length; i++)
            for (byte j = 0; j < terrain[i].length; j++)
                if (terrain[i][j] == 0)
                    indices.add(new int[]{i, j});
        return indices;
    }

    private void populateTerrain() {
        Arrays.stream(terrain).forEach(row -> Arrays.fill(row, 0));
    }

}
