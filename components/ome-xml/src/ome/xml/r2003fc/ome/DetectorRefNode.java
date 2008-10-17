/*
 * ome.xml.r2003fc.ome.DetectorRefNode
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
 * Created by curtis via xsd-fu on 2008-10-16 05:38:12-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r2003fc.ome.*;

import org.w3c.dom.Element;

public class DetectorRefNode extends ReferenceNode
{

	// -- Constructors --

	/** Constructs a DetectorRef node with an associated DOM element. */
	public DetectorRefNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a DetectorRef node with an associated DOM element beneath
	 * a given parent.
	 */
	public DetectorRefNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a DetectorRef node with an associated DOM element beneath
	 * a given parent.
	 */
	public DetectorRefNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "DetectorRef", attach));
	}

	/** 
	 * Returns the <code>DetectorNode</code> which this reference
	 * links to.
	 */
	public DetectorNode getDetector()
	{
		return (DetectorNode)
			getAttrReferencedNode("Detector", "ID");
	}

	/**
	 * Sets the active reference node on this node.
	 * @param node The <code>DetectorNode</code> to set as a
	 * reference.
	 */
	public void setDetectorNode(DetectorNode node)
	{
		setNodeID(node.getNodeID());
	}

	// -- DetectorRef API methods --

	// Attribute
	public Float getOffset()
	{
		return getFloatAttribute("Offset");
	}

	public void setOffset(Float offset)
	{
		setAttribute("Offset", offset);
	}

	// Attribute
	public Float getGain()
	{
		return getFloatAttribute("Gain");
	}

	public void setGain(Float gain)
	{
		setAttribute("Gain", gain);
	}

	// *** WARNING *** Unhandled or skipped property ID

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
