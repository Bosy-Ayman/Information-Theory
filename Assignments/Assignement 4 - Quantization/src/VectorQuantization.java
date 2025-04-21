import java.awt.image.BufferedImage;
import java.util.*;

public class VectorQuantization {

    //Transform image to grayscale
    public static BufferedImage toGray(BufferedImage img) {
        //create a grayscale image container as one channel image instead of 3 channels RGB
        BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        //loop over every pixel and convert to grayscale
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                //get the RGB value of the pixel
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                //convert to grayscale
                int grayVal = (int)(0.3 * r + 0.59 * g + 0.11 * b);
               //set the grayscale value to the pixel
                int grayRGB = (grayVal << 16) | (grayVal << 8) | grayVal;
                gray.setRGB(x, y, grayRGB);
            }
        }
        return gray;
    }

    //Divide image into blocks of size blockSize x blockSize
    public static List<double[]> divideIntoBlocks(BufferedImage img, int blockSize) {
        //Each 2x2 is stored as a 1D array of length 4
        List<double[]> blocks = new ArrayList<>();
        //Loops through top left corner of every 2×2 block
        for (int y = 0; y <= img.getHeight() - blockSize; y += blockSize) {
            for (int x = 0; x <= img.getWidth() - blockSize; x += blockSize) {
                //block holds pixel values of one 2×2 block in 1D.
                double[] block = new double[blockSize * blockSize];
                int idx = 0;
                for (int j = 0; j < blockSize; j++) {
                    for (int i = 0; i < blockSize; i++) {
                        int rgb = img.getRGB(x + i, y + j) & 0xFF;
                        block[idx++] = rgb;
                    }
                }
                blocks.add(block);
            }
        }
        return blocks;
    }
    //Generate codebook of size k
    public static List<double[]> generateCodebook(List<double[]> blocks, int k) {
        List<double[]> codebook = new ArrayList<>();
        codebook.add(averageVector(blocks));
        while (codebook.size() < k) {
            List<double[]> newCodebook = new ArrayList<>();
            for (double[] vec : codebook) {

                newCodebook.add(perturb(vec, 1));
                newCodebook.add(perturb(vec, -1));
            }
            //Take average of newCodebook and add to codebook
            codebook = kMeans(blocks, newCodebook);
        }
        return codebook;
    }

    public static int[] compress(List<double[]> blocks, List<double[]> codebook) {
        //For every image block, we store the index of its closest codebook pattern
        int[] indices = new int[blocks.size()];
        for (int i = 0; i < blocks.size(); i++) {
            //Finds which pattern in codebook looks most like the current block
            indices[i] = nearest(blocks.get(i), codebook);
        }
        return indices;
    }

    public static BufferedImage reconstructImage(int[] indices, List<double[]> codebook, int width, int height, int blockSize) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        int blocksPerRow = width / blockSize;
        for (int b = 0; b < indices.length; b++) {
            //Calculates original position of block b to put it back in image
            int x = (b % blocksPerRow) * blockSize;
            int y = (b / blocksPerRow) * blockSize;
            //Places the codebook pattern block into its original place in the new image
            double[] block = codebook.get(indices[b]);
            int idx = 0;
            for (int j = 0; j < blockSize; j++) {
                for (int i = 0; i < blockSize; i++) {
                    int val = (int) block[idx++];
                    int grayRGB = (val << 16) | (val << 8) | val;
                    result.setRGB(x + i, y + j, grayRGB);
                }
            }
        }
        return result;
    }

    public static double calculateMSE(BufferedImage original, BufferedImage reconstructed) {
        double sum = 0;
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int o = original.getRGB(x, y) & 0xFF;
                int r = reconstructed.getRGB(x, y) & 0xFF;
                sum += Math.pow(o - r, 2);
            }
        }
        return sum / (original.getWidth() * original.getHeight());
    }

    public static double calculateCompressionRatio(BufferedImage img, int blockSize, int k) {
        //Compares how many bits are used
        //every pixel is 8 bits
        int originalBits = img.getWidth() * img.getHeight() * 8;
        int numBlocks = (img.getWidth() / blockSize) * (img.getHeight() / blockSize);
        int bitsPerIndex = (int) Math.ceil(Math.log(k) / Math.log(2));
        //store codebook + indices
        int compressedBits = (k * blockSize * blockSize * 8) + (numBlocks * bitsPerIndex);
        return (double) originalBits / compressedBits;
    }

    private static double[] averageVector(List<double[]> vectors) {
        int len = vectors.get(0).length;
        double[] avg = new double[len];
        for (double[] v : vectors) {
            for (int i = 0; i < len; i++) avg[i] += v[i];
        }
        for (int i = 0; i < len; i++) avg[i] /= vectors.size();
        return avg;
    }

    private static double[] perturb(double[] vec, int direction) {
        double[] result = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            result[i] = vec[i] + direction * 1; // small epsilon
        }
        return result;
    }

    private static List<double[]> kMeans(List<double[]> blocks, List<double[]> initialCenters) {
        List<double[]> centers = new ArrayList<>(initialCenters);
        boolean changed;
        do {
            List<List<double[]>> clusters = new ArrayList<>();
            for (int i = 0; i < centers.size(); i++) clusters.add(new ArrayList<>());

            for (double[] b : blocks) {
                int idx = nearest(b, centers);
                clusters.get(idx).add(b);
            }

            changed = false;
            for (int i = 0; i < centers.size(); i++) {
                if (!clusters.get(i).isEmpty()) {
                    double[] newCenter = averageVector(clusters.get(i));
                    if (!Arrays.equals(newCenter, centers.get(i))) {
                        centers.set(i, newCenter);
                        changed = true;
                    }
                }
            }
        } while (changed);

        return centers;
    }

    private static int nearest(double[] vector, List<double[]> codebook) {
        int minIdx = 0;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < codebook.size(); i++) {
            double dist = euclidean(vector, codebook.get(i));
            if (dist < minDist) {
                minDist = dist;
                minIdx = i;
            }
        }
        return minIdx;
    }

    private static double euclidean(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += Math.pow(a[i] - b[i], 2);
        return Math.sqrt(sum);
    }
}
