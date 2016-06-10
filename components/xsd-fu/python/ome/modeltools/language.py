import copy
import os

from ome.modeltools.exceptions import ModelProcessingError

TYPE_SOURCE = "source"
TYPE_HEADER = "header"


class Language(object):
    """
    Base class for output language.
    Updates the type maps with the model namespace.
    """
    def __init__(self, namespace, templatepath):
        self.modelNamespace = namespace
        self._templatepath = templatepath

        # Separator for package/namespace
        self.package_separator = None

        # The default base class for OME XML model objects.
        self.default_base_class = None

        # A global mapping from XSD Schema types and language types
        # that is used to inform and override type mappings for OME
        # Model properties which are comprised of XML Schema
        # attributes, elements and OME XML reference virtual types. It
        # is a superset of primitive_type_map.
        self.type_map = None

        self.fundamental_types = set()

        self.primitive_types = set()

        self.primitive_base_types = set()

        self.base_class = None

        self.template_map = {
            'ENUM': 'OMEXMLModelEnum.template',
            'ENUM_INCLUDEALL': 'OMEXMLModelAllEnums.template',
            'ENUM_HANDLER': 'OMEXMLModelEnumHandler.template',
            'QUANTITY': 'OMEXMLModelQuantity.template',
            'CLASS': 'OMEXMLModelObject.template',
            'METADATA_STORE': 'MetadataStore.template',
            'METADATA_RETRIEVE': 'MetadataRetrieve.template',
            'METADATA_AGGREGATE': 'AggregateMetadata.template',
            'OMEXML_METADATA': 'OMEXMLMetadataImpl.template',
            'DUMMY_METADATA': 'DummyMetadata.template',
            'FILTER_METADATA': 'FilterMetadata.template'
            }

        # A global type mapping from XSD Schema types to language
        # primitive base classes.
        self.primitive_type_map = {
            'PositiveInt': 'PositiveInteger',
            'NonNegativeInt': 'NonNegativeInteger',
            'PositiveLong': 'PositiveLong',
            'NonNegativeLong': 'NonNegativeLong',
            'PositiveFloat': 'PositiveFloat',
            'NonNegativeFloat': 'NonNegativeFloat',
            'PercentFraction': 'PercentFraction',
            'Color': 'Color',
            'Text': 'Text',
            namespace + 'dateTime':   'Timestamp'
            }
            
        # A global type mapping from XSD Schema substitution groups to language abstract classes
        self.abstract_type_map = dict()
        # A global type mapping from XSD Schema abstract classes to their equivalent substitution group
        self.substitutionGroup_map = dict()    

        # A global type mapping from XSD Schema elements to language model
        # object classes.  This will cause source code generation to be
        # skipped for this type since it's implemented natively.
        self.model_type_map = {}

        # A global type mapping from XSD Schema types to base classes
        # that is used to override places in the model where we do not
        # wish subclassing to take place.
        self.base_type_map = {
            'UniversallyUniqueIdentifier': self.getDefaultModelBaseClass(),
            'base64Binary': self.getDefaultModelBaseClass()
            }
        
        # A global set XSD Schema types use as base classes which are primitive  
        self.primitive_base_types = set([
            "base64Binary"])

        self.model_unit_map = {}
        self.model_unit_default = {}

        self.name = None
        self.template_dir = None
        self.source_suffix = None
        self.header_suffix = None

        self.omexml_model_package = None
        self.omexml_model_enums_package = None
        self.omexml_model_quantity_package = None
        self.omexml_model_omexml_model_enum_handlers_package = None
        self.metadata_package = None
        self.omexml_metadata_package = None

    def _initTypeMap(self):
        self.type_map['Leader'] = 'Experimenter'
        self.type_map['Contact'] = 'Experimenter'
        self.type_map['Pump'] = 'LightSource'

    def getDefaultModelBaseClass(self):
        return None

    def getTemplate(self, name):
        return self.template_map[name]

    def getTemplateDirectory(self):
        return self.template_dir

    def templatepath(self, template):
        return os.path.join(self._templatepath, self.getTemplateDirectory(),
                            self.getTemplate(template))

    def generatedFilename(self, name, type):
        gen_name = None
        if type == TYPE_SOURCE and self.source_suffix is not None:
            gen_name = name + self.source_suffix
        elif type == TYPE_HEADER and self.header_suffix is not None:
            gen_name = name + self.header_suffix
        else:
            raise ModelProcessingError(
                "Invalid language/filetype combination: %s/%s"
                % (self.name, type))
        return gen_name

    def hasBaseType(self, type):
        if type in self.base_type_map:
            return True
        return False

    def baseType(self, type):
        try:
            return self.base_type_map[type]
        except KeyError:
            return None

    def hasFundamentalType(self, type):
        if type in self.fundamental_types:
            return True
        return False

    def hasPrimitiveType(self, type):
        if (type in self.primitive_type_map.values() or
                type in self.primitive_types):
            return True
        return False

    def primitiveType(self, type):
        try:
            return self.primitive_type_map[type]
        except KeyError:
            return None
            
    def hasAbstractType(self, type):
        if (type in self.abstract_type_map):
            return True
        return False

    def abstractType(self, type):
        try:
            return self.abstract_type_map[type]
        except KeyError:
            return None
            
    def hasSubstitutionGroup(self, type):
        if (type in self.substitutionGroup_map):
            return True
        return False

    def substitutionGroup(self, type):
        try:
            return self.substitutionGroup_map[type]
        except KeyError:
            return None
            
    def getSubstitutionTypes(self):
        return self.substitutionGroup_map.keys()
            
    def isPrimitiveBase(self, type):
        if type in self.primitive_base_types:
            return True
        else:
            return False

    def hasType(self, type):
        if type in self.type_map:
            return True
        return False

    def type(self, type):
        try:
            return self.type_map[type]
        except KeyError:
            return None

    def index_signature(self, name, max_occurs, level, dummy=False):
        sig = {
            'type': name,
            }

        if name[:2].isupper():
            sig['argname'] = "%sIndex" % name
        else:
            sig['argname'] = "%s%sIndex" % (name[:1].lower(), name[1:])

        return sig

    def index_string(self, signature, dummy=False):
        if dummy is False:
            return "%s %s" % (signature['argtype'], signature['argname'])
        else:
            return "%s /* %s */" % (signature['argtype'], signature['argname'])

    def index_argname(self, signature, dummy=False):
        return signature['argname']


