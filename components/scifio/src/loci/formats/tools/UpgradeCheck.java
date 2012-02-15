//
// UpgradeCheck.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.tools;

import loci.formats.UpgradeChecker;

/**
 * Utility that checks for a new stable version, and optionally installs
 * a new version of loci_tools.jar.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/UpgradeCheck.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/UpgradeCheck.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class UpgradeCheck {

  public static void main(String[] args) {
    if (args.length > 0 && args[0].equals("-help")) {
      System.out.println("Usage:");
      System.out.println("  java UpgradeCheck [-install X]");
      System.out.println();
      System.out.println("    If no options are specified, an upgrade check");
      System.out.println("    will be performed but no new version will be");
      System.out.println("    downloaded.");
      System.out.println("    With the '-install' option, a version must be");
      System.out.println("    specified; valid values are 'STABLE', 'TRUNK',");
      System.out.println("    and 'DAILY'.  The corresponding build will be");
      System.out.println("    downloaded to the working directory.");
      System.exit(0);
    }

    UpgradeChecker checker = new UpgradeChecker();

    boolean doInstall = args.length > 0 && args[0].equals("-install");

    if (checker.newVersionAvailable()) {
      System.out.println("A newer stable version is available.");
    }
    else {
      System.out.println("A newer stable version is not available.");
    }

    if (doInstall && args.length > 1) {
      String url = "";
      if (args[1].equals("TRUNK")) {
        url = UpgradeChecker.TRUNK_BUILD;
      }
      else if (args[1].equals("DAILY")) {
        url = UpgradeChecker.DAILY_BUILD;
      }
      else if (args[1].equals("STABLE")) {
        url = UpgradeChecker.STABLE_BUILD;
      }
      url += UpgradeChecker.TOOLS;
      checker.install(url, UpgradeChecker.TOOLS);
    }
  }

}
