/*
 * ome.xml.r201004.Image
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
 * Created by callan via xsd-fu on 2010-04-23 13:18:06+0100
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

public class Image extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

	// -- Instance variables --

	// Property
	private String id;

	// Property
	private String name;

	// Property
	private String acquiredDate;

	// Property
	private Experimenter experimenter;

	// Property
	private String description;

	// Property
	private Experiment experiment;

	// Property
	private Group group;

	// Reference DatasetRef
	private List<Dataset> datasetList = new ArrayList<Dataset>();

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
	private List<ROI> roiList = new ArrayList<ROI>();

	// Reference MicrobeamManipulationRef
	private List<MicrobeamManipulation> microbeamManipulationList = new ArrayList<MicrobeamManipulation>();

	// Reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference WellSample_BackReference
	private List<WellSample> wellSample_BackReferenceList = new ArrayList<WellSample>();

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
		super.update(element);
		String tagName = element.getTagName();
		if (!"Image".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of Image got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of Image got %s",
			//		tagName));
		}
		if (!element.hasAttribute("ID"))
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Image missing required ID property."));
		}
		// ID property
		setID(String.valueOf(
					element.getAttribute("ID")));
		// Adding this model object to the model handler
	    	model.addModelObject(getID(), this);
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		NodeList AcquiredDate_nodeList = element.getElementsByTagName("AcquiredDate");
		if (AcquiredDate_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"AcquiredDate node list size %d != 1",
					AcquiredDate_nodeList.getLength()));
		}
		else if (AcquiredDate_nodeList.getLength() != 0)
		{
			// Element property AcquiredDate which is not complex (has no
			// sub-elements)
			setAcquiredDate(AcquiredDate_nodeList.item(0).getTextContent());
		}
		// Element reference ExperimenterRef
		NodeList ExperimenterRef_nodeList = element.getElementsByTagName("ExperimenterRef");
		for (int i = 0; i < ExperimenterRef_nodeList.getLength(); i++)
		{
			Element ExperimenterRef_element = (Element) ExperimenterRef_nodeList.item(i);
			ExperimenterRef experimenter_reference = new ExperimenterRef();
			experimenter_reference.setID(ExperimenterRef_element.getAttribute("ID"));
			model.addReference(this, experimenter_reference);
		}
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
		// Element reference ExperimentRef
		NodeList ExperimentRef_nodeList = element.getElementsByTagName("ExperimentRef");
		for (int i = 0; i < ExperimentRef_nodeList.getLength(); i++)
		{
			Element ExperimentRef_element = (Element) ExperimentRef_nodeList.item(i);
			ExperimentRef experiment_reference = new ExperimentRef();
			experiment_reference.setID(ExperimentRef_element.getAttribute("ID"));
			model.addReference(this, experiment_reference);
		}
		// Element reference GroupRef
		NodeList GroupRef_nodeList = element.getElementsByTagName("GroupRef");
		for (int i = 0; i < GroupRef_nodeList.getLength(); i++)
		{
			Element GroupRef_element = (Element) GroupRef_nodeList.item(i);
			GroupRef group_reference = new GroupRef();
			group_reference.setID(GroupRef_element.getAttribute("ID"));
			model.addReference(this, group_reference);
		}
		// Element reference DatasetRef
		NodeList DatasetRef_nodeList = element.getElementsByTagName("DatasetRef");
		for (int i = 0; i < DatasetRef_nodeList.getLength(); i++)
		{
			Element DatasetRef_element = (Element) DatasetRef_nodeList.item(i);
			DatasetRef datasetList_reference = new DatasetRef();
			datasetList_reference.setID(DatasetRef_element.getAttribute("ID"));
			model.addReference(this, datasetList_reference);
		}
		// Element reference InstrumentRef
		NodeList InstrumentRef_nodeList = element.getElementsByTagName("InstrumentRef");
		for (int i = 0; i < InstrumentRef_nodeList.getLength(); i++)
		{
			Element InstrumentRef_element = (Element) InstrumentRef_nodeList.item(i);
			InstrumentRef instrument_reference = new InstrumentRef();
			instrument_reference.setID(InstrumentRef_element.getAttribute("ID"));
			model.addReference(this, instrument_reference);
		}
		NodeList ObjectiveSettings_nodeList = element.getElementsByTagName("ObjectiveSettings");
		if (ObjectiveSettings_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"ObjectiveSettings node list size %d != 1",
					ObjectiveSettings_nodeList.getLength()));
		}
		else if (ObjectiveSettings_nodeList.getLength() != 0)
		{
			// Element property ObjectiveSettings which is complex (has
			// sub-elements)
			setObjectiveSettings(new ObjectiveSettings(
					(Element) ObjectiveSettings_nodeList.item(0), model));
		}
		NodeList ImagingEnvironment_nodeList = element.getElementsByTagName("ImagingEnvironment");
		if (ImagingEnvironment_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"ImagingEnvironment node list size %d != 1",
					ImagingEnvironment_nodeList.getLength()));
		}
		else if (ImagingEnvironment_nodeList.getLength() != 0)
		{
			// Element property ImagingEnvironment which is complex (has
			// sub-elements)
			setImagingEnvironment(new ImagingEnvironment(
					(Element) ImagingEnvironment_nodeList.item(0), model));
		}
		NodeList StageLabel_nodeList = element.getElementsByTagName("StageLabel");
		if (StageLabel_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"StageLabel node list size %d != 1",
					StageLabel_nodeList.getLength()));
		}
		else if (StageLabel_nodeList.getLength() != 0)
		{
			// Element property StageLabel which is complex (has
			// sub-elements)
			setStageLabel(new StageLabel(
					(Element) StageLabel_nodeList.item(0), model));
		}
		NodeList Pixels_nodeList = element.getElementsByTagName("Pixels");
		if (Pixels_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Pixels node list size %d != 1",
					Pixels_nodeList.getLength()));
		}
		else if (Pixels_nodeList.getLength() != 0)
		{
			// Element property Pixels which is complex (has
			// sub-elements)
			setPixels(new Pixels(
					(Element) Pixels_nodeList.item(0), model));
		}
		// Element reference ROIRef
		NodeList ROIRef_nodeList = element.getElementsByTagName("ROIRef");
		for (int i = 0; i < ROIRef_nodeList.getLength(); i++)
		{
			Element ROIRef_element = (Element) ROIRef_nodeList.item(i);
			ROIRef roiList_reference = new ROIRef();
			roiList_reference.setID(ROIRef_element.getAttribute("ID"));
			model.addReference(this, roiList_reference);
		}
		// Element reference MicrobeamManipulationRef
		NodeList MicrobeamManipulationRef_nodeList = element.getElementsByTagName("MicrobeamManipulationRef");
		for (int i = 0; i < MicrobeamManipulationRef_nodeList.getLength(); i++)
		{
			Element MicrobeamManipulationRef_element = (Element) MicrobeamManipulationRef_nodeList.item(i);
			MicrobeamManipulationRef microbeamManipulationList_reference = new MicrobeamManipulationRef();
			microbeamManipulationList_reference.setID(MicrobeamManipulationRef_element.getAttribute("ID"));
			model.addReference(this, microbeamManipulationList_reference);
		}
		// Element reference AnnotationRef
		NodeList AnnotationRef_nodeList = element.getElementsByTagName("AnnotationRef");
		for (int i = 0; i < AnnotationRef_nodeList.getLength(); i++)
		{
			Element AnnotationRef_element = (Element) AnnotationRef_nodeList.item(i);
			AnnotationRef annotationList_reference = new AnnotationRef();
			annotationList_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationList_reference);
		}
		// *** IGNORING *** Skipped back reference WellSample_BackReference
	}

	// -- Image API methods --

	public void link(Reference reference, OMEModelObject o)
	{
		if (reference instanceof ExperimenterRef)
		{
			Experimenter o_casted = (Experimenter) o;
			o_casted.linkImage(this);
			experimenter = o_casted;
		}
		if (reference instanceof ExperimentRef)
		{
			Experiment o_casted = (Experiment) o;
			o_casted.linkImage(this);
			experiment = o_casted;
		}
		if (reference instanceof GroupRef)
		{
			Group o_casted = (Group) o;
			o_casted.linkImage(this);
			group = o_casted;
		}
		if (reference instanceof DatasetRef)
		{
			Dataset o_casted = (Dataset) o;
			o_casted.linkImage(this);
			datasetList.add(o_casted);
		}
		if (reference instanceof InstrumentRef)
		{
			Instrument o_casted = (Instrument) o;
			o_casted.linkImage(this);
			instrument = o_casted;
		}
		if (reference instanceof ROIRef)
		{
			ROI o_casted = (ROI) o;
			o_casted.linkImage(this);
			roiList.add(o_casted);
		}
		if (reference instanceof MicrobeamManipulationRef)
		{
			MicrobeamManipulation o_casted = (MicrobeamManipulation) o;
			o_casted.linkImage(this);
			microbeamManipulationList.add(o_casted);
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkImage(this);
			annotationList.add(o_casted);
		}
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
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
	public String getAcquiredDate()
	{
		return acquiredDate;
	}

	public void setAcquiredDate(String acquiredDate)
	{
		this.acquiredDate = acquiredDate;
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
	public Group getLinkedGroup()
	{
		return group;
	}

	public void linkGroup(Group o)
	{
		group = o;
	}

	public void unlinkGroup(Group o)
	{
		if (group == o)
		{
			group = null;
		}
	}

	// Reference which occurs more than once
	public int sizeOfLinkedDatasetList()
	{
		return datasetList.size();
	}

	public List<Dataset> copyLinkedDatasetList()
	{
		return new ArrayList<Dataset>(datasetList);
	}

	public Dataset getLinkedDataset(int index)
	{
		return datasetList.get(index);
	}

	public Dataset setLinkedDataset(int index, Dataset o)
	{
		return datasetList.set(index, o);
	}

	public boolean linkDataset(Dataset o)
	{
		o.linkImage(this);
		return datasetList.add(o);
	}

	public boolean unlinkDataset(Dataset o)
	{
		o.unlinkImage(this);
		return datasetList.remove(o);
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
		return roiList.size();
	}

	public List<ROI> copyLinkedROIList()
	{
		return new ArrayList<ROI>(roiList);
	}

	public ROI getLinkedROI(int index)
	{
		return roiList.get(index);
	}

	public ROI setLinkedROI(int index, ROI o)
	{
		return roiList.set(index, o);
	}

	public boolean linkROI(ROI o)
	{
		o.linkImage(this);
		return roiList.add(o);
	}

	public boolean unlinkROI(ROI o)
	{
		o.unlinkImage(this);
		return roiList.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedMicrobeamManipulationList()
	{
		return microbeamManipulationList.size();
	}

	public List<MicrobeamManipulation> copyLinkedMicrobeamManipulationList()
	{
		return new ArrayList<MicrobeamManipulation>(microbeamManipulationList);
	}

	public MicrobeamManipulation getLinkedMicrobeamManipulation(int index)
	{
		return microbeamManipulationList.get(index);
	}

	public MicrobeamManipulation setLinkedMicrobeamManipulation(int index, MicrobeamManipulation o)
	{
		return microbeamManipulationList.set(index, o);
	}

	public boolean linkMicrobeamManipulation(MicrobeamManipulation o)
	{
		o.linkImage(this);
		return microbeamManipulationList.add(o);
	}

	public boolean unlinkMicrobeamManipulation(MicrobeamManipulation o)
	{
		o.unlinkImage(this);
		return microbeamManipulationList.remove(o);
	}

	// Reference which occurs more than once
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

	public boolean linkAnnotation(Annotation o)
	{
		o.linkImage(this);
		return annotationList.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{
		o.unlinkImage(this);
		return annotationList.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedWellSampleList()
	{
		return wellSample_BackReferenceList.size();
	}

	public List<WellSample> copyLinkedWellSampleList()
	{
		return new ArrayList<WellSample>(wellSample_BackReferenceList);
	}

	public WellSample getLinkedWellSample(int index)
	{
		return wellSample_BackReferenceList.get(index);
	}

	public WellSample setLinkedWellSample(int index, WellSample o)
	{
		return wellSample_BackReferenceList.set(index, o);
	}

	public boolean linkWellSample(WellSample o)
	{
		return wellSample_BackReferenceList.add(o);
	}

	public boolean unlinkWellSample(WellSample o)
	{
		return wellSample_BackReferenceList.remove(o);
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
		if (acquiredDate != null)
		{
			// Element property AcquiredDate which is not complex (has no
			// sub-elements)
			Element acquiredDate_element = 
					document.createElementNS(NAMESPACE, "AcquiredDate");
			acquiredDate_element.setTextContent(acquiredDate);
			Image_element.appendChild(acquiredDate_element);
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
			description_element.setTextContent(description);
			Image_element.appendChild(description_element);
		}
		if (experiment != null)
		{
			// Reference property ExperimentRef
			ExperimentRef o = new ExperimentRef();
			o.setID(experiment.getID());
			Image_element.appendChild(o.asXMLElement(document));
		}
		if (group != null)
		{
			// Reference property GroupRef
			GroupRef o = new GroupRef();
			o.setID(group.getID());
			Image_element.appendChild(o.asXMLElement(document));
		}
		if (datasetList != null)
		{
			// Reference property DatasetRef which occurs more than once
			for (Dataset datasetList_value : datasetList)
			{
				DatasetRef o = new DatasetRef();
				o.setID(datasetList_value.getID());
				Image_element.appendChild(o.asXMLElement(document));
			}
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
		if (roiList != null)
		{
			// Reference property ROIRef which occurs more than once
			for (ROI roiList_value : roiList)
			{
				ROIRef o = new ROIRef();
				o.setID(roiList_value.getID());
				Image_element.appendChild(o.asXMLElement(document));
			}
		}
		if (microbeamManipulationList != null)
		{
			// Reference property MicrobeamManipulationRef which occurs more than once
			for (MicrobeamManipulation microbeamManipulationList_value : microbeamManipulationList)
			{
				MicrobeamManipulationRef o = new MicrobeamManipulationRef();
				o.setID(microbeamManipulationList_value.getID());
				Image_element.appendChild(o.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationList_value.getID());
				Image_element.appendChild(o.asXMLElement(document));
			}
		}
		if (wellSample_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference WellSample_BackReference
		}
		return super.asXMLElement(document, Image_element);
	}
}
