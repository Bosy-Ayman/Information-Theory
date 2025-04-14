import java.util.Objects;

public class HuffmanTree {
    Node root;
    private Node nytNode;
    // Initially, root is the NYT node with freq=0 and a high ID = 100

    public HuffmanTree() {
        root = new Node(0, 100, null);
    }
    public boolean isTreeEmpty() {
        // Empty iff root has no children
        return root.fetchLeft() == null && root.fetchRight() == null;
    }

    public void addCharacter(String character) {
        // If char already present, bump its freq; otherwise insert new
        if (searchNode(root, character) != null) {
            updateCharacterFrequency(character);
        } else {
            createNewNode(character);
        }
    }

    //DFS
    public Node searchNode(Node node, String target) {
        if (node == null) return null;

        if (Objects.equals(node.retrieveData(), target)) return node;

        Node leftResult = searchNode(node.fetchLeft(), target);
        if (leftResult != null) return leftResult;

        return searchNode(node.fetchRight(), target);
    }

    public Node getRootNode() {
        return root;
    }

    private String fetchCharacterCode(Node node,String character,String path) {

        if (node == null) return null;

        if (Objects.equals(node.retrieveData(),character)) return path;
        //left
        String leftResult = fetchCharacterCode(node.fetchLeft(), character, path + "0");
        if (leftResult != null) return leftResult;
        //i reached the end
        if (node.fetchLeft() == null && node.fetchRight() == null) return null;
        //Right
        return fetchCharacterCode(node.fetchRight(), character, path + "1");
    }

    public String retrieveCharacterCode(String character) {
        return fetchCharacterCode(root, character, "");
    }
    public void exchangeNodes(Node nodeA, Node nodeB) {
        Node parentA = nodeA.fetchParent();
        Node parentB = nodeB.fetchParent();
        //link node with new parent
        if (parentA.fetchLeft() == nodeA) parentA.assignLeft(nodeB);
        else parentA.assignRight(nodeB);

        if (parentB.fetchLeft() == nodeB) parentB.assignLeft(nodeA);
        else parentB.assignRight(nodeA);

        nodeA.assignParent(parentB);
        nodeB.assignParent(parentA);
        //switch id
        int temp = nodeA.retrieveNodeId();
        nodeA.updateNodeId(nodeB.retrieveNodeId());
        nodeB.updateNodeId(temp);
    }
    public void createNewNode(String character) {
        // parent = root if NYT
        //else  parent = nytnode
        Node parent = (searchNode(root, "NYT") == null) ? root : nytNode;

        int parentId = parent.retrieveNodeId();

        int leftId = parentId - 2;
        int rightId = parentId - 1;

        Node left= new Node(0, leftId, "NYT");
        Node right=new Node(1, rightId, character);

        left.assignParent(parent);
        right.assignParent(parent);

        parent.assignLeft(left);
        parent.assignRight(right);

        parent.increaseFrequency();
        parent.updateData(null);
        nytNode = left;

        while (parent != root) {
            parent = parent.fetchParent();
            Node swappable = findSwappableNode(root, parent);
            if (swappable != null) exchangeNodes(swappable, parent);
            parent.increaseFrequency();
        }
    }
    // the cases that if it is satisfy the condition then we can swap
    public boolean canSwap(Node candidate, Node node) {
        return candidate.retrieveNodeId() < node.retrieveNodeId() &&
                candidate.getFrequency() >= node.getFrequency() &&
                candidate.fetchParent() != node && candidate != node;
    }
    //return the node that we can swap with
    public Node findSwappableNode(Node node, Node candidate) {
        if (node == null) return null;
        if (canSwap(candidate, node)) return node;

        Node leftResult = findSwappableNode(node.fetchLeft(), candidate);
        if (leftResult != null) return leftResult;

        return findSwappableNode(node.fetchRight(), candidate);
    }
    // after finding the character and swapping it with the other one ,check and update the frequency
    public void updateCharacterFrequency(String character) {
        Node node = searchNode(root, character);
        while (node != root) {
            Node swappable = findSwappableNode(root, node);
            //node that we can swap with exists!
            if (swappable != null) exchangeNodes(swappable, node);
            node.increaseFrequency();
            node = node.fetchParent();
        }
        node.increaseFrequency();
    }



    private void displayTreeSummaryHelper(Node node) {
        if (node == null) return;

        if (node.fetchLeft() == null && node.fetchRight() == null && node.retrieveData() != null) {
            String code = retrieveCharacterCode(node.retrieveData());
            if (code == null) {
                code = "";
            }
            System.out.println("symbol '" + node.retrieveData() + "' with code " + code + " and count " + node.getFrequency());
        }
        displayTreeSummaryHelper(node.fetchLeft());
        displayTreeSummaryHelper(node.fetchRight());
    }
    public void displayTreeSummary() {
        System.out.println("The tree contain mainly the following nodes:");
        displayTreeSummaryHelper(root);
    }


}
