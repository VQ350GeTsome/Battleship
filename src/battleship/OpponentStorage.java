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
                if (line.isEmpty()) continue;

                if (line.startsWith("Layer")) {
                    if (currentLayer != null) {
                        currentLayer.setWeightMatrix(weightRows.toArray(new float[0][]));
                        currentLayer.setBiasVector(biases);
                        layers.add(currentLayer);
                    }
                    currentLayer = new ML.MLP.Layer();
                    weightRows = new java.util.ArrayList<>();
                    continue;
                }

                if (line.startsWith("Activation")) {
                    String[] parts = line.split(",");
                    if (parts.length > 1) {
                        switch (parts[1]) {
                            case "ReLU"       -> activation = ML.AI.Activation.RELU;
                            case "Leaky ReLU" -> activation = ML.AI.Activation.LEAKY_RELU;
                            case "Soft Sign"  -> activation = ML.AI.Activation.SOFT_SIGN;
                        }
                        currentLayer.setActivationFunc(activation);
                    }
                    continue;
                }

                if (line.equals("weights")) {
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        if (line.equals("biases")) break;

                        String[] parts = line.split(",");
                        java.util.List<Float> values = new java.util.ArrayList<>();

                        for (String p : parts) {
                            if (p.isEmpty()) continue;
                            values.add(Float.parseFloat(p));
                        }

                        float[] row = new float[values.size()];
                        for (int i = 0; i < row.length; i++) row[i] = values.get(i);
                        weightRows.add(row);
                    }

                    // Now read biases
                    line = br.readLine();
                    if (line != null) {
                        line = line.trim();
                        String[] parts = line.split(",");
                        java.util.List<Float> values = new java.util.ArrayList<>();

                        for (String p : parts) {
                            if (p.isEmpty()) continue;
                            values.add(Float.parseFloat(p));
                        }

                        biases = new float[values.size()];
                        for (int i = 0; i < biases.length; i++) biases[i] = values.get(i);
                    }
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
