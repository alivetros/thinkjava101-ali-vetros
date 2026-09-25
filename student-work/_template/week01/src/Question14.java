public class Question14 {
        public static void main(String[] args) {
            int x = 10;
            x += 5; // x = 15
            x -= 2; // x = 13
            x *= 3; // x = 39
            x /= 3; // x = 13
            x %= 5; // x = 3

            int a, b;
            a = b = 10; // b receives 10, then a receives 10

            System.out.println(x); // 3
            System.out.println(a); // 10
            System.out.println(b); // 10
        }
    }
