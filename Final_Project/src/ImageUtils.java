import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static int[][] extractComponent(BufferedImage img, char comp) {
        int w = img.getWidth(), h = img.getHeight();
        int[][] res = new int[h][w];
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rgb = img.getRGB(x, y);
            res[y][x] = (comp == 'R') ? (rgb >> 16) & 0xFF :
                    (comp == 'G') ? (rgb >> 8) & 0xFF :
                            rgb & 0xFF;
        }
        return res;
    }

    public static List<double[]> getBlocks(int[][] compArr) {
        int h = compArr.length, w = compArr[0].length;
        List<double[]> blocks = new ArrayList<>();
        for (int y = 0; y < h - 1; y += 2) {
            for (int x = 0; x < w - 1; x += 2) {
                double[] b = {
                        compArr[y][x], compArr[y][x+1],
                        compArr[y+1][x], compArr[y+1][x+1]
                };
                blocks.add(b);
            }
        }
        return blocks;
    }

    public static int[][][] compressComponent(int[][] comp, List<double[]> codebook) {
        int h = comp.length, w = comp[0].length;
        int[][][] compressed = new int[h / 2][w / 2][1];

        for (int y = 0; y < h - 1; y += 2) {
            for (int x = 0; x < w - 1; x += 2) {
                double[] block = {
                        comp[y][x], comp[y][x+1],
                        comp[y+1][x], comp[y+1][x+1]
                };
                compressed[y / 2][x / 2][0] = nearest(block, codebook);
            }
        }
        return compressed;
    }

    public static int[][] decompressComponent(int[][][] compressed, List<double[]> codebook) {
        int h = compressed.length, w = compressed[0].length;
        int[][] result = new int[h * 2][w * 2];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double[] block = codebook.get(compressed[y][x][0]);
                result[2*y][2*x]     = (int) block[0];
                result[2*y][2*x+1]   = (int) block[1];
                result[2*y+1][2*x]   = (int) block[2];
                result[2*y+1][2*x+1] = (int) block[3];
            }
        }
        return result;
    }

    public static BufferedImage mergeComponents(int[][] r, int[][] g, int[][] b) {
        int h = r.length, w = r[0].length;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rr = clamp(r[y][x]), gg = clamp(g[y][x]), bb = clamp(b[y][x]);
            img.setRGB(x, y, (rr << 16) | (gg << 8) | bb);
        }
        return img;
    }

    private static int nearest(double[] vec, List<double[]> cb) {
        double minDist = Double.MAX_VALUE;
        int bestIdx = 0;
        for (int i = 0; i < cb.size(); i++) {
            double dist = 0;
            for (int j = 0; j < 4; j++) dist += Math.pow(vec[j] - cb.get(i)[j], 2);
            if (dist < minDist) {
                minDist = dist;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
