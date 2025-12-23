package battleship;

public class Board {
    
    private static final ComplexNoise.PerlinNoise2D perlin = new ComplexNoise.PerlinNoise2D();
    private static final Vectors.vec3 waterDark = new Vectors.vec3(0,0,200), waterLight = new Vectors.vec3(200, 200, 255),
                                      black = new Vectors.vec3(), white = new Vectors.vec3(255);

    // Size of the board, 10 = 10x10 board.
    private final int size;

    // The two grids.
    private final int[][] shipsGrid, shotsGrid;
    private final Ship[] ships = new Ship[5];
    
    // Perlin Noise fields.
    private final int xAdj, yAdj;
    private final float scaleX, scaleY, waterRange, waterRangeStart;
    
    // Grid fields.
    private final int cellSize, gridSize, leftStartX, rightStartX, startY;
    
    // For our shots grid
    public static final int HIT = 1, MISS = -1, EMPTY = 0,
    // For our ships grid
    CARRIER = 5, BATTLESHIP = 4, DESTORYER = 3, SUBMARINE = 2, PATROL = 1;
    
    private static boolean start = false;
    
    private Board enemyBoard;
    
    public Board(int size) {
        this.size = size;
        this.shipsGrid = new int[size][size];
        this.shotsGrid = new int[size][size];
        
        // Fill perlin fields.
        xAdj = (int) (Math.random() * 10000);
        yAdj = (int) (Math.random() * 10000);
        scaleX = 0.025f;
        scaleY = scaleX;
        waterRange = 0.1f;
        waterRangeStart = 0.3f;
        
        // Fill grid fields.
        cellSize = (int) (Math.min(BattleShip.width, BattleShip.height) * 0.095f);
        gridSize = size * cellSize;
        leftStartX = (int) (BattleShip.width * 0.01f);
        rightStartX = BattleShip.width - gridSize - (leftStartX);
        startY = (BattleShip.height - gridSize) / 2;
        
        // Classic ships from the board game.
        ships[4] = new Ship("Carrier",      5, CARRIER);
        ships[3] = new Ship("Battleship",   4, BATTLESHIP);
        ships[2] = new Ship("Destroyer",    3, DESTORYER);
        ships[1] = new Ship("Submarine",    3, SUBMARINE);
        ships[0] = new Ship("Patrol",       2, PATROL);
        
        // Initalize both grids so everything is 0.
        this.clear();
        
    }
    
    public void linkBoard(Board enemyBoard) { this.enemyBoard = enemyBoard; }
    public int queryBoard(int x, int y) {
        if (shipsGrid[x][y] > 0) return Board.HIT;
        else return Board.MISS;
    }
    public void shootAndRecord(int id) {
        if (id == -1) return;
        int x = id % size, y = id / size;
        int info = enemyBoard.queryBoard(x, y);
        shotsGrid[x][y] = info;
    }
    
    public int[][] getShotsGrid() { return this.shotsGrid; }
    
    public int getGridClicked(int mx, int my) {
        // Our grid
        if (mx >= leftStartX && mx < leftStartX + gridSize &&
            my >= startY     && my < startY + gridSize) {

            int col = (mx - leftStartX) / cellSize;
            int row = (my - startY) / cellSize;

            // Return cell ID.
            return row * size + col;
        }

        // Enemy grid
        else if (mx >= rightStartX && mx < rightStartX + gridSize &&
            my >= startY      && my < startY + gridSize) {

            int col = (mx - rightStartX) / cellSize;
            int row = (my - startY) / cellSize;

            // Return cell ID.
            return row * size + col + (size*size);
        }
        return -1;
    }
    public boolean notifyClick(int mx, int my) {
        int id = getGridClicked(mx, my);
        if (id == -1) return false;
        
        // If the click is on the enemy board size
        if (id >= size*size && start) {
            
            // Adjust cell id down
            id -= size*size;
            System.out.println("Player shot at : " + id);
            this.shootAndRecord(id);
            
            // Shot has been made, return true.
            return true;
            
        } else if (id < 100) {
            // place boat
            System.out.println("Player grid click: " + id);
        }
        return false;
    }
    public float[] getShotForTraining(int mx, int my) {
        int id = getGridClicked(mx, my);
        
        float[] shot = new float[size*size];
        
        // -100 to adjust for ID
        shot[id - 100] = 1.0f;
        return shot;
    }
        
    public void notifyStart() { this.start = true; }
    public boolean hasGameStarted() { return start; }

    public boolean placeShip(int id, boolean horizontal, Ship ship) {
        
        if (id > size * size) return false;
        
        int x = id % size, y = id / size;
        
        // Check bounds
        if (horizontal && x + ship.length > size)
            return false;

        if (!horizontal && y + ship.length > size)
            return false;

        // Check collision
        for (int i = 0; i < ship.length; i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;

            if (shipsGrid[cx][cy] != 0)
                return false;
        }

        // Place ship
        for (int i = 0; i < ship.length; i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;

            shipsGrid[cx][cy] = ship.id; // or ship ID
        }
        return true;
    }
    public void shuffleBoats() { 
        this.clearBoats(); 
        for (Ship s : ships) {
            this.placeShipRandomly(s);
            s.placed = true;
        } 
    }
    private void placeShipRandomly(Ship ship) {
        java.util.Random r = new java.util.Random();

        while (true) {
            int id = r.nextInt(size * size);
            boolean horizontal = r.nextBoolean();

            if (placeShip(id, horizontal, ship)) {
                // Update ship data
                ship.startX = id % size;
                ship.startY = id / size;
                ship.horizontal = horizontal;

                // Update the cells the ship is contained in.
                ship.cells.clear();
                for (int i = 0; i < ship.length; i++) {
                    int cx = horizontal ? ship.startX + i : ship.startX;
                    int cy = horizontal ? ship.startY : ship.startY + i;
                    ship.cells.add(new java.awt.Point(cx, cy));
                }

                break; 
            }
        }
    }
    
