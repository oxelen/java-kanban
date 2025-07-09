import java.util.List;

public class Main {

   /* public static void main(String[] args) {

    }*/

    static <T> void printList(List<T> list) {
        for (T item : list) {
            System.out.println(item);
        }
    }
}
