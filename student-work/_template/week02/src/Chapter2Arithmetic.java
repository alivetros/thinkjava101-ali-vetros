public class Chapter2Arithmetic {
        public static void main(String[] args) {
        int hour = 11;
        int minute = 59;
 
        // hour * 60 + minute converts a time of day to minutes
        System.out.print("Number of minutes since midnight: ");
        System.out.println(hour * 60 + minute);
 
        // Integer division always rounds toward zero
        System.out.print("Fraction of the hour that has passed: ");
        System.out.println(minute / 60);
 
        // Multiplying before dividing keeps more precision, as a percentage
        System.out.print("Percent of the hour that has passed: ");
        System.out.println(minute * 100 / 60);
    }
}

