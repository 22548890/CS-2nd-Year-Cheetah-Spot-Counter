import java.awt.Color;

public class Animal {


  public static void greyScale(String args) {
    Picture picture = new Picture(args);
    int width = picture.width();
    int height = picture.height();
    Picture pic2 = new Picture(width, height);

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Color c = new Color(picture.getRGB(j, i));
        int red = (int) (c.getRed() * 0.299);
        int green = (int) (c.getGreen() * 0.587);
        int blue = (int) (c.getBlue() * 0.114);
        Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
        pic2.setRGB(j, i, newColor.getRGB());
      }
    }
    pic2.save("out/cheetah1.png");
  }



  public static void main(String[] args) {
    switch (args[0]) {
      case "0":
        greyScale(args[1]);
        break;
      case "1":
        greyScale(args[1]);
        // noise reduction
        break;
      case "2":
        greyScale(args[1]);
        // noise reduction
        // Edge detection
        break;
      case "3":
        greyScale(args[1]);
        // noise reduction
        // Edge detection
        // spot detection
        break;
    }

  }
}
