Converting files from FV1000 OIB/OIF to OME-TIFF
================================================

This document explains how to convert a file from FV1000 OIB/OIF to OME-TIFF
using Bio-Formats version 4.2 and later.

Working example code is provided in
:download:`FileConvert.java <examples/FileConvert.java>` - code from that
class is referenced here in part. You will need to have
**bioformats_package.jar** in your Java CLASSPATH in order to compile
FileConvert.java.

The first thing that must happen is we must create the object that stores
OME-XML metadata.  This is done as follows:

::

      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      IMetadata omexml = service.createOMEXMLMetadata();

The 'omexml' object can now be used by both a file format reader and a file
format writer for storing and retrieving OME-XML metadata.

Now that have somewhere to put metadata, we need to create a file reader and
writer:

::

      ImageReader reader = new ImageReader();
      ImageWriter writer = new ImageWriter();

Now we must associate the 'omexml' object with the file reader and writer:

::

      reader.setMetadataStore(omexml);
      writer.setMetadataRetrieve(omexml);

The reader now knows to store all of the metadata that it parses into
'omexml', and the writer knows to retrieve any metadata that it needs from
'omexml'.

We now tell the reader and writer which files will be read from and written
to, respectively:

::

      reader.setId("input-file.oib");
      writer.setId("output-file.ome.tiff");

It is critical that the file name given to the writer ends with ".ome.tiff" or
".ome.tif", as it is the file name extension that determines which format will
be written.

Now that everything is set up, we can convert the image data.  This is done
plane by plane:

::

       for (int series=0; series<reader.getSeriesCount(); series++) {
         reader.setSeries(series);
         writer.setSeries(series);

         byte[] plane = new byte[FormatTools.getPlaneSize(reader)];
         for (int image=0; image<reader.getImageCount(); image++) {
           reader.openBytes(image, plane);
           writer.saveBytes(image, plane);
         }
       }


The body of the outer 'for' loop may also be replaced with the following:

::

       reader.setSeries(series);
       writer.setSeries(series);

       for (int image=0; image<reader.getImageCount(); image++) {
         byte[] plane = reader.openBytes(image);
         writer.saveBytes(image, plane);
       }


But note that this will be a little slower.

Finally, we must tell the reader and writer that we are finished, so that the
input and output files can be properly closed:

::

      reader.close();
      writer.close();

There should now be a complete OME-TIFF file at whichever path was specified
above.
