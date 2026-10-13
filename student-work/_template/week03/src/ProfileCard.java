import java.util.Scanner;

public class ProfileCard {
    public static void main(String[] args) {
        int age;
        String name;
        Scanner in = new Scanner(System.in);

        System.out.print("What is your age? ");
        age = in.nextInt();
        // nextInt leaves the line break behind, so nextLine would read it as an empty name.
        in.nextLine(); // read the leftover newline

        System.out.print("What is your name? ");
        name = in.nextLine();
        System.out.printf("Hello %s, age %d\n", name, age);
        System.out.printf("Next year you will be %d\n", age + 1);
    }
}
