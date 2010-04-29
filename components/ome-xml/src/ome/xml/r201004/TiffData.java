/*
 * ome.xml.r201004.TiffData
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
 * Created by callan via xsd-fu on 2010-04-29 09:45:43+0100
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

public class TiffData extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

	// -- Instance variables --

	// Property
	private Integer ifd;

	// Property
	private Integer planeCount;

	// Property
	private Integer firstZ;

	// Property
	private Integer firstC;

	// Property
	private Integer firstT;

	// Property
	private UUID uuid;

	// -- Constructors --

	/** Default constructor. */
	public TiffData()
	{
		super();
	}

	/** 
	 * Constructs TiffData recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public TiffData(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from TiffData specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates TiffData recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"TiffData".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of TiffData got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of TiffData got %s",
			//		tagName));
		}
		if (element.hasAttribute("IFD"))
		{
			// Attribute property IFD
			setIFD(Integer.valueOf(
					element.getAttribute("IFD")));
		}
		if (element.hasAttribute("PlaneCount"))
		{
			// Attribute property PlaneCount
			setPlaneCount(Integer.valueOf(
					element.getAttribute("PlaneCount")));
		}
		if (element.hasAttribute("FirstZ"))
		{
			// Attribute property FirstZ
			setFirstZ(Integer.valueOf(
					element.getAttribute("FirstZ")));
		}
		if (element.hasAttribute("FirstC"))
		{
			// Attribute property FirstC
			setFirstC(Integer.valueOf(
					element.getAttribute("FirstC")));
		}
		if (element.hasAttribute("FirstT"))
		{
			// Attribute property FirstT
			setFirstT(Integer.valueOf(
					element.getAttribute("FirstT")));
		}
		List<Element> UUID_nodeList =
				getChildrenByTagName(element, "UUID");
		if (UUID_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"UUID node list size %d != 1",
					UUID_nodeList.size()));
		}
		else if (UUID_nodeList.size() != 0)
		{
			// Element property UUID which is complex (has
			// sub-elements)
			setUUID(new UUID(
					(Element) UUID_nodeList.get(0), model));
		}
	}

	// -- TiffData API methods --

	public void link(Reference reference, OMEModelObject o)
	{
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
	}


	// Property
	public Integer getIFD()
	{
		return ifd;
	}

	public void setIFD(Integer ifd)
	{
		this.ifd = ifd;
	}

	// Property
	public Integer getPlaneCount()
	{
		return planeCount;
	}

	public void setPlaneCount(Integer planeCount)
	{
		this.planeCount = planeCount;
	}

	// Property
	public Integer getFirstZ()
	{
		return firstZ;
	}

	public void setFirstZ(Integer firstZ)
	{
		this.firstZ = firstZ;
	}

	// Property
	public Integer getFirstC()
	{
		return firstC;
	}

	public void setFirstC(Integer firstC)
	{
		this.firstC = firstC;
	}

	// Property
	public Integer getFirstT()
	{
		return firstT;
	}

	public void setFirstT(Integer firstT)
	{
		this.firstT = firstT;
	}

	// Property
	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element TiffData_element)
	{
		// Creating XML block for TiffData
		if (TiffData_element == null)
		{
			TiffData_element =
					document.createElementNS(NAMESPACE, "TiffData");
		}

		if (ifd != null)
		{
			// Attribute property IFD
			TiffData_element.setAttribute("IFD", ifd.toString());
		}
		if (planeCount != null)
		{
			// Attribute property PlaneCount
			TiffData_element.setAttribute("PlaneCount", planeCount.toString());
		}
		if (firstZ != null)
		{
			// Attribute property FirstZ
			TiffData_element.setAttribute("FirstZ", firstZ.toString());
		}
		if (firstC != null)
		{
			// Attribute property FirstC
			TiffData_element.setAttribute("FirstC", firstC.toString());
		}
		if (firstT != null)
		{
			// Attribute property FirstT
			TiffData_element.setAttribute("FirstT", firstT.toString());
		}
		if (uuid != null)
		{
			// Element property UUID which is complex (has
			// sub-elements)
			TiffData_element.appendChild(uuid.asXMLElement(document));
		}
		return super.asXMLElement(document, TiffData_element);
	}
}
