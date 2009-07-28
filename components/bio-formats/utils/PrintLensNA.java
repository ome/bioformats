//
// PrintLensNA.java
//

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

/**
 * Uses Bio-Formats to extract lens numerical aperture
 * in a format-independent manner from a dataset.
 */
public class PrintLensNA {

  public static void main(String[] args) throws FormatException, IOException {
    // parse command line arguments
    if (args.length < 1) {
      System.err.println("Usage: java PrintLensNA imageFile");
      System.exit(1);
    }
    String id = args[0];

    // configure reader
    IFormatReader reader = new ImageReader();
    IMetadata meta = MetadataTools.createOMEXMLMetadata();
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
        Float lensNA = meta.getObjectiveLensNA(i, o);
        System.out.println("\tObjective #" + o +
          " [" + meta.getObjectiveID(i, o) + "]: LensNA=" + lensNA);
      }
    }
  }

}
