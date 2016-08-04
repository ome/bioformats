/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
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
 * #L%
 */

package spec.schema;

//Java imports
import java.io.File;
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

//Application-internal dependencies
import spec.AbstractTest;
import ome.specification.OmeValidator;
import ome.specification.XMLMockObjects;
import ome.specification.XMLWriter;

import org.testng.Assert;

/**
 * Collections of tests.
 * Checks if the downgrade from current schema to 2010-06 schema works.
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
public class SchemaCurrent_TO_2010_06_Test
	extends AbstractTest
{

	/** The collection of files that have to be deleted. */
	private List<File> files;

	/** The transform */
	private InputStream STYLESHEET_A;
	private InputStream STYLESHEET_B;

	/** The target schema */
	private StreamSource[] schemaArray;

	/** A validator used to check transformed files */
	private OmeValidator anOmeValidator = new OmeValidator();

	/**
	 * Checks if the <code>Image</code> tag was correctly transformed.
	 *
	 * @param destNode The node from the transformed file.
	 * @param srcNode The Image node from the source file
	 */
	private void checkImageNode(Node destNode, Node srcNode)
	{
		Assert.assertNotNull(destNode);
		Assert.assertNotNull(srcNode);
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

		// compare the stored values for ID and Name attributes
		// to those on the output node
		NamedNodeMap attributes = destNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			n = attributes.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.equals(XMLWriter.ID_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), idSrc);
				else if (name.equals(XMLWriter.NAME_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), nameSrc);
			}
		}

		// Find the pixels node in the input image node
		// (if more than one last will be found)
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
		// Find the pixels node in the output image node
		// (if more than one this will be incorrect)
		list = destNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			n = list.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.contains(XMLWriter.PIXELS_TAG))
					//  - compare the found node to the input node stored above
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
		// store the values for ID and Name attribute on the input node
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
					Assert.assertEquals(n.getNodeValue(), idSrc);
				else if (name.equals(XMLWriter.NAME_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), nameSrc);
				else if (name.equals(XMLWriter.SIZE_X_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), sizeX);
				else if (name.equals(XMLWriter.SIZE_Y_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), sizeY);
				else if (name.equals(XMLWriter.SIZE_Z_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), sizeZ);
				else if (name.equals(XMLWriter.SIZE_C_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), sizeC);
				else if (name.equals(XMLWriter.SIZE_T_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), sizeT);
