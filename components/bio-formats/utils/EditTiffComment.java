//
// EditTiffComment.java
//

import java.io.*;
import java.util.Hashtable;
import loci.common.RandomAccessInputStream;
import loci.formats.tiff.*;

/**
 * Allows raw user TIFF comment editing for the given TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/utils/EditTiffComment.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/utils/EditTiffComment.java">SVN</a></dd></dl>
 */
public class EditTiffComment {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java EditTiffComment file1 file2 ...");
      return;
    }
    BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
    for (int i=0; i<args.length; i++) {
      String f = args[i];
      // read comment
      System.out.println("Reading " + f + " ");
      String comment = TiffTools.getComment(f);
      // or if you already have the file open for random access, you can use:
      // RandomAccessInputStream fin = new RandomAccessInputStream(f);
      // TiffParser tiffParser = new TiffParser(fin);
      // String comment = tiffParser.getComment();
      // fin.close();
      System.out.println("[done]");
      // display comment, and prompt for changes
      System.out.println("Comment =");
      System.out.println(comment);
      System.out.println("Enter new comment (no line breaks):");
      String xml = cin.readLine();
      System.out.print("Saving " + f);
      // save results back to the TIFF file
      TiffSaver.overwriteComment(f, xml);
      System.out.println(" [done]");
    }
  }

}
