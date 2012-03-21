//
// SubResolutionExample.java
//

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;

/**
 * Demonstration of the sub-resolution API.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/SubResolutionExample.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/SubResolutionExample.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SubResolutionExample {

  public static void main(String[] args) throws FormatException, IOException {
    // parse command line arguments
    if (args.length < 1) {
      System.err.println("Usage: java SubResolutionExample imageFile");
      System.exit(1);
    }
    String id = args[0];

    // configure reader
    IFormatReader reader = new ImageReader();
    reader.setFlattenedResolutions(false);
    System.out.println("Initializing file: " + id);
    reader.setId(id); // parse metadata

    int seriesCount = reader.getSeriesCount();

    System.out.println("  Series count = " + seriesCount);

    for (int series=0; series<seriesCount; series++) {
      reader.setSeries(series);
      int resolutionCount = reader.getResolutionCount();

      System.out.println("    Resolution count for series #" + series +
        " = " + resolutionCount);

      for (int r=0; r<resolutionCount; r++) {
        reader.setResolution(r);
        System.out.println("      Resolution #" + r + " dimensions = " +
          reader.getSizeX() + " x " + reader.getSizeY());
      }
    }

    reader.close();
  }

}
