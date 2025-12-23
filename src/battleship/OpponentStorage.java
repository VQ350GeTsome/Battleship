package battleship;

public class OpponentStorage {
    
    /**
     * Gets the current directory where the program is stored
     * and returns it as a File
     * @return The File where this program is stored
     */
    public static java.io.File getCurrentDirectory(){
        return new java.io.File(System.getProperty("user.dir"));
    }
    
    public static void saveOpponent(Opponent op) {
        String CSV = op.toCSV();
        java.io.File out = new java.io.File(getCurrentDirectory() + "\\Opponents", op.getName() + ".csv");
        try (java.io.FileWriter writer = new java.io.FileWriter(out)) {
            writer.write(CSV);
            writer.flush();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public static Opponent loadOpponent(String name) throws java.io.IOException {
        java.io.File file = new java.io.File(getCurrentDirectory() + "\\Opponents\\", name + ".csv");
        java.util.List<ML.MLP.Layer> layers = new java.util.ArrayList<>();

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            ML.MLP.Layer currentLayer = null;
            java.util.List<float[]> weightRows = null;
            float[] biases = null;
            ML.AI.Activation activation = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Layer")) {
                    // Start a new layer
                    if (currentLayer != null) {
                        currentLayer.setWeightMatrix(weightRows.toArray(new float[0][]));
                        currentLayer.setBiasVector(biases);
                        layers.add(currentLayer);
                    }
                    // Empty layer and new weight rows.
                    currentLayer = new ML.MLP.Layer();
                    weightRows = new java.util.ArrayList<>();
                }
                
                else if (line.startsWith("Activation")) {
                    String a = line.split(",")[1];
                    switch (a) {
                        case "ReLU"         -> activation = ML.AI.Activation.RELU;
                        case "Leaky ReLU"   -> activation = ML.AI.Activation.LEAKY_RELU;
                        case "Soft Sign"    -> activation = ML.AI.Activation.SOFT_SIGN;
                            
                    }
                    currentLayer.setActivationFunc(activation);
                }

                else if (line.equals("weights,")) {
                    // Read weight rows until we hit "biases,".
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.equals("biases,")) break;
                           
                        // Parse row and add that to weight rows
                        String[] parts = line.split(",");
                        float[] row = new float[parts.length];
                        for (int i = 0; parts.length > i; i++)
                            row[i] = Float.parseFloat(parts[i]);
                        weightRows.add(row);
                    }

                    // Parse the bias vector.
                    line = br.readLine().trim();
                    String[] parts = line.split(",");
                    biases = new float[parts.length];
                    for (int i = 0; parts.length > i; i++) biases[i] = Float.parseFloat(parts[i]);
                }
            }

            // Add last layer.
            if (currentLayer != null) {
                currentLayer.setWeightMatrix(weightRows.toArray(new float[0][]));
                currentLayer.setBiasVector(biases);
                layers.add(currentLayer);
            }
        }
        // Make the brain and use it for the opponent.
        ML.MLP brain = new ML.MLP(BattleShip.LEARNING_RATE, layers.toArray(new ML.MLP.Layer[0]));
        return new Opponent(brain, name);
    }

}
