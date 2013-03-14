"""
Object model and helper classes used in the generation of Java classes from
an OME XML (http://www.ome-xml.org) XSD document.
"""

#
#  Copyright (C) 2009 University of Dundee. All rights reserved.
#
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License along
#  with this program; if not, write to the Free Software Foundation, Inc.,
#  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#


import generateDS.generateDS
import logging
import copy
import re
import os

from generateDS.generateDS import *
from xml import sax

from posix import getuid
from pwd import getpwuid

from util import odict
from xml.etree import ElementTree

try:
    import mx.DateTime as DateTime
    def now():
        return DateTime.ISO.str(DateTime.now())
except ImportError:
    from datetime import datetime
    def now():
        return datetime.now()

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

# A global mapping from XSD Schema types and Java types that is used to
# inform and override type mappings for OME Model properties which are
# comprised of XML Schema attributes, elements and OME XML reference virtual
# types. It is a superset of JAVA_PRIMITIVE_BASE_TYPE_MAP.
JAVA_TYPE_MAP = None

# A global type mapping from XSD Schema types to Java primitive base classes.
JAVA_PRIMITIVE_TYPE_MAP = None

# A global type mapping from XSD Schema types to Java base classes that is
# used to override places in the model where we do not wish subclassing to
# take place.
JAVA_BASE_TYPE_MAP = {}

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

def updateTypeMaps(namespace):
    """
    Updates the type maps with a new namespace. **Must** be executed at least
    once, **before** node class file generation.
    """
    global JAVA_PRIMITIVE_TYPE_MAP
    JAVA_PRIMITIVE_TYPE_MAP = {
        namespace + 'boolean': 'Boolean',
        namespace + 'dateTime': 'Timestamp',
        namespace + 'string': 'String',
        namespace + 'integer': 'Integer',
        namespace + 'int': 'Integer',
        namespace + 'long': 'Long',
        'PositiveInt': 'PositiveInteger',
        'NonNegativeInt': 'NonNegativeInteger',
        'PositiveLong': 'PositiveLong',
        'NonNegativeLong': 'NonNegativeLong',
        'PositiveFloat': 'PositiveFloat',
        namespace + 'float': 'Double',
        namespace + 'double': 'Double',
        namespace + 'anyURI': 'String',
        namespace + 'hexBinary': 'String',
        'PercentFraction': 'PercentFraction',
        'Color': 'Color',
        'AffineTransform': 'AffineTransform',
        'Text': 'Text',
    }
    global JAVA_TYPE_MAP
    JAVA_TYPE_MAP = copy.deepcopy(JAVA_PRIMITIVE_TYPE_MAP)
    JAVA_TYPE_MAP['MIMEtype'] = 'String'
    JAVA_TYPE_MAP['Leader'] = 'Experimenter'
    JAVA_TYPE_MAP['Contact'] = 'Experimenter'
    JAVA_TYPE_MAP['Pump'] = 'LightSource'

    global JAVA_BASE_TYPE_MAP
    JAVA_BASE_TYPE_MAP = {
        'UniversallyUniqueIdentifier': DEFAULT_BASE_CLASS
    }

# The list of properties not to process.
DO_NOT_PROCESS = [] #["ID"]

# Default root XML Schema namespace
DEFAULT_NAMESPACE = "xsd:"

# The default Java base class for OME XML model objects.
DEFAULT_BASE_CLASS = "AbstractOMEModelObject"

# The default Java package for OME XML model objects.
DEFAULT_PACKAGE = "ome.xml.r2008"

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
ENUM_TEMPLATE = "templates/Enum.template"

# The default template for enum handler class processing.
ENUM_HANDLER_TEMPLATE = "templates/EnumHandler.template"

# The default template for class processing.
CLASS_TEMPLATE = "templates/Pojo.template"

# The default template for MetadataStore processing.
METADATA_STORE_TEMPLATE = "templates/MetadataStore.template"

# The default template for MetadataRetrieve processing.
METADATA_RETRIEVE_TEMPLATE = "templates/MetadataRetrieve.template"

# The default template for AggregateMetadata processing.
METADATA_AGGREGATE_TEMPLATE = "templates/AggregateMetadata.template"

# The default template for OME XML metadata processing.
OMEXML_METADATA_TEMPLATE = "templates/OMEXMLMetadataImpl.template"

# The default template for DummyMetadata processing.
DUMMY_METADATA_TEMPLATE = "templates/DummyMetadata.template"

