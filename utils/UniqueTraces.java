//
// UniqueTraces.java
//

import java.io.*;
import java.util.ArrayList;

/**
 * Reads in a bunch of stack traces from standard input,
 * filters out duplicates and outputs what's left.
 */
public class UniqueTraces {

  public static void main(String[] args) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    ArrayList<String> traces = new ArrayList<String>();
    StringBuffer buf = null;
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      if (!line.startsWith(" ") && !line.startsWith("\t")) {
        // start of a new stack trace
        if (buf == null) buf = new StringBuffer(); // first stack trace
        else {
          // record previous stack trace
          traces.add(buf.toString());
          buf.setLength(0);
        }
        // in the middle of a stack trace
      }
      buf.append(line);
      buf.append("\n");
    }
    traces.add(buf.toString());

    String[] traceStrings = new String[traces.size()];
    traces.toArray(traceStrings);

    for (int i=0; i<traceStrings.length; i++) {
      String s = traceStrings[i];
      boolean match = false;
      for (int j=0; j<traceStrings.length; j++) {
        if (i != j && s.equals(traceStrings[j])) {
          match = true;
          break;
        }
      }
      if (!match) System.out.print(s);
    }
  }

}
