public class ReceiptTotalFixed {
    public static void main(String[] args) {
        int quantity = 4;
        int unitPriceCents = 250;

        double totalDollars = (quantity * unitPriceCents) / 100.0;

        System.out.print("Total cost (dollars): ");
        System.out.println(totalDollars);

        double y1 = 1 / 3;
        double y2 = 1.0 / 3.0;

        // y1 uses integer division first, so 1 / 3 becomes 0 before converting to double.
        // y2 divides as doubles from the start, so it keeps the fractional remainder.
        System.out.println("y1 (int division then converted): " + y1);
        System.out.println("y2 (floating-point division): " + y2);
    }
}
