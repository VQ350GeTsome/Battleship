package battleship;

public class Board {
    
    private static final ComplexNoise.PerlinNoise2D perlin = new ComplexNoise.PerlinNoise2D();
    private static final Vectors.vec3 waterDark = new Vectors.vec3(0,0,200), waterLight = new Vectors.vec3(200, 200, 255),
                                      black = new Vectors.vec3(), white = new Vectors.vec3(255);

    // Size of the board, 10 = 10x10 board.
    private final int size;

    // The two grids.
    private final int[][] shipsGrid, shotsGrid;
    
    // Ocean Perlin Noise fields.
    private int xAdj, yAdj;
    private float scaleX, scaleY, waterRange, waterRangeStart;
    
    // Grid fields.
    private int cellSize, gridSize, startX, startY;
    
    // For our shots grid
    public static final int HIT = 1, MISS = -1, EMPTY = 0,
    // For our ships grid
            CARRIER = 5;
    
    public Board(int size) {
        this.size = size;
        this.shipsGrid = new int[size][size];
        this.shotsGrid = new int[size][size];
        
        // Fill ocean perlin fields.
        xAdj = (int) (Math.random() * 10000);
        yAdj = (int) (Math.random() * 10000);
        scaleX = 0.025f;
        scaleY = scaleX / 2;
        waterRange = 0.1f;
        waterRangeStart = 0.3f;
        
        // Fill grid fields.
        cellSize = (int) (BattleShip.height * 0.095f);
        gridSize = size * cellSize;
        startX = (BattleShip.width  - gridSize) / 2;
        startY = (BattleShip.height - gridSize) / 2;
    }
    
    public java.awt.Point getGridClicked(int x, int y) {

        // Check if click is outside the grid if it is return null.
        if (x < startX || x > startX + gridSize || y < startY || y > startY + gridSize) return null;

        int gx = (x - startX) / cellSize, gy = (y - startY) / cellSize;

        return new java.awt.Point(gx, gy);
    }

    private java.awt.image.BufferedImage calculateImage(java.awt.image.BufferedImage image, int time) {       
        for (int x = 0; BattleShip.width > x; x++) for (int y = 0; BattleShip.width > y; y++) {
                        
            // Offset x and y and scale them down.
            float nx = (x + xAdj) * scaleX, ny = (y + yAdj) * scaleY;
            
            // Fractal noise.
            double n1 = perlin.noise(nx, ny),
                   n2 = perlin.noise(nx * 2, ny * 2) * 0.5,
                   n3 = perlin.noise(nx * 4, ny * 4) * 0.25;
            
            // Weighted avg of the perlins.
            double val = (n1 + n2 + n3) / (1 + 0.5 + 0.25); 
            
            // Normalize.
            val = (val + 1) / 2.0;
            
            // Square to amplify.
            val = val*val;
            
            float dy = ((float) y / (float) BattleShip.width);
            float dx = ((float) x / (float) BattleShip.height);
            
            // Darken bottom.
            float darken = dy * 0.4f;
            // Lighten top right corner.
            float lighten = ((float) (dx + 1-dy) / 2.0f) * 0.5f;
            
            // Use time to make the water move.
            float localSpeed = (float) (perlin.noise(nx * 0.3, ny * 0.3) * 0.5 + 0.5); 
            float wave = (float) (Math.sin(time * (0.1 + localSpeed * 0.3)));

            // Calculate the color and blend everything.
            Vectors.vec3 hardColor = (waterRangeStart  + (wave*0.1f) < val && val < waterRange + waterRangeStart + (wave*0.1f)) ? waterLight : waterDark;
            Vectors.vec3 softColor = waterDark.blend(waterLight, (float) val);
            Vectors.vec3 color = hardColor.blend(softColor, 0.3f);
            color = color.blend(black, darken);
            color = color.blend(white, lighten);

            image.setRGB(x, y, color.toAwtColor().getRGB()); 
        }

        java.awt.Graphics2D g = image.createGraphics();
        
        // Grid stroke size is 0.40% of the screens width
        int strokeSize = (int) (0.004f * BattleShip.width);
        g.setStroke(new java.awt.BasicStroke(strokeSize));
        
        g.setColor(new java.awt.Color(0, 0, 0, 128));
        
        for (int i = 0; size >= i; i++) {
            // vertical lines
            g.drawLine(startX + i * cellSize, startY,
                       startX + i * cellSize, startY + gridSize);

            // horizontal lines
            g.drawLine(startX, startY + i * cellSize,
                       startX + gridSize, startY + i * cellSize);
        }
        g.dispose();
        
        return image;
    }
    public java.awt.image.BufferedImage writeToImage(java.awt.image.BufferedImage image, int time) { 
        return this.calculateImage(image, time); 
    }
}

