// SortList.java

import java.io.*;
import java.util.Arrays;
import java.util.Vector;

/** Sorts a text file alphabetically, removing duplicates. */
public class SortList {

  public static void main(String[] args) throws Exception {
    String file = args[0];
    Vector v = new Vector();
    BufferedReader fin = new BufferedReader(new FileReader(file));
    while (true) {
      String line = fin.readLine();
      if (line == null) break;
      v.add(line);
    }
    fin.close();
    String[] lines = new String[v.size()];
    v.copyInto(lines);
    Arrays.sort(lines);
    new File(file).renameTo(new File(file + ".old"));
    PrintWriter fout = new PrintWriter(new FileWriter(file));
    for (int i=0; i<lines.length; i++) {
      if (i == 0 || !lines[i].equals(lines[i-1])) fout.println(lines[i]);
    }
    fout.close();
  }

}
