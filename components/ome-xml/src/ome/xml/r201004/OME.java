
/*
 * ome.xml.r201004.OME
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
 * Created by callan via xsd-fu on 2010-04-20 18:27:32+0100
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

public class OME extends AbstractOMEModelObject
{
	// -- Instance variables --

	// Property
	private String uuid;

	// Property which occurs more than once
	private List<Project> projectList = new ArrayList<Project>();

	// Property which occurs more than once
	private List<Dataset> datasetList = new ArrayList<Dataset>();

	// Property which occurs more than once
	private List<Experiment> experimentList = new ArrayList<Experiment>();

	// Property which occurs more than once
	private List<Plate> plateList = new ArrayList<Plate>();

	// Property which occurs more than once
	private List<Screen> screenList = new ArrayList<Screen>();

	// Property which occurs more than once
	private List<Experimenter> experimenterList = new ArrayList<Experimenter>();

	// Property which occurs more than once
	private List<Group> groupList = new ArrayList<Group>();

	// Property which occurs more than once
	private List<Instrument> instrumentList = new ArrayList<Instrument>();

	// Property which occurs more than once
	private List<Image> imageList = new ArrayList<Image>();

	// Property
	private StructuredAnnotations structuredAnnotations;

	// Property which occurs more than once
	private List<ROI> roiList = new ArrayList<ROI>();

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
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public OME(Element element) throws EnumerationException
	{
		super(element);
		String tagName = element.getTagName();
		if (!"OME".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of OME got %s",
					tagName));
		}
		// Model object: None
		if (element.hasAttribute("UUID"))
		{
			// Attribute property UUID
			setUUID(String.valueOf(
					element.getAttribute("UUID")));
		}
		// Model object: None
		// Element property Project which is complex (has
		// sub-elements) and occurs more than once
		NodeList Project_nodeList = element.getElementsByTagName("Project");
		for (int i = 0; i < Project_nodeList.getLength(); i++)
		{
			addProject(new Project(
					(Element) Project_nodeList.item(i)));
		}
		// Model object: None
		// Element property Dataset which is complex (has
		// sub-elements) and occurs more than once
		NodeList Dataset_nodeList = element.getElementsByTagName("Dataset");
		for (int i = 0; i < Dataset_nodeList.getLength(); i++)
		{
			addDataset(new Dataset(
					(Element) Dataset_nodeList.item(i)));
		}
		// Model object: None
		// Element property Experiment which is complex (has
		// sub-elements) and occurs more than once
		NodeList Experiment_nodeList = element.getElementsByTagName("Experiment");
		for (int i = 0; i < Experiment_nodeList.getLength(); i++)
		{
			addExperiment(new Experiment(
					(Element) Experiment_nodeList.item(i)));
		}
		// Model object: None
		// Element property Plate which is complex (has
		// sub-elements) and occurs more than once
		NodeList Plate_nodeList = element.getElementsByTagName("Plate");
		for (int i = 0; i < Plate_nodeList.getLength(); i++)
		{
			addPlate(new Plate(
					(Element) Plate_nodeList.item(i)));
		}
		// Model object: None
		// Element property Screen which is complex (has
		// sub-elements) and occurs more than once
		NodeList Screen_nodeList = element.getElementsByTagName("Screen");
		for (int i = 0; i < Screen_nodeList.getLength(); i++)
		{
			addScreen(new Screen(
					(Element) Screen_nodeList.item(i)));
		}
		// Model object: None
		// Element property Experimenter which is complex (has
		// sub-elements) and occurs more than once
		NodeList Experimenter_nodeList = element.getElementsByTagName("Experimenter");
		for (int i = 0; i < Experimenter_nodeList.getLength(); i++)
		{
			addExperimenter(new Experimenter(
					(Element) Experimenter_nodeList.item(i)));
		}
		// Model object: None
		// Element property Group which is complex (has
		// sub-elements) and occurs more than once
		NodeList Group_nodeList = element.getElementsByTagName("Group");
		for (int i = 0; i < Group_nodeList.getLength(); i++)
		{
			addGroup(new Group(
					(Element) Group_nodeList.item(i)));
		}
		// Model object: None
		// Element property Instrument which is complex (has
		// sub-elements) and occurs more than once
		NodeList Instrument_nodeList = element.getElementsByTagName("Instrument");
		for (int i = 0; i < Instrument_nodeList.getLength(); i++)
		{
			addInstrument(new Instrument(
					(Element) Instrument_nodeList.item(i)));
		}
		// Model object: None
		// Element property Image which is complex (has
		// sub-elements) and occurs more than once
		NodeList Image_nodeList = element.getElementsByTagName("Image");
		for (int i = 0; i < Image_nodeList.getLength(); i++)
		{
			addImage(new Image(
					(Element) Image_nodeList.item(i)));
		}
		// Model object: None
		NodeList StructuredAnnotations_nodeList = element.getElementsByTagName("StructuredAnnotations");
		if (StructuredAnnotations_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"StructuredAnnotations node list size %d != 1",
					StructuredAnnotations_nodeList.getLength()));
		}
		else if (StructuredAnnotations_nodeList.getLength() != 0)
		{
			// Element property StructuredAnnotations which is complex (has
			// sub-elements)
			setStructuredAnnotations(new StructuredAnnotations(
					(Element) StructuredAnnotations_nodeList.item(0)));
		}
		// Model object: None
		// Element property ROI which is complex (has
		// sub-elements) and occurs more than once
		NodeList ROI_nodeList = element.getElementsByTagName("ROI");
		for (int i = 0; i < ROI_nodeList.getLength(); i++)
		{
			addROI(new ROI(
					(Element) ROI_nodeList.item(i)));
		}
	}

	// -- OME API methods --

	// Property
	public String getUUID()
	{
		return uuid;
	}

	public void setUUID(String uuid)
	{
		this.uuid = uuid;
	}

	// Property which occurs more than once
	public int sizeOfProjectList()
	{
		return projectList.size();
	}

	public List<Project> copyProjectList()
	{
		return new ArrayList<Project>(projectList);
	}

	public Project getProject(int index)
	{
		return projectList.get(index);
	}

	public Project setProject(int index, Project project)
	{
		return projectList.set(index, project);
	}

	public void addProject(Project project)
	{
		projectList.add(project);
	}

	public void removeProject(Project project)
	{
		projectList.remove(project);
	}

	// Property which occurs more than once
	public int sizeOfDatasetList()
	{
		return datasetList.size();
	}

	public List<Dataset> copyDatasetList()
	{
		return new ArrayList<Dataset>(datasetList);
	}

	public Dataset getDataset(int index)
	{
		return datasetList.get(index);
	}

	public Dataset setDataset(int index, Dataset dataset)
	{
		return datasetList.set(index, dataset);
	}

	public void addDataset(Dataset dataset)
	{
		datasetList.add(dataset);
	}

	public void removeDataset(Dataset dataset)
	{
		datasetList.remove(dataset);
	}

	// Property which occurs more than once
	public int sizeOfExperimentList()
	{
		return experimentList.size();
	}

	public List<Experiment> copyExperimentList()
	{
		return new ArrayList<Experiment>(experimentList);
	}

	public Experiment getExperiment(int index)
	{
		return experimentList.get(index);
	}

	public Experiment setExperiment(int index, Experiment experiment)
	{
		return experimentList.set(index, experiment);
	}

	public void addExperiment(Experiment experiment)
	{
		experimentList.add(experiment);
	}

	public void removeExperiment(Experiment experiment)
	{
		experimentList.remove(experiment);
	}

	// Property which occurs more than once
	public int sizeOfPlateList()
	{
		return plateList.size();
	}

	public List<Plate> copyPlateList()
	{
		return new ArrayList<Plate>(plateList);
	}

	public Plate getPlate(int index)
	{
		return plateList.get(index);
	}

	public Plate setPlate(int index, Plate plate)
	{
		return plateList.set(index, plate);
	}

	public void addPlate(Plate plate)
	{
		plateList.add(plate);
	}

	public void removePlate(Plate plate)
	{
		plateList.remove(plate);
	}

	// Property which occurs more than once
	public int sizeOfScreenList()
	{
		return screenList.size();
	}

	public List<Screen> copyScreenList()
	{
		return new ArrayList<Screen>(screenList);
	}

	public Screen getScreen(int index)
	{
		return screenList.get(index);
	}

	public Screen setScreen(int index, Screen screen)
	{
		return screenList.set(index, screen);
	}

	public void addScreen(Screen screen)
	{
		screenList.add(screen);
	}

	public void removeScreen(Screen screen)
	{
		screenList.remove(screen);
	}

	// Property which occurs more than once
	public int sizeOfExperimenterList()
	{
		return experimenterList.size();
	}

	public List<Experimenter> copyExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenterList);
	}

	public Experimenter getExperimenter(int index)
	{
		return experimenterList.get(index);
	}

	public Experimenter setExperimenter(int index, Experimenter experimenter)
	{
		return experimenterList.set(index, experimenter);
	}

	public void addExperimenter(Experimenter experimenter)
	{
		experimenterList.add(experimenter);
	}

	public void removeExperimenter(Experimenter experimenter)
	{
		experimenterList.remove(experimenter);
	}

	// Property which occurs more than once
	public int sizeOfGroupList()
	{
		return groupList.size();
	}

	public List<Group> copyGroupList()
	{
		return new ArrayList<Group>(groupList);
	}

	public Group getGroup(int index)
	{
		return groupList.get(index);
	}

	public Group setGroup(int index, Group group)
	{
		return groupList.set(index, group);
	}

	public void addGroup(Group group)
	{
		groupList.add(group);
	}

	public void removeGroup(Group group)
	{
		groupList.remove(group);
	}

	// Property which occurs more than once
	public int sizeOfInstrumentList()
	{
		return instrumentList.size();
	}

	public List<Instrument> copyInstrumentList()
	{
		return new ArrayList<Instrument>(instrumentList);
	}

	public Instrument getInstrument(int index)
	{
		return instrumentList.get(index);
	}

	public Instrument setInstrument(int index, Instrument instrument)
	{
		return instrumentList.set(index, instrument);
	}

	public void addInstrument(Instrument instrument)
	{
		instrumentList.add(instrument);
	}

	public void removeInstrument(Instrument instrument)
	{
		instrumentList.remove(instrument);
	}

	// Property which occurs more than once
	public int sizeOfImageList()
	{
		return imageList.size();
	}

	public List<Image> copyImageList()
	{
		return new ArrayList<Image>(imageList);
	}

	public Image getImage(int index)
	{
		return imageList.get(index);
	}

	public Image setImage(int index, Image image)
	{
		return imageList.set(index, image);
	}

	public void addImage(Image image)
	{
		imageList.add(image);
	}

	public void removeImage(Image image)
	{
		imageList.remove(image);
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
		return roiList.size();
	}

	public List<ROI> copyROIList()
	{
		return new ArrayList<ROI>(roiList);
	}

	public ROI getROI(int index)
	{
		return roiList.get(index);
	}

	public ROI setROI(int index, ROI roi)
	{
		return roiList.set(index, roi);
	}

	public void addROI(ROI roi)
	{
		roiList.add(roi);
	}

	public void removeROI(ROI roi)
	{
		roiList.remove(roi);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for OME
		Element OME_element = document.createElement("OME");
		if (uuid != null)
		{
			// Attribute property UUID
			OME_element.setAttribute("UUID", uuid.toString());
		}
		if (projectList != null)
		{
			// Element property Project which is complex (has
			// sub-elements) and occurs more than once
			for (Project projectList_value : projectList)
			{
				OME_element.appendChild(projectList_value.asXMLElement(document));
			}
		}
		if (datasetList != null)
		{
			// Element property Dataset which is complex (has
			// sub-elements) and occurs more than once
			for (Dataset datasetList_value : datasetList)
			{
				OME_element.appendChild(datasetList_value.asXMLElement(document));
			}
		}
		if (experimentList != null)
		{
			// Element property Experiment which is complex (has
			// sub-elements) and occurs more than once
			for (Experiment experimentList_value : experimentList)
			{
				OME_element.appendChild(experimentList_value.asXMLElement(document));
			}
		}
		if (plateList != null)
		{
			// Element property Plate which is complex (has
			// sub-elements) and occurs more than once
			for (Plate plateList_value : plateList)
			{
				OME_element.appendChild(plateList_value.asXMLElement(document));
			}
		}
		if (screenList != null)
		{
			// Element property Screen which is complex (has
			// sub-elements) and occurs more than once
			for (Screen screenList_value : screenList)
			{
				OME_element.appendChild(screenList_value.asXMLElement(document));
			}
		}
		if (experimenterList != null)
		{
			// Element property Experimenter which is complex (has
			// sub-elements) and occurs more than once
			for (Experimenter experimenterList_value : experimenterList)
			{
				OME_element.appendChild(experimenterList_value.asXMLElement(document));
			}
		}
		if (groupList != null)
		{
			// Element property Group which is complex (has
			// sub-elements) and occurs more than once
			for (Group groupList_value : groupList)
			{
				OME_element.appendChild(groupList_value.asXMLElement(document));
			}
		}
		if (instrumentList != null)
		{
			// Element property Instrument which is complex (has
			// sub-elements) and occurs more than once
			for (Instrument instrumentList_value : instrumentList)
			{
				OME_element.appendChild(instrumentList_value.asXMLElement(document));
			}
		}
		if (imageList != null)
		{
			// Element property Image which is complex (has
			// sub-elements) and occurs more than once
			for (Image imageList_value : imageList)
			{
				OME_element.appendChild(imageList_value.asXMLElement(document));
			}
		}
		if (structuredAnnotations != null)
		{
			// Element property StructuredAnnotations which is complex (has
			// sub-elements)
			OME_element.appendChild(structuredAnnotations.asXMLElement(document));
		}
		if (roiList != null)
		{
			// Element property ROI which is complex (has
			// sub-elements) and occurs more than once
			for (ROI roiList_value : roiList)
			{
				OME_element.appendChild(roiList_value.asXMLElement(document));
			}
		}
		return OME_element;
	}
}
