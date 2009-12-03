/*
 * ome.xml.r2003fc.ome.ThumbnailNode
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007 Open Microscopy Environment
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

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via xsd-fu on 2008-10-16 06:18:35-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r2003fc.ome.*;

import org.w3c.dom.Element;

public class ThumbnailNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a Thumbnail node with an associated DOM element. */
	public ThumbnailNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Thumbnail node with an associated DOM element beneath
	 * a given parent.
	 */
	public ThumbnailNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Thumbnail node with an associated DOM element beneath
	 * a given parent.
	 */
	public ThumbnailNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Thumbnail", attach));
	}

	// -- Thumbnail API methods --

	// Attribute
	public String getMIMEtype()
	{
		return getStringAttribute("MIMEtype");
	}

	public void setMIMEtype(String mimetype)
	{
		setAttribute("MIMEtype", mimetype);
	}

	// Attribute
	public String gethref()
	{
		return getStringAttribute("href");
	}

	public void sethref(String href)
	{
		setAttribute("href", href);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
