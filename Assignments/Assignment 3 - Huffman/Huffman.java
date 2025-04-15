import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Huffman{

    static class HuffmanNode implements Comparable<HuffmanNode> {
        char character;
        int frequency;
        HuffmanNode left, right;

        public HuffmanNode(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        public int compareTo(HuffmanNode node) {
            return Integer.compare(this.frequency, node.frequency);
        }
    }

    public static double calculateEntropy(Map<Character, Integer> frequencyMap, int totalCharacters) {
        double entropy = 0.0;
        for (var entry : frequencyMap.entrySet()) {
            double probability = (double) entry.getValue() / totalCharacters;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }
        return entropy;
    }

    public static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (var entry : frequencyMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode(left.frequency + right.frequency, left, right);
            pq.add(parent);
        }
        return pq.poll();
    }

    public static void generateHuffmanCodes(HuffmanNode root, String code, Map<Character, String> huffmanCodes) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            huffmanCodes.put(root.character, code);
        }
        generateHuffmanCodes(root.left, code + "0", huffmanCodes);
        generateHuffmanCodes(root.right, code + "1", huffmanCodes);
    }

    public static String encodeText(String text, Map<Character, String> huffmanCodes) {
        StringBuilder encodedText = new StringBuilder();
        for (char ch : text.toCharArray()) {
            encodedText.append(huffmanCodes.get(ch));
        }
        return encodedText.toString();
    }

    public static void saveToFile(String fileName, Object content) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(content);
        }
    }

    public static Object readFromFile(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return ois.readObject();
        }
    }

    public static String decodeHuffman(HuffmanNode root, String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        HuffmanNode current = root;
        for (char bit : encodedText.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;
            if (current.left == null && current.right == null) {
                decodedText.append(current.character);
                current = root;
            }
        }
        return decodedText.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nHuffman Coding Program");
            System.out.println("1. Encode a file");
            System.out.println("2. Decode a file");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter input text file name: ");
                    String inputFile = scanner.nextLine();
                    System.out.print("Enter encoded output file name: ");
                    String encodedFile = scanner.nextLine();
                    encodeFile(inputFile, encodedFile);
                }
                case 2 -> {
                    System.out.print("Enter encoded file name: ");
                    String encodedFile = scanner.nextLine();
                    System.out.print("Enter decoded output file name: ");
                    String decodedFile = scanner.nextLine();
                    decodeFile(encodedFile, decodedFile);
                }
                case 3 -> {
                    System.out.println("Exiting program.");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public static void encodeFile(String inputFile, String encodedFile) {
        try {
            String text = Files.readString(Paths.get(inputFile));

            Map<Character, Integer> frequencyMap = new HashMap<>();
            for (char ch : text.toCharArray()) {
                frequencyMap.put(ch, frequencyMap.getOrDefault(ch, 0) + 1);
            }

            double entropy = calculateEntropy(frequencyMap, text.length());
            System.out.println("Entropy: " + entropy);

            HuffmanNode root = buildHuffmanTree(frequencyMap);
            Map<Character, String> huffmanCodes = new HashMap<>();
            generateHuffmanCodes(root, "", huffmanCodes);

            String encodedText = encodeText(text, huffmanCodes);
            saveToFile(encodedFile, encodedText);
            saveToFile("huffman_codes.ser", huffmanCodes);
            saveToFile("frequency_map.ser", frequencyMap);

            System.out.println("File successfully encoded and saved to " + encodedFile);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public static void decodeFile(String encodedFile, String decodedFile) {
        try {
            String encodedText = (String) readFromFile(encodedFile);
            Map<Character, String> huffmanCodes = (Map<Character, String>) readFromFile("huffman_codes.ser");
            Map<Character, Integer> frequencyMap = (Map<Character, Integer>) readFromFile("frequency_map.ser");

            HuffmanNode root = buildHuffmanTree(frequencyMap);

            String decodedText = decodeHuffman(root, encodedText);
            Files.writeString(Paths.get(decodedFile), decodedText);

            System.out.println("File successfully decoded and saved to " + decodedFile);
        } catch (Exception e) {
            System.out.println("Error decoding file: " + e.getMessage());
        }
    }
}