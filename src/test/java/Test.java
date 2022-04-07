import ch.skyfy.game.logic.Game;

public class Test {
    @org.junit.jupiter.api.Test
    public void testGame(){
        var g = new Game();
        g.move(Game.Direction.UP);
        System.out.println();
    }
}
