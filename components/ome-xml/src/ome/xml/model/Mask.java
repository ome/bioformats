/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:35.744474
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

public class Mask extends Shape
{
	// Base:  -- Name: Mask -- Type: Mask -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/ROI/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Mask.class);

	// -- Instance variables --


	// Property
	private Double y;

	// Property
	private Double x;

	// Property
	private Double height;

	// Property
	private Double width;

	// Property which occurs more than once
	private List<BinData> binDataBlocks = new ArrayList<BinData>();

	// -- Constructors --

	/** Default constructor. */
	public Mask()
	{
		super();
	}

	/** 
	 * Constructs Mask recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Mask(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Mask specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Mask recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element, OMEModel model)
	    throws EnumerationException
	{
		super.update(element, model);
		String tagName = element.getTagName();
		if (!"Mask".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Mask got {}", tagName);
		}
		if (element.hasAttribute("Y"))
		{
			// Attribute property Y
			setY(Double.valueOf(
					element.getAttribute("Y")));
		}
		if (element.hasAttribute("X"))
		{
			// Attribute property X
			setX(Double.valueOf(
					element.getAttribute("X")));
		}
		if (element.hasAttribute("Height"))
		{
			// Attribute property Height
			setHeight(Double.valueOf(
					element.getAttribute("Height")));
		}
		if (element.hasAttribute("Width"))
		{
			// Attribute property Width
			setWidth(Double.valueOf(
					element.getAttribute("Width")));
		}
		// Element property BinData which is complex (has
		// sub-elements) and occurs more than once
		List<Element> BinData_nodeList =
				getChildrenByTagName(element, "BinData");
		for (Element BinData_element : BinData_nodeList)
		{
			addBinData(
					new BinData(BinData_element, model));
		}
	}

	// -- Mask API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public Double getY()
	{
		return y;
	}

	public void setY(Double y)
	{
		this.y = y;
	}

	// Property
	public Double getX()
	{
		return x;
	}

	public void setX(Double x)
	{
		this.x = x;
	}

	// Property
	public Double getHeight()
	{
		return height;
	}

	public void setHeight(Double height)
	{
		this.height = height;
	}

	// Property
	public Double getWidth()
	{
		return width;
	}

	public void setWidth(Double width)
	{
		this.width = width;
	}

	// Property which occurs more than once
	public int sizeOfBinDataList()
	{
		return binDataBlocks.size();
	}

	public List<BinData> copyBinDataList()
	{
		return new ArrayList<BinData>(binDataBlocks);
	}

	public BinData getBinData(int index)
	{
		return binDataBlocks.get(index);
	}

	public BinData setBinData(int index, BinData binData)
	{
        binData.setMask(this);
		return binDataBlocks.set(index, binData);
	}

	public void addBinData(BinData binData)
	{
        binData.setMask(this);
		binDataBlocks.add(binData);
	}

	public void removeBinData(BinData binData)
	{
		binDataBlocks.remove(binData);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Mask_element)
	{
		// Creating XML block for Mask

		if (Mask_element == null)
		{
			Mask_element =
					document.createElementNS(NAMESPACE, "Mask");
		}

		if (y != null)
		{
			// Attribute property Y
			Mask_element.setAttribute("Y", y.toString());
		}
		if (x != null)
		{
			// Attribute property X
			Mask_element.setAttribute("X", x.toString());
		}
		if (height != null)
		{
			// Attribute property Height
			Mask_element.setAttribute("Height", height.toString());
		}
		if (width != null)
		{
			// Attribute property Width
			Mask_element.setAttribute("Width", width.toString());
		}
		if (binDataBlocks != null)
		{
			// Element property BinData which is complex (has
			// sub-elements) and occurs more than once
			for (BinData binDataBlocks_value : binDataBlocks)
			{
				Mask_element.appendChild(binDataBlocks_value.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, Mask_element);
	}
}
