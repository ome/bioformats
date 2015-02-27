/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.tests.testng;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * A TestNG listener that displays a "." for each passed test,
 * an "F" for each failure and an "S" for each skip. Adapted from an
 * <a href="http://testng.org/doc/documentation-main.html#logging">example</a>
 * on the TestNG web site.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/DotTestListener.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/DotTestListener.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class DotTestListener implements ITestListener {

  private int count = 0;

  public void onTestFailure(ITestResult tr) { log("F"); }
  public void onTestSkipped(ITestResult tr) { log("-"); }
  public void onTestSuccess(ITestResult tr) { log("."); }

  public void onFinish(ITestContext tc) { }
  public void onStart(ITestContext tc) { }
  public void onTestFailedButWithinSuccessPercentage(ITestResult tr) { }
  public void onTestStart(ITestResult tr) { }

  private void log(String s) {
    System.out.print(s);
    if (++count % 40 == 0) System.out.println();
  }

}
