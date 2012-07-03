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

public class TiffDataNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a TiffData node with an associated DOM element. */
	public TiffDataNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a TiffData node with an associated DOM element beneath
	 * a given parent.
	 */
	public TiffDataNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a TiffData node with an associated DOM element beneath
	 * a given parent.
	 */
	public TiffDataNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "TiffData", attach));
	}

	// -- TiffData API methods --

	// Attribute
	public Integer getIFD()
	{
		return getIntegerAttribute("IFD");
	}

	public void setIFD(Integer ifd)
	{
		setAttribute("IFD", ifd);
	}

	// Attribute
	public Integer getNumPlanes()
	{
		return getIntegerAttribute("NumPlanes");
	}

	public void setNumPlanes(Integer numPlanes)
	{
		setAttribute("NumPlanes", numPlanes);
	}

	// Attribute
	public Integer getFirstZ()
	{
		return getIntegerAttribute("FirstZ");
	}

	public void setFirstZ(Integer firstZ)
	{
		setAttribute("FirstZ", firstZ);
	}

	// Attribute
	public Integer getFirstC()
	{
		return getIntegerAttribute("FirstC");
	}

	public void setFirstC(Integer firstC)
	{
		setAttribute("FirstC", firstC);
	}

	// Attribute
	public Integer getFirstT()
	{
		return getIntegerAttribute("FirstT");
	}

	public void setFirstT(Integer firstT)
	{
		setAttribute("FirstT", firstT);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
