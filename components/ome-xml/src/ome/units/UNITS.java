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

package ome.xml.units;

import org.unitsofmeasurement.unit.SystemOfUnits;
import org.unitsofmeasurement.quantity.*;
import org.unitsofmeasurement.unit.*;

import org.eclipse.uomo.units.*;
import org.eclipse.uomo.units.SI;
import org.eclipse.uomo.units.impl.system.Imperial;

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
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> VOLT = SI.VOLT;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> HERTZ = SI.HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Length> METRE = SI.METRE;
  public static final org.unitsofmeasurement.unit.Unit<Power> WATT = SI.WATT;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PASCAL = SI.PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Temperature> CELSIUS = SI.CELSIUS;
  public static final org.unitsofmeasurement.unit.Unit<Temperature> KELVIN = SI.KELVIN;
  public static final org.unitsofmeasurement.unit.Unit<Time> SECOND = SI.SECOND;

  public static final org.unitsofmeasurement.unit.Unit<Frequency> YOTTAHZ = HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> ZETTAHZ = HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> EXAHZ =   HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> PETAHZ =  HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> TERAHZ =  HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> GIGAHZ =  HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> MEGAHZ =  HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> KHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> HHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> DAHZ =    HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> HZ =      HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> DHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> CHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> MHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> MICROHZ = HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> NHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> PHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> FHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> AHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> ZHZ =     HERTZ;
  public static final org.unitsofmeasurement.unit.Unit<Frequency> YHZ =     HERTZ;

  public static final org.unitsofmeasurement.unit.Unit<Length> YOTTAM =         METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> ZETTAM =         METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> EXAM =           METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> PETAM =          METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> TERAM =          METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> GIGAM =          METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> MEGAM =          METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> KM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> HM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> DAM =            METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> M =              METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> DM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> CM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> MM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> MICROM =         METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> NM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> PM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> FM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> AM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> ZM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> YM =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> ANGSTROM =       METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> THOU =           METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> LI =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> IN =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> FT =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> YD =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> MI =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> UA =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> LY =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> PC =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> PT =             METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> PIXEL =          METRE;
  public static final org.unitsofmeasurement.unit.Unit<Length> REFERENCEFRAME = METRE;

  public static final org.unitsofmeasurement.unit.Unit<Power> YOTTAW = WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> ZETTAW = WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> EXAW =   WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> PETAW =  WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> TERAW =  WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> GIGAW =  WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> MEGAW =  WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> KW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> HW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> DAW =    WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> W =      WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> DW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> CW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> MW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> MICROW = WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> NW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> PW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> FW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> AW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> ZW =     WATT;
  public static final org.unitsofmeasurement.unit.Unit<Power> YW =     WATT;

  public static final org.unitsofmeasurement.unit.Unit<Pressure> YOTTAPA = PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> ZETTAPA = PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> EXAPA =   PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PETAPA =  PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> TERAPA =  PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> GIGAPA =  PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MEGAPA =  PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> KPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> HPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> DAPA =    PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PETAA =   PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> DPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> CPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MICROPA = PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> NPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> FPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> APA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> ZPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> YPA =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MEGABAR = PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> KBAR =    PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> DBAR =    PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> CBAR =    PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MBAR =    PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> ATM =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> PSI =     PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> TORR =    PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MTORR =   PASCAL;
  public static final org.unitsofmeasurement.unit.Unit<Pressure> MMHG =    PASCAL;

  public static final org.unitsofmeasurement.unit.Unit<Temperature> DEGREEC = KELVIN;
  public static final org.unitsofmeasurement.unit.Unit<Temperature> DEGREEF = KELVIN;
  public static final org.unitsofmeasurement.unit.Unit<Temperature> K =       KELVIN;
  public static final org.unitsofmeasurement.unit.Unit<Temperature> DEGREER = KELVIN;
  public static final org.unitsofmeasurement.unit.Unit<Temperature> ATU =     KELVIN;

  public static final org.unitsofmeasurement.unit.Unit<Time> YOTTAS = SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> ZETTAS = SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> EXAS =   SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> PETAS =  SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> TERAS =  SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> GIGAS =  SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> MEGAS =  SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> KS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> HS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> DAS =    SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> S =      SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> DS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> CS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> MS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> MICROS = SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> NS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> PS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> FS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> AS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> ZS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> YS =     SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> MIN =    SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> H =      SECOND;
  public static final org.unitsofmeasurement.unit.Unit<Time> D =      SECOND;

  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> YOTTAV = VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> ZETTAV = VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> EXAV =   VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> PETAV =  VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> TERAV =  VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> GIGAV =  VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> MEGAV =  VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> KV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> HV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> DAV =    VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> V =      VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> DV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> CV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> MV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> MICROV = VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> NV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> PV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> FV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> AV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> ZV =     VOLT;
  public static final org.unitsofmeasurement.unit.Unit<ElectricPotential> YV =     VOLT;


}