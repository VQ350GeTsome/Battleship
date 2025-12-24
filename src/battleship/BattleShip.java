package battleship;

public class BattleShip extends javax.swing.JPanel {
    
    // Image and dimensions.
    private java.awt.image.BufferedImage image;
    public static final int width = 1500, height = 750;
    
    private final int boardSize = 10;
    private final Board playerBoard, opponentBoard;
    private Opponent op;
    
    public static final float LEARNING_RATE = 0.0025f;
    
    private static boolean start = false;
    
    private BattleAlgo algo = new BattleAlgo();
    
    public BattleShip() {
        // Init image.
        imageSizer();
        
        // The players & opponents boardss.
        playerBoard = new Board(boardSize);
        opponentBoard = new Board(boardSize);
        opponentBoard.shuffleBoats();
        
        // Link the two boards
        playerBoard.linkBoard(opponentBoard);
        opponentBoard.linkBoard(playerBoard);
                
        // The opponent himself.
        //op = new Opponent(100, "In-Training");
        try { op = OpponentStorage.loadOpponent("In-Training"); }
        catch (Exception e) { System.err.println(e.getMessage()); }
        op.linkBoard(opponentBoard);
        
        this.displayBoards(-100, 0);
        
        algo.linkBoard(playerBoard);
    }
    
    public void promptAlgo() {
        java.awt.Point hit = algo.shoot();
        
    }

    public void playerClick(int x, int y) {
        // If player takes a turn, let the
        // opponent take a shot.
        if (playerBoard.notifyClick(x, y)) {
            op.shoot(); 
            op.train(playerBoard.getShotsGrid(), playerBoard.getShotForTraining(x, y));
        }
        
        if (!start) return;


    }
    public void resetAll() {
        playerBoard.clear();
        opponentBoard.clear();
        opponentBoard.shuffleBoats();
        start = false;
    }
    
    public void startGame() { start = true; }
    public static boolean hasGameStarted() { return start; }
    
    public boolean arePlayerShipsPlaced() {
        return playerBoard.areShipsPlaced();
    }
    public void shufflePlayerBoard() { playerBoard.shuffleBoats(); }
    public void resetPlayerBoard() { playerBoard.clearBoats(); }
        
    public void saveOpponent() { System.out.println("Saving Opponent..."); OpponentStorage.saveOpponent(op); }
    public void printOpponent() { System.out.println(op.toCSV()); }
    
    public boolean opponentBoardAreShipsSunk() { return opponentBoard.areShipsSunk(); }
    public boolean playerBoardAreShipsSunk() { return playerBoard.areShipsSunk(); }
    
    public void displayBoards(float wave, int time) {
        image = playerBoard.writeToImage(image, wave, time);
        image = opponentBoard.writeHits(image);
        repaint();
    }
       
    public void imageSizer() { image = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(java.awt.Graphics g){ super.paintComponent(g); g.drawImage(image, 0, 0, getWidth(), getHeight(), null); }
}
