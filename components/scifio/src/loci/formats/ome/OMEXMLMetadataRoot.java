/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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

package loci.formats.ome;

import org.w3c.dom.Element;

import loci.formats.meta.MetadataRoot;
import ome.xml.model.OME;
import ome.xml.model.OMEModel;
import ome.xml.model.enums.EnumerationException;

/**
 * A utility class for constructing and manipulating OME-XML DOMs.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/ome/OMEXMLMetadataRoot.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/ome/OMEXMLMetadataRoot.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Roger Leigh rleigh at dundee.ac.uk
 */
public class OMEXMLMetadataRoot extends OME implements MetadataRoot {

  /** Default constructor. */
  public OMEXMLMetadataRoot()
  {
    super();
  }

  /**
   * Constructs OME recursively from an XML DOM tree.
   * @param element Root of the XML DOM tree to construct a model object
   * graph from.
   * @param model Handler for the OME model which keeps track of instances
   * and references seen during object population.
   * @throws EnumerationException If there is an error instantiating an
   * enumeration during model object creation.
   */
  public OMEXMLMetadataRoot(Element element, OMEModel model)
    throws EnumerationException
  {
    super(element, model);
  }

  /**
   * Construct from existing OME instance.
   *
   * @param ome the OME instance to copy.
   */
  public OMEXMLMetadataRoot(OME ome)
  {
    super(ome);
  }

}
