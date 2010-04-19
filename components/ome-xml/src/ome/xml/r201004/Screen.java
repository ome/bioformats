/*
 * ome.xml.r201004.Screen
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
 * Created by callan via xsd-fu on 2010-04-19 19:23:58+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ome.xml.r201004.enums.*;

public class Screen extends Object
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

	// Property which occurs more than once
	private List<Plate> plateList = new ArrayList<Plate>();

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference Plate_BackReference
	private List<Plate> plate_BackReferenceList = new ArrayList<Plate>();

	// -- Constructors --

	/** Constructs a Screen. */
	public Screen()
	{
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

	// Property which occurs more than once
	public int sizeOfPlateList()
	{
		return plateList.size();
	}

	public List<Plate> copyPlateList()
	{
		return new ArrayList<Plate>(plateList);
	}

	public Plate getPlate(int index)
	{
		return plateList.get(index);
	}

	public Plate setPlate(int index, Plate plate)
	{
		return plateList.set(index, plate);
	}

	public void addPlate(Plate plate)
	{
		plateList.add(plate);
	}

	public void removePlate(Plate plate)
	{
		plateList.remove(plate);
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

	// Back reference Plate_BackReference
	public int sizeOfLinkedPlateList()
	{
		return plate_BackReferenceList.size();
	}

	public List<Plate> copyLinkedPlateList()
	{
		return new ArrayList<Plate>(plate_BackReferenceList);
	}

	public Plate getLinkedPlate(int index)
	{
		return plate_BackReferenceList.get(index);
	}

	public Plate setLinkedPlate(int index, Plate plate_BackReference)
	{
		return plate_BackReferenceList.set(index, plate_BackReference);
	}

	public void linkPlate(Plate plate_BackReference)
	{
		this.plate_BackReferenceList.add(plate_BackReference);
	}

	public void unlinkPlate(Plate plate_BackReference)
	{
		this.plate_BackReferenceList.add(plate_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Screen
		Element Screen_element = document.createElement("Screen");
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
			// Element property PlateRef which is complex (has
			// sub-elements) and occurs more than once
			for (Plate plateList_value : plateList)
			{
				Screen_element.appendChild(plateList_value.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Screen_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		if (plate_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Plate_BackReference
		}
		return Screen_element;
	}
}
