/*
 * ome.xml.r201004.StructuredAnnotations
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2010 Open Microscopy Environment
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
 * Created by callan via xsd-fu on 2010-04-20 12:31:20+0100
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

public class StructuredAnnotations extends Object
{
	// -- Instance variables --

	// Property
	private XMLAnnotation xmlannotation;

	// Property
	private FileAnnotation fileAnnotation;

	// Property
	private ListAnnotation listAnnotation;

	// Property
	private LongAnnotation longAnnotation;

	// Property
	private DoubleAnnotation doubleAnnotation;

	// Property
	private StringAnnotation stringAnnotation;

	// Property
	private BooleanAnnotation booleanAnnotation;

	// Property
	private TimestampAnnotation timestampAnnotation;

	// -- Constructors --

	/** Constructs a StructuredAnnotations. */
	public StructuredAnnotations()
	{
	}

	// -- StructuredAnnotations API methods --

	// Property
	public XMLAnnotation getXMLAnnotation()
	{
		return xmlannotation;
	}

	public void setXMLAnnotation(XMLAnnotation xmlannotation)
	{
		this.xmlannotation = xmlannotation;
	}

	// Property
	public FileAnnotation getFileAnnotation()
	{
		return fileAnnotation;
	}

	public void setFileAnnotation(FileAnnotation fileAnnotation)
	{
		this.fileAnnotation = fileAnnotation;
	}

	// Property
	public ListAnnotation getListAnnotation()
	{
		return listAnnotation;
	}

	public void setListAnnotation(ListAnnotation listAnnotation)
	{
		this.listAnnotation = listAnnotation;
	}

	// Property
	public LongAnnotation getLongAnnotation()
	{
		return longAnnotation;
	}

	public void setLongAnnotation(LongAnnotation longAnnotation)
	{
		this.longAnnotation = longAnnotation;
	}

	// Property
	public DoubleAnnotation getDoubleAnnotation()
	{
		return doubleAnnotation;
	}

	public void setDoubleAnnotation(DoubleAnnotation doubleAnnotation)
	{
		this.doubleAnnotation = doubleAnnotation;
	}

	// Property
	public StringAnnotation getStringAnnotation()
	{
		return stringAnnotation;
	}

	public void setStringAnnotation(StringAnnotation stringAnnotation)
	{
		this.stringAnnotation = stringAnnotation;
	}

	// Property
	public BooleanAnnotation getBooleanAnnotation()
	{
		return booleanAnnotation;
	}

	public void setBooleanAnnotation(BooleanAnnotation booleanAnnotation)
	{
		this.booleanAnnotation = booleanAnnotation;
	}

	// Property
	public TimestampAnnotation getTimestampAnnotation()
	{
		return timestampAnnotation;
	}

	public void setTimestampAnnotation(TimestampAnnotation timestampAnnotation)
	{
		this.timestampAnnotation = timestampAnnotation;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for StructuredAnnotations
		Element StructuredAnnotations_element = document.createElement("StructuredAnnotations");
		if (xmlannotation != null)
		{
			// Element property XMLAnnotation which is complex (has
			// sub-elements)
			StructuredAnnotations_element.appendChild(xmlannotation.asXMLElement(document));
		}
		if (fileAnnotation != null)
		{
			// Element property FileAnnotation which is complex (has
			// sub-elements)
			StructuredAnnotations_element.appendChild(fileAnnotation.asXMLElement(document));
		}
		if (listAnnotation != null)
		{
			// Element property ListAnnotation which is complex (has
			// sub-elements)
			StructuredAnnotations_element.appendChild(listAnnotation.asXMLElement(document));
		}
		if (longAnnotation != null)
		{
			// Element property LongAnnotation which is complex (has
			// sub-elements)
			StructuredAnnotations_element.appendChild(longAnnotation.asXMLElement(document));
		}
		if (doubleAnnotation != null)
		{
			// Element property DoubleAnnotation which is complex (has
			// sub-elements)
			StructuredAnnotations_element.appendChild(doubleAnnotation.asXMLElement(document));
		}
		if (stringAnnotation != null)
		{
			// Element property StringAnnotation which is complex (has
			// sub-elements)
			StructuredAnnotations_element.appendChild(stringAnnotation.asXMLElement(document));
		}
		if (booleanAnnotation != null)
		{
			// Element property BooleanAnnotation which is complex (has
			// sub-elements)
			StructuredAnnotations_element.appendChild(booleanAnnotation.asXMLElement(document));
		}
		if (timestampAnnotation != null)
		{
			// Element property TimestampAnnotation which is complex (has
			// sub-elements)
			StructuredAnnotations_element.appendChild(timestampAnnotation.asXMLElement(document));
		}
		return StructuredAnnotations_element;
	}

	public static StructuredAnnotations fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"StructuredAnnotations".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of StructuredAnnotations got %s",
					tagName));
		}
		StructuredAnnotations instance = new StructuredAnnotations();
		NodeList XMLAnnotation_nodeList = element.getElementsByTagName("XMLAnnotation");
		if (XMLAnnotation_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"XMLAnnotation node list size %d != 1",
					XMLAnnotation_nodeList.getLength()));
		}
		else if (XMLAnnotation_nodeList.getLength() != 0)
		{
			// Element property XMLAnnotation which is complex (has
			// sub-elements)
			instance.setXMLAnnotation(XMLAnnotation.fromXMLElement(
					(Element) XMLAnnotation_nodeList.item(0)));
		}
		NodeList FileAnnotation_nodeList = element.getElementsByTagName("FileAnnotation");
		if (FileAnnotation_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"FileAnnotation node list size %d != 1",
					FileAnnotation_nodeList.getLength()));
		}
		else if (FileAnnotation_nodeList.getLength() != 0)
		{
			// Element property FileAnnotation which is complex (has
			// sub-elements)
			instance.setFileAnnotation(FileAnnotation.fromXMLElement(
					(Element) FileAnnotation_nodeList.item(0)));
		}
		NodeList ListAnnotation_nodeList = element.getElementsByTagName("ListAnnotation");
		if (ListAnnotation_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"ListAnnotation node list size %d != 1",
					ListAnnotation_nodeList.getLength()));
		}
		else if (ListAnnotation_nodeList.getLength() != 0)
		{
			// Element property ListAnnotation which is complex (has
			// sub-elements)
			instance.setListAnnotation(ListAnnotation.fromXMLElement(
					(Element) ListAnnotation_nodeList.item(0)));
		}
		NodeList LongAnnotation_nodeList = element.getElementsByTagName("LongAnnotation");
		if (LongAnnotation_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"LongAnnotation node list size %d != 1",
					LongAnnotation_nodeList.getLength()));
		}
		else if (LongAnnotation_nodeList.getLength() != 0)
		{
			// Element property LongAnnotation which is complex (has
			// sub-elements)
			instance.setLongAnnotation(LongAnnotation.fromXMLElement(
					(Element) LongAnnotation_nodeList.item(0)));
		}
		NodeList DoubleAnnotation_nodeList = element.getElementsByTagName("DoubleAnnotation");
		if (DoubleAnnotation_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"DoubleAnnotation node list size %d != 1",
					DoubleAnnotation_nodeList.getLength()));
		}
		else if (DoubleAnnotation_nodeList.getLength() != 0)
		{
			// Element property DoubleAnnotation which is complex (has
			// sub-elements)
			instance.setDoubleAnnotation(DoubleAnnotation.fromXMLElement(
					(Element) DoubleAnnotation_nodeList.item(0)));
		}
		NodeList StringAnnotation_nodeList = element.getElementsByTagName("StringAnnotation");
		if (StringAnnotation_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"StringAnnotation node list size %d != 1",
					StringAnnotation_nodeList.getLength()));
		}
		else if (StringAnnotation_nodeList.getLength() != 0)
		{
			// Element property StringAnnotation which is complex (has
			// sub-elements)
			instance.setStringAnnotation(StringAnnotation.fromXMLElement(
					(Element) StringAnnotation_nodeList.item(0)));
		}
		NodeList BooleanAnnotation_nodeList = element.getElementsByTagName("BooleanAnnotation");
		if (BooleanAnnotation_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"BooleanAnnotation node list size %d != 1",
					BooleanAnnotation_nodeList.getLength()));
		}
		else if (BooleanAnnotation_nodeList.getLength() != 0)
		{
			// Element property BooleanAnnotation which is complex (has
			// sub-elements)
			instance.setBooleanAnnotation(BooleanAnnotation.fromXMLElement(
					(Element) BooleanAnnotation_nodeList.item(0)));
		}
		NodeList TimestampAnnotation_nodeList = element.getElementsByTagName("TimestampAnnotation");
		if (TimestampAnnotation_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"TimestampAnnotation node list size %d != 1",
					TimestampAnnotation_nodeList.getLength()));
		}
		else if (TimestampAnnotation_nodeList.getLength() != 0)
		{
			// Element property TimestampAnnotation which is complex (has
			// sub-elements)
			instance.setTimestampAnnotation(TimestampAnnotation.fromXMLElement(
					(Element) TimestampAnnotation_nodeList.item(0)));
		}
		return instance;
	}
}
