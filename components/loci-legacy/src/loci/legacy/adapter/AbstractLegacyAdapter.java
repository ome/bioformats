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

package loci.legacy.adapter;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * Abstract superclass of all {@link LegacyAdapter} implementations.
 * <p>
 * Uses a {@link WeakHashMap} implementation for maintaining associations between
 * legacy and modern classes. "True" instances of each class map to wrappers of their
 * companion classes.
 * </p>
 * <p>
 * NB: because wrappers are the values in the map, they must be expected to delegate to their key
 * objects (wrapper values wrap their keys). <br>WeakHashMaps are used to ensure garbage collection when
 * there are no other references to the Legacy/modern pairings. <br>However, since the values in each
 * pairing wraps the key, there is always a strong reference to each key. Thus we map each key 
 * to a {@link WeakReference} of the wrapper.
 * </p>
 * 
 * @author Mark Hiner
 *
 */
public abstract class AbstractLegacyAdapter<L, M> implements LegacyAdapter<L, M> {

  // -- Fields --
  
  private WeakHashMap<L, WeakReference<M>> legacyToModern = new WeakHashMap<L, WeakReference<M>>();
  private WeakHashMap<M, WeakReference<L>> modernToLegacy = new WeakHashMap<M, WeakReference<L>>();

  // -- LegacyAdapter API --

  /* @see LegacyAdapter#getModern(L) */
  public M getModern(L legacy) {
    if (legacy == null) return null;
    
    // unwrap if able
    if (legacy instanceof Wrapper) {
      // object is wrapped
      @SuppressWarnings("unchecked")
      Wrapper<M> fakeLegacy = (Wrapper<M>) legacy;
      M trueModern = fakeLegacy.unwrap();
      return trueModern;
    }
    L trueLegacy = legacy; // argument was actually a legacy object
    M fakeModern;
    synchronized (legacyToModern) {
      WeakReference<M> wr = legacyToModern.get(trueLegacy);
      if (wr == null || wr.get() == null) {
        fakeModern = wrapToModern(trueLegacy);
        legacyToModern.put(trueLegacy, new WeakReference<M>(fakeModern));
      }
      else {
        fakeModern = wr.get();
      }
    }
    return fakeModern;
  }

  /* @see LegacyAdapter#getLegacy(M) */
  public L getLegacy(M modern) {
    if (modern == null) return null;
    
    // unwrap if able
    if (modern instanceof Wrapper) {
      // object is wrapped
      @SuppressWarnings("unchecked")
      Wrapper<L> fakeModern = (Wrapper<L>) modern;
      L trueLegacy = fakeModern.unwrap();
      return trueLegacy;
    }
    M trueModern = modern; // argument was actually a modern object
    L fakeLegacy;
    synchronized (modernToLegacy) {
      WeakReference<L> wr = modernToLegacy.get(trueModern);
      if (wr == null || wr.get() == null) {
        fakeLegacy = wrapToLegacy(trueModern);
        modernToLegacy.put(trueModern, new WeakReference<L>(fakeLegacy));
      }
      else {
        fakeLegacy = wr.get();
      }
    }
    return fakeLegacy;
  }

  /* See LegacyAdapter#clear() */
  public void clear() {
    synchronized (legacyToModern) {
      legacyToModern.clear();
    }
    synchronized (modernToLegacy) {
      modernToLegacy.clear();
    }
  }
  

  /**
   * Used Wraps the given modern object to a new instance of its legacy equivalent.
   * <p>
   * This is a "stupid" operation that always wraps to a new instance.
   * </p>
   * <p>
   * This method must be defined at the concrete implementation level as it requires
   * knowledge of a specific class that extends L but is capable of wrapping M.
   * Doing so reduces code/logic repetition by maintaining a single getLegacy
   * implementation.
   * </p>
   * @param modern - An instance of the modern class.
   * @return A legacy instance wrapping the provided modern instance.
   */
  protected abstract L wrapToLegacy(M modern);

  /**
   * Wraps the given legacy object to a new instance of its modern equivalent.
   * <p>
   * This is a "stupid" operation that always wraps to a new instance.
   * </p>
   * <p>
   * This method must be defined at the concrete implementation level as it requires
   * knowledge of a specific class that extends M but is capable of wrapping L. 
   * Doing so reduces code/logic repetition by maintaining a single getModern
   * implementation.
   * </p>
   * @param legacy - An instance of the legacy class.
   * @return A modern instance wrapping the provided legacy instance.
   */ 
  protected abstract M wrapToModern(L legacy);
}
