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
