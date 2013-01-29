/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:35.744474
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

public class MicrobeamManipulation extends AbstractOMEModelObject
{
	// Base:  -- Name: MicrobeamManipulation -- Type: MicrobeamManipulation -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(MicrobeamManipulation.class);

	// -- Instance variables --


	// Property
	private MicrobeamManipulationType type;

	// Property
	private String id;

	// Property
	private String description;

	// Reference ROIRef
	private List<ROI> roiLinks = new ArrayList<ROI>();

	// Property
	private Experimenter experimenter;

	// Property which occurs more than once
	private List<LightSourceSettings> lightSourceSettingsCombinations = new ArrayList<LightSourceSettings>();

	// Back reference Image_BackReference
	private List<Image> imageLinks = new ArrayList<Image>();

	// Back reference Experiment_BackReference
	private Experiment experiment;

	// -- Constructors --

	/** Default constructor. */
	public MicrobeamManipulation()
	{
		super();
	}

	/** 
	 * Constructs MicrobeamManipulation recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public MicrobeamManipulation(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from MicrobeamManipulation specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates MicrobeamManipulation recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"MicrobeamManipulation".equals(tagName))
		{
			LOGGER.debug("Expecting node name of MicrobeamManipulation got {}", tagName);
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			setType(MicrobeamManipulationType.fromString(
					element.getAttribute("Type")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"MicrobeamManipulation missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
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
		// Element reference ROIRef
		List<Element> ROIRef_nodeList =
				getChildrenByTagName(element, "ROIRef");
		for (Element ROIRef_element : ROIRef_nodeList)
		{
			ROIRef roiLinks_reference = new ROIRef();
			roiLinks_reference.setID(ROIRef_element.getAttribute("ID"));
			model.addReference(this, roiLinks_reference);
		}
		// Element reference ExperimenterRef
		List<Element> ExperimenterRef_nodeList =
				getChildrenByTagName(element, "ExperimenterRef");
		for (Element ExperimenterRef_element : ExperimenterRef_nodeList)
		{
			ExperimenterRef experimenter_reference = new ExperimenterRef();
			experimenter_reference.setID(ExperimenterRef_element.getAttribute("ID"));
			model.addReference(this, experimenter_reference);
		}
		// Element property LightSourceSettings which is complex (has
		// sub-elements) and occurs more than once
		List<Element> LightSourceSettings_nodeList =
				getChildrenByTagName(element, "LightSourceSettings");
		for (Element LightSourceSettings_element : LightSourceSettings_nodeList)
		{
			addLightSourceSettings(
					new LightSourceSettings(LightSourceSettings_element, model));
		}
		// *** IGNORING *** Skipped back reference Image_BackReference
		// *** IGNORING *** Skipped back reference Experiment_BackReference
	}

	// -- MicrobeamManipulation API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof ROIRef)
		{
			ROI o_casted = (ROI) o;
			o_casted.linkMicrobeamManipulation(this);
			if (!roiLinks.contains(o_casted)) {
				roiLinks.add(o_casted);
			}
			return true;
		}
		if (reference instanceof ExperimenterRef)
		{
			Experimenter o_casted = (Experimenter) o;
			o_casted.linkMicrobeamManipulation(this);
			experimenter = o_casted;
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public MicrobeamManipulationType getType()
	{
		return type;
	}

	public void setType(MicrobeamManipulationType type)
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
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	// Reference which occurs more than once
	public int sizeOfLinkedROIList()
	{
		return roiLinks.size();
	}

	public List<ROI> copyLinkedROIList()
	{
		return new ArrayList<ROI>(roiLinks);
	}

	public ROI getLinkedROI(int index)
	{
		return roiLinks.get(index);
	}

	public ROI setLinkedROI(int index, ROI o)
	{
		return roiLinks.set(index, o);
	}

	public boolean linkROI(ROI o)
	{

			o.linkMicrobeamManipulation(this);
		if (!roiLinks.contains(o)) {
			return roiLinks.add(o);
		}
		return false;
	}

	public boolean unlinkROI(ROI o)
	{

			o.unlinkMicrobeamManipulation(this);
		return roiLinks.remove(o);
	}

	// Reference
	public Experimenter getLinkedExperimenter()
	{
		return experimenter;
	}

	public void linkExperimenter(Experimenter o)
	{
		experimenter = o;
	}

	public void unlinkExperimenter(Experimenter o)
	{
		if (experimenter == o)
		{
			experimenter = null;
		}
	}

	// Property which occurs more than once
	public int sizeOfLightSourceSettingsList()
	{
		return lightSourceSettingsCombinations.size();
	}

	public List<LightSourceSettings> copyLightSourceSettingsList()
	{
		return new ArrayList<LightSourceSettings>(lightSourceSettingsCombinations);
	}

	public LightSourceSettings getLightSourceSettings(int index)
	{
		return lightSourceSettingsCombinations.get(index);
	}

	public LightSourceSettings setLightSourceSettings(int index, LightSourceSettings lightSourceSettings)
	{
        lightSourceSettings.setMicrobeamManipulation(this);
		return lightSourceSettingsCombinations.set(index, lightSourceSettings);
	}

	public void addLightSourceSettings(LightSourceSettings lightSourceSettings)
	{
        lightSourceSettings.setMicrobeamManipulation(this);
		lightSourceSettingsCombinations.add(lightSourceSettings);
	}

	public void removeLightSourceSettings(LightSourceSettings lightSourceSettings)
	{
		lightSourceSettingsCombinations.remove(lightSourceSettings);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedImageList()
	{
		return imageLinks.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(imageLinks);
	}

	public Image getLinkedImage(int index)
	{
		return imageLinks.get(index);
	}

	public Image setLinkedImage(int index, Image o)
	{
		return imageLinks.set(index, o);
	}

	public boolean linkImage(Image o)
	{
		if (!imageLinks.contains(o)) {
			return imageLinks.add(o);
		}
		return false;
	}

	public boolean unlinkImage(Image o)
	{
		return imageLinks.remove(o);
	}

	// Property
	public Experiment getExperiment()
	{
		return experiment;
	}

	public void setExperiment(Experiment experiment_BackReference)
	{
		this.experiment = experiment_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element MicrobeamManipulation_element)
	{
		// Creating XML block for MicrobeamManipulation

		if (MicrobeamManipulation_element == null)
		{
			MicrobeamManipulation_element =
					document.createElementNS(NAMESPACE, "MicrobeamManipulation");
		}

		if (type != null)
		{
			// Attribute property Type
			MicrobeamManipulation_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			MicrobeamManipulation_element.setAttribute("ID", id.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
			MicrobeamManipulation_element.appendChild(description_element);
		}
		if (roiLinks != null)
		{
			// Reference property ROIRef which occurs more than once
			for (ROI roiLinks_value : roiLinks)
			{
				ROIRef o = new ROIRef();
				o.setID(roiLinks_value.getID());
				MicrobeamManipulation_element.appendChild(o.asXMLElement(document));
			}
		}
		if (experimenter != null)
		{
			// Reference property ExperimenterRef
			ExperimenterRef o = new ExperimenterRef();
			o.setID(experimenter.getID());
			MicrobeamManipulation_element.appendChild(o.asXMLElement(document));
		}
		if (lightSourceSettingsCombinations != null)
		{
			// Element property LightSourceSettings which is complex (has
			// sub-elements) and occurs more than once
			for (LightSourceSettings lightSourceSettingsCombinations_value : lightSourceSettingsCombinations)
			{
				MicrobeamManipulation_element.appendChild(lightSourceSettingsCombinations_value.asXMLElement(document));
			}
		}
		if (imageLinks != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		if (experiment != null)
		{
			// *** IGNORING *** Skipped back reference Experiment_BackReference
		}
		return super.asXMLElement(document, MicrobeamManipulation_element);
	}
}
