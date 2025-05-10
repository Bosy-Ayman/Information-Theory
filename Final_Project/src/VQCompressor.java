import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class VQCompressor {
    public static void compressAndDecompress(File imgFile,
                                             List<double[]> redCB, List<double[]> greenCB, List<double[]> blueCB) throws Exception {

        BufferedImage img = ImageIO.read(imgFile);
        if (img == null) {
            System.out.println("Skipping: " + imgFile.getName());
            return;
        }

        int[][] r = ImageUtils.extractComponent(img, 'R');
        int[][] g = ImageUtils.extractComponent(img, 'G');
        int[][] b = ImageUtils.extractComponent(img, 'B');

        int[][] dr = ImageUtils.decompressComponent(ImageUtils.compressComponent(r, redCB), redCB);
        int[][] dg = ImageUtils.decompressComponent(ImageUtils.compressComponent(g, greenCB), greenCB);
        int[][] db = ImageUtils.decompressComponent(ImageUtils.compressComponent(b, blueCB), blueCB);

        BufferedImage out = ImageUtils.mergeComponents(dr, dg, db);

        double psnr = Metrics.computePSNR(r, dr);
        System.out.printf("PSNR: %.2f dB\n", psnr);

        File outputDir = new File("output/decompressed");
        if (!outputDir.exists()) outputDir.mkdirs();

        File outFile = new File(outputDir, imgFile.getName().replaceAll("\\.[^.]+$", ".png"));
        ImageIO.write(out, "png", outFile);
        System.out.println("Saved: " + outFile.getPath());
    }
}
