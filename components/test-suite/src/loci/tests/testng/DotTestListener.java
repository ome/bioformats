//
// DotTestListener.java
//

/*
LOCI software automated test suite for TestNG. Copyright (C) 2007-@year@
Melissa Linkert and Curtis Rueden. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loci.tests.testng;

import org.testng.*;

/**
 * A TestNG listener that displays a "." for each passed test,
 * an "F" for each failure and an "S" for each skip. Adapted from an
 * <a href="http://testng.org/doc/documentation-main.html#logging">example</a>
 * on the TestNG web site.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/tests/testng/DotTestListener.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/tests/testng/DotTestListener.java">SVN</a></dd></dl>
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