# The default template for FilterMetadata processing.
FILTER_METADATA_TEMPLATE = "templates/FilterMetadata.template"

# The default template for OMERO metadata processing.
OMERO_METADATA_TEMPLATE = "templates/OmeroMetadata.template"

# The default template for OMERO metadata processing.
OMERO_MODEL_TEMPLATE = "templates/OmeroModel.template"

REF_REGEX = re.compile(r'Ref$|RefNode$')

BACKREF_REGEX = re.compile(r'_BackReference')

PREFIX_CASE_REGEX = re.compile(
        r'^([A-Z]{1})[a-z0-9]+|([A-Z0-9]+)[A-Z]{1}[a-z]+|([A-Z]+)[0-9]*|([a-z]+$)')

def resolve_parents(model, element_name):
    """
    Resolves the parents of an element and returns them as an ordered list.
    """
    parents = dict()
    try:
        my_parents = model.parents[element_name]
    except KeyError:
        return None
    for parent in my_parents:
        parents[parent] = resolve_parents(model, parent)
    return parents

def resolve_paths(paths, level, names):
    """
    Resolves a set of ASCII art graphics for a given hierarchical tree
    structure of element names.
    """
    if names is None:
        return
    for name in names.keys():
        path = "+-- %s" % name
        paths.insert(0, path)
        resolve_paths(paths, level + 1, names[name])

class ModelProcessingError(Exception):
    """
    Raised when there is an error during model processing.
    """
    pass

class ReferenceDelegate(object):
    """
    A "virtual" property delegate to be used with "reference"
    OMEModelProperty instances. This delegate conforms loosely to the same
    interface as a delegate coming from generateDS (ie. an "attribute" or
    an "element").
    """
    def __init__(self, name, dataType, plural):
        self.name = name + "_BackReference"
        self.dataType = dataType
        self.plural = plural
        # Ensures property code which is looking for elements or attributes
        # which conform to an enumeration can still function.
        self.values = None
        self.maxOccurs = 9999
        self.minOccurs = 0

    def getValues(self):
        return self.values

    def getMaxOccurs(self):
        return self.maxOccurs

    def getMinOccurs(self):
        return self.minOccurs

    def getType(self):
        return self.dataType

    def getName(self):
        return self.name

    def isComplex(self):
        return True

class OMEModelEntity(object):
    """
    An abstract root class for properties and model objects containing
    common type resolution and text processing functionality.
    """

    def resolveJavaTypeFromSimpleType(self, simpleTypeName):
        getSimpleType = self.model.getTopLevelSimpleType
        while True:
            simpleType = getSimpleType(simpleTypeName)
            if simpleType is None:
                logging.debug("No simpleType found with name: %s" % \
                        simpleTypeName)
                # Handle cases where the simple type is prefixed by
                # a namespace definition. (ex. OME:LSID).
                namespaceless = simpleTypeName.split(':')[-1]
                if namespaceless != simpleTypeName:
                    simpleTypeName = namespaceless
                    continue
                break
            logging.debug("%s simpleType dump: %s" % \
                    (self, simpleType.__dict__))
            # It's possible the simpleType is a union of other
            # simpleTypes so we need to handle that. We assume
            # that all the unioned simpleTypes are of the same
            # base type (ex. "xsd:string" or "xsd:float").
            if simpleType.unionOf:
                union = getSimpleType(simpleType.unionOf[0])
                try:
                    return JAVA_TYPE_MAP[union.getBase()]
                except KeyError:
                    simpleTypeName = union.getBase()
            try:
                return JAVA_TYPE_MAP[simpleType.getBase()]
            except KeyError:
                simpleTypeName = simpleType.getBase()

    def lowerCasePrefix(self, v):
        if v is None:
            raise ModelProcessingError(
                    'Cannot lower case %s on %s' % (v, self.name))
        match = PREFIX_CASE_REGEX.match(v)
        if match is None:
            raise ModelProcessingError(
                    'No prefix match for %s on %s' % (v, self.name))
        prefix, = filter(None, match.groups())
        return prefix.lower() + v[len(prefix):]

    def _get_omeroPackage(self):
        namespace = self.namespace
        try:
            if self.isBackReference:
                namespace = self.model.getObjectByName(self.type).namespace
        except AttributeError:  # OMEModelObject not OMEModelProperty
            pass
        suffix = PACKAGE_NAMESPACE_RE.match(namespace).group(1)
        try:
            if self.isEnumeration:
                suffix = 'enum'
        except AttributeError:
            pass
        try:
            return OMERO_PACKAGE_OVERRIDES[suffix]
        except:
            return "%s.%s" % (OMERO_DEFAULT_PACKAGE, suffix.lower())
    omeroPackage = property(_get_omeroPackage,
        doc="""The OMERO package of the entity.""")

    def _get_javaArgumentName(self):
        argumentName = REF_REGEX.sub('', self.name)
        return self.lowerCasePrefix(argumentName)
    javaArgumentName = property(_get_javaArgumentName,
        doc="""The property's Java argument name (camelCase).""")

    def _get_javaMethodName(self):
        try:
            name = BACK_REFERENCE_NAME_OVERRIDE[self.key]
            return name[0].upper() + name[1:]
        except (KeyError, AttributeError):
            pass
        return BACKREF_REGEX.sub('', REF_REGEX.sub('', self.name))
    javaMethodName = property(_get_javaMethodName,
        doc="""The property's Java method name.""")

    def _get_isGlobal(self):
        isGlobal = self._isGlobal
        try:
            if self.isBackReference:
                ref = self.model.getObjectByName(BACKREF_REGEX.sub('', self.type))
                if ref.name == self.name:
                    return isGlobal
                return isGlobal or ref.isGlobal
        except AttributeError:
            pass
        if self.isReference:
            ref = self.model.getObjectByName(REF_REGEX.sub('', self.type))
            if ref.name == self.name:
                return isGlobal
            isGlobal = isGlobal or ref.isGlobal
        return isGlobal
    isGlobal = property(_get_isGlobal,
        doc="""Whether or not the model object is an OMERO system type.""")

    def _get_isManyToMany(self):
        try:
            if self.isBackReference:
                reference_to = self.model.getObjectByName(self.type)
                for prop in reference_to.properties.values():
                    if prop.type == self.parent.name + 'Ref':
                        return prop.isManyToMany
        except AttributeError:
            pass
        return self.manyToMany
    isManyToMany = property(_get_isManyToMany,
        doc="""Whether or not the entity is a many-to-many reference.""")

    def _get_isSettings(self):
        return self.name.endswith('Settings')
    isSettings = property(_get_isSettings,
        doc="""Whether or not the entity is a Settings reference.""")

