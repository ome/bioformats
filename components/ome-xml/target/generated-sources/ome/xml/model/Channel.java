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

public class Channel extends AbstractOMEModelObject
{
	// Base:  -- Name: Channel -- Type: Channel -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Channel.class);

	// -- Instance variables --

	// PinholeSize property
	private Length pinholeSize;

	// Name property
	private String name;

	// AcquisitionMode property
	private AcquisitionMode acquisitionMode;

	// Color property
	private Color color;

	// ContrastMethod property
	private ContrastMethod contrastMethod;

	// ExcitationWavelength property
	private Length excitationWavelength;

	// IlluminationType property
	private IlluminationType illuminationType;

	// Fluor property
	private String fluor;

	// PockelCellSetting property
	private Integer pockelCellSetting;

	// NDFilter property
	private Double ndFilter;

	// EmissionWavelength property
	private Length emissionWavelength;

	// ID property
	private String id;

	// SamplesPerPixel property
	private PositiveInteger samplesPerPixel;

	// LightSourceSettings property
	private LightSourceSettings lightSourceSettings;

	// DetectorSettings property
	private DetectorSettings detectorSettings;

	// FilterSetRef reference
	private FilterSet filterSet;

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// LightPath property
	private LightPath lightPath;

	// Pixels_BackReference back reference
	private Pixels pixels;

	// -- Constructors --

	/** Default constructor. */
	public Channel()
	{
	}

