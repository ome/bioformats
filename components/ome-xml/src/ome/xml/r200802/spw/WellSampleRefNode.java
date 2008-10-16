/*
 * ome.xml.r200802.spw.WellSampleRefNode
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
 * Created by curtis via xsd-fu on 2008-10-15 21:58:39-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200802.spw;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200802.ome.*;
import ome.xml.r200802.spw.*;

import org.w3c.dom.Element;

public class WellSampleRefNode extends ReferenceNode
{

	// -- Constructors --

	/** Constructs a WellSampleRef node with an associated DOM element. */
	public WellSampleRefNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a WellSampleRef node with an associated DOM element beneath
	 * a given parent.
	 */
	public WellSampleRefNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a WellSampleRef node with an associated DOM element beneath
	 * a given parent.
	 */
	public WellSampleRefNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "SPW:WellSampleRef", attach));
	}

	/** 
	 * Returns the <code>WellSampleNode</code> which this reference
	 * links to.
	 */
	public WellSampleNode getWellSample()
	{
		return (WellSampleNode)
			getAttrReferencedNode("WellSample", "ID");
	}

	/**
	 * Sets the active reference node on this node.
	 * @param node The <code>WellSampleNode</code> to set as a
	 * reference.
	 */
	public void setWellSampleNode(WellSampleNode node)
	{
		setNodeID(node.getNodeID());
	}

	// -- WellSampleRef API methods --

	// *** WARNING *** Unhandled or skipped property ID

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
