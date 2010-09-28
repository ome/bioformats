//
// MakeData.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

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

import java.io.*;
import java.util.Random;

/**
 * Utility for synthesizing test data for use with curve fitting algorithms.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/slim-plotter/data/MakeData.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/slim-plotter/data/MakeData.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public class MakeData {

  public static void main(String[] args) throws Exception {

    int maxScale = 10000;
    int maxExp = 3000;
    int factors = 1;
    double error = 0.001;
    int maxC = 0;
    int points = 50;
    double timeinc = 0.001;

    if (args.length % 2 == 0) {
      System.out.println("MakeData must have an odd number of args");
      displayHelp();
      System.exit(0);
    }

    //File outfile = new File(args[0]);
    PrintWriter out =
      new PrintWriter(new BufferedWriter(new FileWriter(args[0])));

    for (int i = 1; i < args.length; i += 2) {
      String token = args[i];
      if (token.equals("maxScale")) {
        maxScale = Integer.parseInt(args[i+1]);
      }
      else if (token.equals("maxExp")) {
        maxExp = Integer.parseInt(args[i+1]);
      }
      else if (token.equals("factors")) {
        factors = Integer.parseInt(args[i+1]);
      }
      else if (token.equals("error")) {
        error = Double.parseDouble(args[i+1]);
      }
      else if (token.equals("maxC")) {
        maxC = Integer.parseInt(args[i+1]);
      }
      else if (token.equals("datapoints")) {
        points = Integer.parseInt(args[i+1]);
      }
      else if (token.equals("timeinc")) {
        timeinc = Double.parseDouble(args[i+1]);
      }
      else {
        System.out.println("Unrecognized token " + token);
        displayHelp();
        System.exit(0);
      }
    }

    int[] a = new int[factors];
    int[] b = new int[factors];
    Random r = new Random();

    for (int i = 0; i < factors; i++) {
      a[i] = r.nextInt(maxScale) + 1;
      b[i] = r.nextInt(maxExp) + 1;
    }

    int c = 0;
    if (maxC != 0) c = r.nextInt(maxC) + 1;

    out.println("maxScale " + maxScale);
    out.println("maxExp " + maxExp);
    out.println("factors " + factors);
    out.println("error " + error);
    out.println("maxC " + maxC);
    out.println("datapoints " + points);
    out.println("timeinc " + timeinc);
    for (int i = 0; i < factors; i++) {
      out.println("a " + i + " " + a[i]);
      out.println("b " + i + " " + b[i]);
    }
    out.println("c " + c);
    int[] datapoints = new int[points];
    for (int i = 0; i < points; i++) {
      int thispoint = 0;
      for (int j = 0; j < factors; j++) {
        thispoint += (int)
          (a[j] * Math.pow(Math.E, -b[j] * (i+1) * timeinc) + c);
      }
      double errorfactor = (1.0 - error) + r.nextDouble() * (2 * error);
      thispoint *= errorfactor;
      out.println((i + 1) + " " + thispoint);
    }
    out.close();
  }

  public static void displayHelp() {
    System.out.println("Possible flags:");
    System.out.println("    maxScale <scale>");
    System.out.println("    maxExp <exponent>");
    System.out.println("    factors <factors>");
    System.out.println("    error <error from 0-1>");
    System.out.println("    maxC <c>");
    System.out.println("    datapoints <points>");
    System.out.println("    timeinc <time increment>");
    System.out.println(" example:  java MakeData " +
      "maxScale 100 maxExp 2000 datapoints 100 factors 3 error .001 maxC 0");
  }

}
