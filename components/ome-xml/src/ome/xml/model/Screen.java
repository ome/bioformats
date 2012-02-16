/*
 * ome.xml.model.Screen
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

public class Screen extends AbstractOMEModelObject
{
	// Base:  -- Name: Screen -- Type: Screen -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2011-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Screen.class);

	// -- Instance variables --


	// Property
	private String name;

	// Property
	private String protocolDescription;

	// Property
	private String protocolIdentifier;

	// Property
	private String reagentSetDescription;

	// Property
	private String type;

	// Property
	private String id;

	// Property
	private String reagentSetIdentifier;

	// Property
	private String description;

	// Property which occurs more than once
	private List<Reagent> reagentList = new ArrayList<Reagent>();

	// Reference PlateRef
	private List<Plate> plateList = new ArrayList<Plate>();

	// Reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// -- Constructors --

	/** Default constructor. */
	public Screen()
	{
		super();
	}

	/** 
	 * Constructs Screen recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Screen(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Screen specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Screen recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Screen".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Screen got {}", tagName);
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		if (element.hasAttribute("ProtocolDescription"))
		{
			// Attribute property ProtocolDescription
			setProtocolDescription(String.valueOf(
					element.getAttribute("ProtocolDescription")));
		}
		if (element.hasAttribute("ProtocolIdentifier"))
		{
			// Attribute property ProtocolIdentifier
			setProtocolIdentifier(String.valueOf(
					element.getAttribute("ProtocolIdentifier")));
		}
		if (element.hasAttribute("ReagentSetDescription"))
		{
			// Attribute property ReagentSetDescription
			setReagentSetDescription(String.valueOf(
					element.getAttribute("ReagentSetDescription")));
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property Type
			setType(String.valueOf(
					element.getAttribute("Type")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Screen missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("ReagentSetIdentifier"))
		{
			// Attribute property ReagentSetIdentifier
			setReagentSetIdentifier(String.valueOf(
					element.getAttribute("ReagentSetIdentifier")));
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
		// Element property Reagent which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Reagent_nodeList =
				getChildrenByTagName(element, "Reagent");
		for (Element Reagent_element : Reagent_nodeList)
		{
			addReagent(
					new Reagent(Reagent_element, model));
		}
		// Element reference PlateRef
		List<Element> PlateRef_nodeList =
				getChildrenByTagName(element, "PlateRef");
		for (Element PlateRef_element : PlateRef_nodeList)
		{
			PlateRef plateList_reference = new PlateRef();
			plateList_reference.setID(PlateRef_element.getAttribute("ID"));
			model.addReference(this, plateList_reference);
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

	// -- Screen API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof PlateRef)
		{
			Plate o_casted = (Plate) o;
			o_casted.linkScreen(this);
			if (!plateList.contains(o_casted)) {
				plateList.add(o_casted);
			}
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkScreen(this);
			if (!annotationList.contains(o_casted)) {
				annotationList.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
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
	public String getProtocolDescription()
	{
		return protocolDescription;
	}

	public void setProtocolDescription(String protocolDescription)
	{
		this.protocolDescription = protocolDescription;
	}

	// Property
	public String getProtocolIdentifier()
	{
		return protocolIdentifier;
	}

	public void setProtocolIdentifier(String protocolIdentifier)
	{
		this.protocolIdentifier = protocolIdentifier;
	}

	// Property
	public String getReagentSetDescription()
	{
		return reagentSetDescription;
	}

	public void setReagentSetDescription(String reagentSetDescription)
	{
		this.reagentSetDescription = reagentSetDescription;
	}

	// Property
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
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
	public String getReagentSetIdentifier()
	{
		return reagentSetIdentifier;
	}

	public void setReagentSetIdentifier(String reagentSetIdentifier)
	{
		this.reagentSetIdentifier = reagentSetIdentifier;
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
	public int sizeOfReagentList()
	{
		return reagentList.size();
	}

	public List<Reagent> copyReagentList()
	{
		return new ArrayList<Reagent>(reagentList);
	}

	public Reagent getReagent(int index)
	{
		return reagentList.get(index);
	}

	public Reagent setReagent(int index, Reagent reagent)
	{
		return reagentList.set(index, reagent);
	}

	public void addReagent(Reagent reagent)
	{
		reagentList.add(reagent);
	}

	public void removeReagent(Reagent reagent)
	{
		reagentList.remove(reagent);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedPlateList()
	{
		return plateList.size();
	}

	public List<Plate> copyLinkedPlateList()
	{
		return new ArrayList<Plate>(plateList);
	}

	public Plate getLinkedPlate(int index)
	{
		return plateList.get(index);
	}

	public Plate setLinkedPlate(int index, Plate o)
	{
		return plateList.set(index, o);
	}

	public boolean linkPlate(Plate o)
	{
		o.linkScreen(this);
		if (!plateList.contains(o)) {
			return plateList.add(o);
		}
		return false;
	}

	public boolean unlinkPlate(Plate o)
	{
		o.unlinkScreen(this);
		return plateList.remove(o);
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
		o.linkScreen(this);
		if (!annotationList.contains(o)) {
			return annotationList.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{
		o.unlinkScreen(this);
		return annotationList.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Screen_element)
	{
		// Creating XML block for Screen

		if (Screen_element == null)
		{
			Screen_element =
					document.createElementNS(NAMESPACE, "Screen");
		}

		if (name != null)
		{
			// Attribute property Name
			Screen_element.setAttribute("Name", name.toString());
		}
		if (protocolDescription != null)
		{
			// Attribute property ProtocolDescription
			Screen_element.setAttribute("ProtocolDescription", protocolDescription.toString());
		}
		if (protocolIdentifier != null)
		{
			// Attribute property ProtocolIdentifier
			Screen_element.setAttribute("ProtocolIdentifier", protocolIdentifier.toString());
		}
		if (reagentSetDescription != null)
		{
			// Attribute property ReagentSetDescription
			Screen_element.setAttribute("ReagentSetDescription", reagentSetDescription.toString());
		}
		if (type != null)
		{
			// Attribute property Type
			Screen_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Screen_element.setAttribute("ID", id.toString());
		}
		if (reagentSetIdentifier != null)
		{
			// Attribute property ReagentSetIdentifier
			Screen_element.setAttribute("ReagentSetIdentifier", reagentSetIdentifier.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
			Screen_element.appendChild(description_element);
		}
		if (reagentList != null)
		{
			// Element property Reagent which is complex (has
			// sub-elements) and occurs more than once
			for (Reagent reagentList_value : reagentList)
			{
				Screen_element.appendChild(reagentList_value.asXMLElement(document));
			}
		}
		if (plateList != null)
		{
			// Reference property PlateRef which occurs more than once
			for (Plate plateList_value : plateList)
			{
				PlateRef o = new PlateRef();
				o.setID(plateList_value.getID());
				Screen_element.appendChild(o.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationList_value.getID());
				Screen_element.appendChild(o.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, Screen_element);
	}
}
