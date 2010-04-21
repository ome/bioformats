/*
 * ome.xml.r201004.Screen
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
 * Created by callan via xsd-fu on 2010-04-21 15:20:31+0100
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

public class Screen extends AbstractOMEModelObject
{
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

	// Back reference PlateRef
	private List<Plate> plateList = new ArrayList<Plate>();

	// Back reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference Plate_BackReference
	private List<Plate> plate_BackReferenceList = new ArrayList<Plate>();

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
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Screen(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates Screen recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Screen".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of Screen got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of Screen got %s",
			//		tagName));
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
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("ReagentSetIdentifier"))
		{
			// Attribute property ReagentSetIdentifier
			setReagentSetIdentifier(String.valueOf(
					element.getAttribute("ReagentSetIdentifier")));
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
		// Element property Reagent which is complex (has
		// sub-elements) and occurs more than once
		NodeList Reagent_nodeList = element.getElementsByTagName("Reagent");
		for (int i = 0; i < Reagent_nodeList.getLength(); i++)
		{
			Element Reagent_element = (Element) Reagent_nodeList.item(i);
			addReagent(
					new Reagent(Reagent_element));
		}
		// *** IGNORING *** Skipped back reference PlateRef
		// *** IGNORING *** Skipped back reference AnnotationRef
		// *** IGNORING *** Skipped back reference Plate_BackReference
	}

	// -- Screen API methods --

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

	// Reference PlateRef
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

	public void linkPlate(Plate o)
	{
		this.plateList.add(o);
	}

	public void unlinkPlate(Plate o)
	{
		this.plateList.add(o);
	}

	// Reference AnnotationRef
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

	public void linkAnnotation(Annotation o)
	{
		this.annotationList.add(o);
	}

	public void unlinkAnnotation(Annotation o)
	{
		this.annotationList.add(o);
	}

	// Property which occurs more than once
	public int sizeOfPlateList()
	{
		return plate_BackReferenceList.size();
	}

	public List<Plate> copyPlateList()
	{
		return new ArrayList<Plate>(plate_BackReferenceList);
	}

	public Plate getPlate(int index)
	{
		return plate_BackReferenceList.get(index);
	}

	public Plate setPlate(int index, Plate plate_BackReference)
	{
		return plate_BackReferenceList.set(index, plate_BackReference);
	}

	public void addPlate(Plate plate_BackReference)
	{
		plate_BackReferenceList.add(plate_BackReference);
	}

	public void removePlate(Plate plate_BackReference)
	{
		plate_BackReferenceList.remove(plate_BackReference);
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
			Screen_element = document.createElement("Screen");
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
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
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
			// *** IGNORING *** Skipped back reference PlateRef
		}
		if (annotationList != null)
		{
			// *** IGNORING *** Skipped back reference AnnotationRef
		}
		if (plate_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Plate_BackReference
		}
		return super.asXMLElement(document, Screen_element);
	}
}
