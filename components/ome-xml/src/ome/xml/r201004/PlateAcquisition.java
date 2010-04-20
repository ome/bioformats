/*
 * ome.xml.r201004.PlateAcquisition
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

public class PlateAcquisition extends Object
{
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

	// Property which occurs more than once
	private List<WellSample> wellSampleList = new ArrayList<WellSample>();

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// -- Constructors --

	/** Constructs a PlateAcquisition. */
	public PlateAcquisition()
	{
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

	// Property which occurs more than once
	public int sizeOfAnnotationList()
	{
		return annotationList.size();
	}

	public List<Annotation> copyAnnotationList()
	{
		return new ArrayList<Annotation>(annotationList);
	}

	public Annotation getAnnotation(int index)
	{
		return annotationList.get(index);
	}

	public Annotation setAnnotation(int index, Annotation annotation)
	{
		return annotationList.set(index, annotation);
	}

	public void addAnnotation(Annotation annotation)
	{
		annotationList.add(annotation);
	}

	public void removeAnnotation(Annotation annotation)
	{
		annotationList.remove(annotation);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for PlateAcquisition
		Element PlateAcquisition_element = document.createElement("PlateAcquisition");
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
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			PlateAcquisition_element.appendChild(description_element);
		}
		if (wellSampleList != null)
		{
			// Element property WellSampleRef which is complex (has
			// sub-elements) and occurs more than once
			for (WellSample wellSampleList_value : wellSampleList)
			{
				PlateAcquisition_element.appendChild(wellSampleList_value.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				PlateAcquisition_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		return PlateAcquisition_element;
	}

	public static PlateAcquisition fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"PlateAcquisition".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of PlateAcquisition got %s",
					tagName));
		}
		PlateAcquisition instance = new PlateAcquisition();
		if (element.hasAttribute("MaximumFieldCount"))
		{
			// Attribute property MaximumFieldCount
			instance.setMaximumFieldCount(Integer.valueOf(
					element.getAttribute("MaximumFieldCount")));
		}
		if (element.hasAttribute("EndTime"))
		{
			// Attribute property EndTime
			instance.setEndTime(String.valueOf(
					element.getAttribute("EndTime")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			instance.setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("StartTime"))
		{
			// Attribute property StartTime
			instance.setStartTime(String.valueOf(
					element.getAttribute("StartTime")));
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			instance.setName(String.valueOf(
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
			instance.setDescription(Description_nodeList.item(0).getTextContent());
		}
		// Element property WellSampleRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList WellSampleRef_nodeList = element.getElementsByTagName("WellSampleRef");
		for (int i = 0; i < WellSampleRef_nodeList.getLength(); i++)
		{
			instance.addWellSample(WellSample.fromXMLElement(
					(Element) WellSampleRef_nodeList.item(i)));
		}
		// Element property AnnotationRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList AnnotationRef_nodeList = element.getElementsByTagName("AnnotationRef");
		for (int i = 0; i < AnnotationRef_nodeList.getLength(); i++)
		{
			instance.addAnnotation(Annotation.fromXMLElement(
					(Element) AnnotationRef_nodeList.item(i)));
		}
		return instance;
	}
}
