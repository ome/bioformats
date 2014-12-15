"""
Object model and helper classes used in the generation of classes from
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

from util import odict
import logging

from ome.modeltools.object import OMEModelObject
from ome.modeltools.property import OMEModelProperty
from ome.modeltools.exceptions import ModelProcessingError

from ome.modeltools import config


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


class OMEModel(object):
    def __init__(self, opts):
        self.opts = opts
        self.elementNameObjectMap = dict()
        self.objects = odict()
        self.parents = dict()

    def addObject(self, element, obj):
        elementName = element.getName()
        if element in self.objects:
            raise ModelProcessingError(
                "Element %s has been processed!" % element)
        if elementName in self.elementNameObjectMap:
            if (elementName == "EmissionFilterRef" or
                    elementName == "ExcitationFilterRef"):
                pass
            else:
                logging.warn(
                    "Element %s has duplicate object with same name,"
                    " skipping!" % element)
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

    def getAllHeaders(self):
        headers = set()
        for o in self.objects.values():
            h = o.header_dependencies
            if h is not None:
                headers.union(h)
        return sorted(headers)

    def getEnumHeaders(self):
        headers = set()
        for obj in self.objects.values():
            for prop in obj.properties.values():
                if not prop.isEnumeration:
                    continue
                h = prop.header
                if h is not None:
                    headers.add(h)
        return sorted(headers)

    def getObjectHeaders(self):
        headers = set()
        for obj in self.objects.values():
            h = obj.header
            if h is not None:
                headers.add(h)
        return sorted(headers)

    def processAttributes(self, element):
        """
        Process all the attributes for a given element (a leaf).
        """
        attributes = element.getAttributeDefs()
        obj = self.getObject(element)
        length = len(attributes)
        for i, key in enumerate(attributes):
            logging.debug("Processing attribute: %s %d/%d"
                          % (key, i + 1, length))
            attribute = attributes[key]
            logging.debug("Dump: %s" % attribute.__dict__)
            obj.addAttribute(attribute)

        children = element.getChildren()
        length = len(children)
        for i, child in enumerate(children):
            logging.debug("Processing child: %s %d/%d"
                          % (child, i + 1, length))
            logging.debug("Dump: %s" % child.__dict__)
            obj.addElement(child)

    def processLeaf(self, element, parent):
        """
        Process an element (a leaf).
        """
        e = element
        logging.debug("Processing leaf (topLevel? %s): (%s) --> (%s)"
                      % (e.topLevel, parent, e))
        e_name = e.getName()
        e_type = e.getType()
        if parent is not None:
            if e_name not in self.parents:
                self.parents[e_name] = list()
            self.parents[e_name].append(parent.getName())
        if (not e.isExplicitDefine()
            and (e_name not in config.EXPLICIT_DEFINE_OVERRIDE and
                 not e.topLevel)):
            logging.info(
                "Element %s.%s not an explicit define, skipping."
                % (parent, e))
            return
        if e.getMixedExtensionError():
            logging.error(
                "Element %s.%s extension chain contains mixed and non-mixed"
                " content, skipping." % (parent, e))
            return
        if e_type != e_name and e_name not in config.EXPLICIT_DEFINE_OVERRIDE:
            logging.info(
                "Element %s.%s is not a concrete type (%s != %s), skipping."
                % (parent, e, e_type, e_name))
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
            logging.info("Processing element: %s %d/%d"
                         % (element, i + 1, length))
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
                if not prop.isReference and (
                        prop.isAttribute or prop.maxOccurs == 1
                        or o.name == 'OME' or o.isAbstractProprietary):
                    continue
                shortName = config.REF_REGEX.sub('', prop.type)
                try:
                    if o.name in config.BACK_REFERENCE_OVERRIDE[shortName]:
                        continue
                except KeyError:
                    pass
                if shortName not in references:
                    references[shortName] = list()
                v = {'data_type': o.name, 'property_name': prop.methodName,
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

    def process(klass, contentHandler, opts):
        """
        Main process entry point. All instantiations of this class should be
        made through this class method unless you really know what you are
        doing.
        """
        elements = contentHandler.getRoot().getChildren()
        model = klass(opts)
        model.topLevelSimpleTypes = contentHandler.topLevelSimpleTypes
        model.processTree(elements)
        model.postProcessReferences()
        return model
    process = classmethod(process)

    def resolve_parents(self, element_name):
        """
        Resolves the parents of an element and returns them as an ordered list.
        """
        parents = dict()
        try:
            my_parents = self.parents[element_name]
        except KeyError:
            return None
        for parent in my_parents:
            parents[parent] = self.resolve_parents(parent)
        return parents

    def _get_header_deps(self):
        deps = set()

        for o in self.objects.values():
            dep = o.header
            if dep is not None:
                deps.add(dep)

            deps.update(o.header_dependencies)

        return sorted(deps)
    header_dependencies = property(
        _get_header_deps,
        doc="""The model's dependencies for include/import in headers.""")
