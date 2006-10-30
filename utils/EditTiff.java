//
// EditTiff.java
//

import java.io.*;
import java.util.Hashtable;
import loci.formats.RandomAccessStream;
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
      // read first IFD
      System.out.println("Reading " + f + " ");
      RandomAccessStream fin = new RandomAccessStream(f);
      Hashtable ifd = TiffTools.getFirstIFD(fin);
      fin.close();
      if (ifd == null) System.out.println("Warning: first IFD is null!");
      // extract TIFF comment from IFD
      String ome = (String)
        TiffTools.getIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION);
      System.out.println("[done]");
      // display comment, and prompt for changes
      System.out.println("Comment =");
      System.out.println(ome);
      System.out.println("Enter new comment (no line breaks):");
      String xml = cin.readLine();
      System.out.print("Saving " + f);
      // save results back to the TIFF file
      RandomAccessFile raf = new RandomAccessFile(args[i], "rw");
      TiffTools.overwriteIFDValue(raf, 0, TiffTools.IMAGE_DESCRIPTION, xml);
      raf.close();
      System.out.println(" [done]");
    }
  }

}
