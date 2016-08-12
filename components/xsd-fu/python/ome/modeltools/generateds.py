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

from __future__ import absolute_import

import os
import sys
import keyword
import logging

# The generateDS package and our generateds module
# collide on case-insensitive file systems.
import generateDS.generateDS
from ome.modeltools.exceptions import ModelProcessingError
from xml import sax
from xml.etree import ElementTree
from ome.modeltools.model import OMEModel

XschemaHandler = generateDS.generateDS.XschemaHandler
set_type_constants = generateDS.generateDS.set_type_constants


def parse(opts):
    """
    Entry point for XML Schema parsing into an OME Model.
    """
    # The following two statements are required to "prime" the generateDS
    # code and ensure we have reasonable namespace support.
    filenames = opts.args
    namespace = opts.namespace

    schemas = dict()

    logging.debug("Namespace: %s" % namespace)
    set_type_constants(namespace)
    generateDS.generateDS.XsdNameSpace = namespace
    logging.debug("Type map: %s" % opts.lang.type_map)

    parser = sax.make_parser()
    ch = XschemaHandler()
    parser.setContentHandler(ch)
    for filename in filenames:
        parser.parse(filename)

        schemaname = os.path.split(filename)[1]
        schemaname = os.path.splitext(schemaname)[0]
        schema = ElementTree.parse(filename)
        schemas[schemaname] = schema

    root = ch.getRoot()
    if root is None:
        raise ModelProcessingError(
            "No model objects found, have you set the correct namespace?")
    root.annotate()
    return OMEModel.process(ch, schemas, opts)


def reset():
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
