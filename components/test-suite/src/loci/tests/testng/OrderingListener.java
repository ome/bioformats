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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/OrderingListener.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/OrderingListener.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class OrderingListener implements IMethodInterceptor {

  public List<IMethodInstance> intercept(List<IMethodInstance> methods,
    ITestContext context)
  {
    IMethodInstance[] originalMethods =
      methods.toArray(new IMethodInstance[methods.size()]);

    Arrays.sort(originalMethods, new Comparator<IMethodInstance>() {
      public int compare(IMethodInstance m1, IMethodInstance m2) {
        FormatReaderTest i1 = (FormatReaderTest) m1.getInstances()[0];
        FormatReaderTest i2 = (FormatReaderTest) m2.getInstances()[0];
        String instance1 = i1.getID();
        String instance2 = i2.getID();
        int instance = instance1.compareTo(instance2);
        if (instance != 0) {
          return instance;
        }

        ITestNGMethod method1 = m1.getMethod();
        ITestNGMethod method2 = m2.getMethod();

        return method1.getMethodName().compareTo(method2.getMethodName());
      }
    });

    List<IMethodInstance> orderedMethods = new ArrayList<IMethodInstance>();
    for (IMethodInstance method : originalMethods) {
      orderedMethods.add(method);
    }
    return orderedMethods;
  }

}
