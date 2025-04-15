import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HuffmanTreeVisualizer extends Application {
    private static Node currentRoot;
    private static Pane treePane;

    public static void setTreeRoot(Node root) {
        currentRoot = root;
    }

    public static void launchVisualizer() {
        new Thread(() -> Application.launch(HuffmanTreeVisualizer.class)).start();
    }

    @Override
    public void start(Stage primaryStage) {
        treePane = new Pane();
        Scene scene = new Scene(treePane, 800, 700);
        primaryStage.setTitle("Adaptive Huffman Tree Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial drawing
        updateTree(currentRoot);
    }

    public static void updateTree(Node newRoot) {
        currentRoot = newRoot;
        if (treePane == null) return;

        Platform.runLater(() -> {
            treePane.getChildren().clear();
            if (currentRoot != null) {
                drawTree(currentRoot, treePane, 700, 50, 100);
            }
        });
    }

    private static void drawTree(Node node, Pane pane, double x, double y, double horizontalGap) {
        if (node == null) return;

        Circle circle = new Circle(x, y, 20);
        circle.setFill(Color.LIGHTGREEN);
        circle.setStroke(Color.BLACK);
        pane.getChildren().add(circle);

        String label;
        if (node.isLeaf()) {
            String data = node.retrieveData();
            if (data == null || data.equals("NYT")) {
                label = "NYT\n(" + node.getFrequency() + ")";
            } else {
                label = "'" + data + "'\n(" + node.getFrequency() + ")";
            }
        } else {
            label = "*\n(" + node.getFrequency() + ")";
        }

        Text text = new Text(x - 15, y + 5, label);
        pane.getChildren().add(text);

        if (node.fetchLeft() != null) {
            double childX = x - horizontalGap;
            double childY = y + 80;
            Line line = new Line(x, y + 20, childX, childY - 20);
            pane.getChildren().add(line);
            drawTree(node.fetchLeft(), pane, childX, childY, horizontalGap / 1.5);
        }

        if (node.fetchRight() != null) {
            double childX = x + horizontalGap;
            double childY = y + 80;
            Line line = new Line(x, y +20, childX, childY - 20);
            pane.getChildren().add(line);
            drawTree(node.fetchRight(), pane, childX, childY, horizontalGap / 1.5);
        }
    }
}
