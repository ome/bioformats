/*
 * ome.xml.r200809.ome.OTFRefNode
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
 * Created by curtis via xsd-fu on 2008-10-17 01:41:39-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200809.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200809.ome.*;
import ome.xml.r200809.spw.*;

import org.w3c.dom.Element;

public class OTFRefNode extends ReferenceNode
{

	// -- Constructors --

	/** Constructs a OTFRef node with an associated DOM element. */
	public OTFRefNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a OTFRef node with an associated DOM element beneath
	 * a given parent.
	 */
	public OTFRefNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a OTFRef node with an associated DOM element beneath
	 * a given parent.
	 */
	public OTFRefNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "OTFRef", attach));
	}

	/** 
	 * Returns the <code>OTFNode</code> which this reference
	 * links to.
	 */
	public OTFNode getOTF()
	{
		return (OTFNode)
			getAttrReferencedNode("OTF", "ID");
	}

	/**
	 * Sets the active reference node on this node.
	 * @param node The <code>OTFNode</code> to set as a
	 * reference.
	 */
	public void setOTFNode(OTFNode node)
	{
		setNodeID(node.getNodeID());
	}

	// -- OTFRef API methods --

	// *** WARNING *** Unhandled or skipped property ID

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
