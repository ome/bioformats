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

import java.nio.file.Paths;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class ConfigurationTreeTest {

  ConfigurationTree configTree =
    new ConfigurationTree(path("/data"), path("/config"));

  @DataProvider(name = "configList")
  public Object[][] createConfigList() {
    return new Object[][]{
      {path("/config"), path("/data")},
      {path("/config/"), path("/data")},
      {path("/config/test"), path("/data/test")},
      {path("/config/test/test2/test3/"), path("/data/test/test2/test3")},
      {path("/data"), path("/data")},
      {path("/data/test"), path("/data/test")},
      {path("/data2"), path("/data2")},
      {path("/data2/test"), path("/data2/test")},
    };
  }

  @DataProvider(name = "rootList")
  public Object[][] createRootList() {
    return new Object[][]{
      {path("/data"), path("/config")},
      {path("/data/"), path("/config")},
      {path("/data/test"), path("/config/test")},
      {path("/data/test/test2/test3/"), path("/config/test/test2/test3")},
      {path("/config"), path("/config")},
      {path("/config/test"), path("/config/test")},
      {path("/config2"), path("/config2")},
      {path("/config2/test"), path("/config2/test")},
    };
  }

  @Test(dataProvider = "configList")
  public void testRelocateToRoot(String path, String expected) {
    assertEquals(configTree.relocateToRoot(path), expected);
  }

  @Test(dataProvider = "rootList")
  public void testRelocateToConfig(String path, String expected) {
    assertEquals(configTree.relocateToConfig(path), expected);
  }

  /**
   * Turn the specified path into a system-specific absolute path.
   * On UNIX-based systems, this should return the original path.
   * On Windows systems, the drive letter of the working directory
   * will be prepended.
   */
  private String path(String path) {
    return Paths.get(path).toAbsolutePath().toString();
  }
}
