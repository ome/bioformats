//
// About.java
//

/*
LOCI Checkstyle checks, coded in 2006-@year@ by Curtis Rueden.
Permission is granted to use this code for anything.
*/

package loci.checks;

import javax.swing.JOptionPane;

/** Displays a small information dialog about this package. */
public final class About {

  private About() { }

  public static void main(String[] args) {
    JOptionPane.showMessageDialog(null,
      "LOCI Checkstyle checks, built @date@\n" +
      "Download Checkstyle from http://checkstyle.sourceforge.net/\n" +
      "Download LOCI software from http://www.loci.wisc.edu/software/",
      "LOCI Checkstyle checks", JOptionPane.INFORMATION_MESSAGE);
    System.exit(0);
  }

}