	/**
	 * Constructs Channel recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Channel(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public Channel(Channel orig)
	{
		pinholeSize = orig.pinholeSize;
		name = orig.name;
		acquisitionMode = orig.acquisitionMode;
		color = orig.color;
		contrastMethod = orig.contrastMethod;
		excitationWavelength = orig.excitationWavelength;
		illuminationType = orig.illuminationType;
		fluor = orig.fluor;
		pockelCellSetting = orig.pockelCellSetting;
		ndFilter = orig.ndFilter;
		emissionWavelength = orig.emissionWavelength;
		id = orig.id;
		samplesPerPixel = orig.samplesPerPixel;
		lightSourceSettings = orig.lightSourceSettings;
		detectorSettings = orig.detectorSettings;
		filterSet = orig.filterSet;
		annotationLinks = orig.annotationLinks;
		lightPath = orig.lightPath;
		pixels = orig.pixels;
	}

	// -- Custom content from Channel specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates Channel recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Channel".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Channel got {}", tagName);
		}
		if (element.hasAttribute("PinholeSize"))
		{
			// Attribute property PinholeSize with unit companion PinholeSizeUnit
			String unitSymbol = element.getAttribute("PinholeSizeUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getPinholeSizeUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("PinholeSize"));
			if (baseValue != null) 
			{
				setPinholeSize(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		if (element.hasAttribute("AcquisitionMode"))
		{
			// Attribute property which is an enumeration AcquisitionMode
			setAcquisitionMode(AcquisitionMode.fromString(
					element.getAttribute("AcquisitionMode")));
		}
		if (element.hasAttribute("Color"))
		{
			// Attribute property Color
			setColor(Color.valueOf(
					element.getAttribute("Color")));
		}
		if (element.hasAttribute("ContrastMethod"))
		{
			// Attribute property which is an enumeration ContrastMethod
			setContrastMethod(ContrastMethod.fromString(
					element.getAttribute("ContrastMethod")));
		}
		if (element.hasAttribute("ExcitationWavelength"))
		{
			// Attribute property ExcitationWavelength with unit companion ExcitationWavelengthUnit
			String unitSymbol = element.getAttribute("ExcitationWavelengthUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getExcitationWavelengthUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			PositiveFloat baseValue = PositiveFloat.valueOf(
					element.getAttribute("ExcitationWavelength"));
			if (baseValue != null) 
			{
				setExcitationWavelength(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("IlluminationType"))
		{
			// Attribute property which is an enumeration IlluminationType
			setIlluminationType(IlluminationType.fromString(
					element.getAttribute("IlluminationType")));
		}
		if (element.hasAttribute("Fluor"))
		{
			// Attribute property Fluor
			setFluor(String.valueOf(
					element.getAttribute("Fluor")));
		}
		if (element.hasAttribute("PockelCellSetting"))
		{
			// Attribute property PockelCellSetting
			setPockelCellSetting(Integer.valueOf(
					element.getAttribute("PockelCellSetting")));
		}
		if (element.hasAttribute("NDFilter"))
		{
			// Attribute property NDFilter
			setNDFilter(Double.valueOf(
					element.getAttribute("NDFilter")));
		}
		if (element.hasAttribute("EmissionWavelength"))
		{
			// Attribute property EmissionWavelength with unit companion EmissionWavelengthUnit
			String unitSymbol = element.getAttribute("EmissionWavelengthUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getEmissionWavelengthUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			PositiveFloat baseValue = PositiveFloat.valueOf(
					element.getAttribute("EmissionWavelength"));
			if (baseValue != null) 
			{
				setEmissionWavelength(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Channel missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("SamplesPerPixel"))
		{
			// Attribute property SamplesPerPixel
			setSamplesPerPixel(PositiveInteger.valueOf(
					element.getAttribute("SamplesPerPixel")));
		}
		List<Element> LightSourceSettings_nodeList =
				getChildrenByTagName(element, "LightSourceSettings");
		if (LightSourceSettings_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"LightSourceSettings node list size %d != 1",
					LightSourceSettings_nodeList.size()));
		}
		else if (LightSourceSettings_nodeList.size() != 0)
		{
			// Element property LightSourceSettings which is complex (has
			// sub-elements)
			setLightSourceSettings(new LightSourceSettings(
					(Element) LightSourceSettings_nodeList.get(0), model));
		}
		List<Element> DetectorSettings_nodeList =
				getChildrenByTagName(element, "DetectorSettings");
		if (DetectorSettings_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"DetectorSettings node list size %d != 1",
					DetectorSettings_nodeList.size()));
		}
		else if (DetectorSettings_nodeList.size() != 0)
		{
			// Element property DetectorSettings which is complex (has
			// sub-elements)
			setDetectorSettings(new DetectorSettings(
					(Element) DetectorSettings_nodeList.get(0), model));
		}
		// Element reference FilterSetRef
		List<Element> FilterSetRef_nodeList =
				getChildrenByTagName(element, "FilterSetRef");
		for (Element FilterSetRef_element : FilterSetRef_nodeList)
		{
			FilterSetRef filterSet_reference = new FilterSetRef();
			filterSet_reference.setID(FilterSetRef_element.getAttribute("ID"));
			model.addReference(this, filterSet_reference);
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
		List<Element> LightPath_nodeList =
				getChildrenByTagName(element, "LightPath");
		if (LightPath_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"LightPath node list size %d != 1",
					LightPath_nodeList.size()));
		}
		else if (LightPath_nodeList.size() != 0)
		{
			// Element property LightPath which is complex (has
			// sub-elements)
			setLightPath(new LightPath(
					(Element) LightPath_nodeList.get(0), model));
		}
	}

	// -- Channel API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof FilterSetRef)
		{
			FilterSet o_casted = (FilterSet) o;
			o_casted.linkChannel(this);
			filterSet = o_casted;
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkChannel(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property PinholeSize with unit companion PinholeSizeUnit
	public Length getPinholeSize()
	{
		return pinholeSize;
	}

	public void setPinholeSize(Length pinholeSize)
	{
		this.pinholeSize = pinholeSize;
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

	// Property AcquisitionMode
	public AcquisitionMode getAcquisitionMode()
	{
		return acquisitionMode;
	}

	public void setAcquisitionMode(AcquisitionMode acquisitionMode)
	{
		this.acquisitionMode = acquisitionMode;
	}

	// Property Color
	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	// Property EmissionWavelengthUnit is a unit companion
	public static String getEmissionWavelengthUnitXsdDefault()
	{
		return "nm";
	}

	// Property ContrastMethod
	public ContrastMethod getContrastMethod()
	{
		return contrastMethod;
	}

	public void setContrastMethod(ContrastMethod contrastMethod)
	{
		this.contrastMethod = contrastMethod;
	}

	// Property PinholeSizeUnit is a unit companion
	public static String getPinholeSizeUnitXsdDefault()
	{
		return "µm";
	}

	// Property ExcitationWavelength with unit companion ExcitationWavelengthUnit
	public Length getExcitationWavelength()
	{
		return excitationWavelength;
	}

	public void setExcitationWavelength(Length excitationWavelength)
	{
		this.excitationWavelength = excitationWavelength;
	}

	// Property IlluminationType
	public IlluminationType getIlluminationType()
	{
		return illuminationType;
	}

	public void setIlluminationType(IlluminationType illuminationType)
	{
		this.illuminationType = illuminationType;
	}

	// Property Fluor
	public String getFluor()
	{
		return fluor;
	}

	public void setFluor(String fluor)
	{
		this.fluor = fluor;
	}

	// Property PockelCellSetting
	public Integer getPockelCellSetting()
	{
		return pockelCellSetting;
	}

	public void setPockelCellSetting(Integer pockelCellSetting)
	{
		this.pockelCellSetting = pockelCellSetting;
	}

	// Property ExcitationWavelengthUnit is a unit companion
	public static String getExcitationWavelengthUnitXsdDefault()
	{
		return "nm";
	}

	// Property NDFilter
	public Double getNDFilter()
	{
		return ndFilter;
	}

	public void setNDFilter(Double ndFilter)
	{
		this.ndFilter = ndFilter;
	}

	// Property EmissionWavelength with unit companion EmissionWavelengthUnit
	public Length getEmissionWavelength()
	{
		return emissionWavelength;
	}

	public void setEmissionWavelength(Length emissionWavelength)
	{
		this.emissionWavelength = emissionWavelength;
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

	// Property SamplesPerPixel
	public PositiveInteger getSamplesPerPixel()
	{
		return samplesPerPixel;
	}

	public void setSamplesPerPixel(PositiveInteger samplesPerPixel)
	{
		this.samplesPerPixel = samplesPerPixel;
	}

	// Property LightSourceSettings
	public LightSourceSettings getLightSourceSettings()
	{
		return lightSourceSettings;
	}

	public void setLightSourceSettings(LightSourceSettings lightSourceSettings)
	{
		this.lightSourceSettings = lightSourceSettings;
	}

	// Property DetectorSettings
	public DetectorSettings getDetectorSettings()
	{
		return detectorSettings;
	}

	public void setDetectorSettings(DetectorSettings detectorSettings)
	{
		this.detectorSettings = detectorSettings;
	}

	// Reference
	public FilterSet getLinkedFilterSet()
	{
		return filterSet;
	}

	public void linkFilterSet(FilterSet o)
	{
		filterSet = o;
	}

	public void unlinkFilterSet(FilterSet o)
	{
		if (filterSet == o)
		{
			filterSet = null;
		}
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

			o.linkChannel(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkChannel(this);
		return annotationLinks.remove(o);
	}

	// Property LightPath
	public LightPath getLightPath()
	{
		return lightPath;
	}

	public void setLightPath(LightPath lightPath)
	{
		this.lightPath = lightPath;
	}

	// Property Pixels_BackReference
	public Pixels getPixels()
	{
		return pixels;
	}

	public void setPixels(Pixels pixels_BackReference)
	{
		this.pixels = pixels_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Channel_element)
	{
		// Creating XML block for Channel

		if (Channel_element == null)
		{
			Channel_element =
					document.createElementNS(NAMESPACE, "Channel");
		}


		if (pinholeSize != null)
		{
			// Attribute property PinholeSize with units companion prop.unitsCompanion.name
			if (pinholeSize.value() != null)
			{
				Channel_element.setAttribute("PinholeSize", pinholeSize.value().toString());
			}
			if (pinholeSize.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(pinholeSize.unit().getSymbol());
					Channel_element.setAttribute("PinholeSizeUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Channel:PinholeSizeUnit: {}", e.toString());
				}
			}
		}
		if (name != null)
		{
			// Attribute property Name
			Channel_element.setAttribute("Name", name.toString());
		}
		if (acquisitionMode != null)
		{
			// Attribute property AcquisitionMode
			Channel_element.setAttribute("AcquisitionMode", acquisitionMode.toString());
		}
		if (color != null)
		{
			// Attribute property Color
			Channel_element.setAttribute("Color", color.toString());
		}
		if (contrastMethod != null)
		{
			// Attribute property ContrastMethod
			Channel_element.setAttribute("ContrastMethod", contrastMethod.toString());
		}
		if (excitationWavelength != null)
		{
			// Attribute property ExcitationWavelength with units companion prop.unitsCompanion.name
			if (excitationWavelength.value() != null)
			{
				Channel_element.setAttribute("ExcitationWavelength", excitationWavelength.value().toString());
			}
			if (excitationWavelength.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(excitationWavelength.unit().getSymbol());
					Channel_element.setAttribute("ExcitationWavelengthUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Channel:ExcitationWavelengthUnit: {}", e.toString());
				}
			}
		}
		if (illuminationType != null)
		{
			// Attribute property IlluminationType
			Channel_element.setAttribute("IlluminationType", illuminationType.toString());
		}
		if (fluor != null)
		{
			// Attribute property Fluor
			Channel_element.setAttribute("Fluor", fluor.toString());
		}
		if (pockelCellSetting != null)
		{
			// Attribute property PockelCellSetting
			Channel_element.setAttribute("PockelCellSetting", pockelCellSetting.toString());
		}
		if (ndFilter != null)
		{
			// Attribute property NDFilter
			Channel_element.setAttribute("NDFilter", ndFilter.toString());
		}
		if (emissionWavelength != null)
		{
			// Attribute property EmissionWavelength with units companion prop.unitsCompanion.name
			if (emissionWavelength.value() != null)
			{
				Channel_element.setAttribute("EmissionWavelength", emissionWavelength.value().toString());
			}
			if (emissionWavelength.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(emissionWavelength.unit().getSymbol());
					Channel_element.setAttribute("EmissionWavelengthUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Channel:EmissionWavelengthUnit: {}", e.toString());
				}
			}
		}
		if (id != null)
		{
			// Attribute property ID
			Channel_element.setAttribute("ID", id.toString());
		}
		if (samplesPerPixel != null)
		{
			// Attribute property SamplesPerPixel
			Channel_element.setAttribute("SamplesPerPixel", samplesPerPixel.toString());
		}
		if (lightSourceSettings != null)
		{
			// Element property LightSourceSettings which is complex (has
			// sub-elements)
			Channel_element.appendChild(lightSourceSettings.asXMLElement(document));
		}
		if (detectorSettings != null)
		{
			// Element property DetectorSettings which is complex (has
			// sub-elements)
			Channel_element.appendChild(detectorSettings.asXMLElement(document));
		}
		if (filterSet != null)
		{
			// Reference property FilterSetRef
			FilterSetRef o = new FilterSetRef();
			o.setID(filterSet.getID());
			Channel_element.appendChild(o.asXMLElement(document));
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Channel_element.appendChild(o.asXMLElement(document));
			}
		}
		if (lightPath != null)
		{
			// Element property LightPath which is complex (has
			// sub-elements)
			Channel_element.appendChild(lightPath.asXMLElement(document));
		}
		if (pixels != null)
		{
			// *** IGNORING *** Skipped back reference Pixels_BackReference
		}

		return super.asXMLElement(document, Channel_element);
	}
}
