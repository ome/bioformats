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

public class Image extends AbstractOMEModelObject
{
	// Base:  -- Name: Image -- Type: Image -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Image.class);

	// -- Instance variables --


	// Property
	private String id;

	// Property
	private String name;

	// Property
	private Timestamp acquisitionDate;

	// Property
	private Experimenter experimenter;

	// Property
	private String description;

	// Property
	private Experiment experiment;

	// Property
	private ExperimenterGroup experimenterGroup;

	// Property
	private Instrument instrument;

	// Property
	private ObjectiveSettings objectiveSettings;

	// Property
	private ImagingEnvironment imagingEnvironment;

	// Property
	private StageLabel stageLabel;

	// Property
	private Pixels pixels;

	// Reference ROIRef
	private List<ROI> roiLinks = new ArrayList<ROI>();

	// Reference MicrobeamManipulationRef
	private List<MicrobeamManipulation> microbeamManipulationLinks = new ArrayList<MicrobeamManipulation>();

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

	// Back reference Dataset_BackReference
	private List<Dataset> datasetLinks = new ArrayList<Dataset>();

	// Back reference WellSample_BackReference
	private List<WellSample> wellSamples = new ArrayList<WellSample>();

	// -- Constructors --

	/** Default constructor. */
	public Image()
	{
		super();
	}

