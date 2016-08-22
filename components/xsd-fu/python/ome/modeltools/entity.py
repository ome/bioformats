# Copyright (C) 2009 - 2016 Open Microscopy Environment:
#   - Board of Regents of the University of Wisconsin-Madison
#   - Glencoe Software, Inc.
#   - University of Dundee
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

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
