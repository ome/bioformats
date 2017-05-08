Overview for developers
=======================

From the rest of the Bio-Formats developer documentation one may piece
together a correct and useful understanding of what Bio-Formats does and
how it does it. This section gives a high-level tour of these technical
details, for those new to working on Bio-Formats itself, making it
easier to understand how the information from the other sections fits
into the big picture.


Terms and concepts
------------------

Bio-Formats can read image data from files for many formats, and can
write image data to files for some formats. An image may have many
two-dimensional "planes" of pixel intensity values. Each pixel on a
plane is identified by its *x*, *y* values. Planes within an image may
be identified by various dimensions including *z* (third spatial
dimension), *c* (channel, e.g. wavelength) or *t* (time). Planes may be
divided into tiles, which are rectangular subsections of a plane; this
is helpful in handling very large planes. A file (or set of related
files) on disk may contain multiple images: each image is identified by
a unique *series* number.

An image is more than a set of planes: it also has metadata. Bio-Formats
distinguishes *core metadata*, such as the x, y, z, c, t dimensions of
the image, from format-specific *original metadata*, e.g. information
about the microscope and its settings, which is represented as a
dictionary of values indexed by unique keys. Metadata apply to the image
data as a whole, or separately to specific series within it.

Bio-Formats is able to translate the above metadata into a further form,
*OME metadata*. The translation may be partial or incomplete, but
remains very useful for allowing the metadata of images from different
file formats to be used and compared in a common format defined by the
OME data model.


Implementation
--------------

Bio-Formats is primarily a Java project. It can be used from MATLAB and
there is also a separate C++ implementation (OME Files C++). The
source code is available for download and sometimes the user community
contributes code back into Bio-Formats by opening a pull request on
GitHub. Bio-Formats is built from source with Ant or Maven and some of
the Bio-Formats source code is generated from other files during the
build process. The resulting JARs corresponding to official Bio-Formats
releases are available for download.

Readers and writers for different image file formats are implemented in
separate Java classes. Readers for related formats may reflect that
relationship in the Java class hierarchy. Simple standalone command-line
tools are provided with Bio-Formats, but it is more commonly used as a
third-party library by other applications. Various examples show how one
may use Bio-Formats in different ways in writing a new application that
reads or writes image data. A common pattern is to initialize a reader
based on the image data's primary file, then query that reader for the
metadata and planes of interest.

The set of readers is easily modified. The :source:`readers.txt
<components/formats-api/src/loci/formats/readers.txt>` file lists the
readers to try in determining an image file's format, and there are many
useful classes and methods among the Bio-Formats Java code to assist in
writing new readers and writers.
