package loci.formats;

import java.io.*;

public class OutputFilter extends PrintStream {

  // -- Constructors --

  public OutputFilter(OutputStream out) {
    super(out);
  }

  public OutputFilter(OutputStream out, boolean autoFlush) {
    super(out, autoFlush);
  }

  public OutputFilter(OutputStream out, boolean autoFlush, String encoding) {
    this(out, autoFlush);
  }

  // -- PrintStream API methods --

  public void print(String s) {
    if (!s.trim().startsWith("WARN:")) {
      super.print(s);
    }
  }

  public void println(String x) {
    print(x);
    if (!x.trim().startsWith("WARN:")) {
      println();
    }
  }

}
