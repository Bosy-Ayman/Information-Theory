
import java.util.Scanner;

public class AdaptiveHuffman {
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);

        System.out.println("Enter a message to encode:");
        String userMessage = inputScanner.nextLine();

        Encoder compresser = new Encoder();
        String compressedOutput = compresser.compressMessage(userMessage);
        System.out.println("\nFinal Encoded Text: " + compressedOutput);

        Decoder decompresser = new Decoder();
        String decompressedOutput = decompresser.decompressMessage(compressedOutput);
        System.out.println("\nFinal Decoded Text: " + decompressedOutput);

        inputScanner.close();
    }
}
