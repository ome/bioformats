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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/MinimumWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/MinimumWriter.java;hb=HEAD">Gitweb</a></dd></dl>
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
    int w = 512, h = 512, c = 1;
    int pixelType = FormatTools.UINT16;
    byte[] img = new byte[w * h * c * FormatTools.getBytesPerPixel(pixelType)];

    // fill with random data
    for (int i=0; i<img.length; i++) img[i] = (byte) (256 * Math.random());

    // create metadata object with minimum required metadata fields
    System.out.println("Populating metadata...");

    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();

    MetadataTools.populateMetadata(meta, 0, null, false, "XYZCT",
      FormatTools.getPixelTypeString(pixelType), w, h, 1, c, 1, c);

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
