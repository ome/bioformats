/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import ome.xml.model.enums.*;
import ome.xml.model.enums.handlers.*;
import ome.xml.model.primitives.*;

public class BinaryFile extends AbstractOMEModelObject
{
	// Base:  -- Name: BinaryFile -- Type: BinaryFile -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/BinaryFile/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(BinaryFile.class);

	// -- Instance variables --

	// MIMEType property
	private String mimeType;

	// Size property
	private NonNegativeLong size;

	// FileName property
	private String fileName;

	// External property
	private External external;

	// BinData property
	private BinData binData;

	// -- Constructors --

	/** Default constructor. */
	public BinaryFile()
	{
	}

	/**
	 * Constructs BinaryFile recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public BinaryFile(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public BinaryFile(BinaryFile orig)
	{
		mimeType = orig.mimeType;
		size = orig.size;
		fileName = orig.fileName;
		external = orig.external;
		binData = orig.binData;
	}

	// -- Custom content from BinaryFile specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates BinaryFile recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"BinaryFile".equals(tagName))
		{
			LOGGER.debug("Expecting node name of BinaryFile got {}", tagName);
		}
		if (element.hasAttribute("MIMEType"))
		{
			// Attribute property MIMEType
			setMIMEType(String.valueOf(
					element.getAttribute("MIMEType")));
		}
		if (element.hasAttribute("Size"))
		{
			// Attribute property Size
			setSize(NonNegativeLong.valueOf(
					element.getAttribute("Size")));
		}
		if (element.hasAttribute("FileName"))
		{
			// Attribute property FileName
			setFileName(String.valueOf(
					element.getAttribute("FileName")));
		}
		List<Element> External_nodeList =
				getChildrenByTagName(element, "External");
		if (External_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"External node list size %d != 1",
					External_nodeList.size()));
		}
		else if (External_nodeList.size() != 0)
		{
			// Element property External which is complex (has
			// sub-elements)
			setExternal(new External(
					(Element) External_nodeList.get(0), model));
		}
		List<Element> BinData_nodeList =
				getChildrenByTagName(element, "BinData");
		if (BinData_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"BinData node list size %d != 1",
					BinData_nodeList.size()));
		}
		else if (BinData_nodeList.size() != 0)
		{
			// Element property BinData which is complex (has
			// sub-elements)
			setBinData(new BinData(
					(Element) BinData_nodeList.get(0), model));
		}
	}

	// -- BinaryFile API methods --

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


	// Property MIMEType
	public String getMIMEType()
	{
		return mimeType;
	}

	public void setMIMEType(String mimeType)
	{
		this.mimeType = mimeType;
	}

	// Property Size
	public NonNegativeLong getSize()
	{
		return size;
	}

	public void setSize(NonNegativeLong size)
	{
		this.size = size;
	}

	// Property FileName
	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	// Property External
	public External getExternal()
	{
		return external;
	}

	public void setExternal(External external)
	{
		this.external = external;
	}

	// Property BinData
	public BinData getBinData()
	{
		return binData;
	}

	public void setBinData(BinData binData)
	{
		this.binData = binData;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element BinaryFile_element)
	{
		// Creating XML block for BinaryFile

		if (BinaryFile_element == null)
		{
			BinaryFile_element =
					document.createElementNS(NAMESPACE, "BinaryFile");
		}


		if (mimeType != null)
		{
			// Attribute property MIMEType
			BinaryFile_element.setAttribute("MIMEType", mimeType.toString());
		}
		if (size != null)
		{
			// Attribute property Size
			BinaryFile_element.setAttribute("Size", size.toString());
		}
		if (fileName != null)
		{
			// Attribute property FileName
			BinaryFile_element.setAttribute("FileName", fileName.toString());
		}
		if (external != null)
		{
			// Element property External which is complex (has
			// sub-elements)
			BinaryFile_element.appendChild(external.asXMLElement(document));
		}
		if (binData != null)
		{
			// Element property BinData which is complex (has
			// sub-elements)
			BinaryFile_element.appendChild(binData.asXMLElement(document));
		}

		return super.asXMLElement(document, BinaryFile_element);
	}
}
