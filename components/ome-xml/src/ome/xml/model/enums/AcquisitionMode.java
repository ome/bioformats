/*
 * ome.xml.model.enums.AcquisitionMode
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007 Open Microscopy Environment
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
 * Created by melissa via xsd-fu on 2012-09-10 13:40:23-0400
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model.enums;

public enum AcquisitionMode implements Enumeration
{
  WIDEFIELD("WideField"), LASERSCANNINGCONFOCALMICROSCOPY("LaserScanningConfocalMicroscopy"), SPINNINGDISKCONFOCAL("SpinningDiskConfocal"), SLITSCANCONFOCAL("SlitScanConfocal"), MULTIPHOTONMICROSCOPY("MultiPhotonMicroscopy"), STRUCTUREDILLUMINATION("StructuredIllumination"), SINGLEMOLECULEIMAGING("SingleMoleculeImaging"), TOTALINTERNALREFLECTION("TotalInternalReflection"), FLUORESCENCELIFETIME("FluorescenceLifetime"), SPECTRALIMAGING("SpectralImaging"), FLUORESCENCECORRELATIONSPECTROSCOPY("FluorescenceCorrelationSpectroscopy"), NEARFIELDSCANNINGOPTICALMICROSCOPY("NearFieldScanningOpticalMicroscopy"), SECONDHARMONICGENERATIONIMAGING("SecondHarmonicGenerationImaging"), PALM("PALM"), STORM("STORM"), STED("STED"), TIRF("TIRF"), FSM("FSM"), LCM("LCM"), OTHER("Other");
  
  private AcquisitionMode(String value)
  {
    this.value = value;
  }

  public static AcquisitionMode fromString(String value)
    throws EnumerationException
  {
    if ("WideField".equals(value))
    {
      return WIDEFIELD;
    }
    if ("LaserScanningConfocalMicroscopy".equals(value))
    {
      return LASERSCANNINGCONFOCALMICROSCOPY;
    }
    if ("SpinningDiskConfocal".equals(value))
    {
      return SPINNINGDISKCONFOCAL;
    }
    if ("SlitScanConfocal".equals(value))
    {
      return SLITSCANCONFOCAL;
    }
    if ("MultiPhotonMicroscopy".equals(value))
    {
      return MULTIPHOTONMICROSCOPY;
    }
    if ("StructuredIllumination".equals(value))
    {
      return STRUCTUREDILLUMINATION;
    }
    if ("SingleMoleculeImaging".equals(value))
    {
      return SINGLEMOLECULEIMAGING;
    }
    if ("TotalInternalReflection".equals(value))
    {
      return TOTALINTERNALREFLECTION;
    }
    if ("FluorescenceLifetime".equals(value))
    {
      return FLUORESCENCELIFETIME;
    }
    if ("SpectralImaging".equals(value))
    {
      return SPECTRALIMAGING;
    }
    if ("FluorescenceCorrelationSpectroscopy".equals(value))
    {
      return FLUORESCENCECORRELATIONSPECTROSCOPY;
    }
    if ("NearFieldScanningOpticalMicroscopy".equals(value))
    {
      return NEARFIELDSCANNINGOPTICALMICROSCOPY;
    }
    if ("SecondHarmonicGenerationImaging".equals(value))
    {
      return SECONDHARMONICGENERATIONIMAGING;
    }
    if ("PALM".equals(value))
    {
      return PALM;
    }
    if ("STORM".equals(value))
    {
      return STORM;
    }
    if ("STED".equals(value))
    {
      return STED;
    }
    if ("TIRF".equals(value))
    {
      return TIRF;
    }
    if ("FSM".equals(value))
    {
      return FSM;
    }
    if ("LCM".equals(value))
    {
      return LCM;
    }
    if ("Other".equals(value))
    {
      return OTHER;
    }
    String s = String.format("%s not a supported value of %s",
                             value, AcquisitionMode.class);
    throw new EnumerationException(s);
  }

  public String getValue()
  {
    return value;
  }

  @Override
  public String toString()
  {
    return value;
  }

  private final String value;
}
