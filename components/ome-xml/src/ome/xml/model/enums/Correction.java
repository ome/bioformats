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

public enum Correction implements Enumeration
{
  UV("UV"), PLANAPO("PlanApo"), PLANFLUOR("PlanFluor"), SUPERFLUOR("SuperFluor"), VIOLETCORRECTED("VioletCorrected"), ACHRO("Achro"), ACHROMAT("Achromat"), FLUOR("Fluor"), FL("Fl"), FLUAR("Fluar"), NEOFLUAR("Neofluar"), FLUOTAR("Fluotar"), APO("Apo"), PLANNEOFLUAR("PlanNeofluar"), OTHER("Other");
  
  private Correction(String value)
  {
    this.value = value;
  }

  public static Correction fromString(String value)
    throws EnumerationException
  {
    if ("UV".equals(value))
    {
      return UV;
    }
    if ("PlanApo".equals(value))
    {
      return PLANAPO;
    }
    if ("PlanFluor".equals(value))
    {
      return PLANFLUOR;
    }
    if ("SuperFluor".equals(value))
    {
      return SUPERFLUOR;
    }
    if ("VioletCorrected".equals(value))
    {
      return VIOLETCORRECTED;
    }
    if ("Achro".equals(value))
    {
      return ACHRO;
    }
    if ("Achromat".equals(value))
    {
      return ACHROMAT;
    }
    if ("Fluor".equals(value))
    {
      return FLUOR;
    }
    if ("Fl".equals(value))
    {
      return FL;
    }
    if ("Fluar".equals(value))
    {
      return FLUAR;
    }
    if ("Neofluar".equals(value))
    {
      return NEOFLUAR;
    }
    if ("Fluotar".equals(value))
    {
      return FLUOTAR;
    }
    if ("Apo".equals(value))
    {
      return APO;
    }
    if ("PlanNeofluar".equals(value))
    {
      return PLANNEOFLUAR;
    }
    if ("Other".equals(value))
    {
      return OTHER;
    }
    String s = String.format("%s not a supported value of %s",
                             value, Correction.class);
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
