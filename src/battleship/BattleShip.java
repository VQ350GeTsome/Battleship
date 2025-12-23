package battleship;

public class BattleShip extends javax.swing.JPanel {
    
    // Image and dimensions.
    private java.awt.image.BufferedImage image;
    public static final int width = 1500, height = 750;
    
    private final int boardSize = 10;
    private final Board playerBoard, opponentBoard;
    private Opponent op;
    
    public BattleShip() {
        // Init image.
        imageSizer();
        
        // The players & opponents boards.
        playerBoard = new Board(boardSize);
        opponentBoard = new Board(boardSize);
                
        // The opponent himself.
        //op = new Opponent(boardSize*boardSize, "In-Training");
        try { op = OpponentStorage.loadOpponent("In-Training"); }
        catch (Exception e) { System.err.println(e.getMessage()); }
        
        this.displayBoards(-100);
    }

    public void playerClick(int x, int y) {
        int id = playerBoard.getGridClicked(x, y);
    }
    
    public void startGame() {
        
    }
    
    public boolean arePlayerShipsPlaced() {
        return false;
    }
    
    public void placing(int shipID) {
        
    }
    
    public void saveOpponent() { OpponentStorage.saveOpponent(op); }
    public void printOpponent() { System.out.println(op.toString()); }
    
    public void displayBoards(float time) {
        image = playerBoard.writeToImage(image, time);
        repaint();
    }
       
    public void imageSizer() { image = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(java.awt.Graphics g){ super.paintComponent(g); g.drawImage(image, 0, 0, getWidth(), getHeight(), null); }
}
