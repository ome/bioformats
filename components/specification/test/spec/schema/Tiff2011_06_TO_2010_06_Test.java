/*
 * $Id$
 *
 *   Copyright 2006-2012 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */
package spec.schema;

//Java imports
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

//Third-party libraries
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.springframework.util.ResourceUtils;
import org.apache.commons.io.FileUtils;

// other OME libraries
import java.io.*;
import java.util.Hashtable;
import java.io.BufferedWriter;
import loci.common.RandomAccessInputStream;
import loci.formats.tiff.*;

//Application-internal dependencies
import spec.AbstractTest;
import spec.XMLMockObjects;
import spec.XMLWriter;

/** 
 * Checks if the downgrade of a tiff from 2011-06 schema to 2010-06 schema works.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
public class Tiff2011_06_TO_2010_06_Test 
	extends AbstractTest
{

	/** The collection of files that have to be deleted. */
	private List<File> files;
	
	/** The transform */
	private InputStream STYLESHEET;
	
	/** The target schema */
	private StreamSource[] schemaArray;
	
	/**
	 * Checks if the <code>Image</code> tag was correctly transformed.
	 * 
	 * @param destNode The node from the transformed file.
	 * @param srcNode The Image node from the source file
	 */
	private void checkImageNode(Node destNode, Node srcNode)
	{
		assertNotNull(destNode);
		assertNotNull(srcNode);
		NamedNodeMap attributesSrc = srcNode.getAttributes();
		String nameSrc = "";
		String idSrc = "";
		Node n;
		String name;
		for (int i = 0; i < attributesSrc.getLength(); i++) {
			n = attributesSrc.item(i);
			name = n.getNodeName();
			if (name.equals(XMLWriter.ID_ATTRIBUTE))
				idSrc = n.getNodeValue();
			else if (name.equals(XMLWriter.NAME_ATTRIBUTE))
				nameSrc = n.getNodeValue();
		}
		
		NamedNodeMap attributes = destNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			n = attributes.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.equals(XMLWriter.ID_ATTRIBUTE))
					assertEquals(n.getNodeValue(), idSrc);
				else if (name.equals(XMLWriter.NAME_ATTRIBUTE))
					assertEquals(n.getNodeValue(), nameSrc);
			}
		}
		Node pixelsNode = null;
		NodeList list = srcNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			n = list.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.contains(XMLWriter.PIXELS_TAG))
					pixelsNode = n;
			}
		}
		list = destNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			n = list.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.contains(XMLWriter.PIXELS_TAG))
					checkPixelsNode(n, pixelsNode);
			}
		}
	}
	
	/**
	 * Checks if the <code>Pixels</code> tag was correctly transformed.
	 * 
	 * @param destNode The node from the transformed file.
	 * @param srcNode The Image node from the source file
	 */
	private void checkPixelsNode(Node destNode, Node srcNode)
	{
		NamedNodeMap attributesSrc = srcNode.getAttributes();
		String nameSrc = "";
		String idSrc = "";
		String sizeX = "";
		String sizeY = "";
		String sizeZ = "";
		String sizeC = "";
		String sizeT = "";
		String pixelsType = "";
		String dimensionOrder = "";
		String bigEndian = "";
		Node n;
		String name;
		for (int i = 0; i < attributesSrc.getLength(); i++) {
			n = attributesSrc.item(i);
			name = n.getNodeName();
			if (name.equals(XMLWriter.ID_ATTRIBUTE))
				idSrc = n.getNodeValue();
			else if (name.equals(XMLWriter.NAME_ATTRIBUTE))
				nameSrc = n.getNodeValue();
			else if (name.equals(XMLWriter.SIZE_X_ATTRIBUTE))
				sizeX = n.getNodeValue();
			else if (name.equals(XMLWriter.SIZE_Y_ATTRIBUTE))
				sizeY = n.getNodeValue();
			else if (name.equals(XMLWriter.SIZE_Z_ATTRIBUTE))
				sizeZ = n.getNodeValue();
			else if (name.equals(XMLWriter.SIZE_C_ATTRIBUTE))
				sizeC = n.getNodeValue();
			else if (name.equals(XMLWriter.SIZE_T_ATTRIBUTE))
				sizeT = n.getNodeValue();
			else if (name.equals(XMLWriter.PIXELS_TYPE_ATTRIBUTE))
				pixelsType = n.getNodeValue();
			else if (name.equals(XMLWriter.DIMENSION_ORDER_ATTRIBUTE))
				dimensionOrder = n.getNodeValue();
			else if (name.equals(XMLWriter.BIG_ENDIAN_ATTRIBUTE))
				bigEndian = n.getNodeValue();
		}
		NamedNodeMap attributes = destNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			n = attributes.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.equals(XMLWriter.ID_ATTRIBUTE))
					assertEquals(n.getNodeValue(), idSrc);
				else if (name.equals(XMLWriter.NAME_ATTRIBUTE))
					assertEquals(n.getNodeValue(), nameSrc);
				else if (name.equals(XMLWriter.SIZE_X_ATTRIBUTE))
					assertEquals(n.getNodeValue(), sizeX);
				else if (name.equals(XMLWriter.SIZE_Y_ATTRIBUTE))
					assertEquals(n.getNodeValue(), sizeY);
				else if (name.equals(XMLWriter.SIZE_Z_ATTRIBUTE))
					assertEquals(n.getNodeValue(), sizeZ);
				else if (name.equals(XMLWriter.SIZE_C_ATTRIBUTE))
					assertEquals(n.getNodeValue(), sizeC);
				else if (name.equals(XMLWriter.SIZE_T_ATTRIBUTE))
					assertEquals(n.getNodeValue(), sizeT);
