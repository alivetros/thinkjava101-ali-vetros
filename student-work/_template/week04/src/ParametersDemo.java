public class ParametersDemo {

    public static void printTwice(String s) {
        System.out.println(s);
        System.out.println(s);
    }

    public static void printTime(int hour, int minute) {
        System.out.print(hour);
        System.out.print(":");
        System.out.println(minute);
    }

    public static void main(String[] args) {
        printTwice("Don't make me say this twice!");

        String message = "Never say never.";
        printTwice(message);

        int hour = 11;
        int minute = 59;
        printTime(hour, minute);

        printTime(hour + 1, 0);
    }
}
