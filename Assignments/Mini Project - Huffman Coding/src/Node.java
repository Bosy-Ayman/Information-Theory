public class Node {
    int frequency;
    int nodeId;
    Node left, right, parent;
    String data;

    public Node(int frequency, int nodeId, String data) {
        this.frequency = frequency;
        this.nodeId = nodeId;
        this.data = data;
        this.parent = this.left = this.right = null;
    }
    public Node() {
        this.nodeId = 0;
        this.frequency = 0;
        this.parent = this.left = this.right = null;
        this.data = null;
    }

    public void assignParent(Node par) {
        this.parent = par;
    }

    public void assignLeft(Node left) {
        this.left = left;
    }

    public boolean isLeaf() {
        return this.fetchLeft() == null && this.fetchRight() == null;
    }

    public void assignRight(Node right) {
        this.right = right;
    }

    public int retrieveNodeId() {
        return this.nodeId;
    }

    public String retrieveData() {
        return this.data;
    }

    public Node fetchParent() {
        return this.parent;
    }

    public Node fetchLeft() {
        return this.left;
    }

    public Node fetchRight() {
        return this.right;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void increaseFrequency() {
        this.frequency++;
    }

    public void updateNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void updateData(String data) {
        this.data = data;
    }
}
