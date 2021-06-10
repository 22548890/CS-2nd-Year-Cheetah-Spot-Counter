import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

/**
 * This program grey-scales, noise reduces and detects edges from given images.
 * 
 * @author 22548890
 * 
 * @version 4.1
 */
public class Animal {
  static Picture picGS;
  static Picture picNR;
  static Picture picED;
  static Picture picSD;
  static int spotCount;
  static int picWidth;
  static int picHeight;

  /**
   * Grey-scaling picture from inputed argument. /**
   * 
   * @param args the filename given from argument.
   */
  public static void greyScale(String args) {
    Picture picture = new Picture(args);
    picWidth = picture.width();
    picHeight = picture.height();
    picGS = new Picture(picWidth, picHeight);
    int rgb;
    Color c, newColor;

    for (int i = 0; i < picWidth; i++) {
      for (int j = 0; j < picHeight; j++) {
        c = new Color(picture.getRGB(i, j));
        rgb = (int) ((c.getRed() * 0.299) + (c.getGreen() * 0.587) + (c.getBlue() * 0.114));
        newColor = new Color(rgb, rgb, rgb);
        picGS.setRGB(i, j, newColor.getRGB());
      }
    }
    picGS.save("../out/" + renameFile(args, "_GS")); // ../out/
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
   * @param x the x position
   * @param y the y position
   */
  public static void mostFreqRgb(int[] arrInt, int x, int y) {
    int midRgb = arrInt[2];
    boolean isMidFreq = false;
    Arrays.sort(arrInt);
    int n = arrInt.length;
    int[] arrCount = new int[5];
    int countofmax = 1;
    int temp = arrInt[0];
    int count = 1;

    for (int i = 1; i < n; i++) {
      if (arrInt[i] == arrInt[i - 1]) {
        count++;
        arrCount[i] = count;
      } else {
        if (count >= countofmax) {
          countofmax = count;
          temp = arrInt[i - 1];
          if (temp == midRgb && count >= 2) {
            isMidFreq = true;
          }
        }
        count = 1;
        arrCount[i] = count;
      }
    }
    if (count >= countofmax) {
      countofmax = count;
      temp = arrInt[n - 1];
    }
    if (isMidFreq && countofmax == 2) {
      temp = midRgb;
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
   * @param height the height of picture.
   */

  public static void denoise(int width, int height) {
    int[] arrInt = new int[5];
    for (int x = 1; x < width - 1; x++) {
      for (int y = 1; y < height - 1; y++) {
        arrInt[0] = picGS.get(x - 1, y).getRed();
        arrInt[1] = picGS.get(x, y - 1).getRed();
        arrInt[2] = picGS.get(x, y).getRed();
        arrInt[3] = picGS.get(x, y + 1).getRed();
        arrInt[4] = picGS.get(x + 1, y).getRed();
        mostFreqRgb(arrInt, x, y);
      }
    }
  }

  /**
   * Finalizing noise reduction method.
   * 
   * @param args the filename argument.
   */
  public static void noiseReduction(String args) {
    picNR = new Picture(picGS);
    denoise(picWidth, picHeight);
    picNR.save("../out/" + renameFile(args, "_NR"));
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
    for (int i = 0; i < picWidth; i++) {
      for (int j = 0; j < picHeight; j++) {
        picED.setRGB(i, j, 0);
      }
    }
    for (int x = 1; x < picWidth - 1; x++) {
      for (int y = 1; y < picHeight - 1; y++) {
        int[] arrInt = new int[4];
        arrInt[0] = picNR.get(x - 1, y).getRed();
        arrInt[1] = picNR.get(x, y - 1).getRed();
        // excludes center cell
        arrInt[2] = picNR.get(x, y + 1).getRed();
        arrInt[3] = picNR.get(x + 1, y).getRed();
        int arrLen = arrInt.length;
        for (int i = 0; i < arrLen; i++) {
          if (Math.abs(picNR.get(x, y).getRed() - arrInt[i]) >= iEps) {
            picED.set(x, y, Color.white);
            break;
          } else {
            picED.set(x, y, Color.black);
          }
        }
      }
    }
    picED.save("../out/" + renameFile(args, "_ED"));
  }

  /**
   * Check possible errors and prevent runtime errors.
   * 
   * @param args the filename argument.
   * @return return whether or not all errors passed.
   */
  public static boolean errHandling(String[] args) {
    String sMode;
    int iMode;
    String argsEps;
    try {
      int argslen = args.length;
      sMode = args[0];
      if (args[0].isBlank() || args[1].isBlank()) {
        System.err.println("ERROR: invalid number of arguments");
        return false;
      }
      if (sMode == "2") {
        argsEps = args[2];
      }
    } catch (Exception e) {
      System.err.println("ERROR: invalid number of arguments");
      return false;
    }
    try {
      iMode = Integer.parseInt(sMode);
      if (iMode > 1) {
        Double.parseDouble(args[2]);
      }
    } catch (Exception e) {
      System.err.println("ERROR: invalid argument type");
      return false;
    }
    if (iMode < 0 || iMode > 3) {
      System.err.println("ERROR: invalid mode");
      return false;
    }
    if (iMode > 1) {
      double dEps = Double.parseDouble(args[2]);
      if ((dEps < 0 || dEps > 255) || (dEps - (int) dEps > 0)) {
        System.err.println("ERROR: invalid epsilon");
        return false;
      }
    }
    try {
      Picture pTest = new Picture(args[1]);
    } catch (Exception e) {
      System.err.println("ERROR: invalid or missing file");
      return false;
    }
    return true;
  }

  /**
   * Creates the mask
   * 
   * @param radius the radius given by lower and upper bounds.
   * @param width the width based on radius.
   * @param delta the delta based on radius.
   * @return returns the mask based on the radius, width and delta
   */
  public static int[][] create_spot_mask(int radius, int width, int delta) {
    int[][] xx = new int[2 * radius + 1][2 * radius + 1],
        yy = new int[2 * radius + 1][2 * radius + 1],
        mask = new int[2 * radius + 1][2 * radius + 1];
    double[][] circle = new double[2 * radius + 1][2 * radius + 1];

    for (int i = 0; i < 2 * radius + 1; i++) {
      for (int j = 0; j < 2 * radius + 1; j++) {
        xx[i][j] = i;
        yy[i][j] = j;
        circle[i][j] = Math.pow((xx[i][j] - radius), 2) + Math.pow((yy[i][j] - radius), 2);

        if (circle[i][j] < (Math.pow((radius - delta), 2) + width)
            && (circle[i][j] > (Math.pow((radius - delta), 2) - width))) {
          mask[i][j] = 255;
        } else {
          mask[i][j] = 0;
        }
      }
    }
    return mask;
  }

  /**
   * Creates an array of the pixels.
   * 
   * @return returns the array based on the picture.
   */
  public static int[][] arrPicED() {
    int[][] arrEdges = new int[picWidth][picHeight];
    for (int x = 0; x < picWidth; x++) {
      for (int y = 0; y < picHeight; y++) {
        arrEdges[x][y] = picED.get(x, y).getRed();
      }
    }
    return arrEdges;
  }

  /**
   * Check whether spot meets sum criteria.
   * 
   * @param sum the sum based on the formula.
   * @param radius the radius given.
   * @return returns a boolean.
   */
  public static boolean is_Spot(int sum, int radius) { // add breaks
    switch (radius) {
      case 4: {
        if (sum < 4800) {
          return true;
        }
        break;
      }
      case 5: {
        if (sum < 6625) {
          return true;
        }
        break;
      }
      case 6: {
        if (sum < 11000) {
          return true;
        }
        break;
      }
      case 7: {
        if (sum < 15000) {
          return true;
        }
        break;
      }
      case 8: {
        if (sum < 19000) {
          return true;
        }
        break;
      }
      case 9: {
        if (sum < 23000) {
          return true;
        }
        break;
      }
      case 10: {
        if (sum < 28000) {
          return true;
        }
        break;
      }
      case 11: {
        if (sum < 35000) {
          return true;
        }
        break;
      }
    }
    return false;
  }

  /**
   * Links a delta value compared to radius.
   * 
   * @param radius the radius given.
   * @return returns correct delta value.
   */
  public static int set_delta(int radius) {
    switch (radius) {
      case 4: {
        return 0;
      }
      case 5: {
        return 1;
      }
      case 6: {
        return 1;
      }
      case 7: {
        return 1;
      }
      case 8: {
        return 1;
      }
      case 9: {
        return 1;
      }
      case 10: {
        return 2;
      }
      case 11: {
        return 2;
      }
    }
    return 0;
  }

  /**
   * Sets width based on radius.
   * 
   * @param radius the radius given.
   * @return returns correct width.
   */
  public static int set_Width(int radius) {
    switch (radius) {
      case 4: {
        return 6;
      }
      case 5: {
        return 9;
      }
      case 6: {
        return 12;
      }
      case 7: {
        return 15;
      }
      case 8: {
        return 18;
      }
      case 9: {
        return 21;
      }
      case 10: {
        return 24;
      }
      case 11: {
        return 27;
      }
    }
    return 0;
  }

  /**
   * Sets new picture based on the array.
   * 
   * @param arr the array received.
   */
  public static void set_Sd_image(int[][] arr) {
    int value;
    Color spot;
    for (int i = 0; i < picWidth; i++) {
      for (int j = 0; j < picHeight; j++) {
        value = arr[i][j];
        spot = new Color(value, value, value);
        picSD.set(i, j, spot);
      }
    }
  }

  /**
   * Runs methods to count and save the spot detection.
   * 
   * @param args the args given.
   * @param lower the lower limit of radius given.
   * @param upper the upper limit of radius given.
   */
  public static void spot_detection(String args, int lower, int upper) {
    picSD = new Picture(picED);
    spotCount = 0;
    int radius = lower;
    int width = set_Width(radius);
    int delta = set_delta(radius);
    int[][] arrSpots = new int[picWidth][picHeight];
    int[][] arrEdges = arrPicED();

    while (radius <= upper) {
      find_spots(create_spot_mask(radius, width, delta), radius, arrSpots, arrEdges);
      radius++;
      width = set_Width(radius);
      delta = set_delta(radius);
    }
    set_Sd_image(arrSpots);
    picSD.save("../out/" + renameFile(args, "_SD"));
    System.out.println(spotCount);
  }

  /**
   * Find spots and counts spots, setting the array of spots.
   * 
   * @param mask the mask created earlier.
   * @param radius the radius given.
   * @param arrSpot the array adding founded spots.
   * @param arrEdges the array containing the pixels of picture.
   */
  public static void find_spots(int[][] mask, int radius, int[][] arrSpot, int[][] arrEdges) {
    int maskLength = mask.length;
    int sum;
    for (int i = 0; i < picWidth - maskLength; i++) {
      loop: for (int j = 0; j < picHeight - maskLength; j++) {
        sum = 0;
        for (int x = 0; x < maskLength; x++) {
          for (int y = 0; y < maskLength; y++) {
            sum += Math.abs(arrEdges[x + i][y + j] - mask[x][y]);
            if (arrSpot[i + x][j + y] == 255) {
              continue loop;
            }
          }
        }
        if (is_Spot(sum, radius)) {
          for (int z = 0; z < maskLength; z++) {
            for (int h = 0; h < maskLength; h++) {
              arrSpot[z + i][h + j] = arrEdges[z + i][h + j];
            }
          }
          spotCount++;
        }
      }
    }
  }

  /**
   * Main method that runs methods based on users inputed arguments.
   * 
   * @param args given arguments from user
   */
  public static void main(String[] args) {
    if (errHandling(args)) {
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
          spot_detection(args[1], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
          break;

        default:
          throw new IllegalArgumentException("Unexpected value: " + args[0]);
      }
    }
  }
}
