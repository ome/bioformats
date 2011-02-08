//
// About.java
//

/*
 * ome.xml.About
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

package ome.xml;

/**
 * Displays a small information dialog about the OME-XML Java library.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/ome-xml/src/ome/xml/About.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/ome-xml/src/ome/xml/About.java">SVN</a></dd></dl>
 */
public final class About {

  // -- Constants --

  /** URL for OME-XML Java library web page. */
  public static final String URL_OME_XML_LIBRARY =
    "http://ome-xml.org/wiki/OmeXmlJava";

  // -- Static utility methods --

  public static void about() {
    System.out.println("OME-XML Java library, " +
      "revision @vcs.revision@, built @date@");
    System.out.println("Copyright 2005-@year@ Open Microscopy Environment");
    System.out.println(URL_OME_XML_LIBRARY);
    System.out.println("Authors: Curtis Rueden, Chris Allan");
  }

  // -- Main method --

  public static void main(String[] args) {
    about();
    System.exit(0);
  }

}
