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

import java.util.HashMap;

import loci.common.adapter.CodedEnumAdapter;
import loci.common.adapter.IRandomAccessAdapter;
import loci.common.adapter.StatusListenerAdapter;
import loci.common.adapter.StatusReporterAdapter;

/**
 * A collection of {@link LegacyAdapter}s for wrapping (and thereby delegating)
 * between Legacy classes and their Modern equivalents.
 * <p>
 * As adapters use HashTables to manage delegation between instances, this utility
 * ensures that a single instance of every mapping is statically available, to avoid unnecessary
 * or repetitive wrappings.
 * </p>
 * 
 * @author Mark Hiner
 */
public final class AdapterTools {

  /** Maps LegacyAdapter classes to an instance of that clas, so that a single instance is maintained.
   * This is necessary because each adapter maintains its own static mapping of Legacy and Modern classes.
   */
  private static HashMap<Class<? extends LegacyAdapter<?, ?>>, Object> adapterMap =
      new HashMap<Class<? extends LegacyAdapter<?, ?>>, Object>();
  
  // Manually populate the map of adapters
  static {
    AdapterTools.adapterMap.put(IRandomAccessAdapter.class, new IRandomAccessAdapter());
    AdapterTools.adapterMap.put(CodedEnumAdapter.class, new CodedEnumAdapter());
    AdapterTools.adapterMap.put(StatusListenerAdapter.class, new StatusListenerAdapter());
    AdapterTools.adapterMap.put(StatusReporterAdapter.class, new StatusReporterAdapter());
  }
  
  // -- Adapter Retrieval --
  
  /**
   * Looks up the adapter instance 
   * @return An adapter for converting between legacy and modern IRandomAccess objects
   */
  @SuppressWarnings("unchecked")
  public static <T> T getAdapter(Class<T> adapterClass) {
    Object adapter = adapterMap.get(adapterClass);
    
    // this cast is safe if an adapter was found.
    return adapter == null ? null : (T)adapter;
  }
}