// TODO - Review
//				else if (name.equals(XMLWriter.PIXELS_TYPE_ATTRIBUTE))
//					Assert.assertEquals(n.getNodeValue(), pixelsType);
				else if (name.equals(XMLWriter.DIMENSION_ORDER_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), dimensionOrder);
				else if (name.equals(XMLWriter.BIG_ENDIAN_ATTRIBUTE))
					Assert.assertEquals(n.getNodeValue(), bigEndian);
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
					//  - Add node to the input list
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
					//  - Add node to the output list
					binDataNodeDest.add(n);
			}
		}
        // Compare the lengths of the lists
		Assert.assertNotNull(binDataNodeSrc);
		Assert.assertNotEquals(binDataNodeSrc.size(), 0);
		Assert.assertTrue(binDataNodeSrc.size() > 0);
		Assert.assertEquals(binDataNodeSrc.size(), binDataNodeDest.size());
		// Compare the contents of the lists
		for (int i = 0; i < binDataNodeDest.size(); i++) {
			checkBinDataNode(binDataNodeDest.get(i), binDataNodeSrc.get(i));
		}
		// Compare the Big Endian value from the output stored above
		// with the value used in the input file
		n = binDataNodeSrc.get(0);
		attributesSrc = n.getAttributes();
		//now check that
		for (int i = 0; i < attributesSrc.getLength(); i++) {
			n = attributesSrc.item(i);
			if (n != null) {
				name = n.getNodeName();
//				if (name.contains(XMLWriter.BIG_ENDIAN_ATTRIBUTE))
//					Assert.assertEquals(n.getNodeValue(), bigEndianDst);
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
		Assert.assertNotNull(destNode);
		Assert.assertNotNull(srcNode);

		String compression = "";
		Node n;
		String name;

		// store the values for Compression attribute on the input node
		NamedNodeMap attributesSrc = srcNode.getAttributes();
		Assert.assertNotNull(attributesSrc);

		for (int i = 0; i < attributesSrc.getLength(); i++) {
			n = attributesSrc.item(i);
			name = n.getNodeName();
			if (name.equals(XMLWriter.COMPRESSION_ATTRIBUTE))
				compression = n.getNodeValue();
		}

		// compare the stored value for the Compression attribute
		// to that on the output node
		NamedNodeMap attributes = destNode.getAttributes();
		Assert.assertNotNull(attributes);

		for (int i = 0; i < attributes.getLength(); i++) {
			n = attributes.item(i);
			if (n != null) {
				name = n.getNodeName();
				if (name.equals(XMLWriter.COMPRESSION_ATTRIBUTE)) {
					Assert.assertEquals(n.getNodeValue(), compression);
				}
			}
		}
		// compare the contents of the BinData node
		Assert.assertEquals(destNode.getTextContent(), srcNode.getTextContent());
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
		schemaArray = new StreamSource[6];
		schemaArray[0] = new StreamSource(this.getClass().getResourceAsStream("/released-schema/2010-06/ome.xsd"));
		schemaArray[1] = new StreamSource(this.getClass().getResourceAsStream("/released-schema/2010-06/SPW.xsd"));
		schemaArray[2] = new StreamSource(this.getClass().getResourceAsStream("/released-schema/2010-06/SA.xsd"));
		schemaArray[3] = new StreamSource(this.getClass().getResourceAsStream("/released-schema/2010-06/ROI.xsd"));
		schemaArray[4] = new StreamSource(this.getClass().getResourceAsStream("/released-schema/2010-06/BinaryFile.xsd"));
		schemaArray[5] = new StreamSource(this.getClass().getResourceAsStream("/released-schema/2010-06/OMERO.xsd"));
		//components/specification/released-schema/2010-06/

		/** The transform file */
		STYLESHEET_A = this.getClass().getResourceAsStream("/transforms/2012-06-to-2011-06.xsl");
		STYLESHEET_B = this.getClass().getResourceAsStream("/transforms/2011-06-to-2010-06.xsl");
		//components/specification/transforms/

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
     * Tests the XSLT used to downgrade from current schema to 2010-06.
     * An XML file with an image is created and the stylesheet is applied.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(enabled = false)
	public void testDowngradeTo201006ImageNoMetadata()
		throws Exception
	{
		File inFile = File.createTempFile("testDowngradeTo201006ImageNoMetadata",
				"."+OME_XML_FORMAT);
		files.add(inFile);
		File middleFileA = File.createTempFile("testDowngradeTo201006ImageNoMetadataMiddleA",
				"."+OME_XML_FORMAT);
		files.add(middleFileA);
		File outputFile = File.createTempFile(
				"testDowngradeTo201006ImageNoMetadataOutput",
				"."+OME_XML_FORMAT);
		files.add(outputFile);
		XMLMockObjects xml = new  XMLMockObjects();
		XMLWriter writer = new XMLWriter();
		writer.writeFile(inFile, xml.createImage(), true);

// Dump out file for debuging
//		File fCheckIn  = new File("/Users/andrew/Desktop/wibble1.xml");
//		writer.writeFile(fCheckIn , xml.getRoot(), true);
		transformFileWithStream(inFile, middleFileA, STYLESHEET_A);
		transformFileWithStream(middleFileA, outputFile, STYLESHEET_B);

		Document doc = anOmeValidator.parseFileWithStreamArray(outputFile, schemaArray);
		Assert.assertNotNull(doc);

		//Should only have one root node i.e. OME node
		NodeList list = doc.getChildNodes();
		Assert.assertEquals(list.getLength(), 1);
		Node root = list.item(0);
		Assert.assertEquals(root.getNodeName(), XMLWriter.OME_TAG);
		//now analyse the root node
		list = root.getChildNodes();
		String name;
		Node n;
		Document docSrc = anOmeValidator.parseFile(inFile, null);
		Node rootSrc = docSrc.getChildNodes().item(0);
		Node imageNode = null;
		NodeList listSrc = rootSrc.getChildNodes();
		for (int i = 0; i < listSrc.getLength(); i++) {
			n = listSrc.item(i);
			name = n.getNodeName();
			if (name != null) {
				if (name.contains(XMLWriter.IMAGE_TAG))
					imageNode = n;
			}
		}

		for (int i = 0; i < list.getLength(); i++) {
			n = list.item(i);
			name = n.getNodeName();
			if (name != null) {
				//TODO: add other node
				if (name.contains(XMLWriter.IMAGE_TAG) && imageNode != null)
					checkImageNode(n, imageNode);
			}
		}
	}
}
