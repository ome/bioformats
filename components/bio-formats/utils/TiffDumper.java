//
// TiffDumper.java
//

import java.io.IOException;
import loci.common.RandomAccessInputStream;
import loci.formats.tiff.*;

/** Parses and outputs all IFDs for the given TIFF file(s). */
public class TiffDumper {

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: java TiffDumper file1 file2 ...");
      return;
    }
    for (int i=0; i<args.length; i++) dumpIFDs(args[i]);
  }

  public static void dumpIFDs(String path) throws IOException {
    RandomAccessInputStream in = new RandomAccessInputStream(path);
    TiffParser parser = new TiffParser(in);
    IFDList ifdList = parser.getIFDs();
    for (IFD ifd : ifdList) {
      for (Integer key : ifd.keySet()) {
        int k = key.intValue();
        String name = IFD.getIFDTagName(k);
        Object value = ifd.getIFDValue(k);
        System.out.println(name + " = " + value);
      }
    }
  }

}
