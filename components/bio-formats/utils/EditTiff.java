//
// EditTiff.java
//

import java.io.*;
import java.util.Hashtable;
import loci.common.RandomAccessInputStream;
import loci.formats.tiff.*;

/** Allows raw user TIFF comment editing for the given TIFF files. */
public class EditTiff {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java EditTiff file1 file2 ...");
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
      TiffSaver.overwriteComment(args[i], xml);
      System.out.println(" [done]");
    }
  }

}
