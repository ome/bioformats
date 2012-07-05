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

package loci.common.services;

/**
 * A legacy delegator class for ome.scifio.services.ServiceFactory
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/services/ServiceFactory.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/services/ServiceFactory.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ServiceFactory {

  // -- Fields --
  
  private ome.scifio.services.ServiceFactory sFactory;
  
  // -- Constructor --

  /**
   * Constructor loading service configuration from the default location.
   * @throws DependencyException If there is an error locating or reading from
   * the default configuration location.
   */
  public ServiceFactory() throws DependencyException {
    try {
      sFactory = new ome.scifio.services.ServiceFactory();
    }
    catch (ome.scifio.services.DependencyException e) {
      throw (DependencyException)e;
    }
  }

  /**
   * Constructor loading service configuration from a given location.
   * @param path Location to load service configuration from.
   * @throws DependencyException If there is an error locating or reading from
   * <code>path</code>.
   */
  public ServiceFactory(String path) throws DependencyException {
    try {
      sFactory = new ome.scifio.services.ServiceFactory(path);
    }
    catch (ome.scifio.services.DependencyException e) {
      throw (DependencyException)e;
    }
  }
  
  // -- ServiceFactory API --

  /**
   * Retrieves an instance of a given service.
   * @param type Interface type of the service.
   * @return A newly instantiated service.
   * @throws DependencyException If there is an error instantiating the
   * service instance requested.
   */
  public <T extends ome.scifio.services.Service> T getInstance(Class<T> type)
    throws DependencyException
  {
    try {
      return sFactory.getInstance(type);
    }
    catch (ome.scifio.services.DependencyException e) {
      throw (DependencyException)e;
    }
  }
}
