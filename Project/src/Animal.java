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

  public static int mostFreqRGB(int[] arrInt) {
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


    return temp;
  }

  public static int[][] arrRGBNR(int[][] arrRGB, int width, int height) {
    int[] arrInt = new int[5];

    
    for (int w = 1; w < width - 2; w++) {
      for (int h = 1; h < height - 2; h++) {

        arrInt[0] = arrRGB[w][h + 1];// top middle
        arrInt[1] = arrRGB[w + 1][h];// left
        arrInt[2] = arrRGB[w + 1][h + 1];// middle
        arrInt[3] = arrRGB[w + 1][h + 2];// right
        arrInt[4] = arrRGB[w + 2][h + 1];// bottom
        arrRGB[w + 1][h + 1] = mostFreqRGB(arrInt);
 System.out.println(mostFreqRGB(arrInt));
      }
    }


    return arrRGB;
  }

  public static void noiseReduction(String args) {
    picNR = new Picture(picGS);


    int width = picNR.width();
    int height = picNR.height();
    int[][] arrRGB = new int[width][height];

    // populating array of rgb values
    for (int w = 1; w < width - 1; w++)
      for (int h = 1; h < height - 1; h++)
        arrRGB[w][h] = picGS.getRGB(w, h);

    // reduced array
    arrRGB = arrRGBNR(arrRGB, width, height);

    for (int w = 1; w < width - 1; w++)
      for (int h = 1; h < height - 1; h++)
        picNR.setRGB(w, h, arrRGB[w][h]);



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
