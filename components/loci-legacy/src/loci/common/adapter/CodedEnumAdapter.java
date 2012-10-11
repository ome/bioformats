/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.common.adapter;

import loci.common.enumeration.CodedEnum;
import loci.legacy.adapter.AbstractLegacyAdapter;
import loci.legacy.adapter.Wrapper;

/**
 * As interfaces can not contain implementation, this class manages
 * interface-level delegation between {@link loci.common.enumeration.CodedEnum} and
 * {@link ome.scifio.enumeration.CodedEnum}
 * <p>
 * Delegation is maintained by two WeakHashTables. See {@link AbstractLegacyAdapter}
 * </p>
 * <p>
 * Functionally, the delegation is handled in the nested classes - one for
 * wrapping from ome.scifio.common.enumeration.CodedEnum
 * to loci.common.enumeration.CodedEnum, and one for the reverse direction.
 * </p>
 * @author Mark Hiner
 *
 */
public class CodedEnumAdapter extends 
  AbstractLegacyAdapter<CodedEnum, ome.scifio.enumeration.CodedEnum> {
  
  // -- LegacyAdapter API Methods --
  
  @Override
  protected CodedEnum wrapToLegacy(ome.scifio.enumeration.CodedEnum modern) {
    return new ModernWrapper(modern);
  }

  @Override
  protected ome.scifio.enumeration.CodedEnum wrapToModern(CodedEnum legacy) {
    return new LegacyWrapper(legacy);
  }

  // -- Delegation Classes --

  /**
   * This class can be used to wrap loci.common.enumeration.CodedEnum
   * objects and be passed to API expecting an ome.scifio.enumeration.CodedEnum
   * object.
   * <p>
   * All functionality is delegated to the loci-common implementation.
   * </p>
   * 
   * @author Mark Hiner
   */
  public static class LegacyWrapper 
    implements ome.scifio.enumeration.CodedEnum, Wrapper<CodedEnum> {
    
    // -- Fields --
    
    private CodedEnum ce;
    
    // -- Constructor --
    
    public LegacyWrapper(CodedEnum ce) {
      this.ce = ce;
    }
    
    // -- Wrapper API Methods --
    
    /* @see Wrapper#unwrap() */
    public CodedEnum unwrap() {
      return ce;
    }
    
    // -- CodedEnum API Methods --
    
    public int getCode() {
      return ce.getCode();
    }
    
    // -- Object delegators --

    @Override
    public boolean equals(Object obj) {
      return ce.equals(obj);
    }
    
    @Override
    public int hashCode() {
      return ce.hashCode();
    }
    
    @Override
    public String toString() {
      return ce.toString();
    }
  }
  
  /**
   * This class can be used to wrap ome.scifio.enumeration.CodedEnum
   * objects and be passed to API expecting a loci.common.enumeration.CodedEnum
   * object.
   * <p>
   * All functionality is delegated to the scifio implementation.
   * </p>
   * 
   * @author Mark Hiner
   */
  public static class ModernWrapper implements CodedEnum, Wrapper<ome.scifio.enumeration.CodedEnum> {
    
    // -- Fields --

    private ome.scifio.enumeration.CodedEnum ce;

    // -- Constructor --

    public ModernWrapper(ome.scifio.enumeration.CodedEnum ce) {
      this.ce = ce;
    }
    
    // -- Wrapper API Methods --
    
    /* @see Wrapper#unwrap() */
    public ome.scifio.enumeration.CodedEnum unwrap() {
      return ce;
    }
    
    // -- CodedEnum API Methods --

    public int getCode() {
      return ce.getCode();
    }
    
    // -- Object delegators --

    @Override
    public boolean equals(Object obj) {
      return ce.equals(obj);
    }
    
    @Override
    public int hashCode() {
      return ce.hashCode();
    }
    
    @Override
    public String toString() {
      return ce.toString();
    }
  }
}
