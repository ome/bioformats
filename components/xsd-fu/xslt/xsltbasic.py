#!/usr/bin/env python
# encoding: utf-8
"""
...
"""

#
#  Copyright (C) 2009 - 2016 Open Microscopy Environment. All rights reserved.
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions
#  are met:
#  1. Redistributions of source code must retain the above copyright
#     notice, this list of conditions and the following disclaimer.
#  2. Redistributions in binary form must reproduce the above copyright
#     notice, this list of conditions and the following disclaimer in the
#     documentation and/or other materials provided with the distribution.
#
#  THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
#  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
#  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
#  ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
#  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
#  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
#  OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
#  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
#  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
#  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
#  SUCH DAMAGE.

import unittest

from copy import deepcopy

# We're using lxml's ElementTree implementation for XML manipulation due to
# its XSLT integration.
from lxml.etree import Element

# Handle Python 2.5 built-in ElementTree
# try:
#     from xml.etree.ElementTree import XML, Element, SubElement, ElementTree
#     from xml.etree..ElementTree import dump
# except ImportError:
#     from elementtree.ElementTree import XML, Element, SubElement
#     from elementtree.ElementTree import ElementTree, dump


class XsltBasic(unittest.TestCase):

    # Create the XPath for the element in the scope of root or local.
    # and add attribute if supplied.
    def createXPath(self, scope, NS, elementName, attribute=None):
        if(scope == 'local'):
            scope = './'
        if(scope == 'root'):
            scope = './/'
        if(scope == 'all'):
            return '%s[@%s]' % (elementName, attribute)
        if(attribute is not None):
            return '%s{%s}%s[@%s]' % (scope, NS, elementName, attribute)
        return '%s{%s}%s' % (scope, NS, elementName)

    # return the name of the element without NameSpace e.g.
    # {NameSpace}elementName.
    def localName(self, elementTag):
        return elementTag[elementTag.find("}") + 1:]

    # if var is not none then remove trailing spaces and if '' then return
    # None.
    def stripStr(self, var):
        if(var is not None):
            if(var.strip() != ''):
                return var.strip()
        return None

    # Get all elements from rootElement in elementList in namespace NS.
    def getAllElements(self, rootElement, NS, elementList):
        returnList = []
        for elementName in elementList:
            elementXPath = self.createXPath('root', NS, elementName)
            foundElements = rootElement.findall(elementXPath)
            returnList.extend(foundElements)
        return returnList

    # Check that the elements in the exclusionList are not in the element.
    def checkElementsExcluded(self, element, exclusionList):
        children = element.getchildren()
        for child in children:
            self.assertFalse(child in exclusionList)

    # Check that the attributes in element with Namespace NS are not in
    # exclusionList.
    def checkAttributesExcluded(self, root, NS, element, exclusionList):
        for attribute in exclusionList:
            xpath = self.createXPath('all', NS, element, attribute)
            self.assertTrue(len(root.findall(xpath)) == 0)

    # Check the alll the elements in oldRoot with namespace oldNS have been
    # mapped to newRoot with namespace newNS.
    # Rename those elements in renameMap.
    def checkElementsMapped(self, oldRoot, oldNS, newRoot, newNS, renameMap):
        for mappedElement in renameMap:
            oldXPath = self.createXPath('root', oldNS, mappedElement)
            newXPath = self.createXPath(
                'root', newNS, renameMap[mappedElement])
            oldElements = oldRoot.findall(oldXPath)
            newElements = newRoot.findall(newXPath)
            self.assertEqual(len(oldElements), len(newElements))
            self.assertTrue(len(newRoot.findall(oldXPath)) == 0)

    # Check the alll the elements in oldRoot with namespace oldNS have been
    # mapped to newRoot with namespace newNS.
    # Rename those elements in renameMap.
    def checkElementsMappedNoCount(self, oldRoot, oldNS, newRoot, newNS,
                                   renameMap):
        for mappedElement in renameMap:
            oldXPath = self.createXPath('root', oldNS, mappedElement)
            self.createXPath('root', newNS, renameMap[mappedElement])
            oldElements = oldRoot.findall(oldXPath)
            self.assertTrue(len(oldElements) == 0)

    # Compare Elements in oldElement with the NameSpace oldElementNS to the
    # attributes with the same name in newElement.
    # Don't compare those elements in the exceptionList.
    # Rename those attributes in the renameMap.
    def compareElementsWithAttributes(self, oldElement, oldElementNS,
                                      newElement, exceptionList=None,
                                      renameMap=None):
        for oldChildElement in oldElement.getchildren():
            elementName = self.localName(oldChildElement.tag)
            if(exceptionList is not None):
                if(elementName in exceptionList):
                    continue
            mappedName = elementName
            if(renameMap is not None):
                if(elementName in renameMap):
                    mappedName = renameMap[mappedName]
            newValue = newElement.get(mappedName)
            self.assertFalse(newValue is None)
            self.assertEquals(newValue, self.stripStr(oldChildElement.text))

    # Compare Elements in left with the attributes in right if they are in
    # comparison map.
    def compareElementsWithAttributesFromMap(self, left, right,
                                             comparisonMap):
        for leftChild in left.getchildren():
            leftChildName = self.localName(leftChild.tag)
            if(leftChildName not in comparisonMap):
                continue
            mappedName = comparisonMap[leftChildName]
            newValue = right.get(mappedName)
            self.assertFalse(newValue is None)
            self.assertEquals(newValue, self.stripStr(leftChild.text))

    # Check that the element contains the elements in containsList
    def containsElements(self, element, NS, containsList):
        containsMap = {}
        for name in containsList:
            containsMap[name] = False
        for child in element.getchildren():
            elementName = self.localName(child.tag)
            if(elementName in containsMap):
                containsMap[elementName] = True
        for key in containsMap:
            self.assertEquals(containsMap[key], True)

    # Check that the element contains the elements in containsMap with the
    # values in the map
    def containsElementsWithValues(self, element, NS, containsMap):
        equalsMap = {}
        for key in containsMap:
            equalsMap[key] = False
        for child in element.getchildren():
            elementName = self.localName(child.tag)
            if(elementName in containsMap):
                if(containsMap[elementName] == self.stripStr(child.text)):
                    equalsMap[elementName] = True
        for key in equalsMap:
            self.assertEquals(equalsMap[key], True)

    # Check that the element contains the attributes in containsList
    def containsAttributes(self, element, containsList):
        containsMap = {}
        for name in containsList:
            containsMap[name] = False
        for attribute in element.attrib.keys():
            if(attribute in containsMap):
                containsMap[attribute] = True
        for key in containsMap:
            self.assertEquals(containsMap[key], True)

    # Check that the element contains the attributes in containsMap and the
    # values in the map match the values in the element.
    def containsAttributesWithValues(self, element, containsMap):
        equalsMap = {}
        for key in containsMap:
            equalsMap[key] = False
        for attribute in element.attrib.keys():
            if(attribute in containsMap):
                if(containsMap[attribute] == element.get(attribute)):
                    equalsMap[attribute] = True
        for key in equalsMap:
            self.assertEquals(equalsMap[key], True)

    # Get elements in list as a map from element [name:value], removing
    # namespace.
    def getElementsAsMap(self, element):
        childMap = {}
        for child in element.getchildren():
            childMap[self.localname(child.tag)] = child.text
        return childMap

    # Get attributes in list as a map from element [name:value].
    def getElementsAsMap2(self, element):
        attributeMap = {}
        for attribute in element.attrib.keys():
            attributeMap[attribute] = element.get(attribute)
        return attributeMap

    # Compare elements from oldElement in oldElement NameSpace to the
    # newElement in newElement NameSpace.
    # Don't compare those elements in the exceptionList list.
    # Rename those elements in the renameMap.
    def compareElements(self, oldElement, oldElementNS, newElement,
                        newElementNS, exceptionList=None, renameMap=None,
                        inclusionList=None):
        inclusionMap = {}
        if (inclusionList is not None):
            for elem in inclusionList:
                inclusionMap[elem] = False
        for oldChildElement in oldElement.getchildren():
            elementName = self.localName(oldChildElement.tag)
            if (exceptionList is not None):
                if (elementName in exceptionList):
                    continue
            mappedName = elementName
            if renameMap is not None:
                if(elementName in renameMap):
                    mappedName = renameMap[elementName]
            if(elementName in inclusionMap):
                inclusionMap[elementName] = True
            newChildXPath = self.createXPath(
                'local', newElementNS, mappedName)
            newChildElement = newElement.find(newChildXPath)
            self.assertFalse(newChildElement, None)
            self.assertEquals(self.stripStr(newChildElement.text),
                              self.stripStr(oldChildElement.text))
        for key in inclusionMap:
            self.assertEquals(inclusionMap[key], True)

    # Compare attributes from oldElement to new element
    # Don't compare those elements in the exceptionList.
    # Rename those elements in the renameMap.
    def compareAttributes(self, oldElement, newElement, exceptionList=None,
                          renameMap=None):
        for key in oldElement.attrib.keys():
            if exceptionList is not None:
                if(key in exceptionList):
                    continue
            mappedKey = key
            if renameMap is not None:
                if(key in renameMap):
                    mappedKey = renameMap[key]
            newValue = newElement.get(mappedKey)
            oldValue = oldElement.get(key)
            if(oldValue != newValue):
                print 'FAILURE in xsltbasic.compareAttributes'
                print 'EXCEPTIONLIST %s' % exceptionList
                print 'oldElement.tag %s' % oldElement.tag
                print 'newElement.tag %s' % newElement.tag
                print 'key %s' % key
                print 'old %s' % oldValue
                print 'new %s' % newValue
                print 'END FAILURE'
            self.assertEquals(newValue, oldValue)

    # Get all the child elements from the element, in namespace.
    # Exclude thoses child elements in the exclusions list.
    def getChildElements(self, element, elementNS, exceptionList):
        childList = []
        for child in element.getchildren():
            name = self.localName(child.tag)
            if(name not in exceptionList):
                childList.append(name)
        return childList

    # Return true if the attributes in the elements left and right match and
    # the number if children match.
    def elementsEqual(self, left, right, renamedAttributes):
        if self.localName(left.tag) != self.localName(right.tag):
            return False
        if(len(left) != len(right)):
            return False
        if(len(left.getchildren()) != len(right.getchildren())):
            return False
        for leftAttribute in left:
            if(renamedAttributes[leftAttribute] not in right):
                return False
            if(left[leftAttribute] != right[renamedAttributes[leftAttribute]]):
                return False
        return True

    # Select the element in rightList who's attributes match the element left.
    def getElementFromList(self, left, rightList, renamedAttributes):
        for right in rightList:
            if self.elementsEqual(left, right, renamedAttributes):
                return right
        return None

    # Compare graph's are same, the attributes and elements maybe renamed
    # using the renameAttributes and renameElements map, this method assumes
    # that the graphs are in the same element order.
    def compareGraphs(self, left, right, ignoreAttributes=None,
                      renameAttributes=None, renameElements=None):
        leftChildren = left.getchildren()
        rightChildren = right.getchildren()
        self.assertEqual(len(leftChildren), len(rightChildren))
        if len(leftChildren) == 0:
            return
        for i in range(len(leftChildren)):
            self.compareAttributes(leftChildren[i], rightChildren[i],
                                   ignoreAttributes, renameAttributes)
            if renameElements is None:
                self.assertEqual(self.localName(leftChildren[i].tag),
                                 self.localName(rightChildren[i].tag))
            else:
                leftChildTag = self.localName(leftChildren[i].tag)
                if(leftChildTag in renameElements):
                    leftChildTag = renameElements[leftChildTag]
                self.assertEqual(leftChildTag,
                                 self.localName(rightChildren[i].tag))
            self.assertEqual(self.stripStr(leftChildren[i].text),
                             self.stripStr(rightChildren[i].text))
            self.compareGraphs(
                leftChildren[i], rightChildren[i], ignoreAttributes,
                renameAttributes, renameElements)

    # Compare graph's are same, the attributes and elements maybe renamed
    # using the renameAttributes and renameElements map, this method assumes
    # that the graphs are in the same element order.
    def compareGraphsWithoutOrder(self, left, right, renameAttributes=None,
                                  renameElements=None, ignoreAttributes=None):
        leftChildren = left.getchildren()
        rightChildren = right.getchildren()
        self.assertEqual(len(leftChildren), len(rightChildren))
        if len(leftChildren) == 0:
            return
        for i in range(len(leftChildren)):
            rightChild = self.getElementFromList(
                leftChildren[i], rightChildren, renameAttributes)
            self.assertTrue(rightChild is not None)
            if renameElements is None:
                self.assertEqual(self.localName(leftChildren[i].tag),
                                 self.localName(rightChild.tag))
            else:
                self.assertEqual(
                    renameElements[self.localName(leftChildren[i].tag)],
                    self.localName(rightChild.tag))
            self.assertEqual(self.stripStr(leftChildren[i].text),
                             self.stripStr(rightChild.text))
            self.compareGraphsWithoutOrder(
                leftChildren[i], rightChild, ignoreAttributes,
                renameAttributes, renameElements)

    # get the name of a reference, by removing the Ref suffix.
    def elementRefName(self, name):
        return name[:len(name)-3]

    # return true if the element is a reference, has Ref suffix.
    def isRef(self, element):
        return (element.tag[len(element.tag)-3:] == 'Ref')

    # return the elemenet in the root tree with name element name and id
    # if it does not exist it will return None
    def findElementByID(self, root, NS, elementName, id):
        elements = self.getAllElements(root, NS, [elementName])
        for element in elements:
            if element.get('ID') == id:
                return element
        return None

    # create a new element based on the element param, this will copy the
    # element tag, and attribs but not children. To copy children use
    # deepcopy.
    def shallowcopy(self, element):
        newElement = Element(element.tag)
        newElement.text = element.text
        for key in element.keys():
            newElement.set(key, element.get(key))
        return newElement

    # Replace the references in elemenet with the full element from root, this
    # method only works on the children of the element, to replace all
    # references in element use replaceRefsWithElementRecurse.
    # If RefList is not empty it will only replace the References in RefList.
    # The elements in RefList should only be the name of the element, ROI not
    # ROIRef.
    def replaceRefsWithElements(self, root, NS, element, RefList=None):
        if RefList is None:
            RefList = []
        newElement = self.shallowcopy(element)
        children = element.getchildren()
        if len(children) == 0:
            return
        for i, child in enumerate(children):
            elementName = self.elementRefName(self.localName(child.tag))
            if (self.isRef(child) and elementName in RefList):
                elementFromRef = self.findElementByID(
                    root, NS, elementName, child.get('ID'))
                newElement.append(deepcopy(elementFromRef))
            else:
                newElement.append(deepcopy(child))
        return newElement

    # Replace the references in elemenet with the full element from root, this
    # method works to replace all references in element.
    # If RefList is not empty it will only replace the References in RefList.
    # The elements in RefList should only be the name of the element, ROI not
    # ROIRef.
    def replaceRefsWithElementsRecurse(self, root, NS, element, RefList=None):
        if RefList is None:
            RefList = []
        newElement = self.shallowcopy(element)
        children = element.getchildren()
        if len(children) == 0:
            return newElement
        for i, child in enumerate(children):
            elementName = self.elementRefName(self.localName(child.tag))
            if(self.isRef(child) and elementName in RefList):
                elementFromRef = self.findElementByID(
                    root, NS, elementName, child.get('ID'))
                newElement.append(deepcopy(elementFromRef))
            else:
                newElement.append(self.replaceRefsWithElementsRecurse(
                    root, NS, child, RefList))
        return newElement

    # Move the child elements from removeElement to removeElements parent
    # element and remove it from the element.
    def moveElementsFromChildToParent(self, element, NS, removeElement):
        returnElement = deepcopy(element)

        xpath = self.createXPath('root', NS, removeElement)
        elementsToRemove = returnElement.findall(xpath)
        for elementToRemove in elementsToRemove:
            elementsParent = elementToRemove.getparent()
            elementsParent.remove(elementToRemove)
            for child in elementToRemove.getchildren():
                elementsParent.append(child)
        return returnElement
