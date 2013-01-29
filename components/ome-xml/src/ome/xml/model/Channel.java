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

public class Channel extends AbstractOMEModelObject
{
	// Base:  -- Name: Channel -- Type: Channel -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Channel.class);

	// -- Instance variables --


	// Property
	private Double pinholeSize;

	// Property
	private String name;

	// Property
	private AcquisitionMode acquisitionMode;

	// Property
	private Color color;

	// Property
	private ContrastMethod contrastMethod;

	// Property
	private PositiveInteger excitationWavelength;

	// Property
	private IlluminationType illuminationType;

	// Property
	private String fluor;

	// Property
	private Integer pockelCellSetting;

	// Property
	private PositiveInteger emissionWavelength;

	// Property
	private Double ndFilter;

	// Property
	private String id;

	// Property
	private PositiveInteger samplesPerPixel;

	// Property
	private LightSourceSettings lightSourceSettings;

	// Property
	private DetectorSettings detectorSettings;

	// Property
	private FilterSet filterSet;

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

	// Property
	private LightPath lightPath;

	// Back reference Pixels_BackReference
	private Pixels pixels;

	// -- Constructors --

	/** Default constructor. */
	public Channel()
	{
		super();
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
			// Attribute property PinholeSize
			setPinholeSize(Double.valueOf(
					element.getAttribute("PinholeSize")));
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
			// Attribute property ExcitationWavelength
			setExcitationWavelength(PositiveInteger.valueOf(
					element.getAttribute("ExcitationWavelength")));
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
		if (element.hasAttribute("EmissionWavelength"))
		{
			// Attribute property EmissionWavelength
			setEmissionWavelength(PositiveInteger.valueOf(
					element.getAttribute("EmissionWavelength")));
		}
		if (element.hasAttribute("NDFilter"))
		{
			// Attribute property NDFilter
			setNDFilter(Double.valueOf(
					element.getAttribute("NDFilter")));
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
		// *** IGNORING *** Skipped back reference Pixels_BackReference
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
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public Double getPinholeSize()
	{
		return pinholeSize;
	}

	public void setPinholeSize(Double pinholeSize)
	{
		this.pinholeSize = pinholeSize;
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
	public AcquisitionMode getAcquisitionMode()
	{
		return acquisitionMode;
	}

	public void setAcquisitionMode(AcquisitionMode acquisitionMode)
	{
		this.acquisitionMode = acquisitionMode;
	}

	// Property
	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	// Property
	public ContrastMethod getContrastMethod()
	{
		return contrastMethod;
	}

	public void setContrastMethod(ContrastMethod contrastMethod)
	{
		this.contrastMethod = contrastMethod;
	}

	// Property
	public PositiveInteger getExcitationWavelength()
	{
		return excitationWavelength;
	}

	public void setExcitationWavelength(PositiveInteger excitationWavelength)
	{
		this.excitationWavelength = excitationWavelength;
	}

	// Property
	public IlluminationType getIlluminationType()
	{
		return illuminationType;
	}

	public void setIlluminationType(IlluminationType illuminationType)
	{
		this.illuminationType = illuminationType;
	}

	// Property
	public String getFluor()
	{
		return fluor;
	}

	public void setFluor(String fluor)
	{
		this.fluor = fluor;
	}

	// Property
	public Integer getPockelCellSetting()
	{
		return pockelCellSetting;
	}

	public void setPockelCellSetting(Integer pockelCellSetting)
	{
		this.pockelCellSetting = pockelCellSetting;
	}

	// Property
	public PositiveInteger getEmissionWavelength()
	{
		return emissionWavelength;
	}

	public void setEmissionWavelength(PositiveInteger emissionWavelength)
	{
		this.emissionWavelength = emissionWavelength;
	}

	// Property
	public Double getNDFilter()
	{
		return ndFilter;
	}

	public void setNDFilter(Double ndFilter)
	{
		this.ndFilter = ndFilter;
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
	public PositiveInteger getSamplesPerPixel()
	{
		return samplesPerPixel;
	}

	public void setSamplesPerPixel(PositiveInteger samplesPerPixel)
	{
		this.samplesPerPixel = samplesPerPixel;
	}

	// Property
	public LightSourceSettings getLightSourceSettings()
	{
		return lightSourceSettings;
	}

	public void setLightSourceSettings(LightSourceSettings lightSourceSettings)
	{
		this.lightSourceSettings = lightSourceSettings;
	}

	// Property
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
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkChannel(this);
		return annotationLinks.remove(o);
	}

	// Property
	public LightPath getLightPath()
	{
		return lightPath;
	}

	public void setLightPath(LightPath lightPath)
	{
		this.lightPath = lightPath;
	}

	// Property
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
			// Attribute property PinholeSize
			Channel_element.setAttribute("PinholeSize", pinholeSize.toString());
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
			// Attribute property ExcitationWavelength
			Channel_element.setAttribute("ExcitationWavelength", excitationWavelength.toString());
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
		if (emissionWavelength != null)
		{
			// Attribute property EmissionWavelength
			Channel_element.setAttribute("EmissionWavelength", emissionWavelength.toString());
		}
		if (ndFilter != null)
		{
			// Attribute property NDFilter
			Channel_element.setAttribute("NDFilter", ndFilter.toString());
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
