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

public class OME extends AbstractOMEModelObject
{
	// Base:  -- Name: OME -- Type: OME -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(OME.class);

	// -- Instance variables --


	// Property
	private String uuid;

	// Property
	private String creator;

	// Property which occurs more than once
	private List<Project> projects = new ArrayList<Project>();

	// Property which occurs more than once
	private List<Dataset> datasets = new ArrayList<Dataset>();

	// Property which occurs more than once
	private List<Experiment> experiments = new ArrayList<Experiment>();

	// Property which occurs more than once
	private List<Plate> plates = new ArrayList<Plate>();

	// Property which occurs more than once
	private List<Screen> screens = new ArrayList<Screen>();

	// Property which occurs more than once
	private List<Experimenter> experimenters = new ArrayList<Experimenter>();

	// Property which occurs more than once
	private List<ExperimenterGroup> experimenterGroups = new ArrayList<ExperimenterGroup>();

	// Property which occurs more than once
	private List<Instrument> instruments = new ArrayList<Instrument>();

	// Property which occurs more than once
	private List<Image> images = new ArrayList<Image>();

	// Property
	private StructuredAnnotations structuredAnnotations;

	// Property which occurs more than once
	private List<ROI> roIs = new ArrayList<ROI>();

	// Property
	private BinaryOnly binaryOnly;

	// -- Constructors --

	/** Default constructor. */
	public OME()
	{
		super();
	}

