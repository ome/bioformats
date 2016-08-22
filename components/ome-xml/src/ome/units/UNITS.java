/*
 * #%L
 * The OME Data Model specification
 * %%
 * Copyright (C) 2014 - 2016 Open Microscopy Environment:
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

package ome.units;

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Quantity;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import java.math.BigInteger;

/**
 * Defines unit constants corresponding to unit classes in ome.units.unit.*
 *
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 * @version 1.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 5.1
 */
public final class UNITS
{
  public String getName()
  {
    return "OME-combined";
  }

  // Definitions of units that others are calculated from
  public static final ome.units.unit.Unit<Angle>             RADIAN  = ome.units.unit.Unit.<Angle>CreateBaseUnit("SI.RADIAN", "rad");
  public static final ome.units.unit.Unit<ElectricPotential> VOLT    = ome.units.unit.Unit.<ElectricPotential>CreateBaseUnit("SI.VOLT", "V");
  public static final ome.units.unit.Unit<Frequency>         HERTZ   = ome.units.unit.Unit.<Frequency>CreateBaseUnit("SI.HERTZ", "Hz");
  public static final ome.units.unit.Unit<Length>            METRE   = ome.units.unit.Unit.<Length>CreateBaseUnit("SI.METRE", "m");
  public static final ome.units.unit.Unit<Length>            INCH    = METRE.multiply(0.0254).setSymbol("in");
  public static final ome.units.unit.Unit<Power>             WATT    = ome.units.unit.Unit.<Power>CreateBaseUnit("SI.WATT", "W");
  public static final ome.units.unit.Unit<Pressure>          PASCAL  = ome.units.unit.Unit.<Pressure>CreateBaseUnit("SI.PASCAL", "Pa");
  public static final ome.units.unit.Unit<Temperature>       KELVIN  = ome.units.unit.Unit.<Temperature>CreateBaseUnit("SI.KELVIN", "K");
  public static final ome.units.unit.Unit<Time>              SECOND  = ome.units.unit.Unit.<Time>CreateBaseUnit("SI.SECOND", "s");

