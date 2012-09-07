Structure of the samples:

Each Schema's samples are in a folder named after the Schema's date.

e.g. folder 2010-04 has
http://www.openmicroscopy.org/Schemas/OME/2010-04

There are two sets of samples that were designed to be small enough to examine in detail:

18x24y*-text* are 18 be 24 pixel images, with black and white text on each plane giving its time, z-depth and channel. e.g. C1 Z1 T1

6x4y*-swatch are 6 by 4 pixel images, that have a black and white pattern in the first 2 x-columns, followed by a grey scale pattern in the next 4 columns

The File names contain:
x size, y size, z depth, t timepoints, c channels, and b bitdepth

A file ending in .ome is the OME-XML version
A file ending in .ome-v-Match.ome.tiff is the OME-TIFF version that matches the OME-XML version (normally a hand made example)
A file ending in .ome-v2010-06.ome.tiff is the OME-TIFF version that has been updated to the 2010-06 schema by the Bio-Formats bfconvert tool.


Other files:
The files called one-of-everthing*.xml are not real data and are only a test for an XML parser. The contain one of every element and attribute but the data values are random.
