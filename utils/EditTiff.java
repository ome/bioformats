//
// EditTiff.java
//

import java.io.*;
import java.util.Hashtable;
import loci.common.RandomAccessStream;
import loci.formats.TiffTools;

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
      // RandomAccessStream fin = new RandomAccessStream(f);
      // Hashtable ifd = TiffTools.getFirstIFD(fin);
      // String comment = (String)
      //   TiffTools.getIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION);
      // fin.close();
      System.out.println("[done]");
      // display comment, and prompt for changes
      System.out.println("Comment =");
      System.out.println(comment);
      System.out.println("Enter new comment (no line breaks):");
      String xml = cin.readLine();
      System.out.print("Saving " + f);
      // save results back to the TIFF file
      TiffTools.overwriteComment(args[i], xml);
      // or if you already have the file open for random access, you can use:
      // RandomAccessFile raf = new RandomAccessFile(args[i], "rw");
      // TiffTools.overwriteIFDValue(raf, 0, TiffTools.IMAGE_DESCRIPTION, xml);
      // raf.close();
      System.out.println(" [done]");
    }
  }

}
