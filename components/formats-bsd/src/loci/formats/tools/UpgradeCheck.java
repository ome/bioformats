/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.tools;

import loci.formats.UpgradeChecker;

/**
 * Utility that checks for a new stable version, and optionally installs
 * a new version of Bio-Formats ueber tools.
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

    if (checker.newVersionAvailable(UpgradeChecker.DEFAULT_CALLER)) {
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
