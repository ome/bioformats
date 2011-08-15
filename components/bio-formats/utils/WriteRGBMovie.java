//
// WriteRGBMovie.java
//

import loci.common.services.ServiceFactory;
import loci.formats.*;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Demonstrates writing multiple RGB image planes to a movie.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/WriteRGBMovie.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/WriteRGBMovie.java;hb=HEAD">Gitweb</a></dd></dl>
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

    MetadataTools.populateMetadata(meta, 0, null, false, "XYZCT",
      FormatTools.getPixelTypeString(pixelType), w, h, 1, numChannels,
      numFrames, numChannels);

    // write image planes to disk
    System.out.print("Writing planes to '" + id + "'");
    IFormatWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(meta);
    writer.setId(id);
    for (int t=0; t<numFrames; t++) {
      System.out.print(".");
      writer.saveBytes(t, img[t]);
    }
    writer.close();

    System.out.println("Done.");
  }

}
