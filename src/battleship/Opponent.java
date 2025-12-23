package battleship;

public class Opponent {

    private final ML.MLP brain;
    private final String name;
    private Board board;
    
    public Opponent(int totalBoardSize, String name) {
        this.brain = new ML.MLP(BattleShip.LEARNING_RATE, totalBoardSize, 64, 64, totalBoardSize);
        this.brain.setAllLayersExceptFinalActivation(ML.AI.Activation.RELU);
        this.brain.setFinalLayerActivation(ML.AI.Activation.SOFT_SIGN);
        this.name = name;
    }
    public Opponent(ML.MLP brain, String name) { this.brain = brain; this.name = name; }
    
    public void linkBoard(Board b) { this.board = b; }
    
    public String getName() { return name; }
    
    public void shoot() {       
        float[] unpackedGrid = flattenArray(board.getShotsGrid());

        float[] thought = brain.forward(unpackedGrid);
        
        float max = Float.NEGATIVE_INFINITY;
        int idx = 0;
        for (int i = 0; thought.length > i; i++) {
            if (thought[i] > max) {
                idx = i;
                max = thought[i];
            }
        }
        
        System.out.println("Opponent shot at : " + idx);
        board.shootAndRecord(idx);
    }
    
    public void train(int[][] grid, float[] target) {
        float[] input = flattenArray(grid);
        brain.train(input, target);
    }
    
    private float[] flattenArray(int[][] grid) {
        int col = grid[0].length;
        
        float[] unpackedGrid = new float[grid.length * col];
        
        for (int i = 0; grid.length > i; i++) for (int j = 0; col > j; j++) unpackedGrid[i * col + j] = grid[i][j];
        
        return unpackedGrid;
    }
    
    @Override
    public String toString() { return brain.toString(); }
    public String toCSV() { return name + ",\n" + brain.toCSV(); }
}
