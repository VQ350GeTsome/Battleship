package battleship;

public class Opponent {

    private final ML.MLP brain;
    
    public Opponent(int totalBoardSize) {
        this.brain = new ML.MLP(0.05f, totalBoardSize, 64, 64, totalBoardSize);
        this.brain.setAllLayersExceptFinalActivation(ML.AI.Activation.RELU);
        this.brain.setFinalLayerActivation(ML.AI.Activation.SOFT_SIGN);
    }
    public Opponent(ML.MLP brain) { this.brain = brain; }
    
    public int shoot(int[][] grid) {
        int col = grid[0].length;
        
        float[] unpackedGrid = new float[grid.length * col];
        
        for (int i = 0; grid.length > i; i++) for (int j = 0; col > j; j++) unpackedGrid[i * col + j] = grid[i][j];
  
        float[] thought = brain.forward(unpackedGrid);
        
        float max = Float.NEGATIVE_INFINITY;
        int idx = 0;
        for (int i = 0; thought.length > i; i++) {
            if (thought[i] > max) {
                idx = i;
                max = thought[i];
            }
        }
        
        return idx;
    }
    
    @Override
    public String toString() { return brain.toStringForCopy(); }
}
