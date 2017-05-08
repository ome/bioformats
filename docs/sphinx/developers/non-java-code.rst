Interfacing with Bio-Formats from non-Java code
===============================================

Bio-Formats is written in Java, and is easiest to use with other Java
code. However, it is possible to call Bio-Formats from a program written
in another language. But how to do so depends on your program's needs.

Technologically, there are two broad categories of solutions:
**in-process** approaches, and **inter-process** communication.

For details, see LOCI's article
`Interfacing from non-Java code
<http://loci.wisc.edu/software/interfacing-non-java-code>`_.

Example **in-process solution**: `Bio-Formats JACE C++ bindings <https://github.com/ome/bio-formats-jace>`_
(note that this is a legacy project and no longer actively maintained).

