// InsertBlack.java

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import loci.formats.ImageReader;
import loci.formats.TiffWriter;

/**
 * Inserts one or more black images into a multi-image data file,
 * writing the result to the specific multi-page TIFF.
 */
public class InsertBlack {
  // Usage: java InsertBlack infile outfile index [count]
  public static void main(String[] args) throws Exception {
    String in = args[0];
    String out = args[1];
    int ndx = Integer.parseInt(args[2]);
    int count = args.length > 3 ? Integer.parseInt(args[3]) : 1;
    BufferedImage black = null;
    TiffWriter writer = new TiffWriter();
    ImageReader reader = new ImageReader();
    System.out.print(in + ": ");
    int num = reader.getImageCount(in);
    for (int i=0; i<num; i++) {
      BufferedImage img = (BufferedImage) reader.openImage(in, i);
      if (i == ndx) {
        int w = img.getWidth(), h = img.getHeight();
        black = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = black.createGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, w, h);
        g.dispose();
        for (int j=0; j<count; j++) {
          writer.save(out, black, false);
          System.out.print("x");
        }
      }
      writer.save(out, img, i == num - 1);
      System.out.print(".");
    }
    System.out.println(" [done]");
  }
}
