//
// LociConfig.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.config;

import ij.plugin.PlugIn;

/**
 * TODO
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/config/LociConfig.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/config/LociConfig.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LociConfig implements PlugIn {

  // -- Fields --

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    ConfigWindow cw = new ConfigWindow();
    cw.setVisible(true);

    // Two modes: loci_tools.jar mode, and split JARs mode
    // Up to date, present items are GREEN
    // Out of date items are ORANGE (e.g., ImageJ <1.39)
    // Missing items are RED

    // Options tab?

    // Formats tab
    // - left side: list of all reader classes
    // - right side: options for that reader:
    //  + ability to toggle reader on or off (hacks default classes list)
    //  + other reader-specific options
    //   - QT: "Use QuickTime for Java" checkbox (default off)
    //   - ND2: "Use Nikon's ND2 plugin" checkbox (default off)
    //   - Flex: "LuraWave license code" label and text field (default nothing)
    //    + if sys prop set, but ij prop not set, GRAY OUT AND DO NOT SHOW
    //   - SDT: "Merge lifetime bins to intensity" checkbox

    // Libraries tab with full list of libraries
    // - top:
    //  + 
    // - left side: list of all libraries
    // -            filter to control which kind of libraries are shown?
    // - right side: information on that library:
    //  + Type -- "Core library", "Java library",
    //            "ImageJ plugin", "Native library"
    //  + Status -- INSTALLED or MISSING
    //  + Version -- version #, or blank if missing
    //  + Install/Upgrade button -- next to version
    //   - can upgrade any JAR from LOCI repository
    //    + upgrade button for "ImageJ" just launches ImageJ upgrade plugin
    //   - can install native libs by downloading installer from its web site:
    //    + QuickTime for Java
    //    + Nikon ND2 plugin
    //    + ImageIO Tools
    //  + Files -- JAR file(s) or other relevant file(s)
    //  + URL(s)
    //  + Notes
    //   - brief explanation of what the library is
    //   - for LOCI plugins, whether running in Developer mode (split JARs)
    //  + License if applicable
    //  + "How to install" paragraph

    // Core libraries:
    // - ImageJ
    // - Bio-Formats
    // - MATLAB
    // - Jython
    // - Java3D
    // - Java (SDK vs JRE?)
    //
    // ImageJ plugins:
    // - LOCI plugins
    // - Image5D
    // - View5D
    //
    // Java libraries:
    // - LuraWave j2sdk...jar
    // - bio-formats.jar \
    // - ${libs.bioformats} \
    // -   bufr-1.1.00.jar \
    // -   clibwrapper_jiio.jar \
    // -   grib-5.1.03.jar \
    // -   jai_imageio.jar \
    // -   mdbtools-java.jar \
    // -   netcdf-4.0.jar \
    // -   poi-loci.jar \
    // -   QTJava.zip \
    // -   slf4j-jdk14.jar
    // - ome-java.jar \
    // - ${libs.omejava} \
    // -   commons-httpclient-2.0-rc2.jar \
    // -   commons-logging.jar \
    // -   xmlrpc-1.2-b1.jar
    // - omero-common.jar \
    // - omero-client.jar \
    // - spring.jar \
    // - jbossall-client.jar \
    // - forms-1.0.4.jar \
    // - loci_plugins.jar \
    // - ome-notes.jar
    //
    // Native libraries:
    // - QTJava
    // - Nikon ND2
    // - ImageIO Tools (lossless JPEG)
  }

  public static void main(String[] args) {
    new LociConfig().run(null);
  }

}
