public class ConcatenationPuzzle {
    public static void main(String[] args) {
        String label = "Total: ";
        int a = 2;
        int b = 3;
        int c = 4;

        // Prediction: Total: 9
        System.out.println(label + (a + b + c));

        // Prediction: Total: 234
        System.out.println(label + a + b + c);

        // Prediction: Total: 9
        System.out.println(label + (a + b + c));
    }
}
