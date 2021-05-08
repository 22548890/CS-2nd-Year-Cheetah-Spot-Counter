import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This program greyscales, noise reduces and detects edges from given images.
 * 
 * @author 22548890
 * 
 * @version 3.1
 */
public class Animal {
  static Picture picGS;
  static Picture picNR;
  static Picture picED;

  /**
   * Grey-scaling picture from inputed argument. /**
   * 
   * @param args the filename given from argument
   */
  public static void greyScale(String args) {
    Picture picture = new Picture(args);
    int width = picture.width();
    int height = picture.height();
    picGS = new Picture(width, height);

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        Color c = new Color(picture.getRGB(i, j));
        int rgb = (int) ((c.getRed() * 0.299) + (c.getGreen() * 0.587) + (c.getBlue() * 0.114));
        Color newColor = new Color(rgb, rgb, rgb);
        picGS.setRGB(i, j, newColor.getRGB());
      }
    }

    picGS.save("out/" + renameFile(args, "_GS")); // ../out/
  }

  /**
   * Renaming files to their respective output types and formats.
   * 
   * @param sFilename the given filename
   * @param sMode the mode selected from the specified arguments.
   * 
   * @return the correct file name and format.
   */
  public static String renameFile(String sFilename, String sMode) {
    sFilename = sFilename.substring(sFilename.indexOf("/") + 1);
    sFilename = sFilename.substring(0, sFilename.indexOf("."));
    return sFilename + sMode + ".png";
  }

  /**
   * Identifying the most frequent color pixel.
   * 
   * @param arrInt the neighborhood array of integers of rgb values
   * 
   * @param x the x position
   * @param y the y position
   */
  public static void mostFreqRGB(int[] arrInt, int x, int y) {
    Arrays.sort(arrInt);
    int n = arrInt.length;

    int countofmax = 1;
    int temp = arrInt[0];
    int count = 1;

    for (int i = 1; i < arrInt.length; i++) {
      if (arrInt[i] == arrInt[i - 1]) {
        count++;
      } else {
        if (count >= countofmax) {
          countofmax = count;
          temp = arrInt[i - 1];
        }
        count = 1;
      }
    }


    if (count >= countofmax) {
      countofmax = count;
      temp = arrInt[n - 1];
    }

    if (countofmax > 1) {
      int r = temp;
      Color c = new Color(r, r, r);
      picNR.setRGB(x, y, c.getRGB());
    }

  }

  /**
   * The noise reduction function.
   * 
   * @param width the width of picture
   * 
   * @param height the height of picture.
   */

  public static void denoise(int width, int height) {

    for (int x = 1; x < width - 1; x++) {
      for (int y = 1; y < height - 1; y++) {

        int[] arrInt = new int[5];
        arrInt[0] = picGS.get(x - 1, y).getRed();
        arrInt[1] = picGS.get(x, y - 1).getRed();
        arrInt[2] = picGS.get(x, y).getRed();
        arrInt[3] = picGS.get(x, y + 1).getRed();
        arrInt[4] = picGS.get(x + 1, y).getRed();

        mostFreqRGB(arrInt, x, y);

      }
    }
  }

  /**
   * Finalizing noise reduction method.
   * 
   * @param args the filename argument.
   * 
   */
  public static void noiseReduction(String args) {
    picNR = new Picture(picGS);


    int width = picNR.width();
    int height = picNR.height();


    denoise(width, height);


    picNR.save("out/" + renameFile(args, "_NR"));// ../out/
  }

  /**
   * Detects and outlines edges.
   * 
   * @param args Filename of original picture given
   * @param eps epsilon value given
   */

  public static void edgeDetection(String args, String eps) {
    picED = new Picture(picNR);
    int iEps = Integer.parseInt(eps);
    Color black = new Color(0, 0, 0);
    Color white = new Color(255, 255, 255);

    int width = picED.width();
    int height = picED.height();

    for (int i = 0; i < width; i++)
      for (int j = 0; j < height; j++)
        picED.setRGB(i, j, 0);

    for (int x = 1; x < width - 1; x++) {
      for (int y = 1; y < height - 1; y++) {

        int[] arrInt = new int[4];
        arrInt[0] = picNR.get(x - 1, y).getRed();
        arrInt[1] = picNR.get(x, y - 1).getRed();
        // excludes center cell
        arrInt[2] = picNR.get(x, y + 1).getRed();
        arrInt[3] = picNR.get(x + 1, y).getRed();


        for (int i = 0; i < arrInt.length; i++) {
          if (Math.abs(picNR.get(x, y).getRed() - arrInt[i]) >= iEps) {
            picED.set(x, y, white);
            break;
          } else
            picED.set(x, y, black);
        }
      }
    }

    picED.save("out/" + renameFile(args, "_ED"));// ../out/
  }


  /**
   * Main method that runs methods based on users inputed arguments.
   * 
   * @param args given arguments from user
   */
  public static void main(String[] args) {
    switch (args[0]) {
      case "0":
        greyScale(args[1]);
        break;
      case "1":
        greyScale(args[1]);
        noiseReduction(args[1]);
        break;
      case "2":
        greyScale(args[1]);
        noiseReduction(args[1]);
        edgeDetection(args[1], args[2]);
        break;
      case "3":
        greyScale(args[1]);
        noiseReduction(args[1]);
        edgeDetection(args[1], args[2]);
        // spot detection
        break;

      default:
        throw new IllegalArgumentException("Unexpected value: " + args[0]);
    }
  }

}
