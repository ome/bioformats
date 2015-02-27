/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.common.utests.providers;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for providing instances of IRandomAccessProvider.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/IRandomAccessProviderFactory.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/IRandomAccessProviderFactory.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccessProvider
 */
public class IRandomAccessProviderFactory {

  private static final Map<String, IRandomAccessProvider> providers =
    new HashMap<String, IRandomAccessProvider>();

  static {
    providers.put("NewByteArrayHandle", new NewByteArrayHandleProvider());
    providers.put("ExistingByteArrayHandle",
      new ExistingByteArrayHandleProvider());
    providers.put("ByteArrayHandle", new ByteArrayHandleProvider());
    providers.put("BZip2Handle", new BZip2HandleProvider());
    providers.put("GZipHandle", new GZipHandleProvider());
    providers.put("NIOFileHandle", new NIOFileHandleProvider());
    providers.put("URLHandle", new URLHandleProvider());
    providers.put("ZipHandle", new ZipHandleProvider());
  }

  public IRandomAccessProvider getInstance(String provider) {
    return providers.get(provider);
  }

}
