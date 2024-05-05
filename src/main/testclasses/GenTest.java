public class GenTest {
    public static void main(String[] args) {
        GenNode<Integer> x = new GenNode<>(45);

        GenNode<String> y = new GenNode<>("Hello");

        System.out.println(y.getValue());

        y.setValue("No");

        System.out.println(x.getValue());
    }
}