	/** 
	 * Constructs Image recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Image(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Image specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Image recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Image".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Image got {}", tagName);
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Image missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		List<Element> AcquisitionDate_nodeList =
				getChildrenByTagName(element, "AcquisitionDate");
		if (AcquisitionDate_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"AcquisitionDate node list size %d != 1",
					AcquisitionDate_nodeList.size()));
		}
		else if (AcquisitionDate_nodeList.size() != 0)
		{
			// Element property AcquisitionDate which is not complex (has no
			// sub-elements)
			setAcquisitionDate(
					Timestamp.valueOf(AcquisitionDate_nodeList.get(0).getTextContent()));
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
		// Element reference ExperimentRef
		List<Element> ExperimentRef_nodeList =
				getChildrenByTagName(element, "ExperimentRef");
		for (Element ExperimentRef_element : ExperimentRef_nodeList)
		{
			ExperimentRef experiment_reference = new ExperimentRef();
			experiment_reference.setID(ExperimentRef_element.getAttribute("ID"));
			model.addReference(this, experiment_reference);
		}
		// Element reference ExperimenterGroupRef
		List<Element> ExperimenterGroupRef_nodeList =
				getChildrenByTagName(element, "ExperimenterGroupRef");
		for (Element ExperimenterGroupRef_element : ExperimenterGroupRef_nodeList)
		{
			ExperimenterGroupRef experimenterGroup_reference = new ExperimenterGroupRef();
			experimenterGroup_reference.setID(ExperimenterGroupRef_element.getAttribute("ID"));
			model.addReference(this, experimenterGroup_reference);
		}
		// Element reference InstrumentRef
		List<Element> InstrumentRef_nodeList =
				getChildrenByTagName(element, "InstrumentRef");
		for (Element InstrumentRef_element : InstrumentRef_nodeList)
		{
			InstrumentRef instrument_reference = new InstrumentRef();
			instrument_reference.setID(InstrumentRef_element.getAttribute("ID"));
			model.addReference(this, instrument_reference);
		}
		List<Element> ObjectiveSettings_nodeList =
				getChildrenByTagName(element, "ObjectiveSettings");
		if (ObjectiveSettings_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"ObjectiveSettings node list size %d != 1",
					ObjectiveSettings_nodeList.size()));
		}
		else if (ObjectiveSettings_nodeList.size() != 0)
		{
			// Element property ObjectiveSettings which is complex (has
			// sub-elements)
			setObjectiveSettings(new ObjectiveSettings(
					(Element) ObjectiveSettings_nodeList.get(0), model));
		}
		List<Element> ImagingEnvironment_nodeList =
				getChildrenByTagName(element, "ImagingEnvironment");
		if (ImagingEnvironment_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"ImagingEnvironment node list size %d != 1",
					ImagingEnvironment_nodeList.size()));
		}
		else if (ImagingEnvironment_nodeList.size() != 0)
		{
			// Element property ImagingEnvironment which is complex (has
			// sub-elements)
			setImagingEnvironment(new ImagingEnvironment(
					(Element) ImagingEnvironment_nodeList.get(0), model));
		}
		List<Element> StageLabel_nodeList =
				getChildrenByTagName(element, "StageLabel");
		if (StageLabel_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"StageLabel node list size %d != 1",
					StageLabel_nodeList.size()));
		}
		else if (StageLabel_nodeList.size() != 0)
		{
			// Element property StageLabel which is complex (has
			// sub-elements)
			setStageLabel(new StageLabel(
					(Element) StageLabel_nodeList.get(0), model));
		}
		List<Element> Pixels_nodeList =
				getChildrenByTagName(element, "Pixels");
		if (Pixels_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Pixels node list size %d != 1",
					Pixels_nodeList.size()));
		}
		else if (Pixels_nodeList.size() != 0)
		{
			// Element property Pixels which is complex (has
			// sub-elements)
			setPixels(new Pixels(
					(Element) Pixels_nodeList.get(0), model));
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
		// Element reference MicrobeamManipulationRef
		List<Element> MicrobeamManipulationRef_nodeList =
				getChildrenByTagName(element, "MicrobeamManipulationRef");
		for (Element MicrobeamManipulationRef_element : MicrobeamManipulationRef_nodeList)
		{
			MicrobeamManipulationRef microbeamManipulationLinks_reference = new MicrobeamManipulationRef();
			microbeamManipulationLinks_reference.setID(MicrobeamManipulationRef_element.getAttribute("ID"));
			model.addReference(this, microbeamManipulationLinks_reference);
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
		// *** IGNORING *** Skipped back reference Dataset_BackReference
		// *** IGNORING *** Skipped back reference WellSample_BackReference
	}

	// -- Image API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof ExperimenterRef)
		{
			Experimenter o_casted = (Experimenter) o;
			o_casted.linkImage(this);
			experimenter = o_casted;
			return true;
		}
		if (reference instanceof ExperimentRef)
		{
			Experiment o_casted = (Experiment) o;
			o_casted.linkImage(this);
			experiment = o_casted;
			return true;
		}
		if (reference instanceof ExperimenterGroupRef)
		{
			ExperimenterGroup o_casted = (ExperimenterGroup) o;
			o_casted.linkImage(this);
			experimenterGroup = o_casted;
			return true;
		}
		if (reference instanceof InstrumentRef)
		{
			Instrument o_casted = (Instrument) o;
			o_casted.linkImage(this);
			instrument = o_casted;
			return true;
		}
		if (reference instanceof ROIRef)
		{
			ROI o_casted = (ROI) o;
			o_casted.linkImage(this);
			if (!roiLinks.contains(o_casted)) {
				roiLinks.add(o_casted);
			}
			return true;
		}
		if (reference instanceof MicrobeamManipulationRef)
		{
			MicrobeamManipulation o_casted = (MicrobeamManipulation) o;
			o_casted.linkImage(this);
			if (!microbeamManipulationLinks.contains(o_casted)) {
				microbeamManipulationLinks.add(o_casted);
			}
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkImage(this);
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
			}
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
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	// Property
	public Timestamp getAcquisitionDate()
	{
		return acquisitionDate;
	}

	public void setAcquisitionDate(Timestamp acquisitionDate)
	{
		this.acquisitionDate = acquisitionDate;
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

	// Property
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	// Reference
	public Experiment getLinkedExperiment()
	{
		return experiment;
	}

	public void linkExperiment(Experiment o)
	{
		experiment = o;
	}

	public void unlinkExperiment(Experiment o)
	{
		if (experiment == o)
		{
			experiment = null;
		}
	}

	// Reference
	public ExperimenterGroup getLinkedExperimenterGroup()
	{
		return experimenterGroup;
	}

	public void linkExperimenterGroup(ExperimenterGroup o)
	{
		experimenterGroup = o;
	}

	public void unlinkExperimenterGroup(ExperimenterGroup o)
	{
		if (experimenterGroup == o)
		{
			experimenterGroup = null;
		}
	}

	// Reference
	public Instrument getLinkedInstrument()
	{
		return instrument;
	}

	public void linkInstrument(Instrument o)
	{
		instrument = o;
	}

	public void unlinkInstrument(Instrument o)
	{
		if (instrument == o)
		{
			instrument = null;
		}
	}

	// Property
	public ObjectiveSettings getObjectiveSettings()
	{
		return objectiveSettings;
	}

	public void setObjectiveSettings(ObjectiveSettings objectiveSettings)
	{
		this.objectiveSettings = objectiveSettings;
	}

	// Property
	public ImagingEnvironment getImagingEnvironment()
	{
		return imagingEnvironment;
	}

	public void setImagingEnvironment(ImagingEnvironment imagingEnvironment)
	{
		this.imagingEnvironment = imagingEnvironment;
	}

	// Property
	public StageLabel getStageLabel()
	{
		return stageLabel;
	}

	public void setStageLabel(StageLabel stageLabel)
	{
		this.stageLabel = stageLabel;
	}

	// Property
	public Pixels getPixels()
	{
		return pixels;
	}

	public void setPixels(Pixels pixels)
	{
		this.pixels = pixels;
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

			o.linkImage(this);
		if (!roiLinks.contains(o)) {
			return roiLinks.add(o);
		}
		return false;
	}

	public boolean unlinkROI(ROI o)
	{

			o.unlinkImage(this);
		return roiLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedMicrobeamManipulationList()
	{
		return microbeamManipulationLinks.size();
	}

	public List<MicrobeamManipulation> copyLinkedMicrobeamManipulationList()
	{
		return new ArrayList<MicrobeamManipulation>(microbeamManipulationLinks);
	}

	public MicrobeamManipulation getLinkedMicrobeamManipulation(int index)
	{
		return microbeamManipulationLinks.get(index);
	}

	public MicrobeamManipulation setLinkedMicrobeamManipulation(int index, MicrobeamManipulation o)
	{
		return microbeamManipulationLinks.set(index, o);
	}

	public boolean linkMicrobeamManipulation(MicrobeamManipulation o)
	{

			o.linkImage(this);
		if (!microbeamManipulationLinks.contains(o)) {
			return microbeamManipulationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkMicrobeamManipulation(MicrobeamManipulation o)
	{

			o.unlinkImage(this);
		return microbeamManipulationLinks.remove(o);
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

			o.linkImage(this);
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkImage(this);
		return annotationLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedDatasetList()
	{
		return datasetLinks.size();
	}

	public List<Dataset> copyLinkedDatasetList()
	{
		return new ArrayList<Dataset>(datasetLinks);
	}

	public Dataset getLinkedDataset(int index)
	{
		return datasetLinks.get(index);
	}

	public Dataset setLinkedDataset(int index, Dataset o)
	{
		return datasetLinks.set(index, o);
	}

	public boolean linkDataset(Dataset o)
	{
		if (!datasetLinks.contains(o)) {
			return datasetLinks.add(o);
		}
		return false;
	}

	public boolean unlinkDataset(Dataset o)
	{
		return datasetLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedWellSampleList()
	{
		return wellSamples.size();
	}

	public List<WellSample> copyLinkedWellSampleList()
	{
		return new ArrayList<WellSample>(wellSamples);
	}

	public WellSample getLinkedWellSample(int index)
	{
		return wellSamples.get(index);
	}

	public WellSample setLinkedWellSample(int index, WellSample o)
	{
		return wellSamples.set(index, o);
	}

	public boolean linkWellSample(WellSample o)
	{
		if (!wellSamples.contains(o)) {
			return wellSamples.add(o);
		}
		return false;
	}

	public boolean unlinkWellSample(WellSample o)
	{
		return wellSamples.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Image_element)
	{
		// Creating XML block for Image

		if (Image_element == null)
		{
			Image_element =
					document.createElementNS(NAMESPACE, "Image");
		}

		if (id != null)
		{
			// Attribute property ID
			Image_element.setAttribute("ID", id.toString());
		}
		if (name != null)
		{
			// Attribute property Name
			Image_element.setAttribute("Name", name.toString());
		}
		if (acquisitionDate != null)
		{
			// Element property AcquisitionDate which is not complex (has no
			// sub-elements)
			Element acquisitionDate_element = 
					document.createElementNS(NAMESPACE, "AcquisitionDate");
			acquisitionDate_element.setTextContent(acquisitionDate.toString());
			Image_element.appendChild(acquisitionDate_element);
		}
		if (experimenter != null)
		{
			// Reference property ExperimenterRef
			ExperimenterRef o = new ExperimenterRef();
			o.setID(experimenter.getID());
			Image_element.appendChild(o.asXMLElement(document));
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
			Image_element.appendChild(description_element);
		}
		if (experiment != null)
		{
			// Reference property ExperimentRef
			ExperimentRef o = new ExperimentRef();
			o.setID(experiment.getID());
			Image_element.appendChild(o.asXMLElement(document));
		}
		if (experimenterGroup != null)
		{
			// Reference property ExperimenterGroupRef
			ExperimenterGroupRef o = new ExperimenterGroupRef();
			o.setID(experimenterGroup.getID());
			Image_element.appendChild(o.asXMLElement(document));
		}
		if (instrument != null)
		{
			// Reference property InstrumentRef
			InstrumentRef o = new InstrumentRef();
			o.setID(instrument.getID());
			Image_element.appendChild(o.asXMLElement(document));
		}
		if (objectiveSettings != null)
		{
			// Element property ObjectiveSettings which is complex (has
			// sub-elements)
			Image_element.appendChild(objectiveSettings.asXMLElement(document));
		}
		if (imagingEnvironment != null)
		{
			// Element property ImagingEnvironment which is complex (has
			// sub-elements)
			Image_element.appendChild(imagingEnvironment.asXMLElement(document));
		}
		if (stageLabel != null)
		{
			// Element property StageLabel which is complex (has
			// sub-elements)
			Image_element.appendChild(stageLabel.asXMLElement(document));
		}
		if (pixels != null)
		{
			// Element property Pixels which is complex (has
			// sub-elements)
			Image_element.appendChild(pixels.asXMLElement(document));
		}
		if (roiLinks != null)
		{
			// Reference property ROIRef which occurs more than once
			for (ROI roiLinks_value : roiLinks)
			{
				ROIRef o = new ROIRef();
				o.setID(roiLinks_value.getID());
				Image_element.appendChild(o.asXMLElement(document));
			}
		}
		if (microbeamManipulationLinks != null)
		{
			// Reference property MicrobeamManipulationRef which occurs more than once
			for (MicrobeamManipulation microbeamManipulationLinks_value : microbeamManipulationLinks)
			{
				MicrobeamManipulationRef o = new MicrobeamManipulationRef();
				o.setID(microbeamManipulationLinks_value.getID());
				Image_element.appendChild(o.asXMLElement(document));
			}
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Image_element.appendChild(o.asXMLElement(document));
			}
		}
		if (datasetLinks != null)
		{
			// *** IGNORING *** Skipped back reference Dataset_BackReference
		}
		if (wellSamples != null)
		{
			// *** IGNORING *** Skipped back reference WellSample_BackReference
		}
		return super.asXMLElement(document, Image_element);
	}
}
