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

package loci.plugins.in;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ij.Macro;

import java.io.IOException;

import org.junit.Test;

/**
 * Test to ensure that old macro keys (pre-4.2) are still handled correctly.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/test/loci/plugins/in/MacroTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/test/loci/plugins/in/MacroTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class MacroTest {

  // -- Constants --

  private static final String OLD_MERGE =
    "merge_channels view=Hyperstack stack_order=XYCZT ";
  private static final String NEW_MERGE =
    "color_mode=Composite view=Hyperstack stack_order=XYCZT ";

  private static final String OLD_AUTO_COLORIZE =
    "rgb_colorize view=Hyperstack stack_order=XYCZT ";
  private static final String NEW_AUTO_COLORIZE =
    "color_mode=Colorized view=Hyperstack stack_order=XYCZT ";

  private static final String OLD_CUSTOM_COLORIZE =
    "custom_colorize view=Hyperstack stack_order=XYCZT " +
    "series_0_channel_0_red=231 series_0_channel_0_green=100 " +
    "series_0_channel_0_blue=136 series_0_channel_1_red=143 " +
    "series_0_channel_1_green=214 series_0_channel_1_blue=100 " +
    "series_0_channel_2_red=240 series_0_channel_2_green=200 " +
    "series_0_channel_2_blue=120 ";
  private static final String NEW_CUSTOM_COLORIZE =
    "color_mode=Custom view=Hyperstack stack_order=XYCZT " +
    "series_0_channel_0_red=231 series_0_channel_0_green=100 " +
    "series_0_channel_0_blue=136 series_0_channel_1_red=143 " +
    "series_0_channel_1_green=214 series_0_channel_1_blue=100 " +
    "series_0_channel_2_red=240 series_0_channel_2_green=200 " +
    "series_0_channel_2_blue=120 ";

  private static final String COMPOSITE_MODE = "color_mode=Composite ";
  private static final String COLORIZED_MODE = "color_mode=Colorized ";

  // -- MacroTest methods --

  @Test
  public void testMergeChannels() {
    ImporterOptions oldOptions = getOptions(OLD_MERGE);
    ImporterOptions newOptions = getOptions(NEW_MERGE);
    assertEquals(oldOptions, newOptions);
  }

  @Test
  public void testAutoColorizeChannels() {
    ImporterOptions oldOptions = getOptions(OLD_AUTO_COLORIZE);
    ImporterOptions newOptions = getOptions(NEW_AUTO_COLORIZE);
    assertEquals(oldOptions, newOptions);
  }

  @Test
  public void testCustomColorizeChannels() {
    ImporterOptions oldOptions = getOptions(OLD_CUSTOM_COLORIZE);
    ImporterOptions newOptions = getOptions(NEW_CUSTOM_COLORIZE);
    assertEquals(oldOptions, newOptions);
  }

  @Test
  public void testDisjointColorModes() {
    ImporterOptions composite = getOptions(COMPOSITE_MODE);
    ImporterOptions colorized = getOptions(COLORIZED_MODE);
    assertFalse(composite.equals(colorized));
    assertEquals(composite.getColorMode(), "Composite");
    assertEquals(colorized.getColorMode(), "Colorized");
  }

  @Test
  public void testEqualMacros() {
    ImporterOptions compositeA = getOptions(COMPOSITE_MODE);
    ImporterOptions compositeB = getOptions(COMPOSITE_MODE);
    assertEquals(compositeA, compositeB);
    assertEquals(compositeA.getColorMode(), "Composite");
    assertEquals(compositeB.getColorMode(), "Composite");
  }

  // -- Helper methods --

  private ImporterOptions getOptions(String macro) {
    // Manually set the current thread's name, so that Macro.setOptions and
    // Macro.getOptions will behave as expected.  See the implementation of
    // Macro.getOptions() for more information.
    Thread current = Thread.currentThread();
    current.setName("Run$_" + current.getName());
    try {
      Macro.setOptions(macro);
      ImporterOptions options = new ImporterOptions();
      options.parseArg(macro);
      options.checkObsoleteOptions();
      return options;
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    return null;
  }

}
