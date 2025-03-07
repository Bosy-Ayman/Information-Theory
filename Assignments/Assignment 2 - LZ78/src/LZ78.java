import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LZ78 {
    public static void compress(String inputFile, String outputFile) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(inputFile)));
        Map<String, Integer> dictionary = new HashMap<>();
        List<byte[]> binaryData = new ArrayList<>();
        int dictSize = 1;
        String currentPhrase = "";
        System.out.println("Tags:");

        for (char nextChar : text.toCharArray()) {
            String combinedPhrase = currentPhrase + nextChar;
            if (dictionary.containsKey(combinedPhrase)) {
                currentPhrase = combinedPhrase;
            } else {
                int pos = dictionary.getOrDefault(currentPhrase, 0);
                dictionary.put(combinedPhrase, dictSize++);
                binaryData.add(new byte[]{(byte) pos, (byte) nextChar});
                System.out.println("<" + pos + ", '" + nextChar + "'>");
                currentPhrase = "";
            }
        }

        if (!currentPhrase.isEmpty()) {
            int pos = dictionary.getOrDefault(currentPhrase, 0);
            binaryData.add(new byte[]{(byte) pos, (byte) 0});
            System.out.println("<" + pos + ", ''>");
        }

        writeBinaryToFile(binaryData, outputFile);
        System.out.println("Compression completed.");
    }

    public static void decompress(String inputFilePath, String outputFilePath) throws IOException {
        byte[] binaryData = Files.readAllBytes(Paths.get(inputFilePath));
        List<String> dictionary = new ArrayList<>();
        dictionary.add("");
        StringBuilder decompressedText = new StringBuilder();

        for (int i = 0; i < binaryData.length; i += 2) {
            int pos = Byte.toUnsignedInt(binaryData[i]);
            char nextChar = (char) Byte.toUnsignedInt(binaryData[i + 1]);
            String entry = dictionary.get(pos) + (nextChar == 0 ? "" : nextChar);
            dictionary.add(entry);
            decompressedText.append(entry);
        }

        Files.write(Paths.get(outputFilePath), decompressedText.toString().getBytes());
        System.out.println("Decompression completed.");
    }

    private static void writeBinaryToFile(List<byte[]> binaryData, String outputFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            for (byte[] pair : binaryData) {
                fos.write(pair);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 'c' to compress or 'd' to decompress:");
        String mode = scanner.nextLine();

        if (mode.equalsIgnoreCase("c")) {
            System.out.println("Enter input text file path:");
            String inputFilePath = scanner.nextLine();
            System.out.println("Enter output binary file path:");
            String outputFilePath = scanner.nextLine();

            try {
                compress(inputFilePath, outputFilePath);
            } catch (IOException e) {
                System.out.println("Error during compression: " + e.getMessage());
            }
        } else if (mode.equalsIgnoreCase("d")) {
            System.out.println("Enter input binary file path:");
            String inputFilePath = scanner.nextLine();
            System.out.println("Enter output text file path:");
            String outputFilePath = scanner.nextLine();

            try {
                decompress(inputFilePath, outputFilePath);
            } catch (IOException e) {
                System.out.println("Error during decompression: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid option. Please enter 'c' for compression or 'd' for decompression.");
        }
        scanner.close();
    }
}
