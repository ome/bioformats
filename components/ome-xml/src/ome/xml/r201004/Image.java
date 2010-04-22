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
 * Created by callan via xsd-fu on 2010-04-22 12:03:51+0100
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

	// Back reference ExperimenterRef
	private List<Experimenter> experimenter = new ArrayList<Experimenter>();

	// Property
	private String description;

	// Back reference ExperimentRef
	private List<Experiment> experiment = new ArrayList<Experiment>();

	// Back reference GroupRef
	private List<Group> group = new ArrayList<Group>();

	// Back reference DatasetRef
	private List<Dataset> datasetList = new ArrayList<Dataset>();

	// Back reference InstrumentRef
	private List<Instrument> instrument = new ArrayList<Instrument>();

	// Property
	private ObjectiveSettings objectiveSettings;

	// Property
	private ImagingEnvironment imagingEnvironment;

	// Property
	private StageLabel stageLabel;

	// Property
	private Pixels pixels;

	// Back reference ROIRef
	private List<ROI> roiList = new ArrayList<ROI>();

	// Back reference MicrobeamManipulationRef
	private List<MicrobeamManipulation> microbeamManipulationList = new ArrayList<MicrobeamManipulation>();

	// Back reference AnnotationRef
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
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Image(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates Image recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element) throws EnumerationException
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
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
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
		// *** IGNORING *** Skipped back reference ExperimenterRef
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
		// *** IGNORING *** Skipped back reference ExperimentRef
		// *** IGNORING *** Skipped back reference GroupRef
		// *** IGNORING *** Skipped back reference DatasetRef
		// *** IGNORING *** Skipped back reference InstrumentRef
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
					(Element) ObjectiveSettings_nodeList.item(0)));
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
					(Element) ImagingEnvironment_nodeList.item(0)));
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
					(Element) StageLabel_nodeList.item(0)));
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
					(Element) Pixels_nodeList.item(0)));
		}
		// *** IGNORING *** Skipped back reference ROIRef
		// *** IGNORING *** Skipped back reference MicrobeamManipulationRef
		// *** IGNORING *** Skipped back reference AnnotationRef
		// *** IGNORING *** Skipped back reference WellSample_BackReference
	}

	// -- Image API methods --

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

	// Reference ExperimenterRef
	public int sizeOfLinkedExperimenterList()
	{
		return experimenter.size();
	}

	public List<Experimenter> copyLinkedExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenter);
	}

	public Experimenter getLinkedExperimenter(int index)
	{
		return experimenter.get(index);
	}

	public Experimenter setLinkedExperimenter(int index, Experimenter o)
	{
		return experimenter.set(index, o);
	}

	public void linkExperimenter(Experimenter o)
	{
		this.experimenter.add(o);
	}

	public void unlinkExperimenter(Experimenter o)
	{
		this.experimenter.add(o);
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

	// Reference ExperimentRef
	public int sizeOfLinkedExperimentList()
	{
		return experiment.size();
	}

	public List<Experiment> copyLinkedExperimentList()
	{
		return new ArrayList<Experiment>(experiment);
	}

	public Experiment getLinkedExperiment(int index)
	{
		return experiment.get(index);
	}

	public Experiment setLinkedExperiment(int index, Experiment o)
	{
		return experiment.set(index, o);
	}

	public void linkExperiment(Experiment o)
	{
		this.experiment.add(o);
	}

	public void unlinkExperiment(Experiment o)
	{
		this.experiment.add(o);
	}

	// Reference GroupRef
	public int sizeOfLinkedGroupList()
	{
		return group.size();
	}

	public List<Group> copyLinkedGroupList()
	{
		return new ArrayList<Group>(group);
	}

	public Group getLinkedGroup(int index)
	{
		return group.get(index);
	}

	public Group setLinkedGroup(int index, Group o)
	{
		return group.set(index, o);
	}

	public void linkGroup(Group o)
	{
		this.group.add(o);
	}

	public void unlinkGroup(Group o)
	{
		this.group.add(o);
	}

	// Reference DatasetRef
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

	public void linkDataset(Dataset o)
	{
		this.datasetList.add(o);
	}

	public void unlinkDataset(Dataset o)
	{
		this.datasetList.add(o);
	}

	// Reference InstrumentRef
	public int sizeOfLinkedInstrumentList()
	{
		return instrument.size();
	}

	public List<Instrument> copyLinkedInstrumentList()
	{
		return new ArrayList<Instrument>(instrument);
	}

	public Instrument getLinkedInstrument(int index)
	{
		return instrument.get(index);
	}

	public Instrument setLinkedInstrument(int index, Instrument o)
	{
		return instrument.set(index, o);
	}

	public void linkInstrument(Instrument o)
	{
		this.instrument.add(o);
	}

	public void unlinkInstrument(Instrument o)
	{
		this.instrument.add(o);
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

	// Reference ROIRef
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

	public void linkROI(ROI o)
	{
		this.roiList.add(o);
	}

	public void unlinkROI(ROI o)
	{
		this.roiList.add(o);
	}

	// Reference MicrobeamManipulationRef
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

	public void linkMicrobeamManipulation(MicrobeamManipulation o)
	{
		this.microbeamManipulationList.add(o);
	}

	public void unlinkMicrobeamManipulation(MicrobeamManipulation o)
	{
		this.microbeamManipulationList.add(o);
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
	public int sizeOfWellSampleList()
	{
		return wellSample_BackReferenceList.size();
	}

	public List<WellSample> copyWellSampleList()
	{
		return new ArrayList<WellSample>(wellSample_BackReferenceList);
	}

	public WellSample getWellSample(int index)
	{
		return wellSample_BackReferenceList.get(index);
	}

	public WellSample setWellSample(int index, WellSample wellSample_BackReference)
	{
		return wellSample_BackReferenceList.set(index, wellSample_BackReference);
	}

	public void addWellSample(WellSample wellSample_BackReference)
	{
		wellSample_BackReferenceList.add(wellSample_BackReference);
	}

	public void removeWellSample(WellSample wellSample_BackReference)
	{
		wellSample_BackReferenceList.remove(wellSample_BackReference);
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
					document.createElementNS("http://www.openmicroscopy.org/Schemas/OME/2010-04", "Image");
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
					document.createElementNS("http://www.openmicroscopy.org/Schemas/OME/2010-04", "AcquiredDate");
			acquiredDate_element.setTextContent(acquiredDate);
			Image_element.appendChild(acquiredDate_element);
		}
		if (experimenter != null)
		{
			// *** IGNORING *** Skipped back reference ExperimenterRef
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS("http://www.openmicroscopy.org/Schemas/OME/2010-04", "Description");
			description_element.setTextContent(description);
			Image_element.appendChild(description_element);
		}
		if (experiment != null)
		{
			// *** IGNORING *** Skipped back reference ExperimentRef
		}
		if (group != null)
		{
			// *** IGNORING *** Skipped back reference GroupRef
		}
		if (datasetList != null)
		{
			// *** IGNORING *** Skipped back reference DatasetRef
		}
		if (instrument != null)
		{
			// *** IGNORING *** Skipped back reference InstrumentRef
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
			// *** IGNORING *** Skipped back reference ROIRef
		}
		if (microbeamManipulationList != null)
		{
			// *** IGNORING *** Skipped back reference MicrobeamManipulationRef
		}
		if (annotationList != null)
		{
			// *** IGNORING *** Skipped back reference AnnotationRef
		}
		if (wellSample_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference WellSample_BackReference
		}
		return super.asXMLElement(document, Image_element);
	}
}
