//
// PrintLensNA.java
//

import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Uses Bio-Formats to extract lens numerical aperture
 * in a format-independent manner from a dataset.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/PrintLensNA.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/PrintLensNA.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PrintLensNA {

  public static void main(String[] args)
    throws DependencyException, FormatException, IOException, ServiceException
  {
    // parse command line arguments
    if (args.length < 1) {
      System.err.println("Usage: java PrintLensNA imageFile");
      System.exit(1);
    }
    String id = args[0];

    // configure reader
    IFormatReader reader = new ImageReader();
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();
    reader.setMetadataStore(meta);
    System.out.println("Initializing file: " + id);
    reader.setId(id); // parse metadata

    // output metadata values
    int instrumentCount = meta.getInstrumentCount();
    System.out.println("There are " + instrumentCount +
      " instrument(s) associated with this file");
    for (int i=0; i<instrumentCount; i++) {
      int objectiveCount = meta.getObjectiveCount(i);
      System.out.println();
      System.out.println("Instrument #" + i +
        " [" + meta.getInstrumentID(i) + "]: " +
        objectiveCount + " objective(s) found");
      for (int o=0; o<objectiveCount; o++) {
        Double lensNA = meta.getObjectiveLensNA(i, o);
        System.out.println("\tObjective #" + o +
          " [" + meta.getObjectiveID(i, o) + "]: LensNA=" + lensNA);
      }
    }
  }

}
