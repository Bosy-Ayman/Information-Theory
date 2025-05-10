public class Main {
    public static void main(String[] args) {
        String[] types = {"order-1", "order-2", "adaptive"};
        int[] levels = {8, 16, 32};
        String input = "photo-1611003228941-98852ba62227.jpg";

        for (String t : types) {
            for (int l : levels) {
                PredictiveCompressor tool = new PredictiveCompressor(t, l);
                String output = "result_" + t + "_" + l + ".png";
                try {
                    tool.process(input, output);
                } catch (Exception e) {
                    System.out.println("Error in " + t + " with level " + l + ": " + e.getMessage());
                }
            }
        }
    }
}
