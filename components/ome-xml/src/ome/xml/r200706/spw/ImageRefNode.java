/*
 * ome.xml.r200706.spw.ImageRefNode
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
 * Created by curtis via xsd-fu on 2008-10-15 12:13:45-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200706.spw;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200706.ome.*;

import org.w3c.dom.Element;

public class ImageRefNode extends ReferenceNode
{
	// -- Constructors --

	/** Constructs a ImageRef node with an associated DOM element. */
	public ImageRefNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a ImageRef node with an associated DOM element beneath
	 * a given parent.
	 */
	public ImageRefNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a ImageRef node with an associated DOM element beneath
	 * a given parent.
	 */
	public ImageRefNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "SPW:ImageRef", attach));
	}

	/** 
	 * Returns the <code>ImageNode</code> which this reference
	 * links to.
	 */
	public ImageNode getImage()
	{
		return (ImageNode)
			getAttrReferencedNode("Image", "ID");
	}

	/**
	 * Sets the active reference node on this node.
	 * @param node The <code>ImageNode</code> to set as a
	 * reference.
	 */
	public void setImageNode(ImageNode node)
	{
		setNodeID(node.getNodeID());
	}

	// -- ImageRef API methods --
                                      
	// *** WARNING *** Unhandled or skipped property ID
      
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}
}

