import java.util.Objects;

public class Decoder {
    private HuffmanTree tree;

    public Decoder() {
        this.tree = new HuffmanTree();
    }

    // Convert an 8 bit binary string to its ASCII character
    public String binaryToChar(String binaryStr) {
        int asciiVal = Integer.parseInt(binaryStr, 2);
        return Character.toString((char) asciiVal);
    }
    public String decompressMessage(String binaryStream) {
        StringBuilder originalText = new StringBuilder();
        int bitLength = 8;

        // The first symbol is raw ASCII.
        String firstCharCode = binaryStream.substring(0, bitLength);
        String firstChar = binaryToChar(firstCharCode);
        originalText.append(firstChar);
        tree.addCharacter(firstChar);

        System.out.println("After decoding the first character, the compressed stream is '" + binaryStream.substring(0, bitLength) + "'");
        tree.displayTreeSummary();
        System.out.println("-----------------------------------");

        Node current = tree.getRootNode();
        for (int i = bitLength; i < binaryStream.length(); i++) {
            char bit = binaryStream.charAt(i);
            current = (bit=='0') ? current.fetchLeft():current.fetchRight();

            if (current.retrieveData() != null) {
                if (!Objects.equals(current.retrieveData(), "NYT")) {
                    tree.addCharacter(current.retrieveData());
                    originalText.append(current.retrieveData());
                    System.out.println("After decoding a character, the compressed stream is '" + binaryStream.substring(0, i + 1) + "'");
                    tree.displayTreeSummary();
                    System.out.println("-----------------------------------");
                    current = tree.getRootNode();
                } else {
                    int start = i + 1;
                    int end = start + bitLength;
                    String asciiSegment = binaryStream.substring(start, end);
                    i += bitLength;
                    String newChar = binaryToChar(asciiSegment);
                    tree.addCharacter(newChar);
                    originalText.append(newChar);
                    System.out.println("After decoding a character, the compressed stream is '" + binaryStream.substring(0, i + 1) + "'");
                    tree.displayTreeSummary();
                    System.out.println("-----------------------------------");
                    current = tree.getRootNode();
                }
            }
        }

        return originalText.toString();
    }
}
