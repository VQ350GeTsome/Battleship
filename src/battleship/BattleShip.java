package battleship;

public class BattleShip extends javax.swing.JPanel {
    
    // Image and dimensions.
    private java.awt.image.BufferedImage image;
    public static final int width = 1500, height = 750;
    
    private final int boardSize = 10;
    private final Board playerBoard, opponentBoard;
    private Opponent op;
    
    public static final float LEARNING_RATE = 0.15f;
    
    public BattleShip() {
        // Init image.
        imageSizer();
        
        // The players & opponents boards.
        playerBoard = new Board(boardSize);
        opponentBoard = new Board(boardSize);
        opponentBoard.shuffleBoats();
        
        // Link the two boards
        playerBoard.linkBoard(opponentBoard);
        opponentBoard.linkBoard(playerBoard);
                
        // The opponent himself.
        op = new Opponent(boardSize*boardSize, "In-Training");
        //try { op = OpponentStorage.loadOpponent("In-Training"); }
        //catch (Exception e) { System.err.println(e.getMessage()); }
        op.linkBoard(opponentBoard);
        
        this.displayBoards(-100);
    }

    public void playerClick(int x, int y) {
        // If player takes a turn, let the
        // opponent take a shot.
        if (playerBoard.notifyClick(x, y)) {
            op.shoot(); 
            op.train(playerBoard.getShotsGrid(), playerBoard.getShotForTraining(x, y));
        }
    }
    
    public void startGame() {
        playerBoard.notifyStart();
        opponentBoard.notifyStart();
    }
    public boolean hasGameStarted() { return playerBoard.hasGameStarted(); }
    
    public boolean arePlayerShipsPlaced() {
        return playerBoard.areShipsPlaced();
    }
    public void shufflePlayerBoard() { playerBoard.shuffleBoats(); }
    public void resetPlayerBoard() { playerBoard.clearBoats(); }
        
    public void saveOpponent() { System.out.println("Saving Opponent..."); OpponentStorage.saveOpponent(op); }
    public void printOpponent() { System.out.println(op.toString()); }
    
    public void displayBoards(float time) {
        image = playerBoard.writeToImage(image, time);
        image = opponentBoard.writeHits(image);
        repaint();
    }
       
    public void imageSizer() { image = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(java.awt.Graphics g){ super.paintComponent(g); g.drawImage(image, 0, 0, getWidth(), getHeight(), null); }
}
