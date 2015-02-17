import re


# Default logger configuration
# logging.basicConfig(level=logging.DEBUG,
#                    format='%(asctime)s %(levelname)s %(message)s')

# Types which should be ignored from metadata store, retrieve, etc. code
# generation due either to their incompatibility or complexity as it applies
# to these interfaces and implementations.
METADATA_OBJECT_IGNORE = ('BinData', 'External', 'MapPairs', 'M')

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
BACK_REFERENCE_LINK_OVERRIDE = {
    'Pump': ['Laser'],
    'AnnotationRef': ['Annotation']}

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
ABSTRACT_PROPRIETARY_OVERRIDE = ('Transform', 'AnnotationRef',)
ANNOTATION_OVERRIDE = ('AnnotationRef',)

# The list of properties not to process.
DO_NOT_PROCESS = []  # ["ID"]

# Default root XML Schema namespace
DEFAULT_NAMESPACE = "xsd:"

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

REF_REGEX = re.compile(r'Ref$|RefNode$')

BACKREF_REGEX = re.compile(r'_BackReference')

p = r'^([A-Z]{1})[a-z0-9]+|([A-Z0-9]+)[A-Z]{1}[a-z]+|([A-Z]+)[0-9]*|([a-z]+$)'
PREFIX_CASE_REGEX = re.compile(p)
