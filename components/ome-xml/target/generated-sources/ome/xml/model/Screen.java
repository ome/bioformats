/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import ome.xml.model.enums.*;
import ome.xml.model.enums.handlers.*;
import ome.xml.model.primitives.*;

public class Screen extends AbstractOMEModelObject
{
	// Base:  -- Name: Screen -- Type: Screen -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Screen.class);

	// -- Instance variables --

	// Name property
	private String name;

	// ProtocolDescription property
	private String protocolDescription;

	// ProtocolIdentifier property
	private String protocolIdentifier;

	// ReagentSetDescription property
	private String reagentSetDescription;

	// Type property
	private String type;

	// ID property
	private String id;

	// ReagentSetIdentifier property
	private String reagentSetIdentifier;

	// Description property
	private String description;

	// Reagent property (occurs more than once)
	private List<Reagent> reagents = new ArrayList<Reagent>();

	// PlateRef reference (occurs more than once)
	private List<Plate> plateLinks = new ReferenceList<Plate>();

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// -- Constructors --

	/** Default constructor. */
	public Screen()
	{
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

	/** Copy constructor. */
	public Screen(Screen orig)
	{
		name = orig.name;
		protocolDescription = orig.protocolDescription;
		protocolIdentifier = orig.protocolIdentifier;
		reagentSetDescription = orig.reagentSetDescription;
		type = orig.type;
		id = orig.id;
		reagentSetIdentifier = orig.reagentSetIdentifier;
		description = orig.description;
		reagents = orig.reagents;
		plateLinks = orig.plateLinks;
		annotationLinks = orig.annotationLinks;
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
			PlateRef plateLinks_reference = new PlateRef();
			plateLinks_reference.setID(PlateRef_element.getAttribute("ID"));
			model.addReference(this, plateLinks_reference);
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
			plateLinks.add(o_casted);
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkScreen(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property Name
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	// Property ProtocolDescription
	public String getProtocolDescription()
	{
		return protocolDescription;
	}

	public void setProtocolDescription(String protocolDescription)
	{
		this.protocolDescription = protocolDescription;
	}

	// Property ProtocolIdentifier
	public String getProtocolIdentifier()
	{
		return protocolIdentifier;
	}

	public void setProtocolIdentifier(String protocolIdentifier)
	{
		this.protocolIdentifier = protocolIdentifier;
	}

	// Property ReagentSetDescription
	public String getReagentSetDescription()
	{
		return reagentSetDescription;
	}

	public void setReagentSetDescription(String reagentSetDescription)
	{
		this.reagentSetDescription = reagentSetDescription;
	}

	// Property Type
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	// Property ID
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property ReagentSetIdentifier
	public String getReagentSetIdentifier()
	{
		return reagentSetIdentifier;
	}

	public void setReagentSetIdentifier(String reagentSetIdentifier)
	{
		this.reagentSetIdentifier = reagentSetIdentifier;
	}

	// Property Description
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
		return reagents.size();
	}

	public List<Reagent> copyReagentList()
	{
		return new ArrayList<Reagent>(reagents);
	}

	public Reagent getReagent(int index)
	{
		return reagents.get(index);
	}

	public Reagent setReagent(int index, Reagent reagent)
	{
        reagent.setScreen(this);
		return reagents.set(index, reagent);
	}

	public void addReagent(Reagent reagent)
	{
        reagent.setScreen(this);
		reagents.add(reagent);
	}

	public void removeReagent(Reagent reagent)
	{
		reagents.remove(reagent);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedPlateList()
	{
		return plateLinks.size();
	}

	public List<Plate> copyLinkedPlateList()
	{
		return new ArrayList<Plate>(plateLinks);
	}

	public Plate getLinkedPlate(int index)
	{
		return plateLinks.get(index);
	}

	public Plate setLinkedPlate(int index, Plate o)
	{
		return plateLinks.set(index, o);
	}

	public boolean linkPlate(Plate o)
	{

			o.linkScreen(this);
		return plateLinks.add(o);
	}

	public boolean unlinkPlate(Plate o)
	{

			o.unlinkScreen(this);
		return plateLinks.remove(o);
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

			o.linkScreen(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkScreen(this);
		return annotationLinks.remove(o);
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
		if (reagents != null)
		{
			// Element property Reagent which is complex (has
			// sub-elements) and occurs more than once
			for (Reagent reagents_value : reagents)
			{
				Screen_element.appendChild(reagents_value.asXMLElement(document));
			}
		}
		if (plateLinks != null)
		{
			// Reference property PlateRef which occurs more than once
			for (Plate plateLinks_value : plateLinks)
			{
				PlateRef o = new PlateRef();
				o.setID(plateLinks_value.getID());
				Screen_element.appendChild(o.asXMLElement(document));
			}
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Screen_element.appendChild(o.asXMLElement(document));
			}
		}

		return super.asXMLElement(document, Screen_element);
	}
}
