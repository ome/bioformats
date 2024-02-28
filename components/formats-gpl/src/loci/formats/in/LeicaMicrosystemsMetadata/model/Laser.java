/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.in.LeicaMicrosystemsMetadata.model;

import java.util.Arrays;
import java.util.List;


/**
 * Data structure for laser information extracted from LMS XML
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class Laser {
  public String laserId;
  public String name;
  public double wavelength;
  public String wavelengthUnit;
  public boolean powerStateOn = false;
  public boolean shutterOpen = false;
  public boolean isFrap;
  public LaserSetting laserSetting;

  //SP5
  public enum LmsLightSourceQualifier
  {
    Unknown(0),
    Visible(10),
    PulsedVis(11),
    UV(20),
    IR(30),
    MP(40),
    MP2(41),
    ChaserVis(50),
    ChaserUV(60),
    WLL(70),
    STED1(80),
    STED2(81),
    STED3(82),
    STED4(83),
    CARS(90),
    Pump(91),
    Stokes(92),
    FSOPO(100);

    public final int value;

    private LmsLightSourceQualifier(int value){
      this.value = value;
    }

    public boolean Compare(int i){return value == i;}

    public static LmsLightSourceQualifier getValue(int id){
      LmsLightSourceQualifier[] values = LmsLightSourceQualifier.values();
      for (int i = 0; i < values.length; i++){
        if (values[i].Compare(id))
          return values[i];
      }
      return LmsLightSourceQualifier.Unknown;
    }
  }

  // SP8, STELLARIS
  public enum LmsLightSourceType
  {
    Unknown(-1),
    Visible(0),
    UV(1),
    PulsedSMD(2),
    MP(3),
    WLL(4),
    Sted(5),
    Cars(6),
    ExternSpecial(7);

    public final int value;

    private LmsLightSourceType(int value){
      this.value = value;
    }

    public boolean Compare(int i){return value == i;}

    public static LmsLightSourceType getValue(int id){
      LmsLightSourceType[] values = LmsLightSourceType.values();
      for (int i = 0; i < values.length; i++){
        if (values[i].Compare(id))
          return values[i];
      }
      return LmsLightSourceType.Unknown;
    }
  }

  public LmsLightSourceQualifier lightSourceQualifier;
  public LmsLightSourceType lightSourceType;

  public final List<Double> argonWavelengths = Arrays.asList(458.0, 476.0, 488.0, 496.0, 514.0);

  public boolean hasSameLightSourceType(LaserSetting laserSetting){
    return this.lightSourceQualifier != null && laserSetting.lightSourceQualifier != null ? 
    this.lightSourceQualifier == laserSetting.lightSourceQualifier : 
    this.lightSourceType != null && laserSetting.lightSourceType != null ?
    this.lightSourceType == laserSetting.lightSourceType : 
    false;
  }
}
