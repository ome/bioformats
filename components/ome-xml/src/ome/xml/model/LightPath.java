/*
 * ome.xml.model.LightPath
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

public class LightPath extends AbstractOMEModelObject
{
	// Base:  -- Name: LightPath -- Type: LightPath -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(LightPath.class);

	// -- Instance variables --


	// Reference ExcitationFilterRef
	private List<Filter> excitationFilterLinks = new ArrayList<Filter>();

	// Property
	private Dichroic dichroic;

	// Reference EmissionFilterRef
	private List<Filter> emissionFilterLinks = new ArrayList<Filter>();

	// -- Constructors --

	/** Default constructor. */
	public LightPath()
	{
		super();
	}

	/** 
	 * Constructs LightPath recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public LightPath(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from LightPath specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates LightPath recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"LightPath".equals(tagName))
		{
			LOGGER.debug("Expecting node name of LightPath got {}", tagName);
		}
		// Element reference ExcitationFilterRef
		List<Element> ExcitationFilterRef_nodeList =
				getChildrenByTagName(element, "ExcitationFilterRef");
		for (Element ExcitationFilterRef_element : ExcitationFilterRef_nodeList)
		{
			ExcitationFilterRef excitationFilterLinks_reference = new ExcitationFilterRef();
			excitationFilterLinks_reference.setID(ExcitationFilterRef_element.getAttribute("ID"));
			model.addReference(this, excitationFilterLinks_reference);
		}
		// Element reference DichroicRef
		List<Element> DichroicRef_nodeList =
				getChildrenByTagName(element, "DichroicRef");
		for (Element DichroicRef_element : DichroicRef_nodeList)
		{
			DichroicRef dichroic_reference = new DichroicRef();
			dichroic_reference.setID(DichroicRef_element.getAttribute("ID"));
			model.addReference(this, dichroic_reference);
		}
		// Element reference EmissionFilterRef
		List<Element> EmissionFilterRef_nodeList =
				getChildrenByTagName(element, "EmissionFilterRef");
		for (Element EmissionFilterRef_element : EmissionFilterRef_nodeList)
		{
			EmissionFilterRef emissionFilterLinks_reference = new EmissionFilterRef();
			emissionFilterLinks_reference.setID(EmissionFilterRef_element.getAttribute("ID"));
			model.addReference(this, emissionFilterLinks_reference);
		}
	}

	// -- LightPath API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof ExcitationFilterRef)
		{
			Filter o_casted = (Filter) o;
      o_casted.linkLightPathExcitationFilter(this);
			if (!excitationFilterLinks.contains(o_casted)) {
				excitationFilterLinks.add(o_casted);
			}
			return true;
		}
		if (reference instanceof DichroicRef)
		{
			Dichroic o_casted = (Dichroic) o;
			o_casted.linkLightPath(this);
			dichroic = o_casted;
			return true;
		}
		if (reference instanceof EmissionFilterRef)
		{
			Filter o_casted = (Filter) o;
      o_casted.linkLightPathEmissionFilter(this);
			if (!emissionFilterLinks.contains(o_casted)) {
				emissionFilterLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Reference which occurs more than once
	public int sizeOfLinkedExcitationFilterList()
	{
		return excitationFilterLinks.size();
	}

	public List<Filter> copyLinkedExcitationFilterList()
	{
		return new ArrayList<Filter>(excitationFilterLinks);
	}

	public Filter getLinkedExcitationFilter(int index)
	{
		return excitationFilterLinks.get(index);
	}

	public Filter setLinkedExcitationFilter(int index, Filter o)
	{
		return excitationFilterLinks.set(index, o);
	}

	public boolean linkExcitationFilter(Filter o)
	{

      o.linkLightPathExcitationFilter(this);
		if (!excitationFilterLinks.contains(o)) {
			return excitationFilterLinks.add(o);
		}
		return false;
	}

	public boolean unlinkExcitationFilter(Filter o)
	{

      o.unlinkLightPathExcitationFilter(this);
		return excitationFilterLinks.remove(o);
	}

	// Reference
	public Dichroic getLinkedDichroic()
	{
		return dichroic;
	}

	public void linkDichroic(Dichroic o)
	{
		dichroic = o;
	}

	public void unlinkDichroic(Dichroic o)
	{
		if (dichroic == o)
		{
			dichroic = null;
		}
	}

	// Reference which occurs more than once
	public int sizeOfLinkedEmissionFilterList()
	{
		return emissionFilterLinks.size();
	}

	public List<Filter> copyLinkedEmissionFilterList()
	{
		return new ArrayList<Filter>(emissionFilterLinks);
	}

	public Filter getLinkedEmissionFilter(int index)
	{
		return emissionFilterLinks.get(index);
	}

	public Filter setLinkedEmissionFilter(int index, Filter o)
	{
		return emissionFilterLinks.set(index, o);
	}

	public boolean linkEmissionFilter(Filter o)
	{

      o.linkLightPathEmissionFilter(this);
		if (!emissionFilterLinks.contains(o)) {
			return emissionFilterLinks.add(o);
		}
		return false;
	}

	public boolean unlinkEmissionFilter(Filter o)
	{

      o.unlinkLightPathEmissionFilter(this);
		return emissionFilterLinks.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element LightPath_element)
	{
		// Creating XML block for LightPath

		if (LightPath_element == null)
		{
			LightPath_element =
					document.createElementNS(NAMESPACE, "LightPath");
		}

		if (excitationFilterLinks != null)
		{
			// Reference property ExcitationFilterRef which occurs more than once
			for (Filter excitationFilterLinks_value : excitationFilterLinks)
			{
				ExcitationFilterRef o = new ExcitationFilterRef();
				o.setID(excitationFilterLinks_value.getID());
				LightPath_element.appendChild(o.asXMLElement(document));
			}
		}
		if (dichroic != null)
		{
			// Reference property DichroicRef
			DichroicRef o = new DichroicRef();
			o.setID(dichroic.getID());
			LightPath_element.appendChild(o.asXMLElement(document));
		}
		if (emissionFilterLinks != null)
		{
			// Reference property EmissionFilterRef which occurs more than once
			for (Filter emissionFilterLinks_value : emissionFilterLinks)
			{
				EmissionFilterRef o = new EmissionFilterRef();
				o.setID(emissionFilterLinks_value.getID());
				LightPath_element.appendChild(o.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, LightPath_element);
	}
}
