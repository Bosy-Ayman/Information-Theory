import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String trainingDir = "training";
        String testDir = "test";

        System.out.println("Generating codebooks...");
        List<double[]> redCB   = CodebookGenerator.generate(trainingDir, 'R');
        List<double[]> greenCB = CodebookGenerator.generate(trainingDir, 'G');
        List<double[]> blueCB  = CodebookGenerator.generate(trainingDir, 'B');

        File testFolder = new File(testDir);
        for (File category : testFolder.listFiles()) {
            if (!category.isDirectory()) continue;
            for (File img : category.listFiles()) {
                System.out.println("\nProcessing: " + img.getName());
                VQCompressor.compressAndDecompress(img, redCB, greenCB, blueCB);
            }
        }
    }
}
