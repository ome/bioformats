/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import loci.common.DataTools;

/**
 * Recursively searchs a directory for files that have not been configured for
 * testing.
 */
public class UnconfiguredFiles {

  private ArrayList<String> unconfigured = new ArrayList<String>();

  public void buildUnconfiguredList(File root) throws IOException {
    if (!root.isDirectory()) return;
    String[] list = root.list();
    File configFile = new File(root, ".bioformats");
    String configData = null;
    if (configFile.exists()) {
      configData = DataTools.readFile(configFile.getAbsolutePath());
    }

    for (String file : list) {
      File child = new File(root, file).getAbsoluteFile();
      if (file.startsWith(".")) continue;
      else if (child.isDirectory()) buildUnconfiguredList(child);
      else if (!configFile.exists() ||
        configData.indexOf("\"" + child.getName() + "\"") < 0)
      {
        unconfigured.add(child.getAbsolutePath());
      }
    }
  }

  public void printList() {
    if (unconfigured.size() > 0) {
      System.out.println("Unconfigured files:");
      for (String file : unconfigured) {
        System.out.println("  " + file);
      }
    }
    else System.out.println("All files have been configured!");
  }

  public static void main(String[] args) throws IOException {
    UnconfiguredFiles f = new UnconfiguredFiles();
    f.buildUnconfiguredList(new File(args[0]));
    f.printList();
  }

}
