//
// BlankLinesCheck.java
//

/*
LOCI Checkstyle checks, coded in 2006-@year@ by Curtis Rueden.
Permission is granted to use this code for anything.
*/

package loci.checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * A Checkstyle check for identifying multiple consecutive blank lines.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/checks/BlankLinesCheck.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/checks/BlankLinesCheck.java">SVN</a></dd></dl>
 */
public class BlankLinesCheck extends Check {

  // -- Fields --

  /** Maximum allowed number of consecutive blank lines. */
  private int max = 1;

  // -- BlankLinesCheck API methods --

  /** Sets the maximum allowed number of consecutive blank lines. */
  public void setMax(int max) { this.max = max; }

  // -- Check API methods --

  /* @see com.puppycrawl.tools.checkstyle.api.Check#getDefaultTokens() */
  public int[] getDefaultTokens() {
    return new int[0];
  }

  /* @see com.puppycrawl.tools.checkstyle.api.Check#beginTree(DetailAST) */
  public void beginTree(DetailAST aRootAST) {
    String[] lines = getLines();
    int count = 0;
    for (int i=0; i<lines.length; i++) {
      boolean blank = lines[i].trim().equals("");
      if (blank) count++;
      else {
        if (count > max) {
          log(i - count + 1, 0, "blank.lines",
            new Object[] {new Integer(count)});
        }
        count = 0;
      }
    }
  }

}
