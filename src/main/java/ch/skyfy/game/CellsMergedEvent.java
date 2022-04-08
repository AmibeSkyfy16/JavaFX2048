package ch.skyfy.game;

import ch.skyfy.game.logic.Game;

public interface CellsMergedEvent {
    void merged(int srcRow, int srcCol, int destRow, int destCol, int number, Game.Direction direction, int id);
}
