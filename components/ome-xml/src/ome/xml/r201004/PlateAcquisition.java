/*
 * ome.xml.r201004.PlateAcquisition
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
 * Created by callan via xsd-fu on 2010-04-22 17:27:24+0100
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

public class PlateAcquisition extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2010-04";

	// -- Instance variables --

	// Property
	private Integer maximumFieldCount;

	// Property
	private String endTime;

	// Property
	private String id;

	// Property
	private String startTime;

	// Property
	private String name;

	// Property
	private String description;

	// Reference WellSampleRef
	private List<WellSample> wellSampleList = new ArrayList<WellSample>();

	// Reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// -- Constructors --

	/** Default constructor. */
	public PlateAcquisition()
	{
		super();
	}

	/** 
	 * Constructs PlateAcquisition recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public PlateAcquisition(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates PlateAcquisition recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"PlateAcquisition".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of PlateAcquisition got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of PlateAcquisition got %s",
			//		tagName));
		}
		if (element.hasAttribute("MaximumFieldCount"))
		{
			// Attribute property MaximumFieldCount
			setMaximumFieldCount(Integer.valueOf(
					element.getAttribute("MaximumFieldCount")));
		}
		if (element.hasAttribute("EndTime"))
		{
			// Attribute property EndTime
			setEndTime(String.valueOf(
					element.getAttribute("EndTime")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("StartTime"))
		{
			// Attribute property StartTime
			setStartTime(String.valueOf(
					element.getAttribute("StartTime")));
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		NodeList Description_nodeList = element.getElementsByTagName("Description");
		if (Description_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Description node list size %d != 1",
					Description_nodeList.getLength()));
		}
		else if (Description_nodeList.getLength() != 0)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			setDescription(Description_nodeList.item(0).getTextContent());
		}
		// *** IGNORING *** Skipped back reference WellSampleRef
		// *** IGNORING *** Skipped back reference AnnotationRef
	}

	// -- PlateAcquisition API methods --


	// Property
	public Integer getMaximumFieldCount()
	{
		return maximumFieldCount;
	}

	public void setMaximumFieldCount(Integer maximumFieldCount)
	{
		this.maximumFieldCount = maximumFieldCount;
	}

	// Property
	public String getEndTime()
	{
		return endTime;
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
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
	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	// Property
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	// Property
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	// Reference which occurs more than once
	public int sizeOfLinkedWellSampleList()
	{
		return wellSampleList.size();
	}

	public List<WellSample> copyLinkedWellSampleList()
	{
		return new ArrayList<WellSample>(wellSampleList);
	}

	public WellSample getLinkedWellSample(int index)
	{
		return wellSampleList.get(index);
	}

	public WellSample setLinkedWellSample(int index, WellSample o)
	{
		return wellSampleList.set(index, o);
	}

	public boolean linkWellSample(WellSample o)
	{
		o.linkPlateAcquisition(this);
		return wellSampleList.add(o);
	}

	public boolean unlinkWellSample(WellSample o)
	{
		o.unlinkPlateAcquisition(this);
		return wellSampleList.remove(o);
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
		o.linkPlateAcquisition(this);
		return annotationList.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{
		o.unlinkPlateAcquisition(this);
		return annotationList.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element PlateAcquisition_element)
	{
		// Creating XML block for PlateAcquisition
		if (PlateAcquisition_element == null)
		{
			PlateAcquisition_element =
					document.createElementNS(NAMESPACE, "PlateAcquisition");
		}

		if (maximumFieldCount != null)
		{
			// Attribute property MaximumFieldCount
			PlateAcquisition_element.setAttribute("MaximumFieldCount", maximumFieldCount.toString());
		}
		if (endTime != null)
		{
			// Attribute property EndTime
			PlateAcquisition_element.setAttribute("EndTime", endTime.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			PlateAcquisition_element.setAttribute("ID", id.toString());
		}
		if (startTime != null)
		{
			// Attribute property StartTime
			PlateAcquisition_element.setAttribute("StartTime", startTime.toString());
		}
		if (name != null)
		{
			// Attribute property Name
			PlateAcquisition_element.setAttribute("Name", name.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description);
			PlateAcquisition_element.appendChild(description_element);
		}
		if (wellSampleList != null)
		{
			// Reference property WellSampleRef which occurs more than once
			for (WellSample wellSampleList_value : wellSampleList)
			{
				Element wellSampleList_value_element = 
						document.createElementNS(NAMESPACE, "WellSampleRef");
				wellSampleList_value_element.setAttribute(
						"ID", wellSampleList_value.getID());
				PlateAcquisition_element.appendChild(wellSampleList_value_element);
			}
		}
		if (annotationList != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Element annotationList_value_element = 
						document.createElementNS(NAMESPACE, "AnnotationRef");
				annotationList_value_element.setAttribute(
						"ID", annotationList_value.getID());
				PlateAcquisition_element.appendChild(annotationList_value_element);
			}
		}
		return super.asXMLElement(document, PlateAcquisition_element);
	}
}
