public class NameCard {
    public static void printTwice(String s) {
        System.out.println(s);
        System.out.println(s);
    }

    public static void printCard(String name, int age) {
        System.out.print("Name: ");
        System.out.print(name);
        System.out.print(", Age: ");
        System.out.println(age);
    }

    public static void main(String[] args) {
        printTwice("Methods are reusable!");
        printCard("Ada", 36);

        int age = 40;
        printCard("Grace", age + 1);
    }
}