class OMEModelProperty(OMEModelEntity):
    """
    An aggregate type representing either an OME XML Schema element,
    attribute or our OME XML Schema "Reference" meta element (handled by the
    ReferenceDelegate class). This class equates conceptually to an object
    oriented language instance variable which may be of a singular type or a
    collection.
    """

    def __init__(self, delegate, parent, model):
        self.model = model
        self.delegate = delegate
        self.parent = parent
        self.isAttribute = False
        self.isBackReference = False
        self.isChoice = hasattr(self.delegate, 'choice')
        self.isChoice = self.isChoice and self.delegate.choice is not None
        self._isGlobal = False
        self.plural = None
        self.manyToMany = False
        self.isParentOrdered = False
        self.isChildOrdered = False
        self.isOrdered = False
        self.isUnique = False
        self.isImmutable = False
        self.isInjected = False
        self._isReference = False
        try:
            try:
                root = ElementTree.fromstring(delegate.appinfo)
            except:
                logging.error('Exception while parsing %r' % delegate.appinfo)
                raise
            self.plural = root.findtext('plural')
            if root.find('manytomany') is not None:
                self.manyToMany = True
            if root.find('parentordered') is not None:
                self.isParentOrdered = True
            if root.find('childordered') is not None:
                self.isChildOrdered = True
            if root.find('ordered') is not None:
                self.isOrdered = True
            if root.find('unique') is not None:
                self.isUnique = True
            if root.find('immutable') is not None:
                self.isImmutable = True
            if root.find('injected') is not None:
                self.isInjected = True
            if root.find('global') is not None:
                self._isGlobal = True
        except AttributeError:
            pass

    def _get_type(self):
        if self.isAttribute:
            return self.delegate.getData_type()
        return self.delegate.getType()
    type = property(_get_type, doc="""The property's XML Schema data type.""")

    def _get_maxOccurs(self):
        if self.isAttribute:
            return 1
        choiceMaxOccurs = 1
        if self.isChoice:
            choiceMaxOccurs = self.delegate.choice.getMaxOccurs()
        return max(choiceMaxOccurs, self.delegate.getMaxOccurs())
    maxOccurs = property(_get_maxOccurs,
        doc="""The maximum number of occurances for this property.""")

    def _get_minOccurs(self):
        if self.isAttribute:
            if self.delegate.getUse() == "optional":
                return 0
            return 1
        if hasattr(self.delegate, 'choice') \
           and self.delegate.choice is not None:
            return self.delegate.choice.getMinOccurs()
        return self.delegate.getMinOccurs()
    minOccurs = property(_get_minOccurs,
        doc="""The minimum number of occurances for this property.""")

    def _get_name(self):
        return self.delegate.getName()
    name = property(_get_name, doc="""The property's name.""")

    def _get_namespace(self):
        if self.isReference:
            ref = self.model.getObjectByName(REF_REGEX.sub('', self.type))
            return ref.namespace
        if self.isAttribute or self.isBackReference:
            return self.parent.namespace
        return self.delegate.namespace
    namespace = property(_get_namespace,
        doc="""The root namespace of the property.""")

    def _get_javaType(self):
        try:
            # Hand back the type of enumerations
            if self.isEnumeration:
                javaType = self.name
                if len(self.delegate.values) == 0:
                    # As we have no directly defined possible values we have
                    # no reason to qualify our type explicitly.
                    return self.type
                if javaType == "Type":
                    # One of the OME XML unspecific "Type" properties which
                    # can only be qualified by the parent.
                    if self.type.endswith("string"):
                        # We've been defined entirely inline, prefix our Java
                        # type name with the parent type's name.
                        return "%s%s" % (self.parent.name, javaType)
                    # There's another type which describes us, use its name
                    # as our Java type name.
                    return self.type
                return javaType
            # Handle XML Schema types that directly map to Java types and
            # handle cases where the type is prefixed by a namespace definition.
            # (ex. OME:NonNegativeInt).
            return JAVA_TYPE_MAP[self.type.replace('OME:', '')]
        except KeyError:
            # Hand back the type of references or complex types with the
            # useless OME XML 'Ref' suffix removed.
            if self.isBackReference or \
               (not self.isAttribute and self.delegate.isComplex()):
                return REF_REGEX.sub('', self.type)
            # Hand back the type of complex types
            if not self.isAttribute and self.delegate.isComplex():
                return self.type
            if not self.isEnumeration:
                # We have a property whose type was defined by a top level
                # simpleType.
                simpleTypeName = self.type
                return self.resolveJavaTypeFromSimpleType(simpleTypeName)
            logging.debug("%s dump: %s" % (self, self.__dict__))
            logging.debug("%s delegate dump: %s" % (self, self.delegate.__dict__))
            raise ModelProcessingError, \
                "Unable to find %s Java type for %s" % (self.name, self.type)
    javaType = property(_get_javaType, doc="""The property's Java type.""")

    def _get_metadataStoreType(self):
        if not self.isPrimitive and not self.isEnumeration:
            return "String"
        return self.javaType
    metadataStoreType = property(_get_metadataStoreType,
        doc="""The property's MetadataStore type.""")

    def _get_isAnnotation(self):
        if self.isReference:
            ref = REF_REGEX.sub('', self.type)
            ref = self.model.getObjectByName(ref)
            return ref.isAnnotation
        return False
    isAnnotation = property(_get_isAnnotation,
        doc="""Whether or not the property is an Annotation.""")

    def _get_isPrimitive(self):
        if self.javaType in JAVA_PRIMITIVE_TYPE_MAP.values():
            return True
        return False
    isPrimitive = property(_get_isPrimitive,
        doc="""Whether or not the property's Java type is a primitive.""")

    def _get_isEnumeration(self):
        v = self.delegate.getValues()
        if v is not None and len(v) > 0:
            return True
        return False
    isEnumeration = property(_get_isEnumeration,
        doc="""Whether or not the property is an enumeration.""")

    def _get_isReference(self):
        o = self.model.getObjectByName(self.type)
        if o is not None:
            return o.isReference
        return self._isReference
    isReference = property(_get_isReference,
        doc="""Whether or not the property is a reference.""")

    def _get_possibleValues(self):
        return self.delegate.getValues()
    possibleValues = property(_get_possibleValues,
        doc="""If the property is an enumeration, it's possible values.""")

    def _get_javaInstanceVariableName(self):
        name = self.javaArgumentName
        if self.isManyToMany:
            if self.isBackReference:
                name = self.model.getObjectByName(self.type)
                name = name.javaInstanceVariableName
                name = BACK_REFERENCE_NAME_OVERRIDE.get(self.key, name)
            return name + 'Links'
        try:
            if self.maxOccurs > 1:
                plural = self.plural
                if plural is None:
                    plural = self.model.getObjectByName(self.javaMethodName).plural
                return self.lowerCasePrefix(plural)
        except AttributeError:
            pass
        if self.isBackReference:
            name = BACKREF_REGEX.sub('', name)
        return name
    javaInstanceVariableName = property(_get_javaInstanceVariableName,
        doc="""The property's Java instance variable name.""")

    def isComplex(self):
        """
        Returns whether or not the property has a "complex" content type.
        """
        if self.isAttribute:
            raise ModelProcessingError, \
                "This property is an attribute and has no content model!"
        # FIXME: This hack is in place because of the incorrect content
        # model in the XML Schema document itself for the "Description"
        # element.
        if self.name == "Description":
            return False
        return self.delegate.isComplex()

    def fromAttribute(klass, attribute, parent, model):
        """
        Instantiates a property from an XML Schema attribute.
        """
        instance = klass(attribute, parent, model)
        instance.isAttribute = True
        return instance
    fromAttribute = classmethod(fromAttribute)

    def fromElement(klass, element, parent, model):
        """
        Instantiates a property from an XML Schema element.
        """
        return klass(element, parent, model)
    fromElement = classmethod(fromElement)

    def fromReference(klass, reference, parent, model):
        """
        Instantiates a property from a "virtual" OME XML schema reference.
        """
        instance = klass(reference, parent, model)
        instance.isBackReference = True
        return instance
    fromReference = classmethod(fromReference)

