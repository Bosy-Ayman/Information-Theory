public class Metrics {
    public static double computeMSE(int[][] orig, int[][] recon) {
        int h = Math.min(orig.length, recon.length);
        int w = Math.min(orig[0].length, recon[0].length);
        double sum = 0;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            double diff = orig[y][x] - recon[y][x];
            sum += diff * diff;
        }
        return sum / (h * w);
    }

    public static double computePSNR(int[][] orig, int[][] recon) {
        double mse = computeMSE(orig, recon);
        if (mse == 0) return Double.POSITIVE_INFINITY;
        return 10 * Math.log10((255 * 255) / mse);
    }
}
