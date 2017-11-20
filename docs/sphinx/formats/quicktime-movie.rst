.. index:: QuickTime Movie
.. index:: .mov

QuickTime Movie
===============================================================================

Extensions: .mov


Owner: `Apple Computer <http://www.apple.com/>`_

**Support**


BSD-licensed: |yes|

Export: |yes|

Officially Supported Versions: 

Readers:

- NativeQTReader (:bsd-reader:`Source Code <NativeQTReader.java>`, :doc:`Supported Metadata Fields </metadata/NativeQTReader>`)
- LegacyQTReader (:bsd-reader:`Source Code <LegacyQTReader.java>`, :doc:`Supported Metadata Fields </metadata/LegacyQTReader>`)

Writer: QTWriter (:bsd-writer:`Source Code <QTWriter.java>`)

Freely Available Software:

- `QuickTime Player <https://support.apple.com/downloads/quicktime>`_


We currently have:

* a `QuickTime specification document <https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFPreface/qtffPreface.html#//apple_ref/doc/uid/TP40000939>`_ 
* several QuickTime datasets 
* the ability to produce more datasets

We would like to have:

* more QuickTime datasets, including: 

  * files compressed with a common, unsupported codec 
  * files with audio tracks and/or multiple video tracks

**Ratings**


Pixels: |Good|

Metadata: |Fair|

Openness: |Fair|

Presence: |Outstanding|

Utility: |Poor|

**Additional Information**


Bio-Formats has two modes of operation for QuickTime: 

* The legacy QTJava mode requires QuickTime for Java which will only run 
  with a 32-bit JVM and is no longer available from Apple. 
* Native mode works on systems with no QuickTime (e.g. Linux). 

Bio-Formats can save image stacks as QuickTime movies. 
The following table shows supported codecs: 

====== ================================== =================== ============ 
Codec  Description                        Native              LegacyQTJava 
====== ================================== =================== ============ 
raw    Full Frames (Uncompressed)         read & write        read & write 
iraw   Intel YUV Uncompressed             read only           read & write 
rle    Animation (run length encoded RGB) read only           read & write 
jpeg   Still Image JPEG DIB               read only           read only 
rpza   Apple Video 16 bit "road pizza"    read only (partial) read only 
mjpb   Motion JPEG codec                  read only           read only 
cvid   Cinepak                             -                  read & write 
svq1   Sorenson Video                      -                  read & write 
svq3   Sorenson Video 3                    -                  read & write 
mp4v   MPEG-4                              -                  read & write 
h263   H.263                               -                  read & write 
====== ================================== =================== ============ 

.. seealso:: 
    `QuickTime software overview <https://support.apple.com/quicktime>`_
