package battleship;

public class Opponent {

    private final ML.MLP brain;
    private final String name;
    
    public Opponent(int totalBoardSize, String name) {
        this.brain = new ML.MLP(0.05f, totalBoardSize, 64, 64, totalBoardSize);
        this.brain.setAllLayersExceptFinalActivation(ML.AI.Activation.RELU);
        this.brain.setFinalLayerActivation(ML.AI.Activation.SOFT_SIGN);
        this.name = name;
    }
    public Opponent(ML.MLP brain, String name) { this.brain = brain; this.name = name; }
    
    public String getName() { return name; }
    
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
    public String toString() { return brain.toString(); }
    public String toCSV() { return name + ",\n" + brain.toCSV(); }
}
