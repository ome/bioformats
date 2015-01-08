Code generation with xsd-fu
===========================

:program:`xsd-fu` is a Python application designed to digest OME XML
schema and produce an object-oriented Java infrastructure to ease work
with an XML DOM tree.  It is usually run automatically when building
from source (see :ref:`source-building`) and so running it by hand
should not be needed.  :program:`xsd-fu` is primarily used to generate
the OME-XML model objects, enums and enum handlers, plus the
MetadataStore and MetadataRetrieve interfaces and implementations.

Available options
-----------------

.. cmdoption:: -d, --dry-run

  Run all source generation processing, but don't write output files.
  In combination with :option:`--print-depends` or
  :option:`--print-generated`, this option may be used to dynamically
  introspect command dependencies and output to create build rules on
  the fly for e.g. :program:`cmake`.

.. cmdoption:: --debug

  Enable xsd-fu debugging messages and template debugging.  The code
  templates contain diagnostic messages to debug the template
  processing, which are normally suppressed in the code output;
  enabling debugging will add these diagnostic messages to the
  generated code.

.. cmdoption:: -l language, --language=language

  Generate code for the specified language.  Currently supported
  options are `C++` and `Java`.

.. cmdoption:: --metadata-package=package

  Package or namespace for the metadata store and retrieve classes.

.. cmdoption:: --ome-xml-metadata-package

  Package or namespace for the OME-XML metadata classes.

.. cmdoption:: --ome-xml-model-package=package

  Package or namespace for the OME-XML model classes.

.. cmdoption:: --ome-xml-model-enums-package=package

  Package or namespace for the OME-XML model enum classes.

.. cmdoption:: --ome-xml-model-enum-handlers-package=package

  Package or namespace for the OME-XML model enum handler classes.

.. cmdoption:: -o dir, --output-directory=dir

  Output generated code into the specified directory.  The directory
  will be created if it does not already exist.  Note that the
  directory is the root of the source tree; generated classes will be
  placed into the appropriate module-specific locations under this
  root.

.. cmdoption:: --print-depends

  Print a list of the files required during template processing,
  including schema files, templates and custom template fragments.
  Particularly useful with :option:`--dry-run` to introspect command
  dependencies.

.. cmdoption:: --print-generated

  Print a list of the files generated during template processing.
  Particularly useful with :option:`--dry-run` to determine what a
  given command would generate.

.. cmdoption:: -q, --quiet

  Do not print names of generated files.

.. cmdoption:: -t path, --template-path=path

  Path to search for Genshi template files.  Defaults to the
  language-specific template directory in `components/xsd-fu`.

.. cmdoption:: -n, --xsd-namespace

  XML schema namespace to use.  Defaults to `xsd:`.

.. cmdoption:: -v, --verbose

  Print names of generated files as they are processed.

Available commands
------------------

* doc_gen
* metadata
* omero_metadata
* omero_model
* omexml_metadata
* omexml_metadata_all
* omexml_model
* omexml_model_all
* omexml_model_enums
* omexml_model_enum_handlers
* omexml_model_enum_includeall
* tab_gen

Running the code generator
--------------------------

Run xsd-fu script with no arguments to examine the syntax::

  ./components/xsd-fu/xsd-fu
  Error: Missing subcommand

  xsd-fu: Generate classes from an OME-XML schema definition
  Usage: ./components/xsd-fu/xsd-fu command [options…] -o output_dir schema_files…

  Options:
    -d, --dry-run                              Do not create output files
    --debug                                    Enable xsd-fu and template debugging
    -l, --language=lang                        Generated language
    --metadata-package=pkg                     Metadata package
    --ome-xml-metadata-package=pkg             OME-XML metadata class package
    --ome-xml-model-package=pkg                OME-XML model package
    --ome-xml-model-enums-package=pkg          OME-XML model enum package
    --ome-xml-model-enum-handlers-package=pkg  OME-XML model enum handler package
    -o, --output-directory=dir                 Generated output directory
    -q, --quiet                                Do not output file names
    -t, --template-path=path                   Genshi template path
    -v, --verbose                              Output generated file names
    -n, --xsd-namespace                        XML schema namespace

  Available subcommands:
    debug
    doc_gen
    omexml_model_enum_handlers
    omexml_model_enums
    omexml_model
    metadata
    omero_metadata
    omero_model
    omexml_metadata
    tab_gen

  Default XSD namespace: "xsd:"

  Default Java OME-XML package: "ome.xml.model"
  Default Java OME-XML enum package: "ome.xml.model.enums"
  Default Java OME-XML enum handler package: "ome.xml.model.enums.handlers"
  Default Java metadata package: "loci.formats.meta"
  Default Java OME-XML metadata package: "loci.formats.ome"

  Default C++ OME-XML package: "ome::xml::model"
  Default C++ OME-XML enum package: "ome::xml::model::enums"
  Default C++ metadata package: "ome::xml::meta"
  Default C++ OME-XML metadata package: "ome::xml::meta"

  Examples:
    ./components/xsd-fu/xsd-fu -l Java -n 'xsd:' --ome-xml-model-package=ome.xml.model -o omexml /path/to/schemas/ome.xsd
    ./components/xsd-fu/xsd-fu -l C++ -n 'xsd:' --ome-xml-model-package=ome::xml::model -o omexml /path/to/schemas/ome.xsd

  Report bugs to OME Devel <ome-devel@lists.openmicroscopy.org.uk>

.. note::

  It should not be necessary to run it by hand for a normal
  Bio-Formats build.  :program:`xsd-fu` is run automatically as part
  of the main Bio-Formats build from version 5.0 when building the
  `ome-xml` and `scifio` components.  It is still useful to run by
  hand when debugging, or using non-standard targets.


Generating the OME-XML Java model and metadata classes
------------------------------------------------------

The following sections outline how to generate parts of the OME-XML
Java interfaces and implementations for the object model and metadata
store, which are composed of:

- OME model objects
- enumerations for OME model properties
- enumeration handlers for regular expression matching of enumeration strings
- Metadata store and Metadata retrieve interfaces for all OME model properties
- various implementations of Metadata store and/or Metadata retrieve interfaces

All of the above can be generated by this Ant command:

::

  $ cd components/ome-xml
  $ ant generate-source

Run::


  $ ant generate-source -v

to see the command-line options used.

Working with Enumerations and Enumeration Handlers
--------------------------------------------------

XsdFu code generates enumeration regular expressions using a flexible
:source:`configuration file <components/xsd-fu/cfg/enum_handler.cfg>`.

Each enumeration has a key-value listing of regular expression to exact enumeration value matches. For example:

.. code-block:: ini

  [Correction]
  ".*Pl.*Apo.*" = "PlanApo"
  ".*Pl.*Flu.*" = "PlanFluor"
  "^\\s*Vio.*Corr.*" = "VioletCorrected"
  ".*S.*Flu.*" = "SuperFluor"
  ".*Neo.*flu.*" = "Neofluar"
  ".*Flu.*tar.*" = "Fluotar"
  ".*Fluo.*" = "Fluor"
  ".*Flua.*" = "Fluar"
  "^\\s*Apo.*" = "Apo"


Generate OMERO model specification files
----------------------------------------

Run :program:`xsd-fu` with the ``omero_model`` subcommand.

Special thanks
--------------

A special thanks goes out to `Dave Kuhlman
<http://www.davekuhlman.org/>`_ for his fabulous work on
`generateDS <http://www.davekuhlman.org/generateDS.html>`_ which
:program:`xsd-fu` makes heavy use of internally.
