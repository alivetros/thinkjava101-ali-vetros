public class ReceiptTotal {
    public static void main(String[] args) {
        int quantity = 4;
        int unitPriceCents = 250;

        int totalCents = quantity * unitPriceCents;

        System.out.print("Total cost (cents): ");
        System.out.println(totalCents);

        // Integer division drops the cents part, so this is incomplete.
        // It rounds down and loses the fractional dollar amount.
        int wholeDollars = totalCents / 100;

        System.out.print("Total cost (whole dollars only): ");
        System.out.println(wholeDollars);
    }
}
