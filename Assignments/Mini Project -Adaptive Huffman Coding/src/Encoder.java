public class Encoder {
    private HuffmanTree tree;

    public Encoder() {
        this.tree = new HuffmanTree();
        HuffmanTreeVisualizer.setTreeRoot(tree.getRootNode());
        HuffmanTreeVisualizer.launchVisualizer();
    }

    public String compressMessage(String message) {
        StringBuilder compressedStream = new StringBuilder();
        int processedCount = 0;

        for (char ch : message.toCharArray()) {
            processedCount++;
            String charStr = String.valueOf(ch);
            //If tree is empty, emit raw 8 bit ASCII of first character
            if (tree.isTreeEmpty()) {
                compressedStream.append(String.format("%8s", Integer.toBinaryString(ch)).replace(' ', '0'));
                tree.addCharacter(charStr);
            } else {
                String charCode = tree.retrieveCharacterCode(charStr);
                // the character exists in the tree
                if (charCode != null) {
                    compressedStream.append(charCode);
                    tree.addCharacter(charStr);
                } else { //new character
                    String nytCode = tree.retrieveCharacterCode("NYT");
                    compressedStream.append(nytCode);
                    compressedStream.append(String.format("%8s", Integer.toBinaryString(ch)).replace(' ', '0'));
                    tree.addCharacter(charStr);
                }
            }

            HuffmanTreeVisualizer.updateTree(tree.getRootNode());

            System.out.println("After encoding " + processedCount + "th character, the tree is updated.");
            tree.displayTreeSummary();

            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }

        return compressedStream.toString();
    }
}
