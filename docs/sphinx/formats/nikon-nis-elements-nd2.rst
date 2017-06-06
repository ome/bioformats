.. index:: Nikon NIS-Elements ND2
.. index:: .nd2

Nikon NIS-Elements ND2
===============================================================================

Extensions: .nd2

Developer: `Nikon USA <http://www.nikonusa.com/>`_


**Support**


BSD-licensed: |no|

Export: |no|

Officially Supported Versions: 

Readers:

- NativeND2Reader (:bfreader:`Source Code <NativeND2Reader.java>`, :doc:`Supported Metadata Fields </metadata/NativeND2Reader>`)
- LegacyND2Reader (:bfreader:`Source Code <LegacyND2Reader.java>`, :doc:`Supported Metadata Fields </metadata/LegacyND2Reader>`)


Freely Available Software:

- `NIS-Elements Viewer from Nikon <http://www.nikoninstruments.com/Products/Software/NIS-Elements-Advanced-Research/NIS-Elements-Viewer>`_


We currently have:

* many ND2 datasets

We would like to have:

* an official specification document

**Ratings**


Pixels: |Very good|

Metadata: |Fair|

Openness: |Fair|

Presence: |Very good|

Utility: |Very good|

**Additional Information**


Additional options are available for reading or writing this format type, see
:doc:`options` for information.

There are two distinct versions of ND2: an old version, which uses 
JPEG-2000 compression, and a new version which is either uncompressed or 
Zip-compressed.  We are not aware of the version number or release date 
for either format. 

Bio-Formats uses the `JAI Image I/O Tools <http://java.net/projects/jai-imageio>`_ 
library to read ND2 files compressed with JPEG-2000. 

There is also a **legacy** ND2 reader that uses Nikon's native libraries. 
To use it, you must be using Windows 32-bit and have `Nikon's ND2 reader plugin for ImageJ 
<http://rsb.info.nih.gov/ij/plugins/nd2-reader.html>`_ installed. 
Additionally, you will need to download :source:`LegacyND2Reader.dll 
<lib/LegacyND2Reader.dll?raw=true>` 
and place it in your ImageJ plugin folder. 
Note that this reader is **unmaintained** and no additional support effort 
will be made.