    public boolean areShipsPlaced() {
        for (Ship s : ships) if (!s.placed) return false;
        return true;
    }
    
    public boolean areShipsSunk() {
        int[][] enemyShots = enemyBoard.getShotsGrid();
        for (Ship s : ships) for (java.awt.Point p : s.cells) 
            if (enemyShots[p.x][p.y] == Board.EMPTY) return false;
        return true;
    }
    
    public void clear() {
        this.clearBoats();
        this.clearShots();
    }
    public void clearShots() {
        for (int i = 0; shotsGrid.length > i; i++) for (int j = 0; shotsGrid[0].length > j; j++) shotsGrid[i][j] = Board.EMPTY;
    }
    public void clearBoats() {
        for (int i = 0; shipsGrid.length > i; i++) for (int j = 0; shipsGrid[0].length > j; j++) shipsGrid[i][j] = Board.EMPTY;
        for (Ship s : ships) s.placed = false;
    }

    private java.awt.image.BufferedImage calculateBackground(java.awt.image.BufferedImage image, float time) {       
        for (int x = 0; BattleShip.width > x; x++) for (int y = 0; BattleShip.height > y; y++) {
                        
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
            float darken = dy * 0.5f;
            // Lighten top right corner.
            float lighten = ((float) (dx + 1-dy) / 2.0f) * 0.35f;
            
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
        
        drawGrid(g, rightStartX, startY, size, cellSize);
        drawGrid(g, leftStartX, startY, size, cellSize);
        
        drawShips(g);
        drawShots(g);
        
        g.dispose();
        
        return image;
    }
    private void drawGrid(java.awt.Graphics2D g, int startX, int startY, int size, int cellSize) {

        for (int i = 0; i <= size; i++) {
            // vertical lines
            g.drawLine(startX + i * cellSize, startY,
                       startX + i * cellSize, startY + gridSize);

            // horizontal lines
            g.drawLine(startX, startY + i * cellSize,
                       startX + gridSize, startY + i * cellSize);
        }
    }
    private void drawShips(java.awt.Graphics2D g) {
        g.setColor(new java.awt.Color(192,0,64,128));
        for (int x = 0; size > x; x++) for (int y = 0; size > y; y++) {
            if (shipsGrid[x][y] > 0) {
                int dx = x * cellSize + leftStartX;
                int dy = y * cellSize + startY;

                g.fillRect(dx, dy, cellSize, cellSize);
            }
        }
    }
    private void drawShots(java.awt.Graphics2D g) {
        for (int x = 0; size > x; x++) for (int y = 0; size > y; y++) {
            if (shotsGrid[x][y] == Board.HIT) {
                int dx = x * cellSize + rightStartX;
                int dy = y * cellSize + startY;

                g.setColor(new java.awt.Color(200,0,0,128));
                
                g.fillRect(dx, dy, cellSize, cellSize);
            } else if (shotsGrid[x][y] == Board.MISS) {
                int dx = x * cellSize + rightStartX;
                int dy = y * cellSize + startY;

                g.setColor(new java.awt.Color(255,255,255,128));
                
                g.fillRect(dx, dy, cellSize, cellSize);
            }
        }
    }
    
    public java.awt.image.BufferedImage writeToImage(java.awt.image.BufferedImage image, float time) { 
        return this.calculateBackground(image, time); 
    }
    public java.awt.image.BufferedImage writeHits(java.awt.image.BufferedImage image) {
        java.awt.Graphics2D g = image.createGraphics();
        
        for (int x = 0; size > x; x++) for (int y = 0; size > y; y++) {
            if (shotsGrid[x][y] == Board.HIT) {
                int dx = x * cellSize + leftStartX;
                int dy = y * cellSize + startY;

                g.setColor(new java.awt.Color(200,0,0,128));
                
                g.fillRect(dx, dy, cellSize, cellSize);
            } else if (shotsGrid[x][y] == Board.MISS) {
                int dx = x * cellSize + leftStartX;
                int dy = y * cellSize + startY;

                g.setColor(new java.awt.Color(255,255,255,128));
                
                g.fillRect(dx, dy, cellSize, cellSize);
            }
        }
        
        g.dispose();
        return image;
    }
    
    public class Ship {
        public String name;
        public int length, startX, startY, id;
        public boolean horizontal, placed;
        public java.util.List<java.awt.Point> cells = new java.util.ArrayList<>();

        public Ship(String name, int length, int id) {
            this.name = name; this.length = length; this.id = id;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Ships Grid:\n");
        for (int[] row : shipsGrid) {
            for (int shipID : row) {
                sb.append("\t").append(shipID);
            }
            sb.append("\n");
        }
        
        sb.append("\n").append("Shots Grid:\n");
        for (int[] row : shotsGrid) {
            for (int shot : row) {
                sb.append("\t").append(shot);
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }

}