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

public class ROINode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a ROI node with an associated DOM element. */
	public ROINode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a ROI node with an associated DOM element beneath
	 * a given parent.
	 */
	public ROINode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a ROI node with an associated DOM element beneath
	 * a given parent.
	 */
	public ROINode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "ROI", attach));
	}

	// -- ROI API methods --

	// Attribute
	public Integer getT0()
	{
		return getIntegerAttribute("T0");
	}

	public void setT0(Integer t0)
	{
		setAttribute("T0", t0);
	}

	// Attribute
	public Integer getT1()
	{
		return getIntegerAttribute("T1");
	}

	public void setT1(Integer t1)
	{
		setAttribute("T1", t1);
	}

	// Attribute
	public Integer getY1()
	{
		return getIntegerAttribute("Y1");
	}

	public void setY1(Integer y1)
	{
		setAttribute("Y1", y1);
	}

	// Attribute
	public Integer getY0()
	{
		return getIntegerAttribute("Y0");
	}

	public void setY0(Integer y0)
	{
		setAttribute("Y0", y0);
	}

	// Attribute
	public Integer getX0()
	{
		return getIntegerAttribute("X0");
	}

	public void setX0(Integer x0)
	{
		setAttribute("X0", x0);
	}

	// Attribute
	public Integer getX1()
	{
		return getIntegerAttribute("X1");
	}

	public void setX1(Integer x1)
	{
		setAttribute("X1", x1);
	}

	// Attribute
	public Integer getZ0()
	{
		return getIntegerAttribute("Z0");
	}

	public void setZ0(Integer z0)
	{
		setAttribute("Z0", z0);
	}

	// Attribute
	public Integer getZ1()
	{
		return getIntegerAttribute("Z1");
	}

	public void setZ1(Integer z1)
	{
		setAttribute("Z1", z1);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
