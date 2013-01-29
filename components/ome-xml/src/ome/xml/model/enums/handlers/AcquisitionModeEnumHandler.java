/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:36.791434
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
