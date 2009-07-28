//
// ExtractSDTData.java
//

import java.io.*;
import loci.formats.in.SDTReader;

/** Reads SDT files and creates text files containing histogram data. */
public class ExtractSDTData {

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Usage: java ExtractSDTData file.sdt");
      System.exit(1);
    }
    String id = args[0];
    String tid = id.substring(0, id.indexOf("."));
    SDTReader r = new SDTReader();
    r.setId(id);
    int bins = r.getTimeBinCount();
    int chan = r.getChannelCount();
    int w = r.getSizeX();
    int h = r.getSizeY();
    System.out.println("Data is " + w + " x " + h +
      ", bins=" + bins + ", channels=" + chan);
    byte[][] data = new byte[chan][h * w * bins * 2];
    for (int c=0; c<chan; c++) {
      System.out.println("Reading channel #" + c + "...");
      r.openBytes(c, data[c]);
      int i = 0;
      for (int y=0; y<h; y++) {
        for (int x=0; x<w; x++) {
          String oid = tid + "-c" + c + "-row" + y + "-col" + x;
          System.out.println(oid);
          PrintWriter out = new PrintWriter(new FileWriter(oid));
          for (int b=0; b<bins; b++) {
            i += 2;
            int v0 = data[c][i];
            int v1 = data[c][i + 1];
            int v = (v0 << 8) & v1;
            out.println(b + " " + v);
          }
          out.close();
        }
      }
    }
  }

}
