import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PredictiveCompressor {
    private final int quantLevel;
    private final double quantStep;
    private final String method;

    public PredictiveCompressor(String methodType, int level) {
        this.method = methodType;
        this.quantLevel = level;
        this.quantStep = 510.0 / (quantLevel - 1);
    }

    private double[][] loadImage(String filePath) throws IOException {
        BufferedImage img = ImageIO.read(new File(filePath));
        int height = img.getHeight();
        int width = img.getWidth();

        double[][] matrix = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = img.getRGB(x, y);
                int gray = (pixel >> 16) & 0xFF; // extract red as grayscale
                matrix[y][x] = gray;
            }
        }
        return matrix;
    }

    private void saveImage(double[][] data, String outPath) throws IOException {
        int rows = data.length;
        int cols = data[0].length;
        BufferedImage output = new BufferedImage(cols, rows, BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int val = (int) Math.max(0, Math.min(255, data[i][j]));
                int rgb = (val << 16) | (val << 8) | val;
                output.setRGB(j, i, rgb);
            }
        }
        ImageIO.write(output, "png", new File(outPath));
    }

    private double quantize(double number) {
        return Math.round(number / quantStep) * quantStep;
    }

    private double getPrediction(int row, int col, double[][] image) {
        double left = (col > 0) ? image[row][col - 1] : 0;
        double top = (row > 0) ? image[row - 1][col] : 0;
        double topLeft = (row > 0 && col > 0) ? image[row - 1][col - 1] : 0;

        switch (method.toLowerCase()) {
            case "order-1":
                return left;
            case "order-2":
                return (row > 0 && col > 0) ? (left + top - topLeft) : (col > 0 ? left : top);
            case "adaptive":
                double min = Math.min(left, top);
                double max = Math.max(left, top);
                if (topLeft < min) return max;
                if (topLeft >= max) return min;
                return left + top - topLeft;
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }

    private double[][] compress(double[][] original) {
        int h = original.length;
        int w = original[0].length;

        double[][] residuals = new double[h][w];
        double[][] reconstructed = new double[h][w];

        residuals[0][0] = original[0][0];
        for (int x = 1; x < w; x++) {
            residuals[0][x] = original[0][x];
            reconstructed[0][x] = original[0][x];
        }
        for (int y = 1; y < h; y++) {
            residuals[y][0] = original[y][0];
            reconstructed[y][0] = original[y][0];
        }

        for (int i = 1; i < h; i++) {
            for (int j = 1; j < w; j++) {
                double pred = getPrediction(i, j, reconstructed);
                double error = original[i][j] - pred;
                double quantizedError = quantize(error);
                residuals[i][j] = quantizedError;
                reconstructed[i][j] = pred + quantizedError;
            }
        }

        return residuals;
    }

    private double[][] decompress(double[][] residuals) {
        int rows = residuals.length;
        int cols = residuals[0].length;

        double[][] result = new double[rows][cols];
        result[0][0] = residuals[0][0];
        for (int i = 1; i < cols; i++) result[0][i] = residuals[0][i];
        for (int i = 1; i < rows; i++) result[i][0] = residuals[i][0];

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                double guess = getPrediction(i, j, result);
                result[i][j] = guess + residuals[i][j];
            }
        }
        return result;
    }

    private double calcMSE(double[][] original, double[][] decoded) {
        int height = original.length;
        int width = original[0].length;
        double error = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double diff = original[i][j] - decoded[i][j];
                error += diff * diff;
            }
        }
        return error / (height * width);
    }

    private long getEstimatedSize(int rows, int cols) {
        int bitsPerPixel = (int) Math.ceil(Math.log(quantLevel) / Math.log(2));
        return (long) Math.ceil(rows * cols * bitsPerPixel / 8.0);
    }

    public void process(String inputFile, String outputFile) throws IOException {
        double[][] originalData = loadImage(inputFile);
        double[][] residualMap = compress(originalData);
        double[][] decodedImage = decompress(residualMap);

        long inputSize = new File(inputFile).length();
        long compSize = getEstimatedSize(originalData.length, originalData[0].length);

        saveImage(decodedImage, outputFile);

        double mse = calcMSE(originalData, decodedImage);
        double compressionRatio = (double) inputSize / compSize;

        System.out.println("Method       : " + method);
        System.out.println("Levels       : " + quantLevel);
        System.out.println("Input Size   : " + inputSize + " bytes");
        System.out.println("Compressed   : " + compSize + " bytes");
        System.out.println("Ratio        : " + compressionRatio);
        System.out.println("MSE          : " + mse);
    }
}
