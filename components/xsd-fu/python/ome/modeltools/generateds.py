from __future__ import absolute_import

import sys
import keyword
import logging

# The generateDS package and our generateds module
# collide on case-insensitive file systems.
import generateDS.generateDS
from ome.modeltools.exceptions import ModelProcessingError
from xml import sax
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

    logging.debug("Namespace: %s" % namespace)
    set_type_constants(namespace)
    generateDS.generateDS.XsdNameSpace = namespace
    logging.debug("Type map: %s" % opts.lang.type_map)

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
    return OMEModel.process(ch, opts)


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
