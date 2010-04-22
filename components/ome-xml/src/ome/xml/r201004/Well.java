/*
 * ome.xml.r201004.Well
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
 * Created by callan via xsd-fu on 2010-04-22 17:37:18+0100
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

public class Well extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2010-04";

	// -- Instance variables --

	// Property
	private String status;

	// Property
	private String externalIdentifier;

	// Property
	private Integer column;

	// Property
	private String externalDescription;

	// Property
	private Integer color;

	// Property
	private String id;

	// Property
	private Integer row;

	// Property which occurs more than once
	private List<WellSample> wellSampleList = new ArrayList<WellSample>();

	// Property
	private Reagent reagent;

	// Reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// -- Constructors --

	/** Default constructor. */
	public Well()
	{
		super();
	}

	/** 
	 * Constructs Well recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Well(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates Well recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element) throws EnumerationException
	{	
		super.update(element);
		String tagName = element.getTagName();
		if (!"Well".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of Well got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of Well got %s",
			//		tagName));
		}
		if (element.hasAttribute("Status"))
		{
			// Attribute property Status
			setStatus(String.valueOf(
					element.getAttribute("Status")));
		}
		if (element.hasAttribute("ExternalIdentifier"))
		{
			// Attribute property ExternalIdentifier
			setExternalIdentifier(String.valueOf(
					element.getAttribute("ExternalIdentifier")));
		}
		if (element.hasAttribute("Column"))
		{
			// Attribute property Column
			setColumn(Integer.valueOf(
					element.getAttribute("Column")));
		}
		if (element.hasAttribute("ExternalDescription"))
		{
			// Attribute property ExternalDescription
			setExternalDescription(String.valueOf(
					element.getAttribute("ExternalDescription")));
		}
		if (element.hasAttribute("Color"))
		{
			// Attribute property Color
			setColor(Integer.valueOf(
					element.getAttribute("Color")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("Row"))
		{
			// Attribute property Row
			setRow(Integer.valueOf(
					element.getAttribute("Row")));
		}
		// Element property WellSample which is complex (has
		// sub-elements) and occurs more than once
		NodeList WellSample_nodeList = element.getElementsByTagName("WellSample");
		for (int i = 0; i < WellSample_nodeList.getLength(); i++)
		{
			Element WellSample_element = (Element) WellSample_nodeList.item(i);
			addWellSample(
					new WellSample(WellSample_element));
		}
		// *** IGNORING *** Skipped back reference ReagentRef
		// *** IGNORING *** Skipped back reference AnnotationRef
	}

	// -- Well API methods --


	// Property
	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	// Property
	public String getExternalIdentifier()
	{
		return externalIdentifier;
	}

	public void setExternalIdentifier(String externalIdentifier)
	{
		this.externalIdentifier = externalIdentifier;
	}

	// Property
	public Integer getColumn()
	{
		return column;
	}

	public void setColumn(Integer column)
	{
		this.column = column;
	}

	// Property
	public String getExternalDescription()
	{
		return externalDescription;
	}

	public void setExternalDescription(String externalDescription)
	{
		this.externalDescription = externalDescription;
	}

	// Property
	public Integer getColor()
	{
		return color;
	}

	public void setColor(Integer color)
	{
		this.color = color;
	}

	// Property
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property
	public Integer getRow()
	{
		return row;
	}

	public void setRow(Integer row)
	{
		this.row = row;
	}

	// Property which occurs more than once
	public int sizeOfWellSampleList()
	{
		return wellSampleList.size();
	}

	public List<WellSample> copyWellSampleList()
	{
		return new ArrayList<WellSample>(wellSampleList);
	}

	public WellSample getWellSample(int index)
	{
		return wellSampleList.get(index);
	}

	public WellSample setWellSample(int index, WellSample wellSample)
	{
		return wellSampleList.set(index, wellSample);
	}

	public void addWellSample(WellSample wellSample)
	{
		wellSampleList.add(wellSample);
	}

	public void removeWellSample(WellSample wellSample)
	{
		wellSampleList.remove(wellSample);
	}

	// Reference
	public Reagent getLinkedReagent()
	{
		return reagent;
	}

	public void linkReagent(Reagent o)
	{
		reagent = o;
	}

	public void unlinkReagent(Reagent o)
	{
		if (reagent == o)
		{
			reagent = null;
		}
	}

	// Reference which occurs more than once
	public int sizeOfLinkedAnnotationList()
	{
		return annotationList.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationList);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationList.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationList.set(index, o);
	}

	public boolean linkAnnotation(Annotation o)
	{
		o.linkWell(this);
		return annotationList.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{
		o.unlinkWell(this);
		return annotationList.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Well_element)
	{
		// Creating XML block for Well
		if (Well_element == null)
		{
			Well_element =
					document.createElementNS(NAMESPACE, "Well");
		}

		if (status != null)
		{
			// Attribute property Status
			Well_element.setAttribute("Status", status.toString());
		}
		if (externalIdentifier != null)
		{
			// Attribute property ExternalIdentifier
			Well_element.setAttribute("ExternalIdentifier", externalIdentifier.toString());
		}
		if (column != null)
		{
			// Attribute property Column
			Well_element.setAttribute("Column", column.toString());
		}
		if (externalDescription != null)
		{
			// Attribute property ExternalDescription
			Well_element.setAttribute("ExternalDescription", externalDescription.toString());
		}
		if (color != null)
		{
			// Attribute property Color
			Well_element.setAttribute("Color", color.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Well_element.setAttribute("ID", id.toString());
		}
		if (row != null)
		{
			// Attribute property Row
			Well_element.setAttribute("Row", row.toString());
		}
		if (wellSampleList != null)
		{
			// Element property WellSample which is complex (has
			// sub-elements) and occurs more than once
			for (WellSample wellSampleList_value : wellSampleList)
			{
				Well_element.appendChild(wellSampleList_value.asXMLElement(document));
			}
		}
		if (reagent != null)
		{
			// Reference property ReagentRef
			ReagentRef o = new ReagentRef();
			o.setID(reagent.getID());
			Well_element.appendChild(o.asXMLElement(document));
		}
		if (annotationList != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationList_value.getID());
				Well_element.appendChild(o.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, Well_element);
	}
}
