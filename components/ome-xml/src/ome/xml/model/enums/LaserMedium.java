/*
 * ome.xml.model.enums.LaserMedium
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

public enum LaserMedium implements Enumeration
{
  CU("Cu"), AG("Ag"), ARFL("ArFl"), ARCL("ArCl"), KRFL("KrFl"), KRCL("KrCl"), XEFL("XeFl"), XECL("XeCl"), XEBR("XeBr"), N("N"), AR("Ar"), KR("Kr"), XE("Xe"), HENE("HeNe"), HECD("HeCd"), CO("CO"), CO2("CO2"), H2O("H2O"), HFL("HFl"), NDGLASS("NdGlass"), NDYAG("NdYAG"), ERGLASS("ErGlass"), ERYAG("ErYAG"), HOYLF("HoYLF"), HOYAG("HoYAG"), RUBY("Ruby"), TISAPPHIRE("TiSapphire"), ALEXANDRITE("Alexandrite"), RHODAMINE6G("Rhodamine6G"), COUMARINC30("CoumarinC30"), GAAS("GaAs"), GAALAS("GaAlAs"), EMINUS("EMinus"), OTHER("Other");
  
  private LaserMedium(String value)
  {
    this.value = value;
  }

  public static LaserMedium fromString(String value)
    throws EnumerationException
  {
    if ("Cu".equals(value))
    {
      return CU;
    }
    if ("Ag".equals(value))
    {
      return AG;
    }
    if ("ArFl".equals(value))
    {
      return ARFL;
    }
    if ("ArCl".equals(value))
    {
      return ARCL;
    }
    if ("KrFl".equals(value))
    {
      return KRFL;
    }
    if ("KrCl".equals(value))
    {
      return KRCL;
    }
    if ("XeFl".equals(value))
    {
      return XEFL;
    }
    if ("XeCl".equals(value))
    {
      return XECL;
    }
    if ("XeBr".equals(value))
    {
      return XEBR;
    }
    if ("N".equals(value))
    {
      return N;
    }
    if ("Ar".equals(value))
    {
      return AR;
    }
    if ("Kr".equals(value))
    {
      return KR;
    }
    if ("Xe".equals(value))
    {
      return XE;
    }
    if ("HeNe".equals(value))
    {
      return HENE;
    }
    if ("HeCd".equals(value))
    {
      return HECD;
    }
    if ("CO".equals(value))
    {
      return CO;
    }
    if ("CO2".equals(value))
    {
      return CO2;
    }
    if ("H2O".equals(value))
    {
      return H2O;
    }
    if ("HFl".equals(value))
    {
      return HFL;
    }
    if ("NdGlass".equals(value))
    {
      return NDGLASS;
    }
    if ("NdYAG".equals(value))
    {
      return NDYAG;
    }
    if ("ErGlass".equals(value))
    {
      return ERGLASS;
    }
    if ("ErYAG".equals(value))
    {
      return ERYAG;
    }
    if ("HoYLF".equals(value))
    {
      return HOYLF;
    }
    if ("HoYAG".equals(value))
    {
      return HOYAG;
    }
    if ("Ruby".equals(value))
    {
      return RUBY;
    }
    if ("TiSapphire".equals(value))
    {
      return TISAPPHIRE;
    }
    if ("Alexandrite".equals(value))
    {
      return ALEXANDRITE;
    }
    if ("Rhodamine6G".equals(value))
    {
      return RHODAMINE6G;
    }
    if ("CoumarinC30".equals(value))
    {
      return COUMARINC30;
    }
    if ("GaAs".equals(value))
    {
      return GAAS;
    }
    if ("GaAlAs".equals(value))
    {
      return GAALAS;
    }
    if ("EMinus".equals(value))
    {
      return EMINUS;
    }
    if ("Other".equals(value))
    {
      return OTHER;
    }
    String s = String.format("%s not a supported value of %s",
                             value, LaserMedium.class);
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
