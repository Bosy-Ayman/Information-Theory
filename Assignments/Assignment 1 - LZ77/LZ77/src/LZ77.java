import java.io.*;
import java.util.*;

class Tags {
    int pos, len;
    char lastChar;

    Tags(int pos, int len, char lastChar) {
        this.pos = pos;
        this.len = len;
        this.lastChar= lastChar;
    }
    @Override
    public String toString() {
        return "<" + pos + "," + len + "," + lastChar + ">";
    }
}

public class LZ77 {
    private final int searchSize = 10;  // Max search buffer size

    public ArrayList<Tags> compress(String input) {
        ArrayList<Tags> tagsList = new ArrayList<>();
        int index = 0;

        while (index < input.length()) {
            int bestPos = 0; //best position in the search buffer
            int bestLen = 0; //length of best match
            String searchBuffer= input.substring(Math.max(0, index - searchSize), index); //store characters before index
            String longestMatch = "";
            for (int len = 1;index + len <= input.length(); len++) {
                String temp = input.substring(index, index + len);
                int pos=searchBuffer.lastIndexOf(temp);
                // No match found
                if (pos ==-1) break;

                //update the best match found so far
                longestMatch = temp;
                bestPos =index-(Math.max(0, index - searchSize) + pos); //howa b3ed ad eh 3an the letter elli ana 3ando
                bestLen = len;
            }
            //then is a character after the match ABA + 'C' ,store it in the next char 'C'
            char nextChar = (index + bestLen < input.length()) ? input.charAt(index + bestLen) : '-';
            tagsList.add(new Tags(bestPos, bestLen, nextChar));
            index += bestLen + 1;
        }
        return tagsList;
    }

    // Decompression
    public String decompress(ArrayList<Tags> tagsList) {
        StringBuilder output = new StringBuilder();
        for (Tags tag : tagsList) {
            int startPoint = output.length() - tag.pos;
            //<0,0,A> <0,0,b>
            // current tag <2,1,b> , current start point = 2
            //go the previous character and take it <A>
            for (int j = startPoint; j < startPoint + tag.len; j++) {
                output.append(output.charAt(j));
            }
            //add the last character of the current tag to it +<B>
            if (tag.lastChar != '-') {
                output.append(tag.lastChar);
            }
        }
        return output.toString();
    }

    // File Handling
    private String readFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder text = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            text.append(line);
        }
        reader.close();
        return text.toString();
    }

    private void writeTagsToFile(String filePath, ArrayList<Tags> tagsList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (Tags tag : tagsList) {
            writer.write(tag.toString()+"\n");
        }
        writer.close();
    }
    private ArrayList<Tags> readTagsFromFile(String filePath) throws IOException {
        ArrayList<Tags> tagsList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("[<>]","");
            String[] parts = line.split(",");
            tagsList.add(new Tags(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2].charAt(0)));
        }
        reader.close();
        return tagsList;
    }

    private void writeFile(String filePath, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.close();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LZ77 lz77 = new LZ77();
        while (true) {
            System.out.println("To compress a file...Enter (1)");
            System.out.println("To decompress a file...Enter (2)");
            System.out.println("To exit...Enter (3)");
            System.out.print("Make a choice: ");
            int choice = scanner.nextInt();

            scanner.nextLine();

            try {
                if (choice == 1) {
                    System.out.print("Enter input file path for compression:");
                    String inputPath = scanner.nextLine();
                    System.out.print("Enter output file path for compressed data:");
                    String outputPath = scanner.nextLine();

                    String text = lz77.readFile(inputPath);
                    if (text == null || text.isEmpty()) {
                        System.out.println("Error: Input file is empty.");
                        continue;  }
                    ArrayList<Tags> compressedData = lz77.compress(text);
                    lz77.writeTagsToFile(outputPath, compressedData);
                    System.out.println("Compression completed output saved to "+outputPath);
                } else if (choice == 2) {
                    System.out.print("Enter input file path for decompression:");
                    String inputPath = scanner.nextLine();
                    System.out.print("Enter output file path for decompressed text: ");
                    String outputPath = scanner.nextLine();
                    ArrayList<Tags> compressedData =lz77.readTagsFromFile(inputPath);
                    String decompressedText =lz77.decompress(compressedData);
                    lz77.writeFile(outputPath,decompressedText);
                    System.out.println("Decompression completed..output saved to "+outputPath);
                } else if (choice==3) {
                    System.out.println("Exiting program...");
                    break;
                } else {
                    System.out.println("Invalid choice.please enter 1, 2, or 3.");
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}