// TODO - Review
//				else if (name.equals(XMLWriter.PIXELS_TYPE_ATTRIBUTE))
//					assertEquals(n.getNodeValue(), pixelsType);
				else if (name.equals(XMLWriter.DIMENSION_ORDER_ATTRIBUTE))
					assertEquals(n.getNodeValue(), dimensionOrder);
				else if (name.equals(XMLWriter.BIG_ENDIAN_ATTRIBUTE))
					assertEquals(n.getNodeValue(), bigEndian);
			}
		}
		//check the tag now.
		NodeList list = srcNode.getChildNodes();
		List<Node> binDataNodeSrc = new ArrayList<Node>();
		for (int i = 0; i < list.getLength(); i++) {
			n = list.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.contains(XMLWriter.BIN_DATA_TAG))
					binDataNodeSrc.add(n);
			}
		}
		list = destNode.getChildNodes();
		List<Node> binDataNodeDest = new ArrayList<Node>();
		for (int i = 0; i < list.getLength(); i++) {
			n = list.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.contains(XMLWriter.BIN_DATA_TAG))
					binDataNodeDest.add(n);
			}
		}
		assertTrue(binDataNodeSrc.size() > 0);
		assertEquals(binDataNodeSrc.size(), binDataNodeDest.size());
		for (int i = 0; i < binDataNodeDest.size(); i++) {
			checkBinDataNode(binDataNodeDest.get(i), binDataNodeSrc.get(i));
		}
		n = binDataNodeSrc.get(0);
		attributesSrc = n.getAttributes();
		//now check that 
		for (int i = 0; i < attributesSrc.getLength(); i++) {
			n = attributesSrc.item(i);
			if (n != null) {
				name = n.getNodeName();
//				if (name.contains(XMLWriter.BIG_ENDIAN_ATTRIBUTE))
//					assertEquals(n.getNodeValue(), bigEndianDst);
			}
		}
	}
	
	/**
	 * Checks if the <code>Image</code> tag was correctly transformed.
	 * 
	 * @param destNode The node from the transformed file.
	 * @param srcNode The Image node from the source file
	 */
	private void checkBinDataNode(Node destNode, Node srcNode)
	{
		assertNotNull(destNode);
		assertNotNull(srcNode);
		NamedNodeMap attributesSrc = srcNode.getAttributes();
		String compression = "";
		Node n;
		String name;
		for (int i = 0; i < attributesSrc.getLength(); i++) {
			n = attributesSrc.item(i);
			name = n.getNodeName();
			if (name.equals(XMLWriter.COMPRESSION_ATTRIBUTE))
				compression = n.getNodeValue();
		}
		
		NamedNodeMap attributes = destNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			n = attributes.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.equals(XMLWriter.COMPRESSION_ATTRIBUTE))
					assertEquals(n.getNodeValue(), compression);
			}
		}
		assertEquals(destNode.getTextContent(), srcNode.getTextContent());
	}
	
	/**
	 * Overridden to initialize the list.
	 * @see AbstractTest#setUp()
	 */
    @Override
    @BeforeClass
    protected void setUp() 
    	throws Exception
    {
    	super.setUp();

		/** The target schema file */
//		SCHEMA_2010_06 = this.getClass().getResourceAsStream("/2010-06/V1/ome.xsd");
		//components/specification/Released-Schema/2003-FC/V2/

		schemaArray = new StreamSource[6];

		schemaArray[0] = new StreamSource(this.getClass().getResourceAsStream("/Released-Schema/2010-06/V1/ome.xsd"));
		schemaArray[1] = new StreamSource(this.getClass().getResourceAsStream("/Released-Schema/2010-06/V1/SPW.xsd"));
		schemaArray[2] = new StreamSource(this.getClass().getResourceAsStream("/Released-Schema/2010-06/V1/SA.xsd"));
		schemaArray[3] = new StreamSource(this.getClass().getResourceAsStream("/Released-Schema/2010-06/V1/ROI.xsd"));
		schemaArray[4] = new StreamSource(this.getClass().getResourceAsStream("/Released-Schema/2010-06/V1/BinaryFile.xsd"));
		schemaArray[5] = new StreamSource(this.getClass().getResourceAsStream("/Released-Schema/2010-06/V1/OMERO.xsd"));

		/** The transform file */
		STYLESHEET = this.getClass().getResourceAsStream("/Xslt/2011-06-to-2010-06.xsl");
		//components/specification/Xslt/
		
    	files = new ArrayList<File>();
    }
    
	/**
	 * Overridden to delete the files.
	 * @see AbstractTest#tearDown()
	 */
    @Override
    @AfterClass
    public void tearDown() 
    	throws Exception
    {
    	Iterator<File> i = files.iterator();
    	while (i.hasNext()) {
			i.next().delete();
		}
    	files.clear();
    }
	
	/**
     * Tests the XSLT used to downgrade from schema 2011-06 to 2010-06.
     * An XML file with an image is created and the stylesheet is applied.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(enabled = true)
	public void testDowngradeTiffTo201006ImageNoMetadata()
		throws Exception
	{

// ###################
		String theSourceFilename = "/Users/andrew/Work/ome/components/specification/Samples/OmeFiles/2011-06/LAMBDA-modulo-sample.ome.tiff";
		String theTargetFilename = "/Users/andrew/Work/ome/test-out-LAMBDA-modulo-sample.ome.tiff";
// ###################

		// read comment from file
		String theSourceComment = new TiffParser(theSourceFilename).getComment();

		// Copy the file so we do not modify the origional
		File theSourceFile = new File(theSourceFilename);
		File theTargetFile = new File(theTargetFilename);
		FileUtils.copyFile(theSourceFile, theTargetFile);

		// Create temp files to transform comment in
		File theSourceXmlFile = File.createTempFile(
			"testDowngradeTiffTo201006XmlBefore",
			"."+OME_XML_FORMAT);
		files.add(theSourceXmlFile);
		File theTargetXmlFile = File.createTempFile(
			"testDowngradeTiffTo201006XmlAfter",
			"."+OME_XML_FORMAT);
		files.add(theTargetXmlFile);

		// Write the xml comment to the temp file, transform, and read back
		FileUtils.writeStringToFile(theSourceXmlFile, theSourceComment);
		transformFileWithStream(theSourceXmlFile, theTargetXmlFile, STYLESHEET);
		String theNewComment = FileUtils.readFileToString(theTargetXmlFile);

		// save results back to the TIFF file
		TiffSaver saver = new TiffSaver(theTargetFilename);
		RandomAccessInputStream in = new RandomAccessInputStream(theTargetFilename);
		saver.overwriteComment(in, theNewComment);
		in.close();

		// We now have a downgraded tiff
	}
}
