//
// MultiFileExample.java
//

import java.io.IOException;
import loci.formats.FormatException;
import loci.formats.ImageReader;

/**
 * Simple example of how to open multiple files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/utils/MultiFileExample.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/utils/MultiFileExample.java">SVN</a></dd></dl>
 */
public class MultiFileExample {
  public static void main(String[] args) throws FormatException, IOException {
    if (args.length < 2) {
      System.out.println("You must specify two files.");
      System.exit(1);
    }
    ImageReader[] readers = new ImageReader[args.length];
    for (int i=0; i<readers.length; i++) {
      readers[i] = new ImageReader();
      readers[i].setId(args[i]);
    }

    // read plane #0 from file #0
    readers[0].openBytes(0);

    // read plane #0 from file #1
    readers[1].openBytes(0);

    // the other option is to use a single reader for all of the files
    // this will use a little less memory, but is substantially slower
    // unless you read all of the planes from one file before moving on
    // to the next file
    //
    // if you want one reader total, uncomment the following:

    /*
    ImageReader reader = new ImageReader();
    //read plane #0 from file #0
    reader.setId(args[0]);
    reader.openBytes(0);
    // read plane #0 from file #1
    reader.setId(args[1]);
    reader.openBytes(0);
    */

  }
}
