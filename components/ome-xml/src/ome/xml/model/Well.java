/*
 * ome.xml.model.Well
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
 * Created by melissa via xsd-fu on 2012-01-12 20:06:01-0500
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

public class Well extends AbstractOMEModelObject
{
	// Base:  -- Name: Well -- Type: Well -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2011-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Well.class);

	// -- Instance variables --


	// Property
	private String status;

	// Property
	private String externalIdentifier;

	// Property
	private NonNegativeInteger column;

	// Property
	private String externalDescription;

	// Property
	private Integer color;

	// Property
	private String id;

	// Property
	private NonNegativeInteger row;

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
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Well(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Well specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Well recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Well".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Well got {}", tagName);
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
			setColumn(NonNegativeInteger.valueOf(
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
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Well missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("Row"))
		{
			// Attribute property Row
			setRow(NonNegativeInteger.valueOf(
					element.getAttribute("Row")));
		}
		// Element property WellSample which is complex (has
		// sub-elements) and occurs more than once
		List<Element> WellSample_nodeList =
				getChildrenByTagName(element, "WellSample");
		for (Element WellSample_element : WellSample_nodeList)
		{
			addWellSample(
					new WellSample(WellSample_element, model));
		}
		// Element reference ReagentRef
		List<Element> ReagentRef_nodeList =
				getChildrenByTagName(element, "ReagentRef");
		for (Element ReagentRef_element : ReagentRef_nodeList)
		{
			ReagentRef reagent_reference = new ReagentRef();
			reagent_reference.setID(ReagentRef_element.getAttribute("ID"));
			model.addReference(this, reagent_reference);
		}
		// Element reference AnnotationRef
		List<Element> AnnotationRef_nodeList =
				getChildrenByTagName(element, "AnnotationRef");
		for (Element AnnotationRef_element : AnnotationRef_nodeList)
		{
			AnnotationRef annotationList_reference = new AnnotationRef();
			annotationList_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationList_reference);
		}
	}

	// -- Well API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof ReagentRef)
		{
			Reagent o_casted = (Reagent) o;
			o_casted.linkWell(this);
			reagent = o_casted;
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkWell(this);
			if (!annotationList.contains(o_casted)) {
				annotationList.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


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
	public NonNegativeInteger getColumn()
	{
		return column;
	}

	public void setColumn(NonNegativeInteger column)
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
	public NonNegativeInteger getRow()
	{
		return row;
	}

	public void setRow(NonNegativeInteger row)
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
		if (!annotationList.contains(o)) {
			return annotationList.add(o);
		}
		return false;
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
