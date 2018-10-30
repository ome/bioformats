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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class ConfigurationTreeTest {

  ConfigurationTree configTree = new ConfigurationTree("/data", "/config");

  @DataProvider(name = "configList")
  public Object[][] createConfigList() {
    return new Object[][]{
      {"/config", "/data"},
      {"/config/", "/data"},
      {"/config/test", "/data/test"},
      {"/config/test/test2/test3/", "/data/test/test2/test3"},
    };
  }

  @DataProvider(name = "rootList")
  public Object[][] createRootList() {
    return new Object[][]{
      {"/data", "/config"},
      {"/data/", "/config"},
      {"/data/test", "/config/test"},
      {"/data/test/test2/test3/", "/config/test/test2/test3"},
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
}
