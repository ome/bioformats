/**
 * libxml2 OME-XML writer example -- OME-XML 2008-09 Schema
 * Chris Allan <chris at glencoesoftware dot com>
 *
 * Copyright (c) 2009 Open Microscopy Environment
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

// Requires libxml2-dev package.
// Compile on Linux with:
//   gcc -lxml2 -I/usr/include/libxml2 example.c -o example

#include <stdio.h>
#include <string.h>
#include <libxml/encoding.h>
#include <libxml/xmlwriter.h>

#define DEFAULT_ENCODING "ISO-8859-1"

void
usage(int argc, char* argv[])
{
	printf("%s <output_filename>\n", argv[0]);
	exit(-1);
}


int
main(int argc, char* argv[])
{
	int rc;
	xmlTextWriterPtr writer;
	xmlChar *tmp;
	xmlDocPtr doc;
	const char* file;

	/* Check usage and assign out default filename. */
	if (argc != 2)
	{
		usage(argc, argv);
	}
	file = argv[1];

	/* Create a new XmlWrite for DOM, no compression. */
	writer = xmlNewTextWriterDoc(&doc, 0);
	if (writer == NULL)
	{
		printf("Error creating XML DOM writer.\n");
		return;
	}

	/* Start the document with the xml default for the version,
	 * encoding ISO 8859-1 and the default for the standalone
	 * declaration. */
	rc = xmlTextWriterStartDocument(writer, NULL, DEFAULT_ENCODING, NULL);
	if (rc < 0)
	{
		printf("Error at xmlTextWriterStartDocument.\n");
		return;
	}

	/* Start the OME element. Since this is the first element, this will
	 * be the root element of the document. */
	rc = xmlTextWriterStartElement(writer, BAD_CAST "ome:OME");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterStartElement['ome:OME'].\n");
		return;
	}

	/* Add attributes to the OME element. */
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "xmlns:bf",
			BAD_CAST "http://www.openmicroscopy.org/Schemas/BinaryFile/2008-09");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['xmlns:bf']\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "xmlns:ome",
			BAD_CAST "http://www.openmicroscopy.org/Schemas/OME/2008-09");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['xmlns:ome']\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "xmlns:xsi",
			BAD_CAST "http://www.w3.org/2001/XMLSchema-instance");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['xmlns:xsi']\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "xsi:schemaLocation",
			BAD_CAST "http://www.openmicroscopy.org/Schemas/OME/2008-09 http://www.openmicroscopy.org/Schemas/OME/2008-09/ome.xsd");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['xsi:schemaLocation']\n");
		return;
	}

	/* Start the Image element. */
	rc = xmlTextWriterStartElement(writer, BAD_CAST "ome:Image");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterStartElement['ome:Image'].\n");
		return;
	}

	/* Add attributes to the Image element. */
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "ID",
			BAD_CAST "Image:1");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Image.ID']\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "Name",
			BAD_CAST "Name92");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Image.Name'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "DefaultPixels",
			BAD_CAST "Pixels:1");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Image.DefaultPixels']\n");
		return;
	}

	/* Write the CreationDate element with CDATA. */
	rc = xmlTextWriterWriteFormatElement(writer, BAD_CAST "ome:CreationDate",
			"%s", "2006-05-04T18:13:51.0Z");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterFormatElement['ome:CreationDate'].\n");
		return;
	}

	/* Start the Pixels element. */
	rc = xmlTextWriterStartElement(writer, BAD_CAST "ome:Pixels");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterStartElement['ome:Pixels'].\n");
		return;
	}

	/* Add attributes to the Pixels element. */
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "ID",
			BAD_CAST "Pixels:1");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Pixels.ID'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "DimensionOrder",
			BAD_CAST "XYZCT");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Pixels.DimensionOrder'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "PixelType",
			BAD_CAST "int8");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Pixels.PixelType'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "BigEndian",
			BAD_CAST "false");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Pixels.BigEndian'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "SizeX",
			BAD_CAST "2");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Pixels.SizeX'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "SizeY",
			BAD_CAST "2");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Pixels.SizeY'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "SizeZ",
			BAD_CAST "2");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Pixels.SizeZ'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "SizeC",
			BAD_CAST "2");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Pixels.SizeC'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "SizeT",
			BAD_CAST "2");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['Pixels.SizeT'\n");
		return;
	}

	/* Start the BinData element. */
	rc = xmlTextWriterStartElement(writer, BAD_CAST "bf:BinData");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterStartElement['bf:BinData']\n");
		return;
	}

	/* Add attributes and Base64 CDATA to the BinData element. */
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "Compression",
			BAD_CAST "none");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['BinData.Compression'\n");
		return;
	}
	rc = xmlTextWriterWriteAttribute(writer, BAD_CAST "Length",
			BAD_CAST "10");
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteAttribute['BinData.Length'\n");
		return;
	}
	rc = xmlTextWriterWriteBase64(writer, BAD_CAST "default", 0, 7);
	if (rc < 0)
	{
		printf("Error at xmlTextWriterWriteBase64['BinData']\n");
		return;
	}

	/* Close the Pixels, Image and OME elements using xmlTextWriterEndDocument
	 * instead of calling xmlTextWriterEndElement on each to save some work. */
	rc = xmlTextWriterEndDocument(writer);
	if (rc < 0)
	{
		printf("Error at xmlTextWriterEndDocument.\n");
		return;
	}

	xmlFreeTextWriter(writer);
	xmlSaveFileEnc(file, doc, DEFAULT_ENCODING);
	xmlFreeDoc(doc);
}

