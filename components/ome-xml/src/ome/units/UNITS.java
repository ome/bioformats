/*
 * #%L
 * The OME Data Model specification
 * %%
 * Copyright (C) 2014 - 2015 Open Microscopy Environment:
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
/*
 * $Id$
 *
 *   Copyright 2014 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
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
  public static <T extends Quantity> ome.units.unit.Unit<T> DEKA( ome.units.unit.Unit<T> inUnit){return inUnit.prefixSymbol("da").multiply(BigInteger.TEN.pow(1).doubleValue());}

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
  public static final ome.units.unit.Unit<Frequency> YOTTAHZ = YOTTA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> ZETTAHZ = ZETTA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> EXAHZ =   EXA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> PETAHZ =  PETA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> TERAHZ =  TERA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> GIGAHZ =  GIGA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> MEGAHZ =  MEGA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> KHZ =     KILO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> HHZ =     HECTO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> DAHZ =    DEKA(HERTZ);
  public static final ome.units.unit.Unit<Frequency> HZ =      HERTZ;
  public static final ome.units.unit.Unit<Frequency> DHZ =     DECI(HERTZ);
  public static final ome.units.unit.Unit<Frequency> CHZ =     CENTI(HERTZ);
  public static final ome.units.unit.Unit<Frequency> MHZ =     MILLI(HERTZ);
  public static final ome.units.unit.Unit<Frequency> MICROHZ = MICRO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> NHZ =     NANO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> PHZ =     PICO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> FHZ =     FEMTO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> AHZ =     ATTO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> ZHZ =     ZEPTO(HERTZ);
  public static final ome.units.unit.Unit<Frequency> YHZ =     YOCTO(HERTZ);

  // Length
  public static final ome.units.unit.Unit<Length> YOTTAM =     YOTTA(METRE);
  public static final ome.units.unit.Unit<Length> ZETTAM =     ZETTA(METRE);
  public static final ome.units.unit.Unit<Length> EXAM =       EXA(METRE);
  public static final ome.units.unit.Unit<Length> PETAM =      PETA(METRE);
  public static final ome.units.unit.Unit<Length> TERAM =      TERA(METRE);
  public static final ome.units.unit.Unit<Length> GIGAM =      GIGA(METRE);
  public static final ome.units.unit.Unit<Length> MEGAM =      MEGA(METRE);
  public static final ome.units.unit.Unit<Length> KM =         KILO(METRE);
  public static final ome.units.unit.Unit<Length> HM =         HECTO(METRE);
  public static final ome.units.unit.Unit<Length> DAM =        DEKA(METRE);
  public static final ome.units.unit.Unit<Length> M =          METRE;
  public static final ome.units.unit.Unit<Length> DM =         DECI(METRE);
  public static final ome.units.unit.Unit<Length> CM =         CENTI(METRE);
  public static final ome.units.unit.Unit<Length> MM =         MILLI(METRE);
  public static final ome.units.unit.Unit<Length> MICROM =     MICRO(METRE);
  public static final ome.units.unit.Unit<Length> NM =         NANO(METRE);
  public static final ome.units.unit.Unit<Length> PM =         PICO(METRE);
  public static final ome.units.unit.Unit<Length> FM =         FEMTO(METRE);
  public static final ome.units.unit.Unit<Length> AM =         ATTO(METRE);
  public static final ome.units.unit.Unit<Length> ZM =         ZEPTO(METRE);
  public static final ome.units.unit.Unit<Length> YM =         YOCTO(METRE);
  public static final ome.units.unit.Unit<Length> ANGSTROM =       NM.divide(10).setSymbol("Å");
  public static final ome.units.unit.Unit<Length> THOU =           MILLI(INCH).setSymbol("thou");
  public static final ome.units.unit.Unit<Length> LI =             INCH.divide(12).setSymbol("li");
  public static final ome.units.unit.Unit<Length> IN =             INCH;
  public static final ome.units.unit.Unit<Length> FT =             INCH.multiply(12).setSymbol("ft");
  public static final ome.units.unit.Unit<Length> YD =             FT.multiply(3).setSymbol("yd");
  public static final ome.units.unit.Unit<Length> MI =             YD.multiply(1760).setSymbol("mi");
  public static final ome.units.unit.Unit<Length> UA =             GIGA(METRE.multiply(149.597870700)).setSymbol("ua");
  public static final ome.units.unit.Unit<Length> LY =             PETA(METRE.multiply(9.4607304725808)).setSymbol("ly");
  public static final ome.units.unit.Unit<Length> PC =             GIGA(METRE.multiply(30856776)).setSymbol("pc"); // APPROXIMATE 3.0856776×10^16 m, exact would be UA.divide(tan(DEGREE.divide(3600)))
  public static final ome.units.unit.Unit<Length> PT =             INCH.divide(72).setSymbol("pt");
  public static final ome.units.unit.Unit<Length> PIXEL =          ome.units.unit.Unit.<Length>CreateBaseUnit("Pixel", "pixel");
  public static final ome.units.unit.Unit<Length> REFERENCEFRAME = ome.units.unit.Unit.<Length>CreateBaseUnit("ReferenceFrame", "reference frame");

  // Power
  public static final ome.units.unit.Unit<Power> YOTTAW = YOTTA(WATT);
  public static final ome.units.unit.Unit<Power> ZETTAW = ZETTA(WATT);
  public static final ome.units.unit.Unit<Power> EXAW =   EXA(WATT);
  public static final ome.units.unit.Unit<Power> PETAW =  PETA(WATT);
  public static final ome.units.unit.Unit<Power> TERAW =  TERA(WATT);
  public static final ome.units.unit.Unit<Power> GIGAW =  GIGA(WATT);
  public static final ome.units.unit.Unit<Power> MEGAW =  MEGA(WATT);
  public static final ome.units.unit.Unit<Power> KW =     KILO(WATT);
  public static final ome.units.unit.Unit<Power> HW =     HECTO(WATT);
  public static final ome.units.unit.Unit<Power> DAW =    DEKA(WATT);
  public static final ome.units.unit.Unit<Power> W =      WATT;
  public static final ome.units.unit.Unit<Power> DW =     DECI(WATT);
  public static final ome.units.unit.Unit<Power> CW =     CENTI(WATT);
  public static final ome.units.unit.Unit<Power> MW =     MILLI(WATT);
  public static final ome.units.unit.Unit<Power> MICROW = MICRO(WATT);
  public static final ome.units.unit.Unit<Power> NW =     NANO(WATT);
  public static final ome.units.unit.Unit<Power> PW =     PICO(WATT);
  public static final ome.units.unit.Unit<Power> FW =     FEMTO(WATT);
  public static final ome.units.unit.Unit<Power> AW =     ATTO(WATT);
  public static final ome.units.unit.Unit<Power> ZW =     ZEPTO(WATT);
  public static final ome.units.unit.Unit<Power> YW =     YOCTO(WATT);

  // Pressure
  public static final ome.units.unit.Unit<Pressure> YOTTAPA = YOTTA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> ZETTAPA = ZETTA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> EXAPA =   EXA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> PETAPA =  PETA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> TERAPA =  TERA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> GIGAPA =  GIGA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> MEGAPA =  MEGA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> KPA =     KILO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> HPA =     HECTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> DAPA =    DEKA(PASCAL);
  public static final ome.units.unit.Unit<Pressure> PA =      PASCAL;
  public static final ome.units.unit.Unit<Pressure> DPA =     DECI(PASCAL);
  public static final ome.units.unit.Unit<Pressure> CPA =     CENTI(PASCAL);
  public static final ome.units.unit.Unit<Pressure> MPA =     MILLI(PASCAL);
  public static final ome.units.unit.Unit<Pressure> MICROPA = MICRO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> NPA =     NANO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> PPA =     PICO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> FPA =     FEMTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> APA =     ATTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> ZPA =     ZEPTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> YPA =     YOCTO(PASCAL);
  public static final ome.units.unit.Unit<Pressure> BAR     = PASCAL.multiply(100000).setSymbol("bar");
  public static final ome.units.unit.Unit<Pressure> MEGABAR = MEGA(BAR);
  public static final ome.units.unit.Unit<Pressure> KBAR =    KILO(BAR);
  public static final ome.units.unit.Unit<Pressure> DBAR =    DECI(BAR);
  public static final ome.units.unit.Unit<Pressure> CBAR =    CENTI(BAR);
  public static final ome.units.unit.Unit<Pressure> MBAR =    MILLI(BAR);
  public static final ome.units.unit.Unit<Pressure> ATM =     PASCAL.multiply(101325).setSymbol("atm");
  public static final ome.units.unit.Unit<Pressure> PSI =     PASCAL.multiply(6894.75729316836142).setSymbol("psi"); // APPROXIMATE
  public static final ome.units.unit.Unit<Pressure> TORR =    ATM.divide(760).setSymbol("Torr"); 
  public static final ome.units.unit.Unit<Pressure> MTORR =   MILLI(TORR);
  public static final ome.units.unit.Unit<Pressure> MMHG =    PASCAL.multiply(133.322387415).setSymbol("mm Hg");

  // Temperature
  public static final ome.units.unit.Unit<Temperature> DEGREEC = KELVIN.add(273.15).setSymbol("°C");
  public static final ome.units.unit.Unit<Temperature> K =       KELVIN;
  public static final ome.units.unit.Unit<Temperature> DEGREER = KELVIN.multiply(5).divide(9).setSymbol("°R");
  public static final ome.units.unit.Unit<Temperature> DEGREEF = DEGREER.add(459.67).setSymbol("°F");

  // Time
  public static final ome.units.unit.Unit<Time> YOTTAS = YOTTA(SECOND);
  public static final ome.units.unit.Unit<Time> ZETTAS = ZETTA(SECOND);
  public static final ome.units.unit.Unit<Time> EXAS =   EXA(SECOND);
  public static final ome.units.unit.Unit<Time> PETAS =  PETA(SECOND);
  public static final ome.units.unit.Unit<Time> TERAS =  TERA(SECOND);
  public static final ome.units.unit.Unit<Time> GIGAS =  GIGA(SECOND);
  public static final ome.units.unit.Unit<Time> MEGAS =  MEGA(SECOND);
  public static final ome.units.unit.Unit<Time> KS =     KILO(SECOND);
  public static final ome.units.unit.Unit<Time> HS =     HECTO(SECOND);
  public static final ome.units.unit.Unit<Time> DAS =    DEKA(SECOND);
  public static final ome.units.unit.Unit<Time> S =      SECOND;
  public static final ome.units.unit.Unit<Time> DS =     DECI(SECOND);
  public static final ome.units.unit.Unit<Time> CS =     CENTI(SECOND);
  public static final ome.units.unit.Unit<Time> MS =     MILLI(SECOND);
  public static final ome.units.unit.Unit<Time> MICROS = MICRO(SECOND);
  public static final ome.units.unit.Unit<Time> NS =     NANO(SECOND);
  public static final ome.units.unit.Unit<Time> PS =     PICO(SECOND);
  public static final ome.units.unit.Unit<Time> FS =     FEMTO(SECOND);
  public static final ome.units.unit.Unit<Time> AS =     ATTO(SECOND);
  public static final ome.units.unit.Unit<Time> ZS =     ZEPTO(SECOND);
  public static final ome.units.unit.Unit<Time> YS =     YOCTO(SECOND);
  public static final ome.units.unit.Unit<Time> MIN =    SECOND.multiply(60).setSymbol("min");
  public static final ome.units.unit.Unit<Time> H =      MIN.multiply(60).setSymbol("h");
  public static final ome.units.unit.Unit<Time> D =      H.multiply(24).setSymbol("d");

  // ElectricPotential
  public static final ome.units.unit.Unit<ElectricPotential> YOTTAV = YOTTA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> ZETTAV = ZETTA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> EXAV =   EXA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> PETAV =  PETA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> TERAV =  TERA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> GIGAV =  GIGA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> MEGAV =  MEGA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> KV =     KILO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> HV =     HECTO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> DAV =    DEKA(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> V =      VOLT;
  public static final ome.units.unit.Unit<ElectricPotential> DV =     DECI(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> CV =     CENTI(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> MV =     MILLI(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> MICROV = MICRO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> NV =     NANO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> PV =     PICO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> FV =     FEMTO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> AV =     ATTO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> ZV =     ZEPTO(VOLT);
  public static final ome.units.unit.Unit<ElectricPotential> YV =     YOCTO(VOLT);

  // Angle
  public static final ome.units.unit.Unit<Angle> DEG = RADIAN.multiply(Math.PI).divide(180).setSymbol("deg");
  public static final ome.units.unit.Unit<Angle> RAD = RADIAN;
  public static final ome.units.unit.Unit<Angle> GON = RADIAN.multiply(Math.PI).divide(200).setSymbol("gon");
}
