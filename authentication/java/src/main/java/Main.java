import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    String[] validMethods = {"get", "post"};

    if (args.length != 1 || !Arrays.asList(validMethods).contains(args[0])) {
      throw new IllegalArgumentException("Invalid method, only get or post are valid.");
    }

    if (args[0].equals("get")) {
      Get.get();
    } else {
      Post.post();
    }
  }
}
