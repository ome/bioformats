//
// MinimumWriter.java
//

import java.awt.image.BufferedImage;
import loci.formats.*;
import loci.formats.meta.IMetadata;

/**
 * Demonstrates the minimum amount of metadata
 * necessary to write out an image plane.
 */
public class MinimumWriter {

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Please specify an output file name.");
      System.exit(1);
    }
    String id = args[0];

    // create blank 512x512 image
    System.out.println("Creating random image...");
    int w = 512, h = 512;
    int pixelType = FormatTools.UINT16;
    byte[] img = new byte[w * h * FormatTools.getBytesPerPixel(pixelType)];

    // fill with random data
    for (int i=0; i<img.length; i++) img[i] = (byte) (256 * Math.random());

    // create metadata object with minimum required metadata fields
    IMetadata meta = MetadataTools.createOMEXMLMetadata();
    meta.createRoot();
    meta.setPixelsBigEndian(Boolean.TRUE, 0, 0);
    meta.setPixelsDimensionOrder("XYZCT", 0, 0);
    meta.setPixelsPixelType(FormatTools.getPixelTypeString(pixelType), 0, 0);
    meta.setPixelsSizeX(w, 0, 0);
    meta.setPixelsSizeY(h, 0, 0);
    meta.setPixelsSizeZ(1, 0, 0);
    meta.setPixelsSizeC(1, 0, 0);
    meta.setPixelsSizeT(1, 0, 0);
    meta.setLogicalChannelSamplesPerPixel(1, 0, 0);

    // write image plane to disk
    System.out.println("Writing image to '" + id + "'...");
    IFormatWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(meta);
    writer.setId(id);
    writer.saveBytes(img, true);
    writer.close();

    System.out.println("Done.");
  }

}
