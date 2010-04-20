
/*
 * ome.xml.r201004.Reagent
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
 * Created by callan via xsd-fu on 2010-04-20 18:27:32+0100
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

public class Reagent extends AbstractOMEModelObject
{
	// -- Instance variables --

	// Property
	private String reagentIdentifier;

	// Property
	private String id;

	// Property
	private String name;

	// Property
	private String description;

	// Back reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference Well_BackReference
	private List<Well> well_BackReferenceList = new ArrayList<Well>();

	// -- Constructors --

	/** Default constructor. */
	public Reagent()
	{
		super();
	}

	/** 
	 * Constructs Reagent recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Reagent(Element element) throws EnumerationException
	{
		super(element);
		String tagName = element.getTagName();
		if (!"Reagent".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Reagent got %s",
					tagName));
		}
		// Model object: None
		if (element.hasAttribute("ReagentIdentifier"))
		{
			// Attribute property ReagentIdentifier
			setReagentIdentifier(String.valueOf(
					element.getAttribute("ReagentIdentifier")));
		}
		// Model object: None
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		// Model object: None
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		// Model object: None
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
		// Model object: None
		// *** IGNORING *** Skipped back reference AnnotationRef
		// Model object: None
		// *** IGNORING *** Skipped back reference Well_BackReference
	}

	// -- Reagent API methods --

	// Property
	public String getReagentIdentifier()
	{
		return reagentIdentifier;
	}

	public void setReagentIdentifier(String reagentIdentifier)
	{
		this.reagentIdentifier = reagentIdentifier;
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
	public int sizeOfWellList()
	{
		return well_BackReferenceList.size();
	}

	public List<Well> copyWellList()
	{
		return new ArrayList<Well>(well_BackReferenceList);
	}

	public Well getWell(int index)
	{
		return well_BackReferenceList.get(index);
	}

	public Well setWell(int index, Well well_BackReference)
	{
		return well_BackReferenceList.set(index, well_BackReference);
	}

	public void addWell(Well well_BackReference)
	{
		well_BackReferenceList.add(well_BackReference);
	}

	public void removeWell(Well well_BackReference)
	{
		well_BackReferenceList.remove(well_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Reagent
		Element Reagent_element = document.createElement("Reagent");
		if (reagentIdentifier != null)
		{
			// Attribute property ReagentIdentifier
			Reagent_element.setAttribute("ReagentIdentifier", reagentIdentifier.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Reagent_element.setAttribute("ID", id.toString());
		}
		if (name != null)
		{
			// Attribute property Name
			Reagent_element.setAttribute("Name", name.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			Reagent_element.appendChild(description_element);
		}
		if (annotationList != null)
		{
			// *** IGNORING *** Skipped back reference AnnotationRef
		}
		if (well_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Well_BackReference
		}
		return Reagent_element;
	}
}
