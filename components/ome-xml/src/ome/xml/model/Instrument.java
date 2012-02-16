/*
 * ome.xml.model.Instrument
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

public class Instrument extends AbstractOMEModelObject
{
	// Base:  -- Name: Instrument -- Type: Instrument -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2011-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Instrument.class);

	// -- Instance variables --


	// Property
	private String id;

	// Property
	private Microscope microscope;

	// Property which occurs more than once
	private List<LightSource> lightSourceList = new ArrayList<LightSource>();

	// Property which occurs more than once
	private List<Detector> detectorList = new ArrayList<Detector>();

	// Property which occurs more than once
	private List<Objective> objectiveList = new ArrayList<Objective>();

	// Property which occurs more than once
	private List<FilterSet> filterSetList = new ArrayList<FilterSet>();

	// Property which occurs more than once
	private List<Filter> filterList = new ArrayList<Filter>();

	// Property which occurs more than once
	private List<Dichroic> dichroicList = new ArrayList<Dichroic>();

	// Property which occurs more than once
	private List<OTF> otfList = new ArrayList<OTF>();

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// -- Constructors --

	/** Default constructor. */
	public Instrument()
	{
		super();
	}

	/** 
	 * Constructs Instrument recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Instrument(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Instrument specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Instrument recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Instrument".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Instrument got {}", tagName);
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Instrument missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		List<Element> Microscope_nodeList =
				getChildrenByTagName(element, "Microscope");
		if (Microscope_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Microscope node list size %d != 1",
					Microscope_nodeList.size()));
		}
		else if (Microscope_nodeList.size() != 0)
		{
			// Element property Microscope which is complex (has
			// sub-elements)
			setMicroscope(new Microscope(
					(Element) Microscope_nodeList.get(0), model));
		}
		// Element property LightSource which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		List<Element> LightSource_nodeList =
				getChildrenByTagName(element, "LightSource");
		for (Element LightSource_element : LightSource_nodeList)
		{
			List<Element> Laser_nodeList = 
					getChildrenByTagName(LightSource_element, "Laser");
			for (Element Laser_element : Laser_nodeList)
			{
				Laser o = new Laser(LightSource_element, model);
				o.update(Laser_element, model);
				addLightSource(o);
			}
			List<Element> Filament_nodeList = 
					getChildrenByTagName(LightSource_element, "Filament");
			for (Element Filament_element : Filament_nodeList)
			{
				Filament o = new Filament(LightSource_element, model);
				o.update(Filament_element, model);
				addLightSource(o);
			}
			List<Element> Arc_nodeList = 
					getChildrenByTagName(LightSource_element, "Arc");
			for (Element Arc_element : Arc_nodeList)
			{
				Arc o = new Arc(LightSource_element, model);
				o.update(Arc_element, model);
				addLightSource(o);
			}
			List<Element> LightEmittingDiode_nodeList = 
					getChildrenByTagName(LightSource_element, "LightEmittingDiode");
			for (Element LightEmittingDiode_element : LightEmittingDiode_nodeList)
			{
				LightEmittingDiode o = new LightEmittingDiode(LightSource_element, model);
				o.update(LightEmittingDiode_element, model);
				addLightSource(o);
			}
		}
		// Element property Detector which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Detector_nodeList =
				getChildrenByTagName(element, "Detector");
		for (Element Detector_element : Detector_nodeList)
		{
			addDetector(
					new Detector(Detector_element, model));
		}
		// Element property Objective which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Objective_nodeList =
				getChildrenByTagName(element, "Objective");
		for (Element Objective_element : Objective_nodeList)
		{
			addObjective(
					new Objective(Objective_element, model));
		}
		// Element property FilterSet which is complex (has
		// sub-elements) and occurs more than once
		List<Element> FilterSet_nodeList =
				getChildrenByTagName(element, "FilterSet");
		for (Element FilterSet_element : FilterSet_nodeList)
		{
			addFilterSet(
					new FilterSet(FilterSet_element, model));
		}
		// Element property Filter which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Filter_nodeList =
				getChildrenByTagName(element, "Filter");
		for (Element Filter_element : Filter_nodeList)
		{
			addFilter(
					new Filter(Filter_element, model));
		}
		// Element property Dichroic which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Dichroic_nodeList =
				getChildrenByTagName(element, "Dichroic");
		for (Element Dichroic_element : Dichroic_nodeList)
		{
			addDichroic(
					new Dichroic(Dichroic_element, model));
		}
		// Element property OTF which is complex (has
		// sub-elements) and occurs more than once
		List<Element> OTF_nodeList =
				getChildrenByTagName(element, "OTF");
		for (Element OTF_element : OTF_nodeList)
		{
			addOTF(
					new OTF(OTF_element, model));
		}
		// *** IGNORING *** Skipped back reference Image_BackReference
	}

	// -- Instrument API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
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
	public Microscope getMicroscope()
	{
		return microscope;
	}

	public void setMicroscope(Microscope microscope)
	{
		this.microscope = microscope;
	}

	// Property which occurs more than once
	public int sizeOfLightSourceList()
	{
		return lightSourceList.size();
	}

	public List<LightSource> copyLightSourceList()
	{
		return new ArrayList<LightSource>(lightSourceList);
	}

	public LightSource getLightSource(int index)
	{
		return lightSourceList.get(index);
	}

	public LightSource setLightSource(int index, LightSource lightSource)
	{
		return lightSourceList.set(index, lightSource);
	}

	public void addLightSource(LightSource lightSource)
	{
		lightSourceList.add(lightSource);
	}

	public void removeLightSource(LightSource lightSource)
	{
		lightSourceList.remove(lightSource);
	}

	// Property which occurs more than once
	public int sizeOfDetectorList()
	{
		return detectorList.size();
	}

	public List<Detector> copyDetectorList()
	{
		return new ArrayList<Detector>(detectorList);
	}

	public Detector getDetector(int index)
	{
		return detectorList.get(index);
	}

	public Detector setDetector(int index, Detector detector)
	{
		return detectorList.set(index, detector);
	}

	public void addDetector(Detector detector)
	{
		detectorList.add(detector);
	}

	public void removeDetector(Detector detector)
	{
		detectorList.remove(detector);
	}

	// Property which occurs more than once
	public int sizeOfObjectiveList()
	{
		return objectiveList.size();
	}

	public List<Objective> copyObjectiveList()
	{
		return new ArrayList<Objective>(objectiveList);
	}

	public Objective getObjective(int index)
	{
		return objectiveList.get(index);
	}

	public Objective setObjective(int index, Objective objective)
	{
		return objectiveList.set(index, objective);
	}

	public void addObjective(Objective objective)
	{
		objectiveList.add(objective);
	}

	public void removeObjective(Objective objective)
	{
		objectiveList.remove(objective);
	}

	// Property which occurs more than once
	public int sizeOfFilterSetList()
	{
		return filterSetList.size();
	}

	public List<FilterSet> copyFilterSetList()
	{
		return new ArrayList<FilterSet>(filterSetList);
	}

	public FilterSet getFilterSet(int index)
	{
		return filterSetList.get(index);
	}

	public FilterSet setFilterSet(int index, FilterSet filterSet)
	{
		return filterSetList.set(index, filterSet);
	}

	public void addFilterSet(FilterSet filterSet)
	{
		filterSetList.add(filterSet);
	}

	public void removeFilterSet(FilterSet filterSet)
	{
		filterSetList.remove(filterSet);
	}

	// Property which occurs more than once
	public int sizeOfFilterList()
	{
		return filterList.size();
	}

	public List<Filter> copyFilterList()
	{
		return new ArrayList<Filter>(filterList);
	}

	public Filter getFilter(int index)
	{
		return filterList.get(index);
	}

	public Filter setFilter(int index, Filter filter)
	{
		return filterList.set(index, filter);
	}

	public void addFilter(Filter filter)
	{
		filterList.add(filter);
	}

	public void removeFilter(Filter filter)
	{
		filterList.remove(filter);
	}

	// Property which occurs more than once
	public int sizeOfDichroicList()
	{
		return dichroicList.size();
	}

	public List<Dichroic> copyDichroicList()
	{
		return new ArrayList<Dichroic>(dichroicList);
	}

	public Dichroic getDichroic(int index)
	{
		return dichroicList.get(index);
	}

	public Dichroic setDichroic(int index, Dichroic dichroic)
	{
		return dichroicList.set(index, dichroic);
	}

	public void addDichroic(Dichroic dichroic)
	{
		dichroicList.add(dichroic);
	}

	public void removeDichroic(Dichroic dichroic)
	{
		dichroicList.remove(dichroic);
	}

	// Property which occurs more than once
	public int sizeOfOTFList()
	{
		return otfList.size();
	}

	public List<OTF> copyOTFList()
	{
		return new ArrayList<OTF>(otfList);
	}

	public OTF getOTF(int index)
	{
		return otfList.get(index);
	}

	public OTF setOTF(int index, OTF otf)
	{
		return otfList.set(index, otf);
	}

	public void addOTF(OTF otf)
	{
		otfList.add(otf);
	}

	public void removeOTF(OTF otf)
	{
		otfList.remove(otf);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedImageList()
	{
		return image_BackReferenceList.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(image_BackReferenceList);
	}

	public Image getLinkedImage(int index)
	{
		return image_BackReferenceList.get(index);
	}

	public Image setLinkedImage(int index, Image o)
	{
		return image_BackReferenceList.set(index, o);
	}

	public boolean linkImage(Image o)
	{
		if (!image_BackReferenceList.contains(o)) {
			return image_BackReferenceList.add(o);
		}
		return false;
	}

	public boolean unlinkImage(Image o)
	{
		return image_BackReferenceList.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Instrument_element)
	{
		// Creating XML block for Instrument

		if (Instrument_element == null)
		{
			Instrument_element =
					document.createElementNS(NAMESPACE, "Instrument");
		}

		if (id != null)
		{
			// Attribute property ID
			Instrument_element.setAttribute("ID", id.toString());
		}
		if (microscope != null)
		{
			// Element property Microscope which is complex (has
			// sub-elements)
			Instrument_element.appendChild(microscope.asXMLElement(document));
		}
		if (lightSourceList != null)
		{
			// Element property LightSource which is complex (has
			// sub-elements) and occurs more than once
			for (LightSource lightSourceList_value : lightSourceList)
			{
				Instrument_element.appendChild(lightSourceList_value.asXMLElement(document));
			}
		}
		if (detectorList != null)
		{
			// Element property Detector which is complex (has
			// sub-elements) and occurs more than once
			for (Detector detectorList_value : detectorList)
			{
				Instrument_element.appendChild(detectorList_value.asXMLElement(document));
			}
		}
		if (objectiveList != null)
		{
			// Element property Objective which is complex (has
			// sub-elements) and occurs more than once
			for (Objective objectiveList_value : objectiveList)
			{
				Instrument_element.appendChild(objectiveList_value.asXMLElement(document));
			}
		}
		if (filterSetList != null)
		{
			// Element property FilterSet which is complex (has
			// sub-elements) and occurs more than once
			for (FilterSet filterSetList_value : filterSetList)
			{
				Instrument_element.appendChild(filterSetList_value.asXMLElement(document));
			}
		}
		if (filterList != null)
		{
			// Element property Filter which is complex (has
			// sub-elements) and occurs more than once
			for (Filter filterList_value : filterList)
			{
				Instrument_element.appendChild(filterList_value.asXMLElement(document));
			}
		}
		if (dichroicList != null)
		{
			// Element property Dichroic which is complex (has
			// sub-elements) and occurs more than once
			for (Dichroic dichroicList_value : dichroicList)
			{
				Instrument_element.appendChild(dichroicList_value.asXMLElement(document));
			}
		}
		if (otfList != null)
		{
			// Element property OTF which is complex (has
			// sub-elements) and occurs more than once
			for (OTF otfList_value : otfList)
			{
				Instrument_element.appendChild(otfList_value.asXMLElement(document));
			}
		}
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		return super.asXMLElement(document, Instrument_element);
	}
}
