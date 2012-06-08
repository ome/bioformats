/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
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
	public Double getOffset()
	{
		return getDoubleAttribute("Offset");
	}

	public void setOffset(Double offset)
	{
		setAttribute("Offset", offset);
	}

	// Attribute
	public Double getGain()
	{
		return getDoubleAttribute("Gain");
	}

	public void setGain(Double gain)
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
