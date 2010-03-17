//
// WriteRGBMovie.java
//

import loci.common.services.ServiceFactory;
import loci.formats.*;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Demonstrates writing multiple RGB image planes to a movie.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/utils/WriteRGBMovie.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/utils/WriteRGBMovie.java">SVN</a></dd></dl>
 */
public class WriteRGBMovie {

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Please specify an output file name.");
      System.exit(1);
    }
    String id = args[0];

    // create 20 blank 512x512 image planes
    System.out.println("Creating random image planes...");
    int w = 511, h = 507, numFrames = 20, numChannels = 3;
    int pixelType = FormatTools.UINT8;
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    int planeSize = h * w * numChannels * bpp;
    byte[][] img = new byte[numFrames][planeSize];

    // fill with random data
    for (int t=0; t<numFrames; t++) {
      for (int i=0; i<img[t].length; i+=numChannels) {
        for (int c=0; c<numChannels; c++) {
          img[t][i + c] = (byte) (256 * Math.random());
        }
      }
    }

    // create metadata object with required metadata fields
    System.out.println("Populating metadata...");
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();
    meta.createRoot();
    meta.setPixelsBigEndian(Boolean.TRUE, 0, 0);
    meta.setPixelsDimensionOrder("XYZCT", 0, 0);
    meta.setPixelsPixelType(FormatTools.getPixelTypeString(pixelType), 0, 0);
    meta.setPixelsSizeX(w, 0, 0);
    meta.setPixelsSizeY(h, 0, 0);
    meta.setPixelsSizeZ(1, 0, 0);
    meta.setPixelsSizeC(numChannels, 0, 0);
    meta.setPixelsSizeT(numFrames, 0, 0);
    meta.setLogicalChannelSamplesPerPixel(numChannels, 0, 0);

    // write image planes to disk
    System.out.print("Writing planes to '" + id + "'");
    IFormatWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(meta);
    writer.setId(id);
    for (int t=0; t<numFrames; t++) {
      System.out.print(".");
      writer.saveBytes(img[t], t == numFrames - 1);
    }
    writer.close();

    System.out.println("Done.");
  }

}
