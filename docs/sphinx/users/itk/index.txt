ITK
===

The `Insight Toolkit <http://itk.org/>`_ (ITK) is an open-source,
cross-platform system that provides developers with an extensive suite
of software tools for image analysis. Developed through extreme
programming methodologies, ITK employs leading-edge algorithms for
registering and segmenting multidimensional data.

ITK provides an ImageIO plug-in structure that works via discovery through
a dependency injection scheme. This allows a program built on ITK to
load plug-ins for reading and writing different image types without
actually linking to the ImageIO libraries required for those types. Such
encapsulation automatically grants two major boons: firstly, programs
can be easily extended just by virtue of using ITK (developers do not
have to specifically accommodate or anticipate what plug-ins may be
used). Secondly, the architecture provides a distribution method for
open source software, like Bio-Formats, which have licenses that might
otherwise exclude them from being used with other software suites.

The `SCIFIO ImageIO <https://github.com/scifio/scifio-imageio>`_ plugin
provides an ITK imageIO base that uses Bio-Formats to read and
write supported life sciences file formats. This plugin allows any program
built on ITK to read any of the image types supported by Bio-Formats.