class OMEModelObject(OMEModelEntity):
    """
    A single element of an OME data model.
    """

    def __init__(self, element, parent, model):
        self.model = model
        self.element = element
        self.parent = parent
        self.base = element.getBase()
        self.name = element.getName()
        self.type = element.getType()
        self.properties = odict()
        self.isAbstract = False
        self.isAbstractProprietary = False
        self.isParentOrdered = False
        self.isChildOrdered = False
        self.isOrdered = False
        self.isUnique = False
        self.isImmutable = False
        self._isGlobal = False
        self.base in ('Annotation', 'BasicAnnotation') \
                or self.name == 'Annotation'
        self.plural = None
        self.manyToMany = False
        try:
            try:
                root = ElementTree.fromstring(element.appinfo)
            except:
                logging.error('Exception while parsing %r' % element.appinfo)
                raise
            if root.find('abstract') is not None:
                self.isAbstract = True
            if root.find('abstractproprietary') is not None:
                self.isAbstractProprietary = True
            if root.find('unique') is not None:
                self.isUnique = True
            if root.find('immutable') is not None:
                self.isImmutable = True
            if root.find('global') is not None:
                self._isGlobal = True
            if root.find('parentordered') is not None:
                self.isParentOrdered = True
            if root.find('childordered') is not None:
                self.isChildOrdered = True
            if root.find('ordered') is not None:
                self.isOrdered = True
            self.plural = root.findtext('plural')
        except AttributeError:
            pass

    def addAttribute(self, attribute):
        """
        Adds an OME XML Schema attribute to the object's data model.
        """
        name = attribute.getName()
        self.properties[name] = \
            OMEModelProperty.fromAttribute(attribute, self, self.model)

    def addElement(self, element):
        """
        Adds an OME XML Schema element to the object's data model.
        """
        name = element.getName()
        self.properties[name] = \
            OMEModelProperty.fromElement(element, self, self.model)

    def _get_isAnnotation(self):
        if self.name == 'Annotation':
            return True
        base = self
        while True:
            if base is None:
                return False
            if base.base == 'Annotation':
                return True
            base = self.model.getObjectByName(base.base)
    isAnnotation = property(_get_isAnnotation,
        doc="""Whether or not the model object is an Annotation.""")

    def _get_isReference(self):
        if self.base == "Reference":
            return True
        typeObject = self.model.getObjectByName(self.type)
        if typeObject is not None and typeObject.name != self.name \
           and typeObject.isReference:
            return True
        return False
    isReference = property(_get_isReference,
        doc="""Whether or not the model object is a reference.""")

    def _get_isAnnotated(self):
        for v in self.properties.values():
            if v.name == "AnnotationRef":
                return True
        return False
    isAnnotated = property(_get_isAnnotated,
        doc="""Whether or not the model object is annotated.""")

    def _get_isNamed(self):
        for v in self.properties.values():
            if v.name == "Name" and not v.isUnique:
                return True
        return False
    isNamed = property(_get_isNamed,
        doc="""Whether or not the model object is named.""")

    def _get_isDescribed(self):
        for v in self.properties.values():
            if v.name == "Description":
                return True
        return False
    isDescribed = property(_get_isDescribed,
        doc="""Whether or not the model object is described.""")

    def _get_javaBase(self):
        base = self.element.getBase()
        if base in JAVA_BASE_TYPE_MAP:
            return JAVA_BASE_TYPE_MAP[base]
        if base is None and self.element.attrs['type'] != self.name:
            base = self.element.attrs['type']
        if base is None:
            return DEFAULT_BASE_CLASS
        return base
    javaBase = property(_get_javaBase,
        doc="""The model object's Java base class.""")

    def _get_namespace(self):
        return self.element.namespace
    namespace = property(_get_namespace,
        doc="""The root namespace of the model object.""")

    def _get_baseObjectProperties(self):
        properties = list()
        base = self.base
        while True:
            base = self.model.getObjectByName(base)
            if base is None:
                return properties
            properties += base.properties.values()
            base = base.base
    baseObjectProperties = property(_get_baseObjectProperties,
        doc="""The model object's base object properties.""")

    def _get_refNodeName(self):
        if self.base == "Reference":
            return self.properties["ID"].javaType
        return None
    refNodeName = property(_get_refNodeName,
        doc="""The name of this node's reference node; None otherwise.""")

    def _get_javaType(self):
        try:
            return JAVA_TYPE_MAP[self.base]
        except KeyError:
            if self.base is not None:
                simpleType = self.model.getTopLevelSimpleType(self.base)
                parent = self.model.getObjectByName(self.base)
                if simpleType is not None:
                    return self.resolveJavaTypeFromSimpleType(self.base)
                if parent is not None:
                    return parent.javaType
            return "Object"
    javaType = property(_get_javaType, doc="""The property's Java type.""")

    def _get_javaInstanceVariableName(self):
        if self.isManyToMany:
            return self.javaArgumentName + 'Links'
        try:
            if self.maxOccurs > 1:
                return self.lowerCasePrefix(self.plural)
        except AttributeError:
            pass
        return self.javaArgumentName
    javaInstanceVariableName = property(_get_javaInstanceVariableName,
        doc="""The property's Java instance variable name.""")

    def isComplex(self):
        """
        Returns whether or not the model object has a "complex" content type.
        """
        return self.element.isComplex()

    def __str__(self):
        return self.__repr__()

    def __repr__(self):
        return '<"%s" OMEModelObject instance at 0x%x>' % (self.name, id(self))

