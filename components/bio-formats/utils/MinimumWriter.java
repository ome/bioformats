//
// MinimumWriter.java
//

import loci.common.services.ServiceFactory;
import loci.formats.*;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Demonstrates the minimum amount of metadata
 * necessary to write out an image plane.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/utils/MinimumWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/utils/MinimumWriter.java">SVN</a></dd></dl>
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
    System.out.println("Populating metadata...");

    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();

    meta.createRoot();
    meta.setImageID("Image:0", 0);
    meta.setPixelsID("Pixels:0", 0);
    meta.setPixelsBinDataBigEndian(Boolean.TRUE, 0, 0);
    meta.setPixelsDimensionOrder(DimensionOrder.XYZCT, 0);
    meta.setPixelsType(
      PixelType.fromString(FormatTools.getPixelTypeString(pixelType)), 0);
    meta.setPixelsSizeX(new PositiveInteger(w), 0);
    meta.setPixelsSizeY(new PositiveInteger(h), 0);
    meta.setPixelsSizeZ(new PositiveInteger(1), 0);
    meta.setPixelsSizeC(new PositiveInteger(1), 0);
    meta.setPixelsSizeT(new PositiveInteger(1), 0);
    meta.setChannelID("Channel:0:0", 0, 0);
    meta.setChannelSamplesPerPixel(new PositiveInteger(1), 0, 0);

    // write image plane to disk
    System.out.println("Writing image to '" + id + "'...");
    IFormatWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(meta);
    writer.setId(id);
    writer.saveBytes(0, img);
    writer.close();

    System.out.println("Done.");
  }

}
