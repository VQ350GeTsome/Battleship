package battleship;

public class Board {

    private final int size;

    // The two grids.
    private final int[][] shipsGrid, shotsGrid;
    
    // For our shots grid
    public static final int HIT = 1, MISS = -1, EMPTY = 0,
    // For out ships grid
            CARRIER = 5;
    
    public Board(int size) {
        this.size = size;
        this.shipsGrid = new int[size][size];
        this.shotsGrid = new int[size][size];
    }
}
