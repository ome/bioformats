//
// EnumTest.java
//

/*
LOCI software manual test suite. Copyright (C) 2007-@year@
Curtis Rueden and Melissa Linkert. All rights reserved.

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

package loci.tests;

import java.util.Vector;
import loci.formats.enums.*;

/**
 * A class for testing the {@link loci.formats.enums} package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/test-suite/src/loci/tests/EnumTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/test-suite/src/loci/tests/EnumTest.java">SVN</a></dd></dl>
 */
public class EnumTest {

  private static Vector<TestCase> cases = makeCases();

  private static Vector<TestCase> makeCases() {
    Vector<TestCase> c = new Vector<TestCase>();
    c.add(
      new TestCase(Correction.class, "Violet Corrected", "VioletCorrected"));
    c.add(new TestCase(Correction.class, "PlanFluor", "PlanFluor"));
    c.add(new TestCase(DimensionOrder.class, "   XYCZT\t", "XYCZT"));
    c.add(new TestCase(Immersion.class, "gLyCeRol", "Glycerol"));
    return c;
  }

  static class TestCase {
    public Class<Enumeration> type;
    public String inputValue;
    public String expectedOutput;

    public TestCase(Class type, String input, String output) {
      this.type = (Class<Enumeration>) type;
      inputValue = input;
      expectedOutput = output;
    }
  }

  public static void main(String[] args) {
    IEnumerationProvider provider = new EnumerationProvider();
    for (TestCase t : cases) {
      try {
        Enumeration e = provider.getEnumeration(t.type, t.inputValue);
        boolean success = e.toString().equals(t.expectedOutput);
        if (success) {
          System.out.println(t.type.getSimpleName() +
            " test case passed for '" + t.inputValue + "'");
        }
        else {
          System.out.println(t.type.getSimpleName() +
            " test case failed for '" + t.inputValue + "'.  Got " + e +
            ", expected " + t.expectedOutput);
        }
      }
      catch (EnumerationException ee) {
        System.out.println(t.type.getSimpleName() + " test case failed for '" +
          t.inputValue + "':");
        ee.printStackTrace();
      }
    }
  }

}
