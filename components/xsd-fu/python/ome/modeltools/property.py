import logging
import re

from xml.etree import ElementTree

from ome.modeltools.entity import OMEModelEntity
from ome.modeltools import config
from ome.modeltools import language
from ome.modeltools.exceptions import ModelProcessingError


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
    maxOccurs = property(
        _get_maxOccurs,
        doc="""The maximum number of occurrences for this property.""")

    def _get_minOccurs(self):
        if self.isAttribute:
            if self.delegate.getUse() == "optional":
                return 0
            return 1
        if (hasattr(self.delegate, 'choice')
                and self.delegate.choice is not None):
            return self.delegate.choice.getMinOccurs()
        return self.delegate.getMinOccurs()
    minOccurs = property(
        _get_minOccurs,
        doc="""The minimum number of occurrences for this property.""")

    def _get_name(self):
        return self.delegate.getName()
    name = property(_get_name, doc="""The property's name.""")

    def _get_namespace(self):
        if self.isReference:
            ref = self.model.getObjectByName(
                config.REF_REGEX.sub('', self.type))
            return ref.namespace
        if self.isAttribute or self.isBackReference:
            return self.parent.namespace
        return self.delegate.namespace
    namespace = property(
        _get_namespace, doc="""The root namespace of the property.""")

    def _get_instanceType(self):
        """
        Type for creating a real concrete instance of this specific
        element, without any additional type overrides.  Normally,
        this won't be needed.  Use only where it's essential not to
        have any implicit overrides substituted for the real type.
        """
        return self.name
    instanceType = property(
        _get_instanceType, doc="""The property's instance type.""")

    def _get_instanceTypeNS(self):
        name = self.instanceType
        if isinstance(self.model.opts.lang, language.CXX):
            if self.isEnumeration:
                name = ("%s::enums::%s"
                        % (self.model.opts.lang.omexml_model_package, name))
            elif (self.model.opts.lang.hasPrimitiveType(name) and
                  not self.model.opts.lang.hasFundamentalType(name) and
                  name != "std::string"):
                name = ("%s::primitives::%s"
                        % (self.model.opts.lang.omexml_model_package, name))
            elif (name != self.instanceType or
                  self.model.getObjectByName(self.instanceType) is not None):
                name = "%s::%s" % (self.model.opts.lang.omexml_model_package,
                                   name)

        return name
    instanceTypeNS = property(
        _get_instanceTypeNS, doc="""The property's type with namespace.""")

    def _get_langType(self):
        name = None

        if self.hasUnitsCompanion:
            name = self.unitsType

        # Hand back the type of enumerations
        if self.isEnumeration:
            langType = self.name
            if len(self.delegate.values) == 0:
                # As we have no directly defined possible values we have
                # no reason to qualify our type explicitly.
                name = self.type
            elif langType == "Type":
                # One of the OME XML unspecific "Type" properties which
                # can only be qualified by the parent.
                if self.type.endswith("string"):
                    # We've been defined entirely inline, prefix our
                    # type name with the parent type's name.
                    name = "%s%s" % (self.parent.name, langType)
                else:
                    # There's another type which describes us, use its name
                    # as our type name.
                    name = self.type
            else:
                name = langType
            # Handle XML Schema types that directly map to language types and
            # handle cases where the type is prefixed by a namespace
            # definition. (ex. OME:NonNegativeInt).
        else:
            # This sets name only for those types mentioned in the type_map
            # for the generated language. All other cases set name to None
            # so the following if block is executed
            name = self.model.opts.lang.type(self.type.replace('OME:', ''))

        if name is None:
            # Hand back the type of references or complex types with the
            # useless OME XML 'Ref' suffix removed.
            if (self.isBackReference or
                    (not self.isAttribute and self.delegate.isComplex())):
                name = config.REF_REGEX.sub('', self.type)
            # Hand back the type of complex types
            elif not self.isAttribute and self.delegate.isComplex():
                name = self.type
            elif not self.isEnumeration:
                # We have a property whose type was defined by a top level
                # simpleType.
                simpleTypeName = self.type
                name = self.resolveLangTypeFromSimpleType(simpleTypeName)
            else:
                logging.debug("%s dump: %s" % (self, self.__dict__))
                logging.debug("%s delegate dump: %s"
                              % (self, self.delegate.__dict__))
                raise ModelProcessingError(
                    "Unable to find %s type for %s" % (self.name, self.type))
        return name
    langType = property(_get_langType, doc="""The property's type.""")

    def _get_langTypeNS(self):
        name = self.langType
        if isinstance(self.model.opts.lang, language.CXX):
            if self.isEnumeration:
                name = ("%s::enums::%s"
                        % (self.model.opts.lang.omexml_model_package, name))
            elif (self.model.opts.lang.hasPrimitiveType(name) and
                  not self.model.opts.lang.hasFundamentalType(name) and
                  name != "std::string"):
                name = ("%s::primitives::%s"
                        % (self.model.opts.lang.omexml_model_package, name))
            elif (name != self.langType or
                  self.model.getObjectByName(self.langType) is not None):
                name = ("%s::%s"
                        % (self.model.opts.lang.omexml_model_package, name))

        return name
    langTypeNS = property(
        _get_langTypeNS, doc="""The property's type with namespace.""")

    def _get_metadataStoreArgType(self):
        mstype = None

        if self.hasUnitsCompanion:
            mstype = self.model.opts.lang.typeToUnitsType(
                self.unitsCompanion.metadataStoreArgType)

        if self.name == "Transform":
            if isinstance(self.model.opts.lang, language.Java):
                mstype = "AffineTransform"
            elif isinstance(self.model.opts.lang, language.CXX):
                # TODO: Handle different arg/mstype = types
                # TODO: Allow the model namespace to be configured
                # independently of the metadata namespace.
                mstype = ("const ::%s::AffineTransform&"
                          % (self.model.opts.lang.omexml_model_package))

        if isinstance(self.model.opts.lang, language.Java):
            if (mstype is None and not self.isPrimitive and
                    not self.isEnumeration):
                mstype = "String"
            if mstype is None:
                mstype = self.langType
        elif isinstance(self.model.opts.lang, language.CXX):
            if (mstype is None and not self.isPrimitive and
                    not self.isEnumeration):
                mstype = "const std::string&"
            if mstype is None:
                mstype = self.langTypeNS
        return mstype
    metadataStoreArgType = property(
        _get_metadataStoreArgType,
        doc="""The property's MetadataStore argument type.""")

    def _get_metadataStoreRetType(self):
        mstype = None

        if self.hasUnitsCompanion:
            mstype = self.model.opts.lang.typeToUnitsType(
                self.unitsCompanion.metadataStoreRetType)

        if self.name == "Transform":
            if isinstance(self.model.opts.lang, language.Java):
                mstype = "AffineTransform"
            elif isinstance(self.model.opts.lang, language.CXX):
                # TODO: Handle different arg/mstype = types
                # TODO: Allow the model namespace to be configured
                # independently of the metadata namespace.
                mstype = ("const ::%s::AffineTransform&"
                          % (self.model.opts.lang.omexml_model_package))

        if isinstance(self.model.opts.lang, language.Java):
            if (mstype is None and not self.isPrimitive and
                    not self.isEnumeration):
                mstype = "String"
            if mstype is None:
                mstype = self.langType
        elif isinstance(self.model.opts.lang, language.CXX):
            if (mstype is None and not self.isPrimitive and
                    not self.isEnumeration):
                mstype = "const std::string&"
            if mstype is None:
                mstype = self.langTypeNS
        return mstype
    metadataStoreRetType = property(
        _get_metadataStoreRetType,
        doc="""The property's MetadataStore return type.""")

    def _get_isAnnotation(self):
        if self.isReference:
            ref = config.REF_REGEX.sub('', self.type)
            ref = self.model.getObjectByName(ref)
            return ref.isAnnotation
        return False
    isAnnotation = property(
        _get_isAnnotation,
        doc="""Whether or not the property is an Annotation.""")

    def _get_isPrimitive(self):
        if self.model.opts.lang.hasPrimitiveType(self.langType):
            return True
        return False
    isPrimitive = property(
        _get_isPrimitive,
        doc="""Whether or not the property's language type is a primitive.""")

    def _get_isEnumeration(self):
        v = self.delegate.getValues()
        if v is not None and len(v) > 0:
            return True
        return False
    isEnumeration = property(
        _get_isEnumeration,
        doc="""Whether or not the property is an enumeration.""")

    def _get_isUnitsEnumeration(self):
        if self.langType.startswith("Units"):
            return True
        return False
    isUnitsEnumeration = property(
        _get_isUnitsEnumeration,
        doc="""Whether or not the property is a units enumeration.""")

    def _get_hasUnitsCompanion(self):
        if self.name+"Unit" in self.parent.properties:
            return True
        return False
    hasUnitsCompanion = property(
        _get_hasUnitsCompanion,
        doc="""Whether or not the property has a units companion.""")

    def _get_unitsCompanion(self):
        if self.hasUnitsCompanion:
            return self.parent.properties[self.name+"Unit"]
        return None
    unitsCompanion = property(
        _get_unitsCompanion,
        doc="""The property's units companion.""")

    def _get_unitsType(self):
        if self.hasUnitsCompanion:
            return self.unitsCompanion.langType
        return None
    unitsType = property(
        _get_unitsType,
        doc="""The property's units type.""")

    def _get_isReference(self):
        o = self.model.getObjectByName(self.type)
        if o is not None:
            return o.isReference
        return self._isReference
    isReference = property(
        _get_isReference,
        doc="""Whether or not the property is a reference.""")

    def _get_possibleValues(self):
        return self.delegate.getValues()
    possibleValues = property(
        _get_possibleValues,
        doc="""If the property is an enumeration, its possible values.""")

    def _get_defaultValue(self):
        if "OTHER" in self.delegate.getValues():
            return "OTHER"
        else:
            return self.delegate.getValues()[0]
    defaultValue = property(
        _get_defaultValue,
        doc="""If the property is an enumeration, its default value.""")

    def _get_defaultXsdValue(self):
        return self.delegate.default
    defaultXsdValue = property(
        _get_defaultXsdValue,
        doc="""The default value, if any, that is set on the attribute.""")

    def _isShared(self):
        shared = False

        if isinstance(self.model.opts.lang, language.CXX):
            if (self.model.opts.lang.hasFundamentalType(self.langType) and
                    self.minOccurs > 0):
                pass
            elif self.isEnumeration:
                if self.minOccurs == 0:
                    shared = True
            elif self.isReference or self.isBackReference:
                pass
            elif self.maxOccurs == 1 and (
                    not self.parent.isAbstractProprietary or
                    self.isAttribute or not self.isComplex() or
                    not self.isChoice):
                if self.minOccurs == 0 or (
                        not self.model.opts.lang.hasPrimitiveType(
                            self.langType) and not self.isEnumeration):
                    shared = True
            elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
                shared = True

        return shared
    isShared = property(_isShared, doc="""The property's argument type.""")

    def _get_isWeak(self):
        weak = False

        if isinstance(self.model.opts.lang, language.CXX):
            if (self.model.opts.lang.hasFundamentalType(self.langType)
                    and self.minOccurs > 0):
                pass
            elif self.isEnumeration:
                pass
            elif self.isReference or self.isBackReference:
                weak = True
            elif self.maxOccurs == 1 and (
                    not self.parent.isAbstractProprietary or
                    self.isAttribute or not self.isComplex() or
                    not self.isChoice):
                pass
            elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
                pass

        return weak
    isWeak = property(_get_isWeak, doc="""The property's argument type.""")

    def _get_argType(self):
        itype = None

        if isinstance(self.model.opts.lang, language.Java):
            itype = self.langType
        elif isinstance(self.model.opts.lang, language.CXX):
            ns_sep = self.langTypeNS
            if ns_sep.startswith('::'):
                ns_sep = ' ' + ns_sep
            if (self.model.opts.lang.hasFundamentalType(self.langType) and
                    self.minOccurs > 0):
                itype = self.langTypeNS
            elif self.isEnumeration:
                if self.minOccurs == 0:
                    itype = "std::shared_ptr<%s>&" % ns_sep
                else:
                    itype = "const %s&" % self.langTypeNS
            elif self.isReference or self.isBackReference:
                itype = "std::weak_ptr<%s>&" % ns_sep
            elif self.maxOccurs == 1 and (
                    not self.parent.isAbstractProprietary or
                    self.isAttribute or not self.isComplex() or
                    not self.isChoice):
                if self.minOccurs == 0 or (
                        not self.model.opts.lang.hasPrimitiveType(
                            self.langType) and not self.isEnumeration):
                    itype = "std::shared_ptr<%s>&" % ns_sep
                else:
                    itype = "const %s&" % self.langTypeNS
            elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
                itype = "std::shared_ptr<%s>&" % ns_sep

        return itype
    argType = property(_get_argType, doc="""The property's argument type.""")

    def _get_retType(self):
        """
        Get the return type(s) of a property.  For Java only a single
        value is returned.  For C++, the return value is a map of
        qualifier (const or non-const) to return type.
        """

        itype = None

        if isinstance(self.model.opts.lang, language.Java):
            itype = self.langType
        elif isinstance(self.model.opts.lang, language.CXX):
            itype = self.langTypeNS
            ns_sep = self.langTypeNS
            if ns_sep.startswith('::'):
                ns_sep = ' ' + ns_sep
            if (self.model.opts.lang.hasFundamentalType(self.langType) and
                    self.minOccurs > 0):
                itype = {' const': self.langTypeNS}
            elif self.isEnumeration:
                if self.minOccurs == 0:
                    itype = {' const': "std::shared_ptr<const %s>" % ns_sep,
                             '':       "std::shared_ptr<%s>" % ns_sep}
                else:
                    itype = {' const': "const %s&" % self.langTypeNS,
                             '':       "%s&" % self.langTypeNS}
            elif self.isReference or self.isBackReference:
                itype = {' const': "std::weak_ptr<const %s>" % ns_sep,
                         '':       "std::weak_ptr<%s>" % ns_sep}
            elif self.maxOccurs == 1 and (
                    not self.parent.isAbstractProprietary or
                    self.isAttribute or not self.isComplex() or
                    not self.isChoice):
                if self.minOccurs == 0 or (
                        not self.model.opts.lang.hasPrimitiveType(
                            self.langType) and not self.isEnumeration):
                    itype = {' const': "std::shared_ptr<const %s>" % ns_sep,
                             '':       "std::shared_ptr<%s>" % ns_sep}
                else:
                    itype = {' const': "const %s&" % self.langTypeNS}
            elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
                itype = {' const': "std::shared_ptr<const %s>" % ns_sep,
                         '':      "std::shared_ptr<%s>" % ns_sep}

        return itype
    retType = property(_get_retType, doc="""The property's return type.""")

    def _get_assignableType(self):
        """
        Get the assignable type(s) of a property.  For Java only a
        single value is returned.  For C++, the return value is a map
        of qualifier (const or non-const) to assignable type.  The
        assignable type is a type which may be assigned to which is
        compatible with the property return type.  In the case of weak
        references, the assignment will convert to a strong reference.
        """

        itype = None

        if isinstance(self.model.opts.lang, language.Java):
            itype = self.langType
        elif isinstance(self.model.opts.lang, language.CXX):
            itype = self.langTypeNS
            ns_sep = self.langTypeNS
            if ns_sep.startswith('::'):
                ns_sep = ' ' + ns_sep
            if (self.model.opts.lang.hasFundamentalType(self.langType) and
                    self.minOccurs > 0):
                itype = {' const': self.langTypeNS}
            elif self.isEnumeration:
                if self.minOccurs == 0:
                    itype = {' const': "std::shared_ptr<const %s>" % ns_sep,
                             '':       "std::shared_ptr<%s>" % ns_sep}
                else:
                    itype = {' const': "const %s&" % self.langTypeNS,
                             '':       "%s&" % self.langTypeNS}
            elif self.isReference or self.isBackReference:
                itype = {' const': "std::shared_ptr<const %s>" % ns_sep,
                         '':       "std::shared_ptr<%s>" % ns_sep}
            elif self.maxOccurs == 1 and (
                    not self.parent.isAbstractProprietary or
                    self.isAttribute or not self.isComplex() or
                    not self.isChoice):
                if self.minOccurs == 0 or (
                        not self.model.opts.lang.hasPrimitiveType(
                            self.langType) and not self.isEnumeration):
                    itype = {' const': "std::shared_ptr<const %s>" % ns_sep,
                             '':       "std::shared_ptr<%s>" % ns_sep}
                else:
                    itype = {' const': "const %s&" % self.langTypeNS,
                             '':       "%s&" % self.langTypeNS}
            elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
                itype = {' const': "std::shared_ptr<const %s>" % ns_sep,
                         '':      "std::shared_ptr<%s>" % ns_sep}

        return itype
    assignableType = property(
        _get_assignableType, doc="""The property's assignable type.""")

    def _get_instanceVariableName(self):
        finalName = None
        name = self.argumentName
        if self.isManyToMany:
            if self.isBackReference:
                name = self.model.getObjectByName(self.type)
                name = name.instanceVariableName
                name = config.BACK_REFERENCE_NAME_OVERRIDE.get(self.key, name)
            finalName = name + 'Links'
        if finalName is None:
            try:
                if self.maxOccurs > 1:
                    plural = self.plural
                    if plural is None:
                        plural = self.model.getObjectByName(
                            self.methodName).plural
                        return self.lowerCasePrefix(plural)
            except AttributeError:
                pass
            if self.isBackReference:
                name = config.BACKREF_REGEX.sub('', name)
            finalName = name

        if isinstance(self.model.opts.lang, language.CXX):
            if (finalName == "namespace"):
                finalName = "namespace_"
            elif (finalName == "union"):
                finalName = "union_"

        return finalName
    instanceVariableName = property(
        _get_instanceVariableName,
        doc="""The property's instance variable name.""")

    def _get_instanceVariableType(self):
        itype = None

        if isinstance(self.model.opts.lang, language.Java):
            if self.hasUnitsCompanion:
                itype = self.model.opts.lang.typeToUnitsType(
                    self.unitsCompanion.instanceVariableType)
            elif self.isReference and self.maxOccurs > 1:
                itype = "List<%s>" % self.langTypeNS
            elif self.isBackReference and self.maxOccurs > 1:
                itype = "List<%s>" % self.langTypeNS
            elif self.isBackReference:
                itype = self.langTypeNS
            elif self.maxOccurs == 1 and (
                    not self.parent.isAbstractProprietary or
                    self.isAttribute or not self.isComplex() or
                    not self.isChoice):
                itype = self.langTypeNS
            elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
                itype = "List<%s>" % self.langTypeNS
        elif isinstance(self.model.opts.lang, language.CXX):
            ns_sep = self.langTypeNS
            if ns_sep.startswith('::'):
                ns_sep = ' ' + ns_sep
            if self.hasUnitsCompanion:
                itype = self.model.opts.lang.typeToUnitsType(
                    self.unitsCompanion.instanceVariableType)
            elif self.isReference and self.maxOccurs > 1:
                itype = "std::vector<std::weak_ptr<%s> >" % ns_sep
            elif self.isReference:
                itype = "std::weak_ptr<%s>" % ns_sep
            elif self.isBackReference and self.maxOccurs > 1:
                itype = "std::vector<std::weak_ptr<%s> >" % ns_sep
            elif self.isBackReference:
                itype = "std::weak_ptr<%s>" % ns_sep
            elif self.maxOccurs == 1 and (
                    not self.parent.isAbstractProprietary or
                    self.isAttribute or not self.isComplex() or
                    not self.isChoice):
                if self.minOccurs == 0 or (
                        not self.model.opts.lang.hasPrimitiveType(
                            self.langType) and not self.isEnumeration):
                    itype = "std::shared_ptr<%s>" % ns_sep
                else:
                    itype = self.langTypeNS
            elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
                itype = "std::vector<std::shared_ptr<%s> >" % ns_sep

        return itype
    instanceVariableType = property(
        _get_instanceVariableType,
        doc="""The property's Java instance variable type.""")

    def _get_instanceVariableDefault(self):
        idefault = None

        if isinstance(self.model.opts.lang, language.Java):
            if self.isReference and self.maxOccurs > 1:
                idefault = "ArrayList<%s>" % self.langType
            elif self.isBackReference and self.maxOccurs > 1:
                idefault = "ArrayList<%s>" % self.langType
            elif self.isBackReference:
                idefault = None
            elif self.maxOccurs == 1 and (
                    not self.parent.isAbstractProprietary or
                    self.isAttribute or not self.isComplex() or
                    not self.isChoice):
                idefault = None
            elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
                idefault = "ArrayList<%s>" % self.langType
        elif isinstance(self.model.opts.lang, language.CXX):
            ns_sep = self.langTypeNS
            if ns_sep.startswith('::'):
                ns_sep = ' ' + ns_sep
            if self.isReference and self.maxOccurs > 1:
                pass
            elif self.isBackReference and self.maxOccurs > 1:
                pass
            elif self.isBackReference:
                if self.isEnumeration:
                    if self.minOccurs == 0:
                        idefault = (
                            "std::shared_ptr<%s>(new %s(%s::%s))"
                            % (ns_sep, self.langTypeNS, self.langTypeNS,
                               self.defaultValue.upper()))
                    else:
                        idefault = (
                            "%s::%s"
                            % (self.langTypeNS, self.defaultValue.upper()))
                else:
                    pass
            elif self.maxOccurs == 1 and (
                    not self.parent.isAbstractProprietary or
                    self.isAttribute or not self.isComplex() or
                    not self.isChoice):
                if self.isEnumeration:
                    if self.minOccurs == 0:
                        pass
                    else:
                        idefault = (
                            "%s::%s"
                            % (self.langTypeNS, self.defaultValue.upper()))
                else:
                    pass
            elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
                pass

        return idefault
    instanceVariableDefault = property(
        _get_instanceVariableDefault,
        doc="""The property's Java instance variable type.""")

    def _get_instanceVariableComment(self):
        icomment = ("*** WARNING *** Unhandled or skipped property %s"
                    % self.name)

        if self.isReference and self.maxOccurs > 1:
            icomment = "%s reference (occurs more than once)" % self.name
        elif self.isReference:
            icomment = "%s reference" % self.name
        elif self.isBackReference and self.maxOccurs > 1:
            icomment = "%s back reference (occurs more than once)" % self.name
        elif self.isBackReference:
            icomment = "%s back reference" % self.name
        elif self.maxOccurs == 1 and (
                not self.parent.isAbstractProprietary or self.isAttribute or
                not self.isComplex() or not self.isChoice):
            icomment = "%s property" % self.name
        elif self.maxOccurs > 1 and not self.parent.isAbstractProprietary:
            icomment = "%s property (occurs more than once)" % self.name

        return icomment

    instanceVariableComment = property(
        _get_instanceVariableComment,
        doc="""The property's Java instance variable comment.""")

    def _get_header(self):
        header = None
        if self.name in self.model.opts.lang.model_type_map.keys():
            pass
        elif self.langType is None:
            pass
        elif isinstance(self.model.opts.lang, language.Java):
            if not self.model.opts.lang.hasPrimitiveType(self.langType):
                if self.isEnumeration:
                    header = "ome.xml.model.%s" % self.langType
                else:
                    header = "ome.xml.model.enums.%s" % self.langType
        elif isinstance(self.model.opts.lang, language.CXX):
            path = re.sub("::", "/", self.langType)
            if (not self.model.opts.lang.hasPrimitiveType(self.langType) and
                    not self.model.opts.lang.hasFundamentalType(self.langType)
                    and self.langType != "std::string"):
                if self.isEnumeration:
                    header = "ome/xml/model/enums/%s.h" % path
                else:
                    if self.isReference and self.maxOccurs > 1:
                        pass
                    elif self.isBackReference and self.maxOccurs > 1:
                        pass
                    elif self.isBackReference:
                        header = "ome/xml/model/%s.h" % path
                    elif self.maxOccurs == 1 and (
                            not self.parent.isAbstractProprietary or
                            self.isAttribute or not self.isComplex() or
                            not self.isChoice):
                        header = "ome/xml/model/%s.h" % path
                    elif (self.maxOccurs > 1 and
                            not self.parent.isAbstractProprietary):
                        pass
            elif (self.model.opts.lang.hasPrimitiveType(self.langType) and
                  not self.model.opts.lang.hasFundamentalType(self.langType)
                  and self.langType != "std::string"):
                header = "ome/xml/model/primitives/%s.h" % path
        return header
    header = property(
        _get_header,
        doc="The property's include/import name."
        " Does not include dependent headers.")

    def _get_header_deps(self):
        deps = set()
        h = self.header
        if h is not None:
            deps.add(h)
        return deps
    header_dependencies = property(
        _get_header_deps,
        doc="The property's dependencies for include/import in headers,"
        " including itself.")

    def _get_source_deps(self):
        deps = set()
        if self.name in self.model.opts.lang.model_type_map.keys():
            pass
        elif self.langType is None:
            pass
        elif isinstance(self.model.opts.lang, language.Java):
            if not self.model.opts.lang.hasPrimitiveType(self.langType):
                if self.isEnumeration:
                    deps.add("ome.xml.model.%s" % self.langType)
                else:
                    deps.add("ome.xml.model.enums.%s" % self.langType)
        elif isinstance(self.model.opts.lang, language.CXX):
            path = re.sub("::", "/", self.langType)
            if not self.model.opts.lang.hasPrimitiveType(self.langType):
                if self.isEnumeration:
                    deps.add("ome/xml/model/enums/%s.h" % path)
                else:
                    if self.isReference and self.maxOccurs > 1:
                        deps.add("ome/xml/model/%s.h" % path)
                    elif self.isBackReference and self.maxOccurs > 1:
                        deps.add("ome/xml/model/%s.h" % path)
                    elif self.isBackReference:
                        deps.add("ome/xml/model/%s.h" % path)
                    elif self.maxOccurs == 1 and (
                            not self.parent.isAbstractProprietary or
                            self.isAttribute or not self.isComplex() or
                            not self.isChoice):
                        deps.add("ome/xml/model/%s.h" % path)
                    elif (self.maxOccurs > 1 and
                            not self.parent.isAbstractProprietary):
                        deps.add("ome/xml/model/%s.h" % path)
                if self.isReference:
                    # Make sure that the reference is a real generated object.
                    o = self.model.getObjectByName("%sRef" % path)
                    if o is not None and o in self.model.objects.values():
                        deps.add("ome/xml/model/%sRef.h" % path)
            o = self.model.getObjectByName(self.name)
            if o is not None:
                path = re.sub("::", "/", self.name)
                deps.add("ome/xml/model/%s.h" % path)
                for prop in o.properties.values():
                    deps.update(prop.source_dependencies)

        return deps
    source_dependencies = property(
        _get_source_deps,
        doc="""The property's dependencies for include/import in sources.""")

    def _get_fwd(self):
        fwd = set()
        if self.name in self.model.opts.lang.model_type_map.keys():
            pass
        elif isinstance(self.model.opts.lang, language.CXX):
            if not self.model.opts.lang.hasPrimitiveType(self.langType):
                if not self.isEnumeration:
                    if self.isReference and self.maxOccurs > 1:
                        fwd.add(self.langType)
                    elif self.isBackReference and self.maxOccurs > 1:
                        fwd.add(self.langType)
                    elif self.isBackReference:
                        pass
                    elif self.maxOccurs == 1 and (
                            not self.parent.isAbstractProprietary or
                            self.isAttribute or not self.isComplex() or
                            not self.isChoice):
                        pass
                    elif (self.maxOccurs > 1 and
                            not self.parent.isAbstractProprietary):
                        fwd.add(self.langType)
        return fwd
    forward = property(
        _get_fwd,
        doc="""The property's forward declarations for cycle breaking .""")

    def isComplex(self):
        """
        Returns whether or not the property has a "complex" content type.
        """
        if self.isAttribute:
            raise ModelProcessingError(
                "This property is an attribute and has no content model!")
        # FIXME: This hack is in place because of the incorrect content
        # model in the XML Schema document itself for the "Description"
        # element.
        if self.name == "Description":
            return False
        return self.delegate.isComplex()

    def _get_isAbstractProprietary(self):
        o = self.model.getObjectByName(self.name)
        if o is None:
            return False
        return o.isAbstractProprietary
    isAbstractProprietary = property(
        _get_isAbstractProprietary,
        doc="""Is the property abstract proprietary.""")

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
