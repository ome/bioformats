/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2021 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
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
 * #L%
 */

package loci.formats.in;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of valid DICOM value representations (VRs).
 * See http://dicom.nema.org/medical/dicom/current/output/html/part05.html#sect_6.2
 */
public enum DicomVR {
  AE(0x4145),
  AS(0x4153),
  AT(0x4154),
  CS(0x4353),
  DA(0x4441),
  DS(0x4453),
  DT(0x4454),
  FD(0x4644),
  FL(0x464C),
  IS(0x4953),
  LO(0x4C4F),
  LT(0x4C54),
  OB(0x4F42),
  OD(0x4F44),
  OF(0x4F46),
  OL(0x4F4C),
  OV(0x4F56),
  OW(0x4F57),
  PN(0x504E),
  SH(0x5348),
  SL(0x534C),
  SQ(0x5351),
  SS(0x5353),
  ST(0x5354),
  SV(0x5356),
  TM(0x544D),
  UC(0x5543),
  UI(0x5549),
  UL(0x554C),
  UN(0x554E),
  UR(0x5552),
  US(0x5553),
  UT(0x5554),
  UV(0x5556),
  QQ(0x3F3F),
  IMPLICIT(0x2D2D),
  RESERVED(0xFFFF);

  private int code;

  private static final Map<Integer, DicomVR> lookup = new HashMap<Integer, DicomVR>();

  static {
    for (DicomVR v : EnumSet.allOf(DicomVR.class)) {
      lookup.put(v.getCode(), v);
    }
  }

  private DicomVR(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static DicomVR get(int code) {
    return lookup.get(code);
  }

}
