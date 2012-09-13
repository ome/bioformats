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

public class Instrument extends AbstractOMEModelObject
{
	// Base:  -- Name: Instrument -- Type: Instrument -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Instrument.class);

	// -- Instance variables --


	// Property
	private String id;

	// Property
	private Microscope microscope;

	// Property which occurs more than once
	private List<LightSource> lightSources = new ArrayList<LightSource>();

	// Property which occurs more than once
	private List<Detector> detectors = new ArrayList<Detector>();

	// Property which occurs more than once
	private List<Objective> objectives = new ArrayList<Objective>();

	// Property which occurs more than once
	private List<FilterSet> filterSets = new ArrayList<FilterSet>();

	// Property which occurs more than once
	private List<Filter> filters = new ArrayList<Filter>();

	// Property which occurs more than once
	private List<Dichroic> dichroics = new ArrayList<Dichroic>();

	// Back reference Image_BackReference
	private List<Image> images = new ArrayList<Image>();

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
		return lightSources.size();
	}

	public List<LightSource> copyLightSourceList()
	{
		return new ArrayList<LightSource>(lightSources);
	}

	public LightSource getLightSource(int index)
	{
		return lightSources.get(index);
	}

	public LightSource setLightSource(int index, LightSource lightSource)
	{
        lightSource.setInstrument(this);
		return lightSources.set(index, lightSource);
	}

	public void addLightSource(LightSource lightSource)
	{
        lightSource.setInstrument(this);
		lightSources.add(lightSource);
	}

	public void removeLightSource(LightSource lightSource)
	{
		lightSources.remove(lightSource);
	}

	// Property which occurs more than once
	public int sizeOfDetectorList()
	{
		return detectors.size();
	}

	public List<Detector> copyDetectorList()
	{
		return new ArrayList<Detector>(detectors);
	}

	public Detector getDetector(int index)
	{
		return detectors.get(index);
	}

	public Detector setDetector(int index, Detector detector)
	{
        detector.setInstrument(this);
		return detectors.set(index, detector);
	}

	public void addDetector(Detector detector)
	{
        detector.setInstrument(this);
		detectors.add(detector);
	}

	public void removeDetector(Detector detector)
	{
		detectors.remove(detector);
	}

	// Property which occurs more than once
	public int sizeOfObjectiveList()
	{
		return objectives.size();
	}

	public List<Objective> copyObjectiveList()
	{
		return new ArrayList<Objective>(objectives);
	}

	public Objective getObjective(int index)
	{
		return objectives.get(index);
	}

	public Objective setObjective(int index, Objective objective)
	{
        objective.setInstrument(this);
		return objectives.set(index, objective);
	}

	public void addObjective(Objective objective)
	{
        objective.setInstrument(this);
		objectives.add(objective);
	}

	public void removeObjective(Objective objective)
	{
		objectives.remove(objective);
	}

	// Property which occurs more than once
	public int sizeOfFilterSetList()
	{
		return filterSets.size();
	}

	public List<FilterSet> copyFilterSetList()
	{
		return new ArrayList<FilterSet>(filterSets);
	}

	public FilterSet getFilterSet(int index)
	{
		return filterSets.get(index);
	}

	public FilterSet setFilterSet(int index, FilterSet filterSet)
	{
        filterSet.setInstrument(this);
		return filterSets.set(index, filterSet);
	}

	public void addFilterSet(FilterSet filterSet)
	{
        filterSet.setInstrument(this);
		filterSets.add(filterSet);
	}

	public void removeFilterSet(FilterSet filterSet)
	{
		filterSets.remove(filterSet);
	}

	// Property which occurs more than once
	public int sizeOfFilterList()
	{
		return filters.size();
	}

	public List<Filter> copyFilterList()
	{
		return new ArrayList<Filter>(filters);
	}

	public Filter getFilter(int index)
	{
		return filters.get(index);
	}

	public Filter setFilter(int index, Filter filter)
	{
        filter.setInstrument(this);
		return filters.set(index, filter);
	}

	public void addFilter(Filter filter)
	{
        filter.setInstrument(this);
		filters.add(filter);
	}

	public void removeFilter(Filter filter)
	{
		filters.remove(filter);
	}

	// Property which occurs more than once
	public int sizeOfDichroicList()
	{
		return dichroics.size();
	}

	public List<Dichroic> copyDichroicList()
	{
		return new ArrayList<Dichroic>(dichroics);
	}

	public Dichroic getDichroic(int index)
	{
		return dichroics.get(index);
	}

	public Dichroic setDichroic(int index, Dichroic dichroic)
	{
        dichroic.setInstrument(this);
		return dichroics.set(index, dichroic);
	}

	public void addDichroic(Dichroic dichroic)
	{
        dichroic.setInstrument(this);
		dichroics.add(dichroic);
	}

	public void removeDichroic(Dichroic dichroic)
	{
		dichroics.remove(dichroic);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedImageList()
	{
		return images.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(images);
	}

	public Image getLinkedImage(int index)
	{
		return images.get(index);
	}

	public Image setLinkedImage(int index, Image o)
	{
		return images.set(index, o);
	}

	public boolean linkImage(Image o)
	{
		if (!images.contains(o)) {
			return images.add(o);
		}
		return false;
	}

	public boolean unlinkImage(Image o)
	{
		return images.remove(o);
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
		if (lightSources != null)
		{
			// Element property LightSource which is complex (has
			// sub-elements) and occurs more than once
			for (LightSource lightSources_value : lightSources)
			{
				Instrument_element.appendChild(lightSources_value.asXMLElement(document));
			}
		}
		if (detectors != null)
		{
			// Element property Detector which is complex (has
			// sub-elements) and occurs more than once
			for (Detector detectors_value : detectors)
			{
				Instrument_element.appendChild(detectors_value.asXMLElement(document));
			}
		}
		if (objectives != null)
		{
			// Element property Objective which is complex (has
			// sub-elements) and occurs more than once
			for (Objective objectives_value : objectives)
			{
				Instrument_element.appendChild(objectives_value.asXMLElement(document));
			}
		}
		if (filterSets != null)
		{
			// Element property FilterSet which is complex (has
			// sub-elements) and occurs more than once
			for (FilterSet filterSets_value : filterSets)
			{
				Instrument_element.appendChild(filterSets_value.asXMLElement(document));
			}
		}
		if (filters != null)
		{
			// Element property Filter which is complex (has
			// sub-elements) and occurs more than once
			for (Filter filters_value : filters)
			{
				Instrument_element.appendChild(filters_value.asXMLElement(document));
			}
		}
		if (dichroics != null)
		{
			// Element property Dichroic which is complex (has
			// sub-elements) and occurs more than once
			for (Dichroic dichroics_value : dichroics)
			{
				Instrument_element.appendChild(dichroics_value.asXMLElement(document));
			}
		}
		if (images != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		return super.asXMLElement(document, Instrument_element);
	}
}