  // functions to provide standard SI scaling
  public static <T extends Quantity> ome.units.unit.Unit<T> YOTTA(ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "Y").multiply(BigInteger.TEN.pow(24).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> ZETTA(ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "Z").multiply(BigInteger.TEN.pow(21).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> EXA(  ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "E").multiply(BigInteger.TEN.pow(18).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> PETA( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "P").multiply(BigInteger.TEN.pow(15).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> TERA( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "T").multiply(BigInteger.TEN.pow(12).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> GIGA( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "G").multiply(BigInteger.TEN.pow(9).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> MEGA( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "M").multiply(BigInteger.TEN.pow(6).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> KILO( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "k").multiply(BigInteger.TEN.pow(3).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> HECTO(ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "h").multiply(BigInteger.TEN.pow(2).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> DECA( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol("da").multiply(BigInteger.TEN.pow(1).doubleValue());}

  public static <T extends Quantity> ome.units.unit.Unit<T> DECI( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "d").divide(BigInteger.TEN.pow(1).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> CENTI(ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "c").divide(BigInteger.TEN.pow(2).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> MILLI(ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "m").divide(BigInteger.TEN.pow(3).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> MICRO(ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "µ").divide(BigInteger.TEN.pow(6).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> NANO( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "n").divide(BigInteger.TEN.pow(9).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> PICO( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "p").divide(BigInteger.TEN.pow(12).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> FEMTO(ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "f").divide(BigInteger.TEN.pow(15).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> ATTO( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "a").divide(BigInteger.TEN.pow(18).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> ZEPTO(ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "z").divide(BigInteger.TEN.pow(21).doubleValue());}
  public static <T extends Quantity> ome.units.unit.Unit<T> YOCTO(ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol( "y").divide(BigInteger.TEN.pow(24).doubleValue());}

  // Frequency
  public static final ome.units.unit.Unit<Frequency> YOTTAHERTZ = YOTTA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> ZETTAHERTZ = ZETTA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> EXAHERTZ   = EXA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> PETAHERTZ  = PETA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> TERAHERTZ  = TERA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> GIGAHERTZ  = GIGA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> MEGAHERTZ  = MEGA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> KILOHERTZ  = KILO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> HECTOHERTZ = HECTO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> DECAHERTZ  = DECA(HERTZ);
  // HERTZ already defined
  public static final ome.units.unit.Unit<Frequency> DECIHERTZ  = DECI(HERTZ);
  public static final ome.units.unit.Unit<Frequency> CENTIHERTZ = CENTI(HERTZ);
  public static final ome.units.unit.Unit<Frequency> MILLIHERTZ = MILLI(HERTZ);
  public static final ome.units.unit.Unit<Frequency> MICROHERTZ = MICRO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> NANOHERTZ  = NANO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> PICOHERTZ  = PICO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> FEMTOHERTZ = FEMTO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> ATTOHERTZ  = ATTO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> ZEPTOHERTZ = ZEPTO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> YOCTOHERTZ = YOCTO(HERTZ);

  // Deprecated old names
  /**
   * @deprecated Use {@link #YOTTAHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> YOTTAHZ = YOTTAHERTZ;
  /**
   * @deprecated Use {@link #ZETTAHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> ZETTAHZ = ZETTAHERTZ;
  /**
   * @deprecated Use {@link #EXAHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> EXAHZ =   EXAHERTZ;
  /**
   * @deprecated Use {@link #PETAHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> PETAHZ =  PETAHERTZ;
  /**
   * @deprecated Use {@link #TERAHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> TERAHZ =  TERAHERTZ;
  /**
   * @deprecated Use {@link #GIGAHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> GIGAHZ =  GIGAHERTZ;
  /**
   * @deprecated Use {@link #MEGAHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> MEGAHZ =  MEGAHERTZ;
  /**
   * @deprecated Use {@link #KILOHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> KHZ =     KILOHERTZ;
  /**
   * @deprecated Use {@link #HECTOHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> HHZ =     HECTOHERTZ;
  /**
   * @deprecated Use {@link #DECAHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> DAHZ =    DECAHERTZ;
  /**
   * @deprecated Use {@link #HERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> HZ =      HERTZ;
  /**
   * @deprecated Use {@link #DECIHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> DHZ =     DECIHERTZ;
  /**
   * @deprecated Use {@link #CENTIHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> CHZ =     CENTIHERTZ;
  /**
   * @deprecated Use {@link #MILLIHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> MHZ =     MILLIHERTZ;
  /**
   * @deprecated Use {@link #MICROHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> MICROHZ = MICROHERTZ;
  /**
   * @deprecated Use {@link #NANOHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> NHZ =     NANOHERTZ;
  /**
   * @deprecated Use {@link #PICOHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> PHZ =     PICOHERTZ;
  /**
   * @deprecated Use {@link #FEMTOHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> FHZ =     FEMTOHERTZ;
  /**
   * @deprecated Use {@link #ATTOHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> AHZ =     ATTOHERTZ;
  /**
   * @deprecated Use {@link #ZEPTOHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> ZHZ =     ZEPTOHERTZ;
  /**
   * @deprecated Use {@link #YOCTOHERTZ} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> YHZ =     YOCTOHERTZ;

  // Length
  public static final ome.units.unit.Unit<Length> YOTTAMETRE =       YOTTA(METRE);
  public static final ome.units.unit.Unit<Length> ZETTAMETRE =       ZETTA(METRE);
  public static final ome.units.unit.Unit<Length> EXAMETRE =         EXA(METRE);
  public static final ome.units.unit.Unit<Length> PETAMETRE =        PETA(METRE);
  public static final ome.units.unit.Unit<Length> TERAMETRE =        TERA(METRE);
  public static final ome.units.unit.Unit<Length> GIGAMETRE =        GIGA(METRE);
  public static final ome.units.unit.Unit<Length> MEGAMETRE =        MEGA(METRE);
  public static final ome.units.unit.Unit<Length> KILOMETRE =        KILO(METRE);
  public static final ome.units.unit.Unit<Length> HECTOMETRE =       HECTO(METRE);
  public static final ome.units.unit.Unit<Length> DECAMETRE =        DECA(METRE);
  // METRE already defined
  public static final ome.units.unit.Unit<Length> DECIMETRE =        DECI(METRE);
  public static final ome.units.unit.Unit<Length> CENTIMETRE =       CENTI(METRE);
  public static final ome.units.unit.Unit<Length> MILLIMETRE =       MILLI(METRE);
  public static final ome.units.unit.Unit<Length> MICROMETRE =       MICRO(METRE);
  public static final ome.units.unit.Unit<Length> NANOMETRE =        NANO(METRE);
  public static final ome.units.unit.Unit<Length> PICOMETRE =        PICO(METRE);
  public static final ome.units.unit.Unit<Length> FEMTOMETRE =       FEMTO(METRE);
  public static final ome.units.unit.Unit<Length> ATTOMETRE =        ATTO(METRE);
  public static final ome.units.unit.Unit<Length> ZEPTOMETRE =       ZEPTO(METRE);
  public static final ome.units.unit.Unit<Length> YOCTOMETRE =       YOCTO(METRE);

  public static final ome.units.unit.Unit<Length> YOTTAMETER =       YOTTAMETRE;
  public static final ome.units.unit.Unit<Length> ZETTAMETER =       ZETTAMETRE;
  public static final ome.units.unit.Unit<Length> EXAMETER =         EXAMETRE;
  public static final ome.units.unit.Unit<Length> PETAMETER =        PETAMETRE;
  public static final ome.units.unit.Unit<Length> TERAMETER =        TERAMETRE;
  public static final ome.units.unit.Unit<Length> GIGAMETER =        GIGAMETRE;
  public static final ome.units.unit.Unit<Length> MEGAMETER =        MEGAMETRE;
  public static final ome.units.unit.Unit<Length> KILOMETER =        KILOMETRE;
  public static final ome.units.unit.Unit<Length> HECTOMETER =       HECTOMETRE;
  public static final ome.units.unit.Unit<Length> DECAMETER =        DECAMETRE;
  public static final ome.units.unit.Unit<Length> METER =            METRE;
  public static final ome.units.unit.Unit<Length> DECIMETER =        DECIMETRE;
  public static final ome.units.unit.Unit<Length> CENTIMETER =       CENTIMETRE;
  public static final ome.units.unit.Unit<Length> MILLIMETER =       MILLIMETRE;
  public static final ome.units.unit.Unit<Length> MICROMETER =       MICROMETRE;
  public static final ome.units.unit.Unit<Length> NANOMETER =        NANOMETRE;
  public static final ome.units.unit.Unit<Length> PICOMETER =        PICOMETRE;
  public static final ome.units.unit.Unit<Length> FEMTOMETER =       FEMTOMETRE;
  public static final ome.units.unit.Unit<Length> ATTOMETER =        ATTOMETRE;
  public static final ome.units.unit.Unit<Length> ZEPTOMETER =       ZEPTOMETRE;
  public static final ome.units.unit.Unit<Length> YOCTOMETER =       YOCTOMETRE;

  public static final ome.units.unit.Unit<Length> ANGSTROM =         NANOMETRE.divide(10).setSymbol("Å");
  public static final ome.units.unit.Unit<Length> THOU =             MILLI(INCH).setSymbol("thou");
  public static final ome.units.unit.Unit<Length> LINE =             INCH.divide(12).setSymbol("li");
  // INCH already defined
  public static final ome.units.unit.Unit<Length> FOOT =             INCH.multiply(12).setSymbol("ft");
  public static final ome.units.unit.Unit<Length> YARD =             FOOT.multiply(3).setSymbol("yd");
  public static final ome.units.unit.Unit<Length> MILE =             YARD.multiply(1760).setSymbol("mi");
  public static final ome.units.unit.Unit<Length> ASTRONOMICALUNIT = GIGA(METRE.multiply(149.597870700)).setSymbol("ua");
  public static final ome.units.unit.Unit<Length> LIGHTYEAR =        PETA(METRE.multiply(9.4607304725808)).setSymbol("ly");
  public static final ome.units.unit.Unit<Length> PARSEC =           GIGA(METRE.multiply(30856776)).setSymbol("pc"); // APPROXIMATE 3.0856776×10^16 m, exact would be UA.divide(tan(DEGREE.divide(3600)))
  public static final ome.units.unit.Unit<Length> POINT =            INCH.divide(72).setSymbol("pt");
  public static final ome.units.unit.Unit<Length> PIXEL =            ome.units.unit.Unit.<Length>CreateBaseUnit("Pixel", "pixel");
  public static final ome.units.unit.Unit<Length> REFERENCEFRAME =   ome.units.unit.Unit.<Length>CreateBaseUnit("ReferenceFrame", "reference frame");

  // Deprecated old names
  /**
   * @deprecated Use {@link #YOTTAMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> YOTTAM =     YOTTAMETRE;
  /**
   * @deprecated Use {@link #ZETTAMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> ZETTAM =     ZETTAMETRE;
  /**
   * @deprecated Use {@link #EXAMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> EXAM =       EXAMETRE;
  /**
   * @deprecated Use {@link #PETAMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> PETAM =      PETAMETRE;
  /**
   * @deprecated Use {@link #TERAMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> TERAM =      TERAMETRE;
  /**
   * @deprecated Use {@link #GIGAMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> GIGAM =      GIGAMETRE;
  /**
   * @deprecated Use {@link #MEGAMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> MEGAM =      MEGAMETRE;
  /**
   * @deprecated Use {@link #KILOMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> KM =         KILOMETRE;
  /**
   * @deprecated Use {@link #HECTOMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> HM =         HECTOMETRE;
  /**
   * @deprecated Use {@link #DECAMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> DAM =        DECAMETRE;
  /**
   * @deprecated Use {@link #METRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> M =          METRE;
  /**
   * @deprecated Use {@link #DECIMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> DM =         DECIMETRE;
  /**
   * @deprecated Use {@link #CENTIMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> CM =         CENTIMETRE;
  /**
   * @deprecated Use {@link #MILLIMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> MM =         MILLIMETRE;
  /**
   * @deprecated Use {@link #MICROMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> MICROM =     MICROMETRE;
  /**
   * @deprecated Use {@link #NANOMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> NM =         NANOMETRE;
  /**
   * @deprecated Use {@link #PICOMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> PM =         PICOMETRE;
  /**
   * @deprecated Use {@link #FEMTOMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> FM =         FEMTOMETRE;
  /**
   * @deprecated Use {@link #ATTOMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> AM =         ATTOMETRE;
  /**
   * @deprecated Use {@link #ZEPTOMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> ZM =         ZEPTOMETRE;
  /**
   * @deprecated Use {@link #YOCTOMETRE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> YM =         YOCTOMETRE;
  /**
   * @deprecated Use {@link #LINE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> LI =         LINE;
  /**
   * @deprecated Use {@link #INCH} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> IN =         INCH;
  /**
   * @deprecated Use {@link #FOOT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> FT =         FOOT;
  /**
   * @deprecated Use {@link #YARD} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> YD =         YARD;
  /**
   * @deprecated Use {@link #MILE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> MI =         MILE;
  /**
   * @deprecated Use {@link #ASTRONOMICALUNIT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> UA =         ASTRONOMICALUNIT;
  /**
   * @deprecated Use {@link #LIGHTYEAR} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> LY =         LIGHTYEAR;
  /**
   * @deprecated Use {@link #PARSEC} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> PC =         PARSEC;
  /**
   * @deprecated Use {@link #POINT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Length> PT =         POINT;

  // Power
  public static final ome.units.unit.Unit<Power> YOTTAWATT = YOTTA(WATT);
  public static final ome.units.unit.Unit<Power> ZETTAWATT = ZETTA(WATT);
  public static final ome.units.unit.Unit<Power> EXAWATT =   EXA(WATT);
  public static final ome.units.unit.Unit<Power> PETAWATT =  PETA(WATT);
  public static final ome.units.unit.Unit<Power> TERAWATT =  TERA(WATT);
  public static final ome.units.unit.Unit<Power> GIGAWATT =  GIGA(WATT);
  public static final ome.units.unit.Unit<Power> MEGAWATT =  MEGA(WATT);
  public static final ome.units.unit.Unit<Power> KILOWATT =  KILO(WATT);
  public static final ome.units.unit.Unit<Power> HECTOWATT = HECTO(WATT);
  public static final ome.units.unit.Unit<Power> DECAWATT =  DECA(WATT);
  // WATT already defined
  public static final ome.units.unit.Unit<Power> DECIWATT =  DECI(WATT);
  public static final ome.units.unit.Unit<Power> CENTIWATT = CENTI(WATT);
  public static final ome.units.unit.Unit<Power> MILLIWATT = MILLI(WATT);
  public static final ome.units.unit.Unit<Power> MICROWATT = MICRO(WATT);
  public static final ome.units.unit.Unit<Power> NANOWATT =  NANO(WATT);
  public static final ome.units.unit.Unit<Power> PICOWATT =  PICO(WATT);
  public static final ome.units.unit.Unit<Power> FEMTOWATT = FEMTO(WATT);
  public static final ome.units.unit.Unit<Power> ATTOWATT =  ATTO(WATT);
  public static final ome.units.unit.Unit<Power> ZEPTOWATT = ZEPTO(WATT);
  public static final ome.units.unit.Unit<Power> YOCTOWATT = YOCTO(WATT);

  // Deprecated old names
  /**
   * @deprecated Use {@link #YOTTAWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> YOTTAW = YOTTAWATT;
  /**
   * @deprecated Use {@link #ZETTAWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> ZETTAW = ZETTAWATT;
  /**
   * @deprecated Use {@link #EXAWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> EXAW =   EXAWATT;
  /**
   * @deprecated Use {@link #PETAWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> PETAW =  PETAWATT;
  /**
   * @deprecated Use {@link #TERAWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> TERAW =  TERAWATT;
  /**
   * @deprecated Use {@link #GIGAWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> GIGAW =  GIGAWATT;
  /**
   * @deprecated Use {@link #MEGAWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> MEGAW =  MEGAWATT;
  /**
   * @deprecated Use {@link #KILOWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> KW =     KILOWATT;
  /**
   * @deprecated Use {@link #HECTOWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> HW =     HECTOWATT;
  /**
   * @deprecated Use {@link #DECAWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> DAW =    DECAWATT;
  /**
   * @deprecated Use {@link #WATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> W =      WATT;
  /**
   * @deprecated Use {@link #DECIWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> DW =     DECIWATT;
  /**
   * @deprecated Use {@link #CENTIWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> CW =     CENTIWATT;
  /**
   * @deprecated Use {@link #MILLIWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> MW =     MILLIWATT;
  /**
   * @deprecated Use {@link #MICROWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> MICROW = MICROWATT;
  /**
   * @deprecated Use {@link #NANOWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> NW =     NANOWATT;
  /**
   * @deprecated Use {@link #PICOWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> PW =     PICOWATT;
  /**
   * @deprecated Use {@link #FEMTOWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> FW =     FEMTOWATT;
  /**
   * @deprecated Use {@link #ATTOWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> AW =     ATTOWATT;
  /**
   * @deprecated Use {@link #ZEPTOWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> ZW =     ZEPTOWATT;
  /**
   * @deprecated Use {@link #YOCTOWATT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Power> YW =     YOCTOWATT;

  // Pressure
  public static final ome.units.unit.Unit<Pressure> YOTTAPASCAL = YOTTA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> ZETTAPASCAL = ZETTA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> EXAPASCAL =   EXA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> PETAPASCAL =  PETA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> TERAPASCAL =  TERA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> GIGAPASCAL =  GIGA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> MEGAPASCAL =  MEGA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> KILOPASCAL =  KILO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> HECTOPASCAL = HECTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> DECAPASCAL =  DECA(PASCAL);
  // PASCAL already defined
  public static final ome.units.unit.Unit<Pressure> DECIPASCAL =  DECI(PASCAL);
  public static final ome.units.unit.Unit<Pressure> CENTIPASCAL = CENTI(PASCAL);
  public static final ome.units.unit.Unit<Pressure> MILLIPASCAL = MILLI(PASCAL);
  public static final ome.units.unit.Unit<Pressure> MICROPASCAL = MICRO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> NANOPASCAL =  NANO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> PICOPASCAL =  PICO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> FEMTOPASCAL = FEMTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> ATTOPASCAL =  ATTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> ZEPTOPASCAL = ZEPTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> YOCTOPASCAL = YOCTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> BAR     =     PASCAL.multiply(100000).setSymbol("bar");
  public static final ome.units.unit.Unit<Pressure> MEGABAR =     MEGA(BAR);
  public static final ome.units.unit.Unit<Pressure> KILOBAR =     KILO(BAR);
  public static final ome.units.unit.Unit<Pressure> DECIBAR =     DECI(BAR);
  public static final ome.units.unit.Unit<Pressure> CENTIBAR =    CENTI(BAR);
  public static final ome.units.unit.Unit<Pressure> MILLIBAR =    MILLI(BAR);
  public static final ome.units.unit.Unit<Pressure> ATMOSPHERE =  PASCAL.multiply(101325).setSymbol("atm");
  public static final ome.units.unit.Unit<Pressure> PSI =         PASCAL.multiply(6894.75729316836142).setSymbol("psi"); // APPROXIMATE
  public static final ome.units.unit.Unit<Pressure> TORR =        ATMOSPHERE.divide(760).setSymbol("Torr");
  public static final ome.units.unit.Unit<Pressure> MILLITORR =   MILLI(TORR);
  public static final ome.units.unit.Unit<Pressure> MMHG =        PASCAL.multiply(133.322387415).setSymbol("mm Hg");

  // Deprecated old names
  /**
   * @deprecated Use {@link #YOTTAPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> YOTTAPA = YOTTAPASCAL;
  /**
   * @deprecated Use {@link #ZETTAPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> ZETTAPA = ZETTAPASCAL;
  /**
   * @deprecated Use {@link #EXAPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> EXAPA =   EXAPASCAL;
  /**
   * @deprecated Use {@link #PETAPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> PETAPA =  PETAPASCAL;
  /**
   * @deprecated Use {@link #TERAPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> TERAPA =  TERAPASCAL;
  /**
   * @deprecated Use {@link #GIGAPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> GIGAPA =  GIGAPASCAL;
  /**
   * @deprecated Use {@link #MEGAPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MEGAPA =  MEGAPASCAL;
  /**
   * @deprecated Use {@link #KILOPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> KPA =     KILOPASCAL;
  /**
   * @deprecated Use {@link #HECTOPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> HPA =     HECTOPASCAL;
  /**
   * @deprecated Use {@link #DECAPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> DAPA =    DECAPASCAL;
  /**
   * @deprecated Use {@link #PASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> PA =      PASCAL;
  /**
   * @deprecated Use {@link #DECIPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> DPA =     DECIPASCAL;
  /**
   * @deprecated Use {@link #CENTIPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> CPA =     CENTIPASCAL;
  /**
   * @deprecated Use {@link #MILLIPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MPA =     MILLIPASCAL;
  /**
   * @deprecated Use {@link #MICROPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MICROPA = MICROPASCAL;
  /**
   * @deprecated Use {@link #NANOPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> NPA =     NANOPASCAL;
  /**
   * @deprecated Use {@link #PICOPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> PPA =     PICOPASCAL;
  /**
   * @deprecated Use {@link #FEMTOPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> FPA =     FEMTOPASCAL;
  /**
   * @deprecated Use {@link #ATTOPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> APA =     ATTOPASCAL;
  /**
   * @deprecated Use {@link #ZEPTOPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> ZPA =     ZEPTOPASCAL;
  /**
   * @deprecated Use {@link #YOCTOPASCAL} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> YPA =     YOCTOPASCAL;
  /**
   * @deprecated Use {@link #KILOBAR} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> KBAR =    KILOBAR;
  /**
   * @deprecated Use {@link #DECIBAR} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> DBAR =    DECIBAR;
  /**
   * @deprecated Use {@link #CENTIBAR} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> CBAR =    CENTIBAR;
  /**
   * @deprecated Use {@link #MILLIBAR} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MBAR =    MILLIBAR;
  /**
   * @deprecated Use {@link #ATMOSPHERE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> ATM =     ATMOSPHERE;
  /**
   * @deprecated Use {@link #MILLITORR} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MTORR =   MILLITORR;

  // Temperature
  public static final ome.units.unit.Unit<Temperature> CELSIUS = KELVIN.add(273.15).setSymbol("°C");
  // KELVIN already defined
  public static final ome.units.unit.Unit<Temperature> RANKINE = KELVIN.multiply(5).divide(9).setSymbol("°R");
  public static final ome.units.unit.Unit<Temperature> FAHRENHEIT = RANKINE.add(459.67).setSymbol("°F");

  // Deprecated old names
  /**
   * @deprecated Use {@link #CELSIUS} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Temperature> DEGREEC = CELSIUS;
  /**
   * @deprecated Use {@link #KELVIN} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Temperature> K =       KELVIN;
  /**
   * @deprecated Use {@link #RANKINE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Temperature> DEGREER = RANKINE;
  /**
   * @deprecated Use {@link #FAHRENHEIT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Temperature> DEGREEF = FAHRENHEIT;

  // Time
  public static final ome.units.unit.Unit<Time> YOTTASECOND = YOTTA(SECOND);
  public static final ome.units.unit.Unit<Time> ZETTASECOND = ZETTA(SECOND);
  public static final ome.units.unit.Unit<Time> EXASECOND =   EXA(SECOND);
  public static final ome.units.unit.Unit<Time> PETASECOND =  PETA(SECOND);
  public static final ome.units.unit.Unit<Time> TERASECOND =  TERA(SECOND);
  public static final ome.units.unit.Unit<Time> GIGASECOND =  GIGA(SECOND);
  public static final ome.units.unit.Unit<Time> MEGASECOND =  MEGA(SECOND);
  public static final ome.units.unit.Unit<Time> KILOSECOND =  KILO(SECOND);
  public static final ome.units.unit.Unit<Time> HECTOSECOND = HECTO(SECOND);
  public static final ome.units.unit.Unit<Time> DECASECOND =  DECA(SECOND);
  // SECOND already defined
  public static final ome.units.unit.Unit<Time> DECISECOND =  DECI(SECOND);
  public static final ome.units.unit.Unit<Time> CENTISECOND = CENTI(SECOND);
  public static final ome.units.unit.Unit<Time> MILLISECOND = MILLI(SECOND);
  public static final ome.units.unit.Unit<Time> MICROSECOND = MICRO(SECOND);
  public static final ome.units.unit.Unit<Time> NANOSECOND =  NANO(SECOND);
  public static final ome.units.unit.Unit<Time> PICOSECOND =  PICO(SECOND);
  public static final ome.units.unit.Unit<Time> FEMTOSECOND = FEMTO(SECOND);
  public static final ome.units.unit.Unit<Time> ATTOSECOND =  ATTO(SECOND);
  public static final ome.units.unit.Unit<Time> ZEPTOSECOND = ZEPTO(SECOND);
  public static final ome.units.unit.Unit<Time> YOCTOSECOND = YOCTO(SECOND);
  public static final ome.units.unit.Unit<Time> MINUTE =      SECOND.multiply(60).setSymbol("min");
  public static final ome.units.unit.Unit<Time> HOUR =        MINUTE.multiply(60).setSymbol("h");
  public static final ome.units.unit.Unit<Time> DAY =         HOUR.multiply(24).setSymbol("d");

  // Deprecated old names
  /**
   * @deprecated Use {@link #YOTTASECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> YOTTAS = YOTTASECOND;
  /**
   * @deprecated Use {@link #ZETTASECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> ZETTAS = ZETTASECOND;
  /**
   * @deprecated Use {@link #EXASECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> EXAS =   EXASECOND;
  /**
   * @deprecated Use {@link #PETASECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> PETAS =  PETASECOND;
  /**
   * @deprecated Use {@link #TERASECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> TERAS =  TERASECOND;
  /**
   * @deprecated Use {@link #GIGASECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> GIGAS =  GIGASECOND;
  /**
   * @deprecated Use {@link #MEGASECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> MEGAS =  MEGASECOND;
  /**
   * @deprecated Use {@link #KILOSECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> KS =     KILOSECOND;
  /**
   * @deprecated Use {@link #HECTOSECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> HS =     HECTOSECOND;
  /**
   * @deprecated Use {@link #DECASECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> DAS =    DECASECOND;
  /**
   * @deprecated Use {@link #SECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> S =      SECOND;
  /**
   * @deprecated Use {@link #DECISECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> DS =     DECISECOND;
  /**
   * @deprecated Use {@link #CENTISECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> CS =     CENTISECOND;
  /**
   * @deprecated Use {@link #MILLISECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> MS =     MILLISECOND;
  /**
   * @deprecated Use {@link #MICROSECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> MICROS = MICROSECOND;
  /**
   * @deprecated Use {@link #NANOSECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> NS =     NANOSECOND;
  /**
   * @deprecated Use {@link #PICOSECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> PS =     PICOSECOND;
  /**
   * @deprecated Use {@link #FEMTOSECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> FS =     FEMTOSECOND;
  /**
   * @deprecated Use {@link #ATTOSECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> AS =     ATTOSECOND;
  /**
   * @deprecated Use {@link #ZEPTOSECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> ZS =     ZEPTOSECOND;
  /**
   * @deprecated Use {@link #YOCTOSECOND} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> YS =     YOCTOSECOND;
  /**
   * @deprecated Use {@link #MINUTE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> MIN =    MINUTE;
  /**
   * @deprecated Use {@link #HOUR} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> H =      HOUR;
  /**
   * @deprecated Use {@link #DAY} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Time> D =      DAY;

  // ElectricPotential
  public static final ome.units.unit.Unit<ElectricPotential> YOTTAVOLT = YOTTA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> ZETTAVOLT = ZETTA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> EXAVOLT =   EXA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> PETAVOLT =  PETA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> TERAVOLT =  TERA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> GIGAVOLT =  GIGA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> MEGAVOLT =  MEGA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> KILOVOLT =  KILO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> HECTOVOLT = HECTO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> DECAVOLT =  DECA(VOLT);
  // VOLT already defined
  public static final ome.units.unit.Unit<ElectricPotential> DECIVOLT =  DECI(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> CENTIVOLT = CENTI(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> MILLIVOLT = MILLI(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> MICROVOLT = MICRO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> NANOVOLT =  NANO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> PICOVOLT =  PICO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> FEMTOVOLT = FEMTO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> ATTOVOLT =  ATTO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> ZEPTOVOLT = ZEPTO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> YOCTOVOLT = YOCTO(VOLT);

  // Deprecated old names
  /**
   * @deprecated Use {@link #YOTTAVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> YOTTAV = YOTTAVOLT;
  /**
   * @deprecated Use {@link #ZETTAVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> ZETTAV = ZETTAVOLT;
  /**
   * @deprecated Use {@link #EXAVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> EXAV =   EXAVOLT;
  /**
   * @deprecated Use {@link #PETAVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> PETAV =  PETAVOLT;
  /**
   * @deprecated Use {@link #TERAVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> TERAV =  TERAVOLT;
  /**
   * @deprecated Use {@link #GIGAVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> GIGAV =  GIGAVOLT;
  /**
   * @deprecated Use {@link #MEGAVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> MEGAV =  MEGAVOLT;
  /**
   * @deprecated Use {@link #KILOVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> KV =     KILOVOLT;
  /**
   * @deprecated Use {@link #HECTOVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> HV =     HECTOVOLT;
  /**
   * @deprecated Use {@link #DECAVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> DAV =    DECAVOLT;
  /**
   * @deprecated Use {@link #VOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> V =      VOLT;
  /**
   * @deprecated Use {@link #DECIVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> DV =     DECIVOLT;
  /**
   * @deprecated Use {@link #CENTIVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> CV =     CENTIVOLT;
  /**
   * @deprecated Use {@link #MILLIVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> MV =     MILLIVOLT;
  /**
   * @deprecated Use {@link #MICROVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> MICROV = MICROVOLT;
  /**
   * @deprecated Use {@link #NANOVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> NV =     NANOVOLT;
  /**
   * @deprecated Use {@link #PICOVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> PV =     PICOVOLT;
  /**
   * @deprecated Use {@link #FEMTOVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> FV =     FEMTOVOLT;
  /**
   * @deprecated Use {@link #ATTOVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> AV =     ATTOVOLT;
  /**
   * @deprecated Use {@link #ZEPTOVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> ZV =     ZEPTOVOLT;
  /**
   * @deprecated Use {@link #YOCTOVOLT} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> YV =     YOCTOVOLT;

  // Angle
  public static final ome.units.unit.Unit<Angle> DEGREE =  RADIAN.multiply(Math.PI).divide(180).setSymbol("deg");
  // RADIAN already defined
  public static final ome.units.unit.Unit<Angle> GRADIAN = RADIAN.multiply(Math.PI).divide(200).setSymbol("gon");

  // Deprecated old names
  /**
   * @deprecated Use {@link #DEGREE} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Angle> DEG = DEGREE;
  /**
   * @deprecated Use {@link #RADIAN} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Angle> RAD = RADIAN;
  /**
   * @deprecated Use {@link #GRADIAN} instead.
   */
  @Deprecated
  public static final ome.units.unit.Unit<Angle> GON = GRADIAN;
}
