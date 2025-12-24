package battleship;

public class BattleAlgo {
    
    private Board board;
    
    public void linkBoard(Board b) { board = b; }
    
    public java.awt.Point shoot() {
        return new java.awt.Point(50, 50);
    }
}
