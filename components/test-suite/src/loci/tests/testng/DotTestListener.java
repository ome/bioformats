/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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
 */
public class DotTestListener implements ITestListener {

  private int count = 0;

  @Override
  public void onTestFailure(ITestResult tr) { log("F"); }
  @Override
  public void onTestSkipped(ITestResult tr) { log("-"); }
  @Override
  public void onTestSuccess(ITestResult tr) { log("."); }

  @Override
  public void onFinish(ITestContext tc) { }
  @Override
  public void onStart(ITestContext tc) { }
  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult tr) { }
  @Override
  public void onTestStart(ITestResult tr) { }

  private void log(String s) {
    System.out.print(s);
    if (++count % 40 == 0) System.out.println();
  }

}
