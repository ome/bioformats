import copy
import os

from ome.modeltools.exceptions import ModelProcessingError
from ome.modeltools import config

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

        self.base_class = None

        # A global type mapping from XSD Schema types to language
        # primitive base classes.
        self.primitive_type_map = {
            'PositiveInt': 'PositiveInteger',
            'NonNegativeInt': 'NonNegativeInteger',
            'PositiveLong': 'PositiveLong',
            'NonNegativeLong': 'NonNegativeLong',
            'PositiveFloat': 'PositiveFloat',
            'PercentFraction': 'PercentFraction',
            'Color': 'Color',
            'Text': 'Text',
            namespace + 'dateTime': 'Timestamp'
            }

        # A global type mapping from XSD Schema types to base classes
        # that is used to override places in the model where we do not
        # wish subclassing to take place.
        self.base_type_map = {
            'UniversallyUniqueIdentifier': self.getDefaultModelBaseClass()
            }

        self.name = None
        self.template_dir = None
        self.source_suffix = None
        self.header_suffix = None

    def _initTypeMap(self):
        self.type_map['Leader'] = 'Experimenter'
        self.type_map['Contact'] = 'Experimenter'
        self.type_map['Pump'] = 'LightSource'

    def getDefaultModelBaseClass(self):
        return None

    def getTemplateDirectory(self):
        return self.template_dir

    def templatepath(self, template):
        return os.path.join(self._templatepath, self.getTemplateDirectory(), template)

    def generatedFilename(self, name, type):
        gen_name = None
        if type == TYPE_SOURCE and self.source_suffix is not None:
            gen_name = name + self.source_suffix
        elif type == TYPE_HEADER and self.header_suffix is not None:
            gen_name = name + self.header_suffix
        else:
            raise ModelProcessingError(
                "Invalid language/filetype combination: %s/%s" % (self.name, type))
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
        if type in self.primitive_type_map.values() or type in self.primitive_types:
            return True
        return False

    def primitiveType(self, type):
        try:
            return self.primitive_type_map[type]
        except KeyError:
            return None

    def hasType(self, type):
        if type in self.type_map:
            return True
        return False

    def type(self, type):
        try:
            return self.type_map[type]
        except KeyError:
            return None

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

        self.type_map = copy.deepcopy(self.primitive_type_map)
        self._initTypeMap()
        self.type_map['MIMEtype'] = 'String'

        self.name = "Java"
        self.template_dir = "templates-java"
        self.source_suffix = ".java"
        self.header_suffix = None

    def getDefaultModelBaseClass(self):
        return "AbstractOMEModelObject"


class CXX(Language):
    def __init__(self, namespace, templatepath):
        super(CXX, self).__init__(namespace, templatepath)

        self.package_separator = '::'

        self.fundamental_types = set(["bool",
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

        self.primitive_types = self.primitive_types.union(set(["Color",
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

        self.type_map = copy.deepcopy(self.primitive_type_map)
        self._initTypeMap()
        self.type_map['MIMEtype'] = 'std::string'

        self.name = "C++"
        self.template_dir = "templates-cpp"
        self.source_suffix = ".cpp"
        self.header_suffix = ".h"

    def getDefaultModelBaseClass(self):
        return "OMEModelObject"


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