class OMEModel(object):
    def __init__(self):
        self.elementNameObjectMap = dict()
        self.objects = odict()
        self.parents = dict()

    def addObject(self, element, obj):
        elementName = element.getName()
        if self.objects.has_key(element):
            raise ModelProcessingError(
                "Element %s has been processed!" % element)
        if elementName in self.elementNameObjectMap:
            logging.warn(
                "Element %s has duplicate object with same name, skipping!" \
                % element)
            return
        self.elementNameObjectMap[element.getName()] = obj
        self.objects[element] = obj

    def getObject(self, element):
        try:
            return self.objects[element]
        except KeyError:
            return None

    def getObjectByName(self, name):
        try:
            return self.elementNameObjectMap[name]
        except KeyError:
            return None

    def getTopLevelSimpleType(self, name):
        """
        Returns the simpleType that has a given name from the list of top
        level simple types for this model.
        """
        for simpleType in self.topLevelSimpleTypes:
            if simpleType.name == name:
                return simpleType
        return None

    def processAttributes(self, element):
        """
        Process all the attributes for a given element (a leaf).
        """
        attributes = element.getAttributeDefs()
        obj = self.getObject(element)
        length = len(attributes)
        for i, key in enumerate(attributes):
            logging.debug("Processing attribute: %s %d/%d" % (key, i + 1, length))
            attribute = attributes[key]
            logging.debug("Dump: %s" % attribute.__dict__)
            name = attribute.getName()
            obj.addAttribute(attribute)

        children = element.getChildren()
        length = len(children)
        for i, child in enumerate(children):
            logging.debug("Processing child: %s %d/%d" % (child, i + 1, length))
            logging.debug("Dump: %s" % child.__dict__)
            name = child.getCleanName()
            obj.addElement(child)

    def processLeaf(self, element, parent):
        """
        Process an element (a leaf).
        """
        e = element
        logging.debug("Processing leaf (topLevel? %s): (%s) --> (%s)" % (e.topLevel, parent, e))
        e_name = e.getName()
        e_type = e.getType()
        if parent is not None:
            if e_name not in self.parents:
                self.parents[e_name] = list()
            self.parents[e_name].append(parent.getName())
        if not e.isExplicitDefine() \
           and e_name not in EXPLICIT_DEFINE_OVERRIDE and not e.topLevel:
            logging.info("Element %s.%s not an explicit define, skipping." % (parent, e))
            return
        if e.getMixedExtensionError():
            logging.error("Element %s.%s extension chain contains mixed and non-mixed content, skipping." % (parent, e))
            return
        if e_type != e_name and e_name not in EXPLICIT_DEFINE_OVERRIDE:
            logging.info("Element %s.%s is not a concrete type (%s != %s), skipping." % (parent, e, e_type, e_name))
            return
        obj = OMEModelObject(e, parent, self)
        self.addObject(e, obj)
        self.processAttributes(e)

    def processTree(self, elements, parent=None):
        """
        Recursively processes a tree of elements.
        """
        length = len(elements)
        for i, element in enumerate(elements):
            logging.info("Processing element: %s %d/%d" % (element, i + 1, length))
            self.processLeaf(element, parent)
            children = element.getChildren()
            if children:
                self.processTree(children, element)

    def calculateMaxOccurs(self, o, prop):
        if prop.isReference:
            return 9999
        return 1

    def calculateMinOccurs(self, o, prop):
        if prop.isReference or prop.isSettings:
            return 0
        return 1

    def postProcessReferences(self):
        """
        Examines the list of objects in the model for instances that conform
        to the OME XML Schema referential object naming conventions and
        injects properties into referenced objects to provide back links.
        """
        references = dict()
        for o in self.objects.values():
            if o.isSettings and not o.isAbstract:
                shortName = o.name.replace('Settings', '')
                ref = '%sRef' % (shortName)
                delegate = ReferenceDelegate(ref, ref, None)
                # Override back reference naming and default cardinality. Also
                # set the namespace to be the same
                delegate.name = ref
                delegate.minOccurs = 1
                delegate.maxOccurs = 1
                delegate.namespace = o.namespace
                prop = OMEModelProperty.fromReference(delegate, o, self)
                o.properties[ref] = prop
        for o in self.objects.values():
            for prop in o.properties.values():
                if not prop.isReference and (prop.isAttribute or prop.maxOccurs == 1 \
                        or o.name == 'OME' or o.isAbstractProprietary):
                    continue
                shortName = REF_REGEX.sub('', prop.type)
                try:
                    if o.name in BACK_REFERENCE_OVERRIDE[shortName]:
                        continue
                except KeyError:
                    pass
                if shortName not in references:
                    references[shortName] = list()
                v = {'data_type': o.name, 'property_name': prop.javaMethodName,
                     'plural': prop.plural,
                     'maxOccurs': self.calculateMaxOccurs(o, prop),
                     'minOccurs': self.calculateMinOccurs(o, prop),
                     'isOrdered': prop.isOrdered,
                     'isChildOrdered': prop.isChildOrdered,
                     'isParentOrdered': prop.isParentOrdered,
                     'isInjected': prop.isInjected}
                references[shortName].append(v)
        logging.debug("Model references: %s" % references)

        for o in self.objects.values():
            if o.name not in references:
                continue
            for ref in references[o.name]:
                key = '%s.%s' % (ref['data_type'], ref['property_name'])
                delegate = ReferenceDelegate(
                        ref['data_type'], ref['data_type'], ref['plural'])
                delegate.minOccurs = ref['minOccurs']
                delegate.maxOccurs = ref['maxOccurs']
                prop = OMEModelProperty.fromReference(delegate, o, self)
                prop.key = key
                prop.isChildOrdered = ref['isChildOrdered']
                prop.isParentOrdered = ref['isParentOrdered']
                prop.isOrdered = ref['isOrdered']
                prop.isInjected = ref['isInjected']
                o.properties[key] = prop

    def process(klass, contentHandler):
        """
        Main process entry point. All instantiations of this class should be
        made through this class method unless you really know what you are
        doing.
        """
        elements = contentHandler.getRoot().getChildren()
        model = klass()
        model.topLevelSimpleTypes = contentHandler.topLevelSimpleTypes
        model.processTree(elements)
        model.postProcessReferences()
        return model
    process = classmethod(process)

    def __str__(self):
        a = str()
        for o in model.objects.values():
            if o.base:
                a += "%s extends %s\n" % (o.name, o.base)
            else:
                a += "%s\n" % (o.name)
            attributes = o.flattenedAttributes(model)
            for name, atype in attributes.items():
                a += "   + Attribute: %s Type: %s\n" % (name, atype)
        return a

