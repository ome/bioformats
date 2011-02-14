//
// SumPlanes.java
//

import java.awt.image.*;
import loci.formats.*;
import loci.formats.gui.BufferedImageReader;
import loci.formats.gui.BufferedImageWriter;

/**
 * Sums together the image planes from the given file,
 * and saves the result to a 16-bit TIFF.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/SumPlanes.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/SumPlanes.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SumPlanes {

  public static void main(String[] args) throws Exception {
    String id = args[0];
    BufferedImageReader r = new BufferedImageReader();
    System.out.print("Reading " + id);
    r.setId(id);
    int imageCount = r.getImageCount();
    BufferedImage[] images = new BufferedImage[imageCount];
    for (int i=0; i<imageCount; i++) {
      System.out.print(".");
      images[i] = r.openImage(i);
    }
    r.close();
    System.out.println(" [done]");

    String outId = id + ".tif";
    BufferedImageWriter w = new BufferedImageWriter();
    System.out.print("Writing " + outId);
    w.setId(outId);
    w.saveImage(sum(images), true);
    w.close();
    System.out.println(" [done]");
  }

   public static BufferedImage sum(BufferedImage[] images) {
    // Assuming that all images have the same dimensions and type
    int w = images[0].getWidth();
    int h = images[0].getHeight();
    //int type = images[0].getType();

    BufferedImage result = new BufferedImage(w, h,
      BufferedImage.TYPE_USHORT_GRAY); // type == 0 for some reason...
    WritableRaster raster = result.getRaster().createCompatibleWritableRaster();
    int bands = raster.getNumBands();

    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        for (int b=0; b<bands; b++) {
          float sum = 0;
          for (int i=0; i<images.length; i++) {
            sum = sum + images[i].getRaster().getSample(x, y, b);
          }
          raster.setSample(x, y, b, sum);
        }
      }
    }
    result.setData(raster);
    return result;
  }

}
