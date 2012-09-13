/*
 * ome.xml.model.PlateAcquisition
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
 * Created by melissa via xsd-fu on 2012-09-10 13:40:21-0400
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

public class PlateAcquisition extends AbstractOMEModelObject
{
	// Base:  -- Name: PlateAcquisition -- Type: PlateAcquisition -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(PlateAcquisition.class);

	// -- Instance variables --


	// Property
	private PositiveInteger maximumFieldCount;

	// Property
	private Timestamp endTime;

	// Property
	private String id;

	// Property
	private Timestamp startTime;

	// Property
	private String name;

	// Property
	private String description;

	// Reference WellSampleRef
	private List<WellSample> wellSamples = new ArrayList<WellSample>();

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

	// Back reference Plate_BackReference
	private Plate plate;

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
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public PlateAcquisition(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from PlateAcquisition specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates PlateAcquisition recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"PlateAcquisition".equals(tagName))
		{
			LOGGER.debug("Expecting node name of PlateAcquisition got {}", tagName);
		}
		if (element.hasAttribute("MaximumFieldCount"))
		{
			// Attribute property MaximumFieldCount
			setMaximumFieldCount(PositiveInteger.valueOf(
					element.getAttribute("MaximumFieldCount")));
		}
		if (element.hasAttribute("EndTime"))
		{
			// Attribute property EndTime
			setEndTime(Timestamp.valueOf(
					element.getAttribute("EndTime")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"PlateAcquisition missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("StartTime"))
		{
			// Attribute property StartTime
			setStartTime(Timestamp.valueOf(
					element.getAttribute("StartTime")));
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		List<Element> Description_nodeList =
				getChildrenByTagName(element, "Description");
		if (Description_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Description node list size %d != 1",
					Description_nodeList.size()));
		}
		else if (Description_nodeList.size() != 0)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			setDescription(
					String.valueOf(Description_nodeList.get(0).getTextContent()));
		}
		// Element reference WellSampleRef
		List<Element> WellSampleRef_nodeList =
				getChildrenByTagName(element, "WellSampleRef");
		for (Element WellSampleRef_element : WellSampleRef_nodeList)
		{
			WellSampleRef wellSamples_reference = new WellSampleRef();
			wellSamples_reference.setID(WellSampleRef_element.getAttribute("ID"));
			model.addReference(this, wellSamples_reference);
		}
		// Element reference AnnotationRef
		List<Element> AnnotationRef_nodeList =
				getChildrenByTagName(element, "AnnotationRef");
		for (Element AnnotationRef_element : AnnotationRef_nodeList)
		{
			AnnotationRef annotationLinks_reference = new AnnotationRef();
			annotationLinks_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationLinks_reference);
		}
		// *** IGNORING *** Skipped back reference Plate_BackReference
	}

	// -- PlateAcquisition API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof WellSampleRef)
		{
			WellSample o_casted = (WellSample) o;
			o_casted.linkPlateAcquisition(this);
			if (!wellSamples.contains(o_casted)) {
				wellSamples.add(o_casted);
			}
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkPlateAcquisition(this);
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public PositiveInteger getMaximumFieldCount()
	{
		return maximumFieldCount;
	}

	public void setMaximumFieldCount(PositiveInteger maximumFieldCount)
	{
		this.maximumFieldCount = maximumFieldCount;
	}

	// Property
	public Timestamp getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Timestamp endTime)
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
	public Timestamp getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Timestamp startTime)
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
		return wellSamples.size();
	}

	public List<WellSample> copyLinkedWellSampleList()
	{
		return new ArrayList<WellSample>(wellSamples);
	}

	public WellSample getLinkedWellSample(int index)
	{
		return wellSamples.get(index);
	}

	public WellSample setLinkedWellSample(int index, WellSample o)
	{
		return wellSamples.set(index, o);
	}

	public boolean linkWellSample(WellSample o)
	{

			o.linkPlateAcquisition(this);
		if (!wellSamples.contains(o)) {
			return wellSamples.add(o);
		}
		return false;
	}

	public boolean unlinkWellSample(WellSample o)
	{

			o.unlinkPlateAcquisition(this);
		return wellSamples.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedAnnotationList()
	{
		return annotationLinks.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationLinks);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationLinks.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationLinks.set(index, o);
	}

	public boolean linkAnnotation(Annotation o)
	{

			o.linkPlateAcquisition(this);
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkPlateAcquisition(this);
		return annotationLinks.remove(o);
	}

	// Property
	public Plate getPlate()
	{
		return plate;
	}

	public void setPlate(Plate plate_BackReference)
	{
		this.plate = plate_BackReference;
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
			description_element.setTextContent(description.toString());
			PlateAcquisition_element.appendChild(description_element);
		}
		if (wellSamples != null)
		{
			// Reference property WellSampleRef which occurs more than once
			for (WellSample wellSamples_value : wellSamples)
			{
				WellSampleRef o = new WellSampleRef();
				o.setID(wellSamples_value.getID());
				PlateAcquisition_element.appendChild(o.asXMLElement(document));
			}
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				PlateAcquisition_element.appendChild(o.asXMLElement(document));
			}
		}
		if (plate != null)
		{
			// *** IGNORING *** Skipped back reference Plate_BackReference
		}
		return super.asXMLElement(document, PlateAcquisition_element);
	}
}
