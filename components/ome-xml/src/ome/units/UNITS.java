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
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> YOTTAHZ = YOTTAHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> ZETTAHZ = ZETTAHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> EXAHZ =   EXAHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> PETAHZ =  PETAHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> TERAHZ =  TERAHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> GIGAHZ =  GIGAHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> MEGAHZ =  MEGAHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> KHZ =     KILOHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> HHZ =     HECTOHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> DAHZ =    DECAHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> HZ =      HERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> DHZ =     DECIHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> CHZ =     CENTIHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> MHZ =     MILLIHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> MICROHZ = MICROHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> NHZ =     NANOHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> PHZ =     PICOHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> FHZ =     FEMTOHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> AHZ =     ATTOHERTZ;
  @Deprecated
  public static final ome.units.unit.Unit<Frequency> ZHZ =     ZEPTOHERTZ;
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
  @Deprecated
  public static final ome.units.unit.Unit<Length> YOTTAM =     YOTTAMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> ZETTAM =     ZETTAMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> EXAM =       EXAMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> PETAM =      PETAMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> TERAM =      TERAMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> GIGAM =      GIGAMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> MEGAM =      MEGAMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> KM =         KILOMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> HM =         HECTOMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> DAM =        DECAMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> M =          METRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> DM =         DECIMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> CM =         CENTIMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> MM =         MILLIMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> MICROM =     MICROMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> NM =         NANOMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> PM =         PICOMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> FM =         FEMTOMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> AM =         ATTOMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> ZM =         ZEPTOMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> YM =         YOCTOMETRE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> LI =         LINE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> IN =         INCH;
  @Deprecated
  public static final ome.units.unit.Unit<Length> FT =         FOOT;
  @Deprecated
  public static final ome.units.unit.Unit<Length> YD =         YARD;
  @Deprecated
  public static final ome.units.unit.Unit<Length> MI =         MILE;
  @Deprecated
  public static final ome.units.unit.Unit<Length> UA =         ASTRONOMICALUNIT;
  @Deprecated
  public static final ome.units.unit.Unit<Length> LY =         LIGHTYEAR;
  @Deprecated
  public static final ome.units.unit.Unit<Length> PC =         PARSEC;
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
  @Deprecated
  public static final ome.units.unit.Unit<Power> YOTTAW = YOTTAWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> ZETTAW = ZETTAWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> EXAW =   EXAWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> PETAW =  PETAWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> TERAW =  TERAWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> GIGAW =  GIGAWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> MEGAW =  MEGAWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> KW =     KILOWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> HW =     HECTOWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> DAW =    DECAWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> W =      WATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> DW =     DECIWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> CW =     CENTIWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> MW =     MILLIWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> MICROW = MICROWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> NW =     NANOWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> PW =     PICOWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> FW =     FEMTOWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> AW =     ATTOWATT;
  @Deprecated
  public static final ome.units.unit.Unit<Power> ZW =     ZEPTOWATT;
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
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> YOTTAPA = YOTTAPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> ZETTAPA = ZETTAPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> EXAPA =   EXAPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> PETAPA =  PETAPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> TERAPA =  TERAPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> GIGAPA =  GIGAPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MEGAPA =  MEGAPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> KPA =     KILOPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> HPA =     HECTOPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> DAPA =    DECAPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> PA =      PASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> DPA =     DECIPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> CPA =     CENTIPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MPA =     MILLIPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MICROPA = MICROPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> NPA =     NANOPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> PPA =     PICOPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> FPA =     FEMTOPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> APA =     ATTOPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> ZPA =     ZEPTOPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> YPA =     YOCTOPASCAL;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> KBAR =    KILO(BAR);
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> DBAR =    DECI(BAR);
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> CBAR =    CENTI(BAR);
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MBAR =    MILLI(BAR);
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> ATM =     ATMOSPHERE;
  @Deprecated
  public static final ome.units.unit.Unit<Pressure> MTORR =   MILLITORR;

  // Temperature
  public static final ome.units.unit.Unit<Temperature> CELSIUS = KELVIN.add(273.15).setSymbol("°C");
  // KELVIN already defined
  public static final ome.units.unit.Unit<Temperature> RANKINE = KELVIN.multiply(5).divide(9).setSymbol("°R");
  public static final ome.units.unit.Unit<Temperature> FAHRENHEIT = RANKINE.add(459.67).setSymbol("°F");

  // Deprecated old names
  @Deprecated
  public static final ome.units.unit.Unit<Temperature> DEGREEC = CELSIUS;
  @Deprecated
  public static final ome.units.unit.Unit<Temperature> K =       KELVIN;
  @Deprecated
  public static final ome.units.unit.Unit<Temperature> DEGREER = RANKINE;
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
  @Deprecated
  public static final ome.units.unit.Unit<Time> YOTTAS = YOTTASECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> ZETTAS = ZETTASECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> EXAS =   EXASECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> PETAS =  PETASECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> TERAS =  TERASECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> GIGAS =  GIGASECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> MEGAS =  MEGASECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> KS =     KILOSECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> HS =     HECTOSECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> DAS =    DECASECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> S =      SECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> DS =     DECISECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> CS =     CENTISECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> MS =     MILLISECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> MICROS = MICROSECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> NS =     NANOSECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> PS =     PICOSECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> FS =     FEMTOSECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> AS =     ATTOSECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> ZS =     ZEPTOSECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> YS =     YOCTOSECOND;
  @Deprecated
  public static final ome.units.unit.Unit<Time> MIN =    MINUTE;
  @Deprecated
  public static final ome.units.unit.Unit<Time> H =      HOUR;
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
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> YOTTAV = YOTTAVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> ZETTAV = ZETTAVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> EXAV =   EXAVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> PETAV =  PETAVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> TERAV =  TERAVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> GIGAV =  GIGAVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> MEGAV =  MEGAVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> KV =     KILOVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> HV =     HECTOVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> DAV =    DECAVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> V =      VOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> DV =     DECIVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> CV =     CENTIVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> MV =     MILLIVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> MICROV = MICROVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> NV =     NANOVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> PV =     PICOVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> FV =     FEMTOVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> AV =     ATTOVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> ZV =     ZEPTOVOLT;
  @Deprecated
  public static final ome.units.unit.Unit<ElectricPotential> YV =     YOCTOVOLT;

  // Angle
  public static final ome.units.unit.Unit<Angle> DEGREE =  RADIAN.multiply(Math.PI).divide(180).setSymbol("deg");
  // RADIAN already defined
  public static final ome.units.unit.Unit<Angle> GRADIAN = RADIAN.multiply(Math.PI).divide(200).setSymbol("gon");

  // Deprecated old names
  @Deprecated
  public static final ome.units.unit.Unit<Angle> DEG = DEGREE;
  @Deprecated
  public static final ome.units.unit.Unit<Angle> RAD = RADIAN;
  @Deprecated
  public static final ome.units.unit.Unit<Angle> GON = GRADIAN;
}
