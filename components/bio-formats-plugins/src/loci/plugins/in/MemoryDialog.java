/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
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

package loci.plugins.in;

import ij.gui.GenericDialog;

/**
 * Bio-Formats Importer memory usage confirmation dialog box.
 */
public class MemoryDialog extends ImporterDialog {

  // -- Constants --

  /** Minimum amount of wiggle room for available memory, in bytes. */
  private static final long MINIMUM_MEMORY_PADDING = 20 * 1024 * 1024; // 20 MB

  // -- Fields --

  /** Required memory for the dataset, in bytes. */
  private long needMem;

  /** Remaining memory for the JVM. */
  private long availMem;

  // -- Constructor --

  /** Creates a memory confirmation dialog for the Bio-Formats Importer. */
  public MemoryDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    if (process.isWindowless()) return false;
    if (process.getOptions().getStackFormat().equals(ImporterOptions.VIEW_NONE))
    {
      return false;
    }
    needMem = process.getMemoryUsage();
    availMem = getAvailableMemory();
    // NB: Prompt if dataset will leave too little memory available.
    return availMem - needMem < MINIMUM_MEMORY_PADDING;
  }

  @Override
  protected GenericDialog constructDialog() {
    final long needMB = needMem / 1048576;
    final long availMB = availMem / 1048576;
    GenericDialog gd = new GenericDialog("Bio-Formats Memory Usage");
    gd.addMessage("Warning: It will require approximately " + needMB +
      " MB to open this dataset.\nHowever, only " + availMB +
      " MB is currently available. You may receive an error\n" +
      "message about insufficient memory. Are you sure you want to proceed?");
    return gd;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    return gd.wasOKed();
  }

  // -- Helper methods --

  private long getAvailableMemory() {
    final Runtime r = Runtime.getRuntime();
    final long usedMem = r.totalMemory() - r.freeMemory();
    return r.maxMemory() - usedMem;
  }

}