class TemplateInfo(object):
    """
    Basic status information to pass to the template engine.
    """
    def __init__(self, outputDirectory, package):
        self.outputDirectory = outputDirectory
        self.package = package
        self.date = now()
        self.user = getpwuid(getuid())[0]
        self.DO_NOT_PROCESS = DO_NOT_PROCESS
        self.BACK_REFERENCE_OVERRIDE = BACK_REFERENCE_OVERRIDE
        self.BACK_REFERENCE_LINK_OVERRIDE = BACK_REFERENCE_LINK_OVERRIDE
        self.BACK_REFERENCE_NAME_OVERRIDE = BACK_REFERENCE_NAME_OVERRIDE
        self.REF_REGEX = REF_REGEX
        self.OMERO_NAMED_OPTIONAL = OMERO_NAMED_OPTIONAL

    def link_overridden(self, property_name, class_name):
        """Whether or not a back reference link should be overridden."""
        try:
            return class_name in self.BACK_REFERENCE_LINK_OVERRIDE[property_name]
        except KeyError:
            return False

    def backReference_overridden(self, property_name, class_name):
        """Whether or not a back reference link name should be overridden."""
        try:
            name = class_name + "." + self.REF_REGEX.sub('', property_name)
            return self.BACK_REFERENCE_NAME_OVERRIDE[name]
        except KeyError:
            return False

