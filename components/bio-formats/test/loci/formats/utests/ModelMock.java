//
// ModelMock.java
//

/*
 * ome.xml.utests
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007-2008 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.utests;


import java.io.UnsupportedEncodingException;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ome.xml.model.OME;

/**
 * @author Chris Allan <callan at blackcat dot ca>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/test/ome/xml/utests/ModelMock.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/test/ome/xml/utests/ModelMock.java">SVN</a></dd></dl>
 */
public interface ModelMock {

  public OME getRoot();

  public void postProcess(Element root, Document document);

  public String asString(Document document)
  throws TransformerException, UnsupportedEncodingException;

}