	/** 
	 * Constructs OME recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public OME(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from OME specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates OME recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"OME".equals(tagName))
		{
			LOGGER.debug("Expecting node name of OME got {}", tagName);
		}
		if (element.hasAttribute("UUID"))
		{
			// Attribute property UUID
			setUUID(String.valueOf(
					element.getAttribute("UUID")));
		}
		if (element.hasAttribute("Creator"))
		{
			// Attribute property Creator
			setCreator(String.valueOf(
					element.getAttribute("Creator")));
		}
		// Element property Project which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Project_nodeList =
				getChildrenByTagName(element, "Project");
		for (Element Project_element : Project_nodeList)
		{
			addProject(
					new Project(Project_element, model));
		}
		// Element property Dataset which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Dataset_nodeList =
				getChildrenByTagName(element, "Dataset");
		for (Element Dataset_element : Dataset_nodeList)
		{
			addDataset(
					new Dataset(Dataset_element, model));
		}
		// Element property Experiment which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Experiment_nodeList =
				getChildrenByTagName(element, "Experiment");
		for (Element Experiment_element : Experiment_nodeList)
		{
			addExperiment(
					new Experiment(Experiment_element, model));
		}
		// Element property Plate which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Plate_nodeList =
				getChildrenByTagName(element, "Plate");
		for (Element Plate_element : Plate_nodeList)
		{
			addPlate(
					new Plate(Plate_element, model));
		}
		// Element property Screen which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Screen_nodeList =
				getChildrenByTagName(element, "Screen");
		for (Element Screen_element : Screen_nodeList)
		{
			addScreen(
					new Screen(Screen_element, model));
		}
		// Element property Experimenter which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Experimenter_nodeList =
				getChildrenByTagName(element, "Experimenter");
		for (Element Experimenter_element : Experimenter_nodeList)
		{
			addExperimenter(
					new Experimenter(Experimenter_element, model));
		}
		// Element property ExperimenterGroup which is complex (has
		// sub-elements) and occurs more than once
		List<Element> ExperimenterGroup_nodeList =
				getChildrenByTagName(element, "ExperimenterGroup");
		for (Element ExperimenterGroup_element : ExperimenterGroup_nodeList)
		{
			addExperimenterGroup(
					new ExperimenterGroup(ExperimenterGroup_element, model));
		}
		// Element property Instrument which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Instrument_nodeList =
				getChildrenByTagName(element, "Instrument");
		for (Element Instrument_element : Instrument_nodeList)
		{
			addInstrument(
					new Instrument(Instrument_element, model));
		}
		// Element property Image which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Image_nodeList =
				getChildrenByTagName(element, "Image");
		for (Element Image_element : Image_nodeList)
		{
			addImage(
					new Image(Image_element, model));
		}
		List<Element> StructuredAnnotations_nodeList =
				getChildrenByTagName(element, "StructuredAnnotations");
		if (StructuredAnnotations_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"StructuredAnnotations node list size %d != 1",
					StructuredAnnotations_nodeList.size()));
		}
		else if (StructuredAnnotations_nodeList.size() != 0)
		{
			// Element property StructuredAnnotations which is complex (has
			// sub-elements)
			setStructuredAnnotations(new StructuredAnnotations(
					(Element) StructuredAnnotations_nodeList.get(0), model));
		}
		// Element property ROI which is complex (has
		// sub-elements) and occurs more than once
		List<Element> ROI_nodeList =
				getChildrenByTagName(element, "ROI");
		for (Element ROI_element : ROI_nodeList)
		{
			addROI(
					new ROI(ROI_element, model));
		}
		List<Element> BinaryOnly_nodeList =
				getChildrenByTagName(element, "BinaryOnly");
		if (BinaryOnly_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"BinaryOnly node list size %d != 1",
					BinaryOnly_nodeList.size()));
		}
		else if (BinaryOnly_nodeList.size() != 0)
		{
			// Element property BinaryOnly which is complex (has
			// sub-elements)
			setBinaryOnly(new BinaryOnly(
					(Element) BinaryOnly_nodeList.get(0), model));
		}
	}

	// -- OME API methods --

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
	public String getUUID()
	{
		return uuid;
	}

	public void setUUID(String uuid)
	{
		this.uuid = uuid;
	}

	// Property
	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	// Property which occurs more than once
	public int sizeOfProjectList()
	{
		return projects.size();
	}

	public List<Project> copyProjectList()
	{
		return new ArrayList<Project>(projects);
	}

	public Project getProject(int index)
	{
		return projects.get(index);
	}

	public Project setProject(int index, Project project)
	{
		return projects.set(index, project);
	}

	public void addProject(Project project)
	{
		projects.add(project);
	}

	public void removeProject(Project project)
	{
		projects.remove(project);
	}

	// Property which occurs more than once
	public int sizeOfDatasetList()
	{
		return datasets.size();
	}

	public List<Dataset> copyDatasetList()
	{
		return new ArrayList<Dataset>(datasets);
	}

	public Dataset getDataset(int index)
	{
		return datasets.get(index);
	}

	public Dataset setDataset(int index, Dataset dataset)
	{
		return datasets.set(index, dataset);
	}

	public void addDataset(Dataset dataset)
	{
		datasets.add(dataset);
	}

	public void removeDataset(Dataset dataset)
	{
		datasets.remove(dataset);
	}

	// Property which occurs more than once
	public int sizeOfExperimentList()
	{
		return experiments.size();
	}

	public List<Experiment> copyExperimentList()
	{
		return new ArrayList<Experiment>(experiments);
	}

	public Experiment getExperiment(int index)
	{
		return experiments.get(index);
	}

	public Experiment setExperiment(int index, Experiment experiment)
	{
		return experiments.set(index, experiment);
	}

	public void addExperiment(Experiment experiment)
	{
		experiments.add(experiment);
	}

	public void removeExperiment(Experiment experiment)
	{
		experiments.remove(experiment);
	}

	// Property which occurs more than once
	public int sizeOfPlateList()
	{
		return plates.size();
	}

	public List<Plate> copyPlateList()
	{
		return new ArrayList<Plate>(plates);
	}

	public Plate getPlate(int index)
	{
		return plates.get(index);
	}

	public Plate setPlate(int index, Plate plate)
	{
		return plates.set(index, plate);
	}

	public void addPlate(Plate plate)
	{
		plates.add(plate);
	}

	public void removePlate(Plate plate)
	{
		plates.remove(plate);
	}

	// Property which occurs more than once
	public int sizeOfScreenList()
	{
		return screens.size();
	}

	public List<Screen> copyScreenList()
	{
		return new ArrayList<Screen>(screens);
	}

	public Screen getScreen(int index)
	{
		return screens.get(index);
	}

	public Screen setScreen(int index, Screen screen)
	{
		return screens.set(index, screen);
	}

	public void addScreen(Screen screen)
	{
		screens.add(screen);
	}

	public void removeScreen(Screen screen)
	{
		screens.remove(screen);
	}

	// Property which occurs more than once
	public int sizeOfExperimenterList()
	{
		return experimenters.size();
	}

	public List<Experimenter> copyExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenters);
	}

	public Experimenter getExperimenter(int index)
	{
		return experimenters.get(index);
	}

	public Experimenter setExperimenter(int index, Experimenter experimenter)
	{
		return experimenters.set(index, experimenter);
	}

	public void addExperimenter(Experimenter experimenter)
	{
		experimenters.add(experimenter);
	}

	public void removeExperimenter(Experimenter experimenter)
	{
		experimenters.remove(experimenter);
	}

	// Property which occurs more than once
	public int sizeOfExperimenterGroupList()
	{
		return experimenterGroups.size();
	}

	public List<ExperimenterGroup> copyExperimenterGroupList()
	{
		return new ArrayList<ExperimenterGroup>(experimenterGroups);
	}

	public ExperimenterGroup getExperimenterGroup(int index)
	{
		return experimenterGroups.get(index);
	}

	public ExperimenterGroup setExperimenterGroup(int index, ExperimenterGroup experimenterGroup)
	{
		return experimenterGroups.set(index, experimenterGroup);
	}

	public void addExperimenterGroup(ExperimenterGroup experimenterGroup)
	{
		experimenterGroups.add(experimenterGroup);
	}

	public void removeExperimenterGroup(ExperimenterGroup experimenterGroup)
	{
		experimenterGroups.remove(experimenterGroup);
	}

	// Property which occurs more than once
	public int sizeOfInstrumentList()
	{
		return instruments.size();
	}

	public List<Instrument> copyInstrumentList()
	{
		return new ArrayList<Instrument>(instruments);
	}

	public Instrument getInstrument(int index)
	{
		return instruments.get(index);
	}

	public Instrument setInstrument(int index, Instrument instrument)
	{
		return instruments.set(index, instrument);
	}

	public void addInstrument(Instrument instrument)
	{
		instruments.add(instrument);
	}

	public void removeInstrument(Instrument instrument)
	{
		instruments.remove(instrument);
	}

	// Property which occurs more than once
	public int sizeOfImageList()
	{
		return images.size();
	}

	public List<Image> copyImageList()
	{
		return new ArrayList<Image>(images);
	}

	public Image getImage(int index)
	{
		return images.get(index);
	}

	public Image setImage(int index, Image image)
	{
		return images.set(index, image);
	}

	public void addImage(Image image)
	{
		images.add(image);
	}

	public void removeImage(Image image)
	{
		images.remove(image);
	}

	// Property
	public StructuredAnnotations getStructuredAnnotations()
	{
		return structuredAnnotations;
	}

	public void setStructuredAnnotations(StructuredAnnotations structuredAnnotations)
	{
		this.structuredAnnotations = structuredAnnotations;
	}

	// Property which occurs more than once
	public int sizeOfROIList()
	{
		return roIs.size();
	}

	public List<ROI> copyROIList()
	{
		return new ArrayList<ROI>(roIs);
	}

	public ROI getROI(int index)
	{
		return roIs.get(index);
	}

	public ROI setROI(int index, ROI roi)
	{
		return roIs.set(index, roi);
	}

	public void addROI(ROI roi)
	{
		roIs.add(roi);
	}

	public void removeROI(ROI roi)
	{
		roIs.remove(roi);
	}

	// Property
	public BinaryOnly getBinaryOnly()
	{
		return binaryOnly;
	}

	public void setBinaryOnly(BinaryOnly binaryOnly)
	{
		this.binaryOnly = binaryOnly;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element OME_element)
	{
		// Creating XML block for OME

		if (OME_element == null)
		{
			OME_element =
					document.createElementNS(NAMESPACE, "OME");
		}

		if (uuid != null)
		{
			// Attribute property UUID
			OME_element.setAttribute("UUID", uuid.toString());
		}
		if (creator != null)
		{
			// Attribute property Creator
			OME_element.setAttribute("Creator", creator.toString());
		}
		if (projects != null)
		{
			// Element property Project which is complex (has
			// sub-elements) and occurs more than once
			for (Project projects_value : projects)
			{
				OME_element.appendChild(projects_value.asXMLElement(document));
			}
		}
		if (datasets != null)
		{
			// Element property Dataset which is complex (has
			// sub-elements) and occurs more than once
			for (Dataset datasets_value : datasets)
			{
				OME_element.appendChild(datasets_value.asXMLElement(document));
			}
		}
		if (experiments != null)
		{
			// Element property Experiment which is complex (has
			// sub-elements) and occurs more than once
			for (Experiment experiments_value : experiments)
			{
				OME_element.appendChild(experiments_value.asXMLElement(document));
			}
		}
		if (plates != null)
		{
			// Element property Plate which is complex (has
			// sub-elements) and occurs more than once
			for (Plate plates_value : plates)
			{
				OME_element.appendChild(plates_value.asXMLElement(document));
			}
		}
		if (screens != null)
		{
			// Element property Screen which is complex (has
			// sub-elements) and occurs more than once
			for (Screen screens_value : screens)
			{
				OME_element.appendChild(screens_value.asXMLElement(document));
			}
		}
		if (experimenters != null)
		{
			// Element property Experimenter which is complex (has
			// sub-elements) and occurs more than once
			for (Experimenter experimenters_value : experimenters)
			{
				OME_element.appendChild(experimenters_value.asXMLElement(document));
			}
		}
		if (experimenterGroups != null)
		{
			// Element property ExperimenterGroup which is complex (has
			// sub-elements) and occurs more than once
			for (ExperimenterGroup experimenterGroups_value : experimenterGroups)
			{
				OME_element.appendChild(experimenterGroups_value.asXMLElement(document));
			}
		}
		if (instruments != null)
		{
			// Element property Instrument which is complex (has
			// sub-elements) and occurs more than once
			for (Instrument instruments_value : instruments)
			{
				OME_element.appendChild(instruments_value.asXMLElement(document));
			}
		}
		if (images != null)
		{
			// Element property Image which is complex (has
			// sub-elements) and occurs more than once
			for (Image images_value : images)
			{
				OME_element.appendChild(images_value.asXMLElement(document));
			}
		}
		if (structuredAnnotations != null)
		{
			// Element property StructuredAnnotations which is complex (has
			// sub-elements)
			OME_element.appendChild(structuredAnnotations.asXMLElement(document));
		}
		if (roIs != null)
		{
			// Element property ROI which is complex (has
			// sub-elements) and occurs more than once
			for (ROI roIs_value : roIs)
			{
				OME_element.appendChild(roIs_value.asXMLElement(document));
			}
		}
		if (binaryOnly != null)
		{
			// Element property BinaryOnly which is complex (has
			// sub-elements)
			OME_element.appendChild(binaryOnly.asXMLElement(document));
		}
		return super.asXMLElement(document, OME_element);
	}
}
