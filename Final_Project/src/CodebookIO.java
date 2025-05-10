import java.io.*;
import java.util.ArrayList; // Added import
import java.util.List;

public class CodebookIO {

    public static void saveCodebook(List<double[]> codebook, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (double[] vector : codebook) {
                StringBuilder sb = new StringBuilder();
                for (double value : vector) {
                    sb.append(value).append(" ");
                }
                writer.write(sb.toString().trim());
                writer.newLine();  // Move to the next line for the next vector
            }
        }
    }

    public static List<double[]> loadCodebook(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<double[]> codebook = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(" ");
                double[] vector = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    vector[i] = Double.parseDouble(values[i]);
                }
                codebook.add(vector);
            }
            return codebook;
        }
    }
}
