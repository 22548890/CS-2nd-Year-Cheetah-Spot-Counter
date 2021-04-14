import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class Animal {
  static Picture picGS;
  static Picture picNR;

  public static void greyScale(String args) {
    Picture picture = new Picture(args);
    int width = picture.width();
    int height = picture.height();
    picGS = new Picture(width, height);

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Color c = new Color(picture.getRGB(j, i));
        int red = (int) (c.getRed() * 0.299);
        int green = (int) (c.getGreen() * 0.587);
        int blue = (int) (c.getBlue() * 0.114);
        Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
        picGS.setRGB(j, i, newColor.getRGB());
      }
    }

    picGS.save("out/" + renameFile(args, "_GS"));
  }

  public static String renameFile(String sFilename, String sMode) {
    sFilename = sFilename.substring(sFilename.indexOf("/") + 1);
    sFilename = sFilename.substring(0, sFilename.indexOf("."));
    return sFilename + sMode + ".png";
  }

  public static void mostFreqRGB(int[] arrInt, int x, int y) {
    Arrays.sort(arrInt);
    int n = arrInt.length;

    int countofmax = 1;
    int temp = arrInt[0];
    int count = 1;

    for (int i = 1; i < arrInt.length; i++) {
      if (arrInt[i] == arrInt[i - 1])
        count++;
      else {
        if (count > countofmax) {
          countofmax = count;
          temp = arrInt[i - 1];
        }
        count = 1;
      }
    }


    if (count > countofmax) {
      countofmax = count;
      temp = arrInt[n - 1];
    }

    if (countofmax > 1) {
      int r = temp;
      Color c = new Color(r, r, r);
      picNR.setRGB(x, y, c.getRGB());
    }

  }

  public static void denoise(int width, int height) {
    int x = 0;
    int y = 0;
    for (x = 1; x < width - 1; x++) {
      for (y = 1; y < height - 1; y++) {

        int[] arrInt = new int[5];
        // arrInt[0]= picGS.get(x - 1, y - 1).getRed();
        arrInt[0] = picGS.get(x - 1, y).getRed();
        // arrInt[2]= picGS.get(x - 1, y + 1).getRed();
        arrInt[1] = picGS.get(x, y - 1).getRed();
        arrInt[2] = picGS.get(x, y).getRed();
        arrInt[3] = picGS.get(x, y + 1).getRed();
        // arrInt[6]= picGS.get(x + 1, y - 1).getRed();
        arrInt[4] = picGS.get(x + 1, y).getRed();
        // arrInt[8]= picGS.get(x + 1, y + 1).getRed();
        mostFreqRGB(arrInt, x, y);

      }
    }
  }

  public static void noiseReduction(String args) {
    picNR = new Picture(picGS);


    int width = picNR.width();
    int height = picNR.height();


    denoise(width, height);


    picNR.save("out/" + renameFile(args, "_NR"));
  }



  public static void main(String[] args) {
    switch (args[0]) {
      case "0":
        greyScale(args[1]);
        break;
      case "1":
        greyScale(args[1]);
        noiseReduction(args[1]);
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