class Java(Language):
    def __init__(self, namespace, templatepath):
        super(Java, self).__init__(namespace, templatepath)

        self.package_separator = '.'

        self.base_class = "Object"

        self.primitive_type_map[namespace + 'boolean'] = 'Boolean'
        self.primitive_type_map[namespace + 'string'] = 'String'
        self.primitive_type_map[namespace + 'integer'] = 'Integer'
        self.primitive_type_map[namespace + 'int'] = 'Integer'
        self.primitive_type_map[namespace + 'long'] = 'Long'
        self.primitive_type_map[namespace + 'float'] = 'Double'
        self.primitive_type_map[namespace + 'double'] = 'Double'
        self.primitive_type_map[namespace + 'anyURI'] = 'String'
        self.primitive_type_map[namespace + 'hexBinary'] = 'String'
        self.primitive_type_map['base64Binary'] = 'byte[]'
        self.primitive_type_map['Map'] = 'List<MapPair>'

        self.model_type_map['Map'] = None
        self.model_type_map['M'] = None
        self.model_type_map['K'] = None
        self.model_type_map['V'] = None

        self.model_unit_map['UnitsLength'] = 'Length'
        self.model_unit_map['UnitsPressure'] = 'Pressure'
        self.model_unit_map['UnitsAngle'] = 'Angle'
        self.model_unit_map['UnitsTemperature'] = 'Temperature'
        self.model_unit_map['UnitsElectricPotential'] = 'ElectricPotential'
        self.model_unit_map['UnitsPower'] = 'Power'
        self.model_unit_map['UnitsFrequency'] = 'Frequency'

        self.model_unit_default['UnitsLength'] = 'UNITS.METRE'
        self.model_unit_default['UnitsTime'] = 'UNITS.SECOND'
        self.model_unit_default['UnitsPressure'] = 'UNITS.PASCAL'
        self.model_unit_default['UnitsAngle'] = 'UNITS.RADIAN'
        self.model_unit_default['UnitsTemperature'] = 'UNITS.KELVIN'
        self.model_unit_default['UnitsElectricPotential'] = 'UNITS.VOLT'
        self.model_unit_default['UnitsPower'] = 'UNITS.WATT'
        self.model_unit_default['UnitsFrequency'] = 'UNITS.HERTZ'

        self.type_map = copy.deepcopy(self.primitive_type_map)
        self._initTypeMap()
        self.type_map['MIMEtype'] = 'String'

        self.name = "Java"
        self.template_dir = "templates-java"
        self.source_suffix = ".java"
        self.header_suffix = None

        self.omexml_model_package = "ome.xml.model"
        self.omexml_model_enums_package = "ome.xml.model.enums"
        self.omexml_model_omexml_model_enum_handlers_package = \
            "ome.xml.model.enums.handlers"
        self.metadata_package = "ome.xml.meta"
        self.omexml_metadata_package = "ome.xml.meta"

        self.units_implementation_is = "ome"
        self.units_package = "ome.units"
        self.units_implementation_imports = \
            "import ome.units.quantity.*;\nimport ome.units.*;"
        self.model_unit_map['UnitsTime'] = 'Time'

    def getDefaultModelBaseClass(self):
        return "AbstractOMEModelObject"

    def typeToUnitsType(self, valueType):
        return self.model_unit_map[valueType]

    def typeToDefault(self, valueType):
        return self.model_unit_default[valueType]

    def index_signature(self, name, max_occurs, level, dummy=False):
        """Makes a Java method signature dictionary from an index name."""

        sig = super(Java, self).index_signature(name, max_occurs, level, dummy)
        sig['argtype'] = 'int'

        return sig


