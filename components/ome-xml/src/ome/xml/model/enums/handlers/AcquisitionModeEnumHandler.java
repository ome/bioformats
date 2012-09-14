/*
 * ome.xml.model.enums.handlers.AcquisitionModeHandler
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2005-@year@ Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via xsd-fu on 2012-09-10 13:40:24-0400
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model.enums.handlers;

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ome.xml.model.enums.Enumeration;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.AcquisitionMode;

/**
 * Enumeration handler for AcquisitionMode.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/enums/handler/AcquisitionModeHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/enums/handler/AcquisitionModeHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class AcquisitionModeEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(AcquisitionModeEnumHandler.class);

  // -- Fields --

  /** Every AcquisitionMode value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*WideField\\s*", "WideField");
    p.put("^\\s*LaserScanningConfocalMicroscopy\\s*", "LaserScanningConfocalMicroscopy");
    p.put("^\\s*SpinningDiskConfocal\\s*", "SpinningDiskConfocal");
    p.put("^\\s*SlitScanConfocal\\s*", "SlitScanConfocal");
    p.put("^\\s*MultiPhotonMicroscopy\\s*", "MultiPhotonMicroscopy");
    p.put("^\\s*StructuredIllumination\\s*", "StructuredIllumination");
    p.put("^\\s*SingleMoleculeImaging\\s*", "SingleMoleculeImaging");
    p.put("^\\s*TotalInternalReflection\\s*", "TotalInternalReflection");
    p.put("^\\s*FluorescenceLifetime\\s*", "FluorescenceLifetime");
    p.put("^\\s*SpectralImaging\\s*", "SpectralImaging");
    p.put("^\\s*FluorescenceCorrelationSpectroscopy\\s*", "FluorescenceCorrelationSpectroscopy");
    p.put("^\\s*NearFieldScanningOpticalMicroscopy\\s*", "NearFieldScanningOpticalMicroscopy");
    p.put("^\\s*SecondHarmonicGenerationImaging\\s*", "SecondHarmonicGenerationImaging");
    p.put("^\\s*PALM\\s*", "PALM");
    p.put("^\\s*STORM\\s*", "STORM");
    p.put("^\\s*STED\\s*", "STED");
    p.put("^\\s*TIRF\\s*", "TIRF");
    p.put("^\\s*FSM\\s*", "FSM");
    p.put("^\\s*LCM\\s*", "LCM");
    p.put("^\\s*Other\\s*", "Other");
    // BEGIN custom enumeration mappings
    p.put(".*Widefield.*", "WideField");
    return p;
  }

  // -- IEnumerationHandler API methods --

  /* @see IEnumerationHandler#getEnumeration(String) */
  public Enumeration getEnumeration(String value)
    throws EnumerationException {
    if (value != null) {
      for (String pattern : patterns.keySet()) {
        if (value.toLowerCase().matches(pattern.toLowerCase())) {
          String v = patterns.get(pattern);
          return AcquisitionMode.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "AcquisitionMode", value);
    return AcquisitionMode.OTHER;
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return AcquisitionMode.class;
  }

}
