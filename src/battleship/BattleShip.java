package battleship;

public class BattleShip extends javax.swing.JPanel {
    
    // Image and dimensions.
    private java.awt.image.BufferedImage image;
    public static final int width = 1500, height = 750;
    
    private final int boardSize = 10;
    private final Board playerBoard, opponentBoard;
    private final Opponent op;
    
    public BattleShip() {
        // Init image.
        imageSizer();
        
        // The players & opponents boards.
        playerBoard = new Board(boardSize);
        opponentBoard = new Board(boardSize);
                
        // The opponent himself.
        op = new Opponent(size*size);
        
        this.displayBoards(-100);
    }
    
    public void displayBoards(float time) {
        image = playerBoard.writeToImage(image, time);
        repaint();
    }
    
    public void playerClick(int x, int y) {
        int id = playerBoard.getGridClicked(x, y);
    }
    
    public void printOpponent() { System.out.println(op.toString()); }
       
    public void imageSizer() { image = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(java.awt.Graphics g){ super.paintComponent(g); g.drawImage(image, 0, 0, getWidth(), getHeight(), null); }
}