def parseXmlSchema(filenames, namespace=DEFAULT_NAMESPACE):
    """
    Entry point for XML Schema parsing into an OME Model.
    """
    # The following two statements are required to "prime" the generateDS
    # code and ensure we have reasonable namespace support.
    logging.debug("Namespace: %s" % namespace)
    set_type_constants(namespace)
    updateTypeMaps(namespace)
    generateDS.generateDS.XsdNameSpace = namespace
    logging.debug("Java type map: %s" % JAVA_TYPE_MAP)

    parser = sax.make_parser()
    ch = XschemaHandler()
    parser.setContentHandler(ch)
    for filename in filenames:
        parser.parse(filename)

    root = ch.getRoot()
    if root is None:
        raise ModelProcessingError(
            "No model objects found, have you set the correct namespace?")
    root.annotate()
    return OMEModel.process(ch)

def resetGenerateDS():
    """
    Since the generateDS module contains many globals and package scoped
    variables we need the ability to reset its state if we are to re-use
    it multiple times in the same process.
    """
    generateDS.generateDS.GenerateProperties = 0
    generateDS.generateDS.UseOldGetterSetter = 0
    generateDS.generateDS.MemberSpecs = None
    generateDS.generateDS.DelayedElements = []
    generateDS.generateDS.DelayedElements_subclass = []
    generateDS.generateDS.AlreadyGenerated = []
    generateDS.generateDS.AlreadyGenerated_subclass = []
    generateDS.generateDS.PostponedExtensions = []
    generateDS.generateDS.ElementsForSubclasses = []
    generateDS.generateDS.ElementDict = {}
    generateDS.generateDS.Force = 0
    generateDS.generateDS.Dirpath = []
    generateDS.generateDS.ExternalEncoding = sys.getdefaultencoding()

    generateDS.generateDS.NamespacesDict = {}
    generateDS.generateDS.Targetnamespace = ""

    generateDS.generateDS.NameTable = {
        'type': 'type_',
        'float': 'float_',
    }
    for kw in keyword.kwlist:
        generateDS.generateDS.NameTable[kw] = '%sxx' % kw

    generateDS.generateDS.SubclassSuffix = 'Sub'
    generateDS.generateDS.RootElement = None
    generateDS.generateDS.AttributeGroups = {}
    generateDS.generateDS.SubstitutionGroups = {}
    #
    # SubstitutionGroups can also include simple types that are
    #   not (defined) elements.  Keep a list of these simple types.
    #   These are simple types defined at top level.
    generateDS.generateDS.SimpleElementDict = {}
    generateDS.generateDS.SimpleTypeDict = {}
    generateDS.generateDS.ValidatorBodiesBasePath = None
    generateDS.generateDS.UserMethodsPath = None
    generateDS.generateDS.UserMethodsModule = None
    generateDS.generateDS.XsdNameSpace = ''