class CXX(Language):
    def __init__(self, namespace, templatepath):
        super(CXX, self).__init__(namespace, templatepath)

        self.package_separator = '::'

        self.template_map['OMEXML_METADATA'] = 'OMEXMLMetadata.template'

        self.fundamental_types = set([
            "bool",
            "char", "signed char", "unsigned char",
            "short", "signed short", "unsigned short",
            "int", "signed int", "unsigned int",
            "long", "signed long", "unsigned long",
            "long long", "signed long long", "unsigned long long",
            "float", "double", "long double",
            "int8_t", "uint8_t",
            "int16_t", "uint16_t",
            "int32_t", "uint32_t",
            "int64_t", "uint64_t"])

        self.primitive_types = self.primitive_types.union(set([
            "Color",
            "NonNegativeFloat",
            "NonNegativeInteger",
            "NonNegativeLong",
            "PercentFraction",
            "PositiveFloat",
            "PositiveInteger",
            "PositiveLong",
            "Timestamp"]))

        self.primitive_type_map[namespace + 'boolean'] = 'bool'
        self.primitive_type_map[namespace + 'string'] = 'std::string'
        self.primitive_type_map[namespace + 'integer'] = 'int32_t'
        self.primitive_type_map[namespace + 'int'] = 'int32_t'
        self.primitive_type_map[namespace + 'long'] = 'int64_t'
        self.primitive_type_map[namespace + 'float'] = 'double'
        self.primitive_type_map[namespace + 'double'] = 'double'
        self.primitive_type_map[namespace + 'anyURI'] = 'std::string'
        self.primitive_type_map[namespace + 'hexBinary'] = 'std::string'
        self.primitive_type_map['base64Binary'] = 'std::vector<uint8_t>'
        self.primitive_type_map['Map'] = 'OrderedMultimap'

        self.model_type_map['Map'] = None
        self.model_type_map['M'] = None
        self.model_type_map['K'] = None
        self.model_type_map['V'] = None

        self.type_map = copy.deepcopy(self.primitive_type_map)
        self._initTypeMap()
        self.type_map['MIMEtype'] = 'std::string'

        self.name = "C++"
        self.template_dir = "templates-cpp"
        self.source_suffix = ".cpp"
        self.header_suffix = ".h"

        self.omexml_model_package = "ome::xml::model"
        self.omexml_model_enums_package = "ome::xml::model::enums"
        self.omexml_model_quantity_package = "ome::xml::model::primitives"
        self.omexml_model_omexml_model_enum_handlers_package = \
            "ome::xml::model::enums::handlers"
        self.metadata_package = "ome::xml::meta"
        self.omexml_metadata_package = "ome::xml::meta"

    def getDefaultModelBaseClass(self):
        return "detail::OMEModelObject"

    def typeToUnitsType(self, valueType):
        return "%s::Quantity<%s > " % (self.omexml_model_quantity_package, valueType)

    def index_signature(self, name, max_occurs, level, dummy=False):
        """Makes a C++ method signature dictionary from an index name."""

        sig = super(CXX, self).index_signature(name, max_occurs, level, dummy)
        sig['argtype'] = 'index_type'

        return sig


def create(language, namespace, templatepath):
    """
    Create a language by name.
    """

    lang = None

    if language == "Java":
        lang = Java(namespace, templatepath)
    elif language == "C++":
        lang = CXX(namespace, templatepath)
    else:
        raise ModelProcessingError(
            "Invalid language: %s" % language)

    return lang
