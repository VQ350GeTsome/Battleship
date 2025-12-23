package battleship;

public class Opponent {

    private final ML.MLP brain;
    
    public Opponent(int totalBoardSize) {
        this.brain = new ML.MLP(0.05f, totalBoardSize, 64, 64, totalBoardSize);
    }
}
