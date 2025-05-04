import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String imagePath = "image3.jpeg"; // Input RGB image
        int blockSize = 6; // Example: 6x6
        int codebookSize = 16; // Number of code vectors

        BufferedImage rgbImage = ImageIO.read(new File(imagePath));
        BufferedImage grayImage = VectorQuantization.toGray(rgbImage);

        List<double[]> blocks = VectorQuantization.divideIntoBlocks(grayImage, blockSize);
        List<double[]> codebook = VectorQuantization.generateCodebook(blocks, codebookSize);

        int[] compressedIndices = VectorQuantization.compress(blocks, codebook);
        BufferedImage reconstructedImage = VectorQuantization.reconstructImage(compressedIndices, codebook, grayImage.getWidth(), grayImage.getHeight(), blockSize);

        ImageIO.write(reconstructedImage, "png", new File("reconstructed.png"));

        double mse = VectorQuantization.calculateMSE(grayImage, reconstructedImage);
        double compressionRatio = VectorQuantization.calculateCompressionRatio(grayImage, blockSize, codebookSize);

        System.out.println("MSE: " + mse);
        System.out.println("Compression Ratio: " + compressionRatio);
    }
}