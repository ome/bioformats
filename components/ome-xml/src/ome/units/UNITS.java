/*
 * #%L
 * The OME Data Model specification
 * %%
 * Copyright (C) 2014 Open Microscopy Environment:
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

import org.unitsofmeasurement.unit.SystemOfUnits;
import org.unitsofmeasurement.quantity.*;
import org.unitsofmeasurement.unit.*;

import java.math.BigInteger;

import org.eclipse.uomo.units.SI;
import org.eclipse.uomo.units.AbstractSystemOfUnits;
import org.eclipse.uomo.units.internal.NonSI;
import org.eclipse.uomo.units.impl.system.Imperial;
import org.eclipse.uomo.units.impl.BaseUnit;

/**
 * A wrapper-like for the SI and Imperial classes from the units implimintation.
 *
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 * @version 1.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 5.1
 */
public final class UNITS extends AbstractSystemOfUnits
{
  public String getName()
  {
    return "OME-combined";
  }
  // Definitions from external library that others are calculated from
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> VOLT = SI.VOLT;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> HERTZ = SI.HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Length> METRE = SI.METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> INCH = Imperial.INCH;
  public static final org.unitsofmeasurement.unit.Unit<Power> WATT = SI.WATT;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PASCAL = SI.PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> BAR = PASCAL.multiply(100000);
  public static final org.unitsofmeasurement.unit.Unit<Temperature> CELSIUS = SI.CELSIUS;
  public static final org.unitsofmeasurement.unit.Unit<Temperature> KELVIN = SI.KELVIN;
  public static final org.unitsofmeasurement.unit.Unit<Time> SECOND = SI.SECOND;

  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> YOTTA(org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(24).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> ZETTA(org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(21).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> EXA(  org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(18).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> PETA( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(15).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> TERA( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(12).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> GIGA( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(9).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> MEGA( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(6).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> KILO( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(3).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> HECTO(org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(2).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> DEKA( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.multiply(BigInteger.TEN.pow(1).doubleValue());}

  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> DECI( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(1).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> CENTI(org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(2).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> MILLI(org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(3).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> MICRO(org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(6).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> NANO( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(9).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> PICO( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(12).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> FEMTO(org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(15).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> ATTO( org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(18).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> ZEPTO(org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(21).doubleValue());}
  public static <T extends Quantity<T>> org.unitsofmeasurement.unit.Unit<T> YOCTO(org.unitsofmeasurement.unit.Unit<T> inUnit){return inUnit.divide(BigInteger.TEN.pow(24).doubleValue());}

  public static final org.unitsofmeasurement.unit.Unit<Frequency> YOTTAHZ = YOTTA(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> ZETTAHZ = ZETTA(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> EXAHZ =   EXA(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> PETAHZ =  PETA(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> TERAHZ =  TERA(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> GIGAHZ =  GIGA(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> MEGAHZ =  MEGA(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> KHZ =     KILO(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> HHZ =     HECTO(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> DAHZ =    DEKA(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> HZ =      HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> DHZ =     DECI(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> CHZ =     CENTI(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> MHZ =     MILLI(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> MICROHZ = MICRO(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> NHZ =     NANO(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> PHZ =     PICO(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> FHZ =     FEMTO(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> AHZ =     ATTO(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> ZHZ =     ZEPTO(HERTZ);
  public static final org.unitsofmeasurement.unit.Unit<Frequency> YHZ =     YOCTO(HERTZ);

  public static final org.unitsofmeasurement.unit.Unit<Length> YOTTAM =     YOTTA(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> ZETTAM =     ZETTA(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> EXAM =       EXA(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> PETAM =      PETA(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> TERAM =      TERA(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> GIGAM =      GIGA(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> MEGAM =      MEGA(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> KM =         KILO(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> HM =         HECTO(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> DAM =        DEKA(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> M =          METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> DM =         DECI(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> CM =         CENTI(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> MM =         MILLI(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> MICROM =     MICRO(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> NM =         NANO(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> PM =         PICO(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> FM =         FEMTO(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> AM =         ATTO(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> ZM =         ZEPTO(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> YM =         YOCTO(METRE);
  public static final org.unitsofmeasurement.unit.Unit<Length> ANGSTROM =       NM.divide(10);
  public static final org.unitsofmeasurement.unit.Unit<Length> THOU =           MILLI(INCH);
  public static final org.unitsofmeasurement.unit.Unit<Length> LI =             INCH.divide(12);
  public static final org.unitsofmeasurement.unit.Unit<Length> IN =             INCH;
  public static final org.unitsofmeasurement.unit.Unit<Length> FT =             INCH.multiply(12);
  public static final org.unitsofmeasurement.unit.Unit<Length> YD =             FT.multiply(3);
  public static final org.unitsofmeasurement.unit.Unit<Length> MI =             YD.multiply(1760);
  public static final org.unitsofmeasurement.unit.Unit<Length> UA =             GIGA(METRE.multiply(149.597870700));
  public static final org.unitsofmeasurement.unit.Unit<Length> LY =             PETA(METRE.multiply(9.4607304725808));
  public static final org.unitsofmeasurement.unit.Unit<Length> PC =             GIGA(METRE.multiply(30856776)); // APPROXIMATE 3.0856776×10^16 m, exact would be UA.divide(tan(DEGREE.divide(3600)))
  public static final org.unitsofmeasurement.unit.Unit<Length> PT =             INCH.divide(72);
  public static final org.unitsofmeasurement.unit.Unit<Length> PIXEL =          new BaseUnit<Length>("Pixel");
  public static final org.unitsofmeasurement.unit.Unit<Length> REFERENCEFRAME = new BaseUnit<Length>("ReferenceFrame");

  public static final org.unitsofmeasurement.unit.Unit<Power> YOTTAW = YOTTA(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> ZETTAW = ZETTA(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> EXAW =   EXA(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> PETAW =  PETA(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> TERAW =  TERA(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> GIGAW =  GIGA(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> MEGAW =  MEGA(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> KW =     KILO(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> HW =     HECTO(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> DAW =    DEKA(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> W =      WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> DW =     DECI(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> CW =     CENTI(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> MW =     MILLI(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> MICROW = MICRO(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> NW =     NANO(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> PW =     PICO(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> FW =     FEMTO(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> AW =     ATTO(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> ZW =     ZEPTO(WATT);
  public static final org.unitsofmeasurement.unit.Unit<Power> YW =     YOCTO(WATT);

  public static final org.unitsofmeasurement.unit.Unit<Pressure> YOTTAPA = YOTTA(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> ZETTAPA = ZETTA(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> EXAPA =   EXA(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PETAPA =  PETA(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> TERAPA =  TERA(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> GIGAPA =  GIGA(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MEGAPA =  MEGA(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> KPA =     KILO(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> HPA =     HECTO(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> DAPA =    DEKA(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PETAA =   PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> DPA =     DECI(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> CPA =     CENTI(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MPA =     MILLI(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MICROPA = MICRO(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> NPA =     NANO(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PPA =     PICO(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> FPA =     FEMTO(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> APA =     ATTO(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> ZPA =     ZEPTO(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> YPA =     YOCTO(PASCAL);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MEGABAR = MEGA(BAR);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> KBAR =    KILO(BAR);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> DBAR =    DECI(BAR);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> CBAR =    CENTI(BAR);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MBAR =    MILLI(BAR);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> ATM =     PASCAL.multiply(101325);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PSI =     PASCAL.multiply(6894.75729316836142); // APPROXIMATE
  public static final org.unitsofmeasurement.unit.Unit<Pressure> TORR =    ATM.divide(760); 
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MTORR =   MILLI(TORR);
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MMHG =    PASCAL.multiply(133.322387415);

  public static final org.unitsofmeasurement.unit.Unit<Temperature> DEGREEC = KELVIN.add(273.15);
  public static final org.unitsofmeasurement.unit.Unit<Temperature> K =       KELVIN;
  public static final org.unitsofmeasurement.unit.Unit<Temperature> DEGREER = KELVIN.multiply(5).divide(9);
  public static final org.unitsofmeasurement.unit.Unit<Temperature> DEGREEF = DEGREER.add(459.67);

  public static final org.unitsofmeasurement.unit.Unit<Time> YOTTAS = YOTTA(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> ZETTAS = ZETTA(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> EXAS =   EXA(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> PETAS =  PETA(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> TERAS =  TERA(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> GIGAS =  GIGA(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> MEGAS =  MEGA(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> KS =     KILO(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> HS =     HECTO(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> DAS =    DEKA(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> S =      SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> DS =     DECI(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> CS =     CENTI(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> MS =     MILLI(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> MICROS = MICRO(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> NS =     NANO(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> PS =     PICO(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> FS =     FEMTO(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> AS =     ATTO(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> ZS =     ZEPTO(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> YS =     YOCTO(SECOND);
  public static final org.unitsofmeasurement.unit.Unit<Time> MIN =    SECOND.multiply(60);
  public static final org.unitsofmeasurement.unit.Unit<Time> H =      MIN.multiply(60);
  public static final org.unitsofmeasurement.unit.Unit<Time> D =      H.multiply(24);

  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> YOTTAV = YOTTA(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> ZETTAV = ZETTA(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> EXAV =   EXA(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> PETAV =  PETA(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> TERAV =  TERA(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> GIGAV =  GIGA(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> MEGAV =  MEGA(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> KV =     KILO(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> HV =     HECTO(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> DAV =    DEKA(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> V =      VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> DV =     DECI(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> CV =     CENTI(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> MV =     MILLI(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> MICROV = MICRO(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> NV =     NANO(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> PV =     PICO(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> FV =     FEMTO(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> AV =     ATTO(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> ZV =     ZEPTO(VOLT);
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> YV =     YOCTO(VOLT);                                               

}