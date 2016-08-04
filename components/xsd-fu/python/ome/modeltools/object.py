from util import odict
import logging
import re

from xml.etree import ElementTree

from ome.modeltools.entity import OMEModelEntity
from ome.modeltools.property import OMEModelProperty

from ome.modeltools import config
from ome.modeltools import language


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
    isAnnotation = property(
        _get_isAnnotation,
        doc="""Whether or not the model object is an Annotation.""")

    def _get_isReference(self):
        if self.base == "Reference":
            return True
        typeObject = self.model.getObjectByName(self.type)
        if (typeObject is not None and typeObject.name != self.name and
                typeObject.isReference):
            return True
        return False
    isReference = property(
        _get_isReference,
        doc="""Whether or not the model object is a reference.""")

    def _get_isAnnotated(self):
        for v in self.properties.values():
            if v.name == "AnnotationRef":
                return True
        return False
    isAnnotated = property(
        _get_isAnnotated,
        doc="""Whether or not the model object is annotated.""")

    def _get_isNamed(self):
        for v in self.properties.values():
            if v.name == "Name" and not v.isUnique:
                return True
        return False
    isNamed = property(
        _get_isNamed,
        doc="""Whether or not the model object is named.""")

    def _get_isDescribed(self):
        for v in self.properties.values():
            if v.name == "Description":
                return True
        return False
    isDescribed = property(
        _get_isDescribed,
        doc="""Whether or not the model object is described.""")

    def _get_modelBaseType(self):
        """
        The base type is Object or String (Java) or None or std::string (C++).
        """
        base = self.element.getBase()
        if self.model.opts.lang.hasBaseType(base):
            base = self.model.opts.lang.baseType(base)
        if base is None and self.element.attrs['type'] != self.name:
            base = self.element.attrs['type']
        if base is None:
            base = self.model.opts.lang.getDefaultModelBaseClass()
        return base
    modelBaseType = property(
        _get_modelBaseType,
        doc="""The model object's base class.""")

    def _get_namespace(self):
        return self.element.namespace
    namespace = property(
        _get_namespace,
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
    baseObjectProperties = property(
        _get_baseObjectProperties,
        doc="""The model object's base object properties.""")

    def _get_langType(self):
        return self.name
    langType = property(_get_langType, doc="""The model object's type.""")

    def _get_langTypeNS(self):
        return "%s%s%s" % (self.model.opts.lang.omexml_model_package,
                           self.model.opts.lang.package_separator,
                           self.langType)
    langTypeNS = property(
        _get_langTypeNS, doc="""The model object's type with namespace.""")

    def _get_langBaseType(self):
        if self.model.opts.lang.hasType(self.base):
            return self.model.opts.lang.type(self.base)
        else:
            if self.base is not None:
                simpleType = self.model.getTopLevelSimpleType(self.base)
                parent = self.model.getObjectByName(self.base)
                if simpleType is not None:
                    return self.resolveLangTypeFromSimpleType(self.base)
                if parent is not None:
                    return parent.langBaseType
            return self.model.opts.lang.base_class
    langBaseType = property(
        _get_langBaseType, doc="The model object's base type.")

    def _get_langBaseTypeNS(self):
        name = self.langBaseType
        if isinstance(self.model.opts.lang, language.Java):
            name = "ome.xml.model.%s" % self.langBaseType
        if isinstance(self.model.opts.lang, language.CXX):
            name = "::ome::xml::model::%s" % self.langBaseType
        return name
    langBaseTypeNS = property(
        _get_langBaseTypeNS, doc="The model object's type with namespace.")

    def _get_instanceVariableName(self):
        name = None
        if self.isManyToMany:
            name = self.argumentName + 'Links'
        if name is None:
            try:
                if self.maxOccurs > 1:
                    name = self.lowerCasePrefix(self.plural)
            except AttributeError:
                pass
        if name is None:
            name = self.argumentName

        if isinstance(self.model.opts.lang, language.CXX):
            if (name == "namespace"):
                name = "namespace_"
            elif (name == "union"):
                name = "union_"

        return name

    instanceVariableName = property(
        _get_instanceVariableName,
        doc="""The property's instance variable name.""")

    def _get_instanceVariables(self):
        props = list()

        if self.langBaseType != self.model.opts.lang.base_class:
            props.append([
                self.langBaseType, "value", None, "Element's text data",
                False])
        for prop in self.properties.values():
            props.append([
                prop.instanceVariableType, prop.instanceVariableName,
                prop.instanceVariableDefault, prop.instanceVariableComment,
                prop.isUnitsEnumeration])
        return props
    instanceVariables = property(
        _get_instanceVariables,
        doc="""The instance variables of this class.""")

    def _get_header(self):
        header = None
        if self.name in self.model.opts.lang.model_type_map.keys():
            pass
        elif isinstance(self.model.opts.lang, language.Java):
            header = "ome.xml.model.%s" % self.name
        elif isinstance(self.model.opts.lang, language.CXX):
            path = re.sub("::", "/", self.name)
            header = "ome/xml/model/%s.h" % path
        return header
    header = property(
        _get_header,
        doc="The model object's include/import name. "
        "Does not include dependent headers.")

    def _get_header_deps(self):
        deps = set()

        myself = None

        if isinstance(self.model.opts.lang, language.Java):
            myself = "ome.xml.model.%s" % self.langBaseType
            if self.parentName is not None:
                deps.add("ome.xml.model.%s" % self.parentName)
        elif isinstance(self.model.opts.lang, language.CXX):
            if self.langBaseType is not None:
                path = re.sub("::", "/", self.langBaseType)
                myself = "ome/xml/model/%s.h" % path
            if (self.parentName is not None and
                    self.parentName != self.model.opts.lang.base_class):
                path = re.sub("::", "/", self.parentName)
                deps.add("ome/xml/model/%s.h" % path)

        for prop in self.properties.values():
            for dep in prop.header_dependencies:
                deps.add(dep)

        if myself in deps:
            deps.remove(myself)

        return sorted(deps)
    header_dependencies = property(
        _get_header_deps,
        doc="""The object's dependencies for include/import in headers.""")

    def _get_source_deps(self):
        deps = set()

        if self.name in self.model.opts.lang.model_type_map.keys():
            pass
        elif isinstance(self.model.opts.lang, language.Java):
            pass
        elif isinstance(self.model.opts.lang, language.CXX):
            path = re.sub("::", "/", self.name)
            deps.add("ome/xml/model/%s.h" % path)
            deps.add("ome/xml/model/OMEModel.h")

        for prop in self.properties.values():
            deps.update(prop.source_dependencies)

        return sorted(deps)
    source_dependencies = property(
        _get_source_deps,
        doc="""The object's dependencies for include/import in sources.""")

    def _get_fwd(self):
        fwd = set()

        if self.name in self.model.opts.lang.model_type_map.keys():
            pass
        elif isinstance(self.model.opts.lang, language.Java):
            pass
        elif isinstance(self.model.opts.lang, language.CXX):
            fwd.add("OMEModel")

        for prop in self.properties.values():
            for f in prop.forward:
                fwd.add(f)
        if self.name in fwd:
            fwd.remove(self.name)

        return sorted(fwd)
    forward = property(
        _get_fwd, doc="The object's forward declarations for cycle breaking.")

    def _get_parents(self):
        return self.model.resolve_parents(self.name)
    parents = property(
        _get_parents, doc="""The parents for this object.""")

    def _get_parent(self):
        parents = self.model.resolve_parents(self.name)

        parent = None

        if parents is not None:
            parent = self.model.getObjectByName(parents.keys()[0])

        return parent

    def _get_parentName(self):
        parent = self._get_parent()
        name = self.modelBaseType

        if (parent is not None and parent.isAbstractProprietary and
                self.name not in config.ANNOTATION_OVERRIDE):
            name = parent.name

        return name
    parentName = property(
        _get_parentName, doc="""The parent class name for this object.""")

    def _get_isParentAbstractProprietary(self):
        parent = self._get_parent()

        abstract = False

        if (parent is not None and parent.isAbstractProprietary and
                self.name not in config.ANNOTATION_OVERRIDE):
            abstract = True

        return abstract
    isParentAbstractProprietary = property(
        _get_isParentAbstractProprietary,
        doc="""Returns whether or not the model object has an abstract"""
        """ proprietary parent.""")

    def __str__(self):
        return self.__repr__()

    def __repr__(self):
        return '<"%s" OMEModelObject instance at 0x%x>' % (self.name, id(self))
