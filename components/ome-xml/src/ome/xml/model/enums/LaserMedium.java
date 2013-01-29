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
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:36.486351
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
