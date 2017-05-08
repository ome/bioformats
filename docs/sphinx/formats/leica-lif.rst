.. index:: Leica LAS AF LIF (Leica Image File Format)
.. index:: .lif

Leica LAS AF LIF (Leica Image File Format)
===============================================================================

Extensions: .lif

Developer: `Leica Microsystems CMS GmbH <http://www.leica-microsystems.com/>`_

Owner: `Leica <http://www.leica.com/>`_

**Support**


BSD-licensed: |no|

Export: |no|

Officially Supported Versions: 1.0, 2.0

Reader: LIFReader (:bfreader:`Source Code <LIFReader.java>`, :doc:`Supported Metadata Fields </metadata/LIFReader>`)


Freely Available Software:

- `Leica LAS AF Lite <http://www.leica-microsystems.com/products/microscope-software/software-for-life-science-research/las-x/>`_ (links at bottom of page)


We currently have:

* a LIF/XLLF/XLEF/LOF specification document (version 3.2, from no later than 2016 December 15, in PDF) 
* a LIF specification document (version 2, from no later than 2007 July 26, in PDF) 
* a LIF specification document (version 1, from no later than 2006 April 3, in PDF) 
* numerous LIF datasets 
* `public sample images <http://downloads.openmicroscopy.org/images/Leica-LIF/>`__

We would like to have:


**Ratings**


Pixels: |Outstanding|

Metadata: |Very good|

Openness: |Very good|

Presence: |Good|

Utility: |Very good|

**Additional Information**

**Please note that while we have specification documents for this
format, we are not able to distribute them to third parties.**

Additional options are available for reading or writing this format type, see
:doc:`options` for information.

LAS stands for "Leica Application Suite". 
AF stands for "Advanced Fluorescence". 

Commercial applications that support LIF include: 

* `Bitplane Imaris <http://www.bitplane.com/>`_ 
* `SVI Huygens <http://svi.nl/>`_ 
* `Amira <http://www.amira.com/>`_ 

Versions of Bio-Formats prior to 5.3.3 incorrectly calculated the physical 
pixel width and height.  The physical image width and height were divided by 
the number of pixels, which was inconsistent with the official Leica LIF 
specification documents.  Versions 5.3.3 and later correctly calculate 
physical pixel sizes by dividing the physical image size by the number of 
pixels minus one.  To revert to the old method of physical pixel size 
calculation in 5.3.3 and later, set the ``leicalif.old_physical_size`` option 
to ``true`` as described in :doc:`options`.
