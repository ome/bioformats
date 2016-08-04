import logging

from ome.modeltools import config
from ome.modeltools import language
from ome.modeltools.exceptions import ModelProcessingError


class OMEModelEntity(object):
    """
    An abstract root class for properties and model objects containing
    common type resolution and text processing functionality.
    """

    def resolveLangTypeFromSimpleType(self, simpleTypeName):
        getSimpleType = self.model.getTopLevelSimpleType
        while True:
            simpleType = getSimpleType(simpleTypeName)
            if simpleType is None:
                logging.debug("No simpleType found with name: %s"
                              % simpleTypeName)
                # Handle cases where the simple type is prefixed by
                # a namespace definition. (ex. OME:LSID).
                namespaceless = simpleTypeName.split(':')[-1]
                if namespaceless != simpleTypeName:
                    simpleTypeName = namespaceless
                    continue
                break
            logging.debug("%s simpleType dump: %s"
                          % (self, simpleType.__dict__))
            # It's possible the simpleType is a union of other
            # simpleTypes so we need to handle that. We assume
            # that all the unioned simpleTypes are of the same
            # base type (ex. "xsd:string" or "xsd:float").
            if simpleType.unionOf:
                union = getSimpleType(simpleType.unionOf[0])
                if self.model.opts.lang.hasType(union.getBase()):
                    return self.model.opts.lang.type(union.getBase())
                else:
                    simpleTypeName = union.getBase()
            if self.model.opts.lang.hasType(simpleType.getBase()):
                return self.model.opts.lang.type(simpleType.getBase())
            else:
                simpleTypeName = simpleType.getBase()
            # TODO: The above logic looks wrong.  simpleTypeName is
            # asigned but not used and then nothing is returned.

    def lowerCasePrefix(self, v):
        if v is None:
            raise ModelProcessingError(
                'Cannot lower case %s on %s' % (v, self.name))
        match = config.PREFIX_CASE_REGEX.match(v)
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
        suffix = config.PACKAGE_NAMESPACE_RE.match(namespace).group(1)
        try:
            if self.isEnumeration:
                suffix = 'enum'
        except AttributeError:
            pass
        try:
            return config.OMERO_PACKAGE_OVERRIDES[suffix]
        except:
            return "%s.%s" % (config.OMERO_DEFAULT_PACKAGE, suffix.lower())
    omeroPackage = property(
        _get_omeroPackage,
        doc="""The OMERO package of the entity.""")

    def _get_argumentName(self):
        argumentName = config.REF_REGEX.sub('', self.name)
        argumentName = self.lowerCasePrefix(argumentName)
        if isinstance(self.model.opts.lang, language.CXX):
            if (argumentName == "namespace"):
                argumentName = "namespace_"
            elif (argumentName == "union"):
                argumentName = "union_"
        return argumentName
    argumentName = property(
        _get_argumentName,
        doc="""The property's argument name (camelCase).""")

    def _get_methodName(self):
        try:
            name = config.BACK_REFERENCE_NAME_OVERRIDE[self.key]
            return name[0].upper() + name[1:]
        except (KeyError, AttributeError):
            pass
        return config.BACKREF_REGEX.sub(
            '', config.REF_REGEX.sub('', self.name))
    methodName = property(
        _get_methodName,
        doc="""The property's method name.""")

    def _get_isGlobal(self):
        isGlobal = self._isGlobal
        try:
            if self.isBackReference:
                ref = self.model.getObjectByName(
                    config.BACKREF_REGEX.sub('', self.type))
                if ref.name == self.name:
                    return isGlobal
                return isGlobal or ref.isGlobal
        except AttributeError:
            pass
        if self.isReference:
            ref = self.model.getObjectByName(
                config.REF_REGEX.sub('', self.type))
            if ref.name == self.name:
                return isGlobal
            isGlobal = isGlobal or ref.isGlobal
        return isGlobal
    isGlobal = property(
        _get_isGlobal,
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
    isManyToMany = property(
        _get_isManyToMany,
        doc="""Whether or not the entity is a many-to-many reference.""")

    def _get_isSettings(self):
        return self.name.endswith('Settings')
    isSettings = property(
        _get_isSettings,
        doc="""Whether or not the entity is a Settings reference.""")
