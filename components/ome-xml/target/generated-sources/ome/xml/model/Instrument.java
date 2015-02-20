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

public class Instrument extends AbstractOMEModelObject
{
	// Base:  -- Name: Instrument -- Type: Instrument -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Instrument.class);

	// -- Instance variables --

	// ID property
	private String id;

	// Microscope property
	private Microscope microscope;

	// LightSource property (occurs more than once)
	private List<LightSource> lightSources = new ArrayList<LightSource>();

	// Detector property (occurs more than once)
	private List<Detector> detectors = new ArrayList<Detector>();

	// Objective property (occurs more than once)
	private List<Objective> objectives = new ArrayList<Objective>();

	// FilterSet property (occurs more than once)
	private List<FilterSet> filterSets = new ArrayList<FilterSet>();

	// Filter property (occurs more than once)
	private List<Filter> filters = new ArrayList<Filter>();

	// Dichroic property (occurs more than once)
	private List<Dichroic> dichroics = new ArrayList<Dichroic>();

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// Image_BackReference back reference (occurs more than once)
	private List<Image> images = new ReferenceList<Image>();

	// -- Constructors --

	/** Default constructor. */
	public Instrument()
	{
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

	/** Copy constructor. */
	public Instrument(Instrument orig)
	{
		id = orig.id;
		microscope = orig.microscope;
		lightSources = orig.lightSources;
		detectors = orig.detectors;
		objectives = orig.objectives;
		filterSets = orig.filterSets;
		filters = orig.filters;
		dichroics = orig.dichroics;
		annotationLinks = orig.annotationLinks;
		images = orig.images;
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
			List<Element> GenericExcitationSource_nodeList =
					getChildrenByTagName(LightSource_element, "GenericExcitationSource");
			for (Element GenericExcitationSource_element : GenericExcitationSource_nodeList)
			{
				GenericExcitationSource o = new GenericExcitationSource(LightSource_element, model);
				o.update(GenericExcitationSource_element, model);
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

	// -- Instrument API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkInstrument(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
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

	// Property Microscope
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

			o.linkInstrument(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkInstrument(this);
		return annotationLinks.remove(o);
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
		return images.add(o);
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
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Instrument_element.appendChild(o.asXMLElement(document));
			}
		}
		if (images != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}

		return super.asXMLElement(document, Instrument_element);
	}
}
