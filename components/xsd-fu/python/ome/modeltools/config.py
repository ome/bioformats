import re
import copy


# Default logger configuration
#logging.basicConfig(level=logging.DEBUG,
#                    format='%(asctime)s %(levelname)s %(message)s')

# Types which should be ignored from metadata store, retrieve, etc. code
# generation due either to their incompatibility or complexity as it applies
# to these interfaces and implementations.
METADATA_OBJECT_IGNORE = ('BinData', 'External')

# Type counts which should be ignored from metadata store, retrieve, etc. code
# generation due either to their incompatibility or complexity as it applies
# to these interfaces and implementations.
METADATA_COUNT_IGNORE = {'Annotation': ['AnnotationRef']}

# Types which have not been recognized as explicit defines (XML Schema
# definitions that warrant a the creation of a first class model object) that
# we wish to be treated otherwise. As part of the code generation process they
# will also be confirmed to be top-level types.
EXPLICIT_DEFINE_OVERRIDE = ('EmissionFilterRef', 'ExcitationFilterRef')

# Back references that we do not want in the model either because they
# conflict with other properties or do not make sense.
BACK_REFERENCE_OVERRIDE = {'Annotation': ['Annotation'], 'Event': ['Event']}

# Reference properties of a given type for which back reference link methods
# should not be code generated for.
BACK_REFERENCE_LINK_OVERRIDE = {'Pump': ['Laser'], 'AnnotationRef': ['Annotation']}

# Back reference instance variable name overrides which will be used in place
# of the standard name translation logic.
BACK_REFERENCE_NAME_OVERRIDE = {
    'FilterSet.ExcitationFilter': 'filterSetExcitationFilter',
    'FilterSet.EmissionFilter': 'filterSetEmissionFilter',
    'LightPath.ExcitationFilter': 'lightPathExcitationFilter',
    'LightPath.EmissionFilter': 'lightPathEmissionFilter',
}

# Back reference class name overrides which will be used when generating
# fully qualified class names.
BACK_REFERENCE_CLASS_NAME_OVERRIDE = {
    'FilterSet.ExcitationFilter': 'FilterSetExcitationFilterLink',
    'FilterSet.EmissionFilter': 'FilterSetEmissionFilterLink',
    'LightPath.ExcitationFilter': 'LightPathExcitationFilterLink',
    'LightPath.EmissionFilter': 'LightPathEmissionFilterLink',
}

# Properties within abstract proprietary types that should be code generated
# for.
ABSTRACT_PROPRIETARY_OVERRIDE = ('Transform',)

# The list of properties not to process.
DO_NOT_PROCESS = [] #["ID"]

# Default root XML Schema namespace
DEFAULT_NAMESPACE = "xsd:"

# The default Java package for OME XML model objects.
DEFAULT_JAVA_OMEXML_PACKAGE = "ome.xml.model"

# The default C++ package for OME XML model objects.
DEFAULT_CXX_OMEXML_PACKAGE = "ome::xml::model"

# The default Java package for metadata store.
DEFAULT_JAVA_METADATA_PACKAGE = "ome.xml.meta"

# The default C++ package for metadata store.
DEFAULT_CXX_METADATA_PACKAGE = "ome::bioformats::meta"

# The default Java package for OME XML metadata store.
DEFAULT_JAVA_OMEXMLMETADATA_PACKAGE = "ome.xml.meta"

# The default C++ package for OME XML metadata store.
DEFAULT_CXX_OMEXMLMETADATA_PACKAGE = "ome::bioformats::ome"

# The package regular expression for OME namespaces.
PACKAGE_NAMESPACE_RE = re.compile(
        r'http://www.openmicroscopy.org/Schemas/(\w+)/\d+-\w+')

# The default OMERO package.
OMERO_DEFAULT_PACKAGE = "ome.model"

# The OMERO package overrides.
OMERO_PACKAGE_OVERRIDES = {
        "OME": "ome.model.core",
        "enum": "ome.model.enums",
        "OMERO": "ome.model.meta",
}

# The OMERO classes for which the type's Name attribute is optional
OMERO_NAMED_OPTIONAL = (
        "ROI",
        "Channel",
        "RenderingDef",
)


# The default template for enum class processing.
ENUM_TEMPLATE = 'Enum.template'

# The default template for enum inclusion.
ENUM_INCLUDEALL_TEMPLATE = 'Enums.template'

# The default template for enum handler class processing.
ENUM_HANDLER_TEMPLATE = 'EnumHandler.template'

# The default template for class processing.
CLASS_TEMPLATE = 'Pojo.template'

# The default template for MetadataStore processing.
METADATA_STORE_TEMPLATE = 'MetadataStore.template'

# The default template for MetadataRetrieve processing.
METADATA_RETRIEVE_TEMPLATE = 'MetadataRetrieve.template'

# The default template for AggregateMetadata processing.
METADATA_AGGREGATE_TEMPLATE = 'AggregateMetadata.template'

# The default template for OME XML metadata processing.
OMEXML_METADATA_TEMPLATE = 'OMEXMLMetadataImpl.template'

# The default template for DummyMetadata processing.
DUMMY_METADATA_TEMPLATE = 'DummyMetadata.template'

# The default template for FilterMetadata processing.
FILTER_METADATA_TEMPLATE = 'FilterMetadata.template'

# The default template for OMERO metadata processing.
OMERO_METADATA_TEMPLATE = 'OmeroMetadata.template'

# The default template for OMERO metadata processing.
OMERO_MODEL_TEMPLATE = 'OmeroModel.template'

REF_REGEX = re.compile(r'Ref$|RefNode$')

BACKREF_REGEX = re.compile(r'_BackReference')

PREFIX_CASE_REGEX = re.compile(
        r'^([A-Z]{1})[a-z0-9]+|([A-Z0-9]+)[A-Z]{1}[a-z]+|([A-Z]+)[0-9]*|([a-z]+$)')
