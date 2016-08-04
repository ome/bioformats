/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
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

package loci.plugins.util;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageStatistics;
import ij.process.LUT;

import java.io.IOException;
import java.util.List;

import loci.formats.IFormatReader;
import loci.plugins.util.RecordedImageProcessor.MethodEntry;

/**
 * Extension of {@link ij.ImagePlus} that supports
 * Bio-Formats-driven virtual stacks.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class VirtualImagePlus extends ImagePlus {

  // -- Fields --

  private IFormatReader r;
  private LUT[] luts;

  // -- Constructor --

  public VirtualImagePlus(String title, ImageStack stack) {
    super(title, stack);
    // call getStatistics() to ensure that single-slice stacks have the
    // correct pixel type
    getStatistics();
  }

  // -- VirtualImagePlus API methods --

  public void setReader(IFormatReader r) {
    this.r = r;
  }

  public void setLUTs(LUT[] luts) {
    this.luts = luts;
  }

  // -- ImagePlus API methods --

  @Override
  public synchronized void setSlice(int index) {
    super.setSlice(index);

    BFVirtualStack stack = null;
    if (getStack() instanceof BFVirtualStack) {
      stack = (BFVirtualStack) getStack();
      RecordedImageProcessor proc = stack.getRecordedProcessor();
      List<MethodEntry> methods = stack.getMethodStack();
      if (methods != null) {
        proc.applyMethodStack(methods);
      }
      // if we call setProcessor(getTitle(), proc), the type will be set
      // to GRAY32 (regardless of the actual processor type)
      setProcessor(getTitle(), proc.getChild());
      int channel = getChannel() - 1;
      if (channel >= 0 && luts != null && channel < luts.length) {
        getProcessor().setColorModel(luts[channel]);
      }
      this.ip = proc;
    }
  }

  @Override
  public void close() {
    super.close();
    try {
      r.close();
    }
    catch (IOException e) { }
  }

  @Override
  public ImageStatistics getStatistics(int mOptions, int nBins,
    double histMin, double histMax)
  {
    if (this.ip instanceof RecordedImageProcessor) {
      RecordedImageProcessor currentProc = (RecordedImageProcessor) this.ip;
      this.ip = currentProc.getChild();
      setProcessor(getTitle(), this.ip);
      ImageStatistics s =
        super.getStatistics(mOptions, nBins, histMin, histMax);
      this.ip = currentProc;
      return s;
    }
    return super.getStatistics(mOptions, nBins, histMin, histMax);
  }

}
