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

import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.StatusReporter;
import loci.legacy.adapter.AbstractLegacyAdapter;
import loci.legacy.adapter.Wrapper;

/**
 * As interfaces can not contain implementation, this class manages
 * interface-level delegation between {@link loci.common.StatusReporter} and
 * {@link ome.scifio.common.StatusReporter}
 * <p>
 * Delegation is maintained by two WeakHashTables. See {@link AbstractLegacyAdapter}
 * </p>
 * <p>
 * Functionally, the delegation is handled in the nested classes - one for
 * wrapping from ome.scifio.common.StatusReporter to loci.common.StatusReporter,
 * and one for the reverse direction.
 * </p>
 * @author Mark Hiner
 *
 */
public class StatusReporterAdapter extends 
  AbstractLegacyAdapter<StatusReporter, ome.scifio.common.StatusReporter> {
  
  //-- LegacyAdapter API Methods --
  
  @Override
  protected StatusReporter wrapToLegacy(ome.scifio.common.StatusReporter modern) {
    return new ModernWrapper(modern);
  }

  @Override
  protected ome.scifio.common.StatusReporter wrapToModern(StatusReporter legacy) {
    return new LegacyWrapper(legacy);
  }
 
  // -- Delegation Classes --

  /**
   * This class can be used to wrap loci.common.StatusReporter
   * objects and be passed to API expecting an ome.scifio.common.StatusReporter
   * object.
   * <p>
   * All functionality is delegated to the loci-common implementation.
   * </p>
   * 
   * @author Mark Hiner
   */
  public static class LegacyWrapper 
    implements ome.scifio.common.StatusReporter, Wrapper<StatusReporter> {
    
    // -- Fields --
    
    private StatusReporter sr;
    
    private StatusListenerAdapter adapter = new StatusListenerAdapter();
    
    // -- Constructor --
    
    public LegacyWrapper(StatusReporter sr) {
      this.sr = sr;
    }
    
    // -- Wrapper API Methods --
    
    /* @see Wrapper#unwrap() */
    public StatusReporter unwrap() {
      return sr;
    }
    
    // -- StatusReporter API Methods --

    public void addStatusListener(ome.scifio.common.StatusListener l) {
      sr.addStatusListener(adapter.getLegacy(l));
    }

    public void removeStatusListener(ome.scifio.common.StatusListener l) {
      sr.removeStatusListener(adapter.getLegacy(l));
    }

    public void notifyListeners(ome.scifio.common.StatusEvent e) {
      sr.notifyListeners(new StatusEvent(e));
    }
    
    // -- Object delegators --

    @Override
    public boolean equals(Object obj) {
      return sr.equals(obj);
    }
    
    @Override
    public int hashCode() {
      return sr.hashCode();
    }
    
    @Override
    public String toString() {
      return sr.toString();
    }
  }
  
  /**
   * This class can be used to wrap ome.scifio.common.StatusReporter
   * objects and be passed to API expecting a loci.common.StatusReporter
   * object.
   * <p>
   * All functionality is delegated to the scifio implementation.
   * </p>
   * 
   * @author Mark Hiner
   */
  public static class ModernWrapper
    implements StatusReporter, Wrapper<ome.scifio.common.StatusReporter> {
    
    // -- Fields --

    private ome.scifio.common.StatusReporter sr;
    
    private StatusListenerAdapter adapter = new StatusListenerAdapter();

    // -- Constructor --

    public ModernWrapper(ome.scifio.common.StatusReporter sr) {
      this.sr = sr;
    }
    
    // -- Wrapper API Methods --
    
    /* @see wrapper#unwrap() */
    public ome.scifio.common.StatusReporter unwrap() {
      return sr;
    }
    
    // -- StatusReporter API Methods --

    public void addStatusListener(StatusListener l) {
      sr.addStatusListener(adapter.getModern(l));
    }

    public void removeStatusListener(StatusListener l) {
      sr.removeStatusListener(adapter.getModern(l));
    }

    public void notifyListeners(StatusEvent e) {
      sr.notifyListeners(e.getEvent());
    }
    
    // -- Object delegators --

    @Override
    public boolean equals(Object obj) {
      return sr.equals(obj);
    }
    
    @Override
    public int hashCode() {
      return sr.hashCode();
    }
    
    @Override
    public String toString() {
      return sr.toString();
    }
  }
}
