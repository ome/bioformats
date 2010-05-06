//
// GetPhysicalMetadata.java
//

import java.util.Arrays;

import loci.common.DateTools;
import loci.common.services.ServiceFactory;
import loci.formats.FormatReader;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Uses Bio-Formats to extract some basic standardized
 * (format-independent) metadata.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/utils/GetPhysicalMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/utils/GetPhysicalMetadata.java">SVN</a></dd></dl>
 */
public class GetPhysicalMetadata {

  /** Outputs dimensional information. */
  public static void printPixelDimensions(IFormatReader reader) {
    // output dimensional information
    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    int sizeZ = reader.getSizeZ();
    int sizeC = reader.getSizeC();
    int sizeT = reader.getSizeT();
    int imageCount = reader.getImageCount();
    System.out.println();
    System.out.println("Pixel dimensions:");
    System.out.println("\tWidth = " + sizeX);
    System.out.println("\tHeight = " + sizeY);
    System.out.println("\tFocal planes = " + sizeZ);
    System.out.println("\tChannels = " + sizeC);
    System.out.println("\tTimepoints = " + sizeT);
    System.out.println("\tTotal planes = " + imageCount);
  }

  /** Outputs global timing details. */
  public static void printPhysicalDimensions(IMetadata meta, int series) {
    double physicalSizeX = meta.getPixelsPhysicalSizeX(series);
    double physicalSizeY = meta.getPixelsPhysicalSizeY(series);
    double physicalSizeZ = meta.getPixelsPhysicalSizeZ(series);
    double timeIncrement = meta.getPixelsTimeIncrement(series);
    System.out.println();
    System.out.println("Physical dimensions:");
    System.out.println("\tX spacing = " + physicalSizeX + " microns");
    System.out.println("\tY spacing = " + physicalSizeY + " microns");
    System.out.println("\tZ spacing = " + physicalSizeZ + " microns");
    System.out.println("\tTime increment = " + timeIncrement + " seconds");
  }

  public static void main(String[] args) throws Exception {
    // parse command line arguments
    if (args.length < 1) {
      System.err.println("Usage: java GetMetadata imageFile [seriesNo]");
      System.exit(1);
    }
    String id = args[0];
    int series = args.length > 1 ? Integer.parseInt(args[1]) : 0;

    // create OME-XML metadata store
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();

    // create format reader
    IFormatReader reader = new ImageReader();
    reader.setMetadataStore(meta);

    // initialize file
    System.out.println("Initializing " + id);
    reader.setId(id);

    int seriesCount = reader.getSeriesCount();
    if (series < seriesCount) reader.setSeries(series);
    series = reader.getSeries();
    System.out.println("\tImage series = " + series + " of " + seriesCount);

    printPixelDimensions(reader);
    printPhysicalDimensions(meta, series);
  }

}
