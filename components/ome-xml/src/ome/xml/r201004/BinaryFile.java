/*
 * ome.xml.r201004.BinaryFile
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) @year@ Open Microscopy Environment
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
 * Created by callan via xsd-fu on 2010-04-30 16:52:08+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.r201004.enums.*;
import ome.xml.r201004.primitives.*;

public class BinaryFile extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/BinaryFile/2010-04";

	// -- Instance variables --

	// Property
	private String mimetype;

	// Property
	private Integer size;

	// Property
	private String fileName;

	// Property
	private External external;

	// Property
	private BinData binData;

	// -- Constructors --

	/** Default constructor. */
	public BinaryFile()
	{
		super();
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
			System.err.println(String.format(
					"WARNING: Expecting node name of BinaryFile got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of BinaryFile got %s",
			//		tagName));
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
			setSize(Integer.valueOf(
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

	public void link(Reference reference, OMEModelObject o)
	{
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
	}


	// Property
	public String getMIMEType()
	{
		return mimetype;
	}

	public void setMIMEType(String mimetype)
	{
		this.mimetype = mimetype;
	}

	// Property
	public Integer getSize()
	{
		return size;
	}

	public void setSize(Integer size)
	{
		this.size = size;
	}

	// Property
	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	// Property
	public External getExternal()
	{
		return external;
	}

	public void setExternal(External external)
	{
		this.external = external;
	}

	// Property
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

		if (mimetype != null)
		{
			// Attribute property MIMEType
			BinaryFile_element.setAttribute("MIMEType", mimetype.toString());
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
