//
// About.java
//

/*
LOCI Checkstyle checks, coded in 2006-@year@ by Curtis Rueden.
Permission is granted to use this code for anything.
*/

package loci.checks;

import javax.swing.JOptionPane;

/**
 * Displays a small information dialog about this package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/checkstyle/src/loci/checks/About.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/checkstyle/src/loci/checks/About.java">SVN</a></dd></dl>
 */
public final class About {

  /** URL of LOCI Software web page. */
  public static final String URL_LOCI_SOFTWARE =
    "http://www.loci.wisc.edu/software";

  /** URL of Checkstyle web page. */
  public static final String URL_CHECKSTYLE =
    "http://checkstyle.sourceforge.net/";

  private About() { }

  public static void main(String[] args) {
    JOptionPane.showMessageDialog(null,
      "LOCI Checkstyle checks, revision @svn.revision@, built @date@\n" +
      "Download Checkstyle from " + URL_CHECKSTYLE + "\n" +
      "Download LOCI software from " + URL_LOCI_SOFTWARE,
      "LOCI Checkstyle checks", JOptionPane.INFORMATION_MESSAGE);
    System.exit(0);
  }

}
