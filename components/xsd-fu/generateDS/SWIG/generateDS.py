#!/usr/bin/env python

## LICENSE

## Copyright (c) 2002 Dave Kuhlman

## Permission is hereby granted, free of charge, to any person obtaining
## a copy of this software and associated documentation files (the
## "Software"), to deal in the Software without restriction, including
## without limitation the rights to use, copy, modify, merge, publish,
## distribute, sublicense, and/or sell copies of the Software, and to
## permit persons to whom the Software is furnished to do so, subject to
## the following conditions:

## The above copyright notice and this permission notice shall be
## included in all copies or substantial portions of the Software.

## THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
## EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
## MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
## IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
## CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
## TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
## SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.




from __future__ import generators   # only needed for Python 2.2

import sys, os.path
import getopt
from xml.sax import saxexts, saxlib, saxutils
from xml.sax import handler


##from IPython.Shell import IPythonShellEmbed
##IPShell = IPythonShellEmbed('-nosep',
##    banner = 'Entering interpreter.  Ctrl-D to exit.',
##    exit_msg = 'Leaving Interpreter.')

# Use the following line where and when you want to drop into the
# IPython shell:
#    IPShell(vars(), '<a msg>')

#
# Global variables etc.
#
DelayedElements = []
AlreadyGenerated = []
Force = 0
YamlGen = 0
NameTable = {
    'class': 'klass',
    'import': 'emport'
    }



def showLevel(outfile, level):
    for idx in range(level):
        outfile.write('    ')

#
# Representation of element definition.
#
class XschemaElement:
    def __init__(self, name, attrs):
        self.name = name
        self.cleanName = ''
        self.attrs = attrs
        self.children = []
        self.maxOccurs = 1
        self.complex = 0
        self.type = 'NoneType'
        self.attributeDefs = []

    def addChild(self, element):
        self.children.append(element)
    def getChildren(self): return self.children
    def getName(self): return self.name
    def getCleanName(self): return self.cleanName
    def getUnmappedCleanName(self): return self.unmappedCleanName
    def setName(self, name): self.name = name
    def getAttrs(self): return self.attrs
    def getMaxOccurs(self): return self.maxOccurs
    def getType(self): return self.type
    def isComplex(self): return self.complex
    def addAttributeDefs(self, attrs): self.attributeDefs.append(attrs)
    def getAttributeDefs(self): return self.attributeDefs
    def show(self, outfile, level):
        showLevel(outfile, level)
        outfile.write('Name: %s  Type: %s\n' % (self.name, self.type))
        showLevel(outfile, level)
        outfile.write('  - Complex: %d  MaxOccurs: %d\n' % \
            (self.complex, self.maxOccurs))
        showLevel(outfile, level)
        outfile.write('  - Attrs: %s\n' % self.attrs)
        showLevel(outfile, level)
        outfile.write('  - AttributeDefs: %s\n' % self.attributeDefs)
        for key in self.attributeDefs.keys():
            showLevel(outfile, level + 1)
            outfile.write('key: %s  value: %s\n' % \
                (key, self.attributeDefs[key]))
        for child in self.children:
            child.show(outfile, level + 1)

    def annotate(self):
        # If there is a namespace, replace it with an underscore.
        self.unmappedCleanName = self.name.replace(':', '_')
        self.cleanName = mapName(self.unmappedCleanName)
        if 'maxOccurs' in self.attrs.keys():
            max = self.attrs['maxOccurs']
            if max == 'unbounded':
                max = 99999
            else:
                try:
                    max = int(self.attrs['maxOccurs'])
                except ValueError:
                    sys.stderr.write('*** %s/%s  maxOccurs must be integer or "unbounded".' % \
                        (element.getName(), child.getName())
                        )
                    sys.exit(-1)
        else:
            max = 1
        self.maxOccurs = max
        if 'type' in self.attrs.keys():
            type1 = self.attrs['type']
            if type1 == 'xs:string' or \
                type1 == 'xs:integer' or \
                type1 == 'xs:float':
                self.complex = 0
            else:
                self.complex = 1
            self.type = self.attrs['type']
        else:
            self.complex = 1
            self.type = 'NoneType'
        # If it does not have a type, then make the type the same as the name.
        if self.type == 'NoneType' and self.name:
            self.type = self.name
        # Do it recursively for all descendents.
        for child in self.children:
            child.annotate()


#
# SAX handler
#
class XschemaHandler(handler.ContentHandler):
    def __init__(self):
        self.stack = []
        self.root = None
        self.inElement = 0
        self.inComplexType = 0
        self.inSequence = 0
        self.inChoice = 1

    def getRoot(self):
        return self.root

    def showError(self, msg):
        print msg
        sys.exit(-1)

    def startElement(self, name, attrs):
        #print '(startElement) name: %s len(stack): %d' % \
        #      (name, len(self.stack))
        if name == 'xs:element':
            self.inElement = 1
            if 'name' in attrs.keys() and 'type' in attrs.keys():
                if attrs['type'] == 'xs:string' or \
                       attrs['type'] == 'xs:integer' or \
                       attrs['type'] == 'xs:float':
                    element = XschemaElement(attrs['name'], attrs)
                    #self.stack[-1].addChild(element)
                else:
                    element = XschemaElement(attrs['name'], attrs)
            elif 'name' in attrs.keys():
                element = XschemaElement(attrs['name'], attrs)
            elif 'type' in attrs:
                element = XschemaElement('', attrs)
            else:
                element = XschemaElement('', attrs)
            self.stack.append(element)
        elif name == 'xs:complexType':
            self.inComplexType = 1
        elif name == 'xs:sequence':
            self.inSequence = 1
        elif name == 'xs:choice':
            self.inChoice = 1
        elif name == 'xs:attribute':
            self.inAttribute = 1
            if 'name' in attrs.keys():
                if not 'type' in attrs.keys():
                    attrs['type'] = 'xs:string'
                self.stack[-1].addAttributeDefs(attrs)
        elif name == 'xs:schema':
            self.inSchema = 1
            element = XschemaElement('root', {})
            self.stack.append(element)

    def endElement(self, name):
        if name == 'xs:element':
            self.inElement = 0
            element = self.stack.pop()
            self.stack[-1].addChild(element)
        elif name == 'xs:complexType':
            self.inComplexType = 0
        elif name == 'xs:sequence':
            self.inSequence = 0
        elif name == 'xs:choice':
            self.inChoice = 0
        elif name == 'xs:attribute':
            self.inAttribute = 0
        elif name == 'xs:schema':
            self.inSchema = 0
            if len(self.stack) != 1:
                print '*** error stack'
                sys.exit(-1)
            self.root = self.stack[0]

    def characters(self, chrs):
        if self.inElement:
            pass
        elif self.inComplexType:
            pass
        elif self.inSequence:
            pass
        elif self.inChoice:
            pass


#
# Code generation
#

def generateExportFn_1(outfile, child, name, fill):
    if child.getType() == 'xs:string':
        s1 = '%s        showIndent(outfile, level)\n' % fill
        outfile.write(s1)
        s1 = "%s        outfile.write('<%s>%%s</%s>\\n' %% self.get%s())\n" % \
            (fill, name, name, name.capitalize())
        outfile.write(s1)
    elif child.getType() == 'xs:integer':
        s1 = '%s        showIndent(outfile, level)\n' % fill
        outfile.write(s1)
        s1 = "%s        outfile.write('<%s>%%d</%s>\\n' %% self.get%s())\n" % \
            (fill, name, name, name.capitalize())
        outfile.write(s1)
    elif child.getType() == 'xs:float':
        s1 = '%s        showIndent(outfile, level)\n' % fill
        outfile.write(s1)
        s1 = "%s        outfile.write('<%s>%%f</%s>\\n' %% self.get%s())\n" % \
            (fill, name, name, name.capitalize())
        outfile.write(s1)
    else:
        s1 = "%s        if self.%s:\n" % (fill, name)
        outfile.write(s1)
        s1 = "%s            self.%s.export(outfile, level)\n" % (fill, name)
        outfile.write(s1)


def generateExportFn_2(outfile, child, name, fill):
    if child.getType() == 'xs:string':
        s1 = '%s        showIndent(outfile, level)\n' % fill
        outfile.write(s1)
        s1 = "%s        outfile.write('<%s>%%s</%s>\\n' %% %s)\n" % \
            (fill, name, name, name)
        outfile.write(s1)
    elif child.getType() == 'xs:integer':
        s1 = '%s        showIndent(outfile, level)\n' % fill
        outfile.write(s1)
        s1 = "%s        outfile.write('<%s>%%d</%s>\\n' %% %s)\n" % \
            (fill, name, name, name)
        outfile.write(s1)
    elif child.getType() == 'xs:float':
        s1 = '%s        showIndent(outfile, level)\n' % fill
        outfile.write(s1)
        s1 = "%s        outfile.write('<%s>%%f</%s>\\n' %% %s)\n" % \
            (fill, name, name, name)
        outfile.write(s1)
    else:
        s1 = "%s        %s.export(outfile, level)\n" % (fill, name)
        outfile.write(s1)


def generateExportFn(outfile, prefix, element):
    s1 = '    def export(self, outfile, level):\n'
    outfile.write(s1)
    s1 = '        showIndent(outfile, level)\n'
    outfile.write(s1)
    if len(element.getAttributeDefs()) > 0:
        s2 = ''
        s3 = ''
        for attr in element.getAttributeDefs():
            s2 += ' %s="%%s"' % (attr['name'],)
            s3 += 'self.%s, ' % attr['name']
        s1 = "        outfile.write('<%s%s>\\n' %% (%s))\n" % \
            (element.getName(), s2, s3)
        outfile.write(s1)
    else:
        s1 = "        outfile.write('<%s>\\n')\n" % element.getName()
        outfile.write(s1)
    s1 = "        level += 1\n"
    outfile.write(s1)
    for child in element.getChildren():
        name = child.getCleanName()
        if child.getMaxOccurs() > 1:
            s1 = "        for %s in self.%s:\n" % (name, name)
            outfile.write(s1)
            generateExportFn_2(outfile, child, name, '    ')
        else:
            generateExportFn_1(outfile, child, name, '')
    s1 = "        level -= 1\n"
    outfile.write(s1)
    s1 = '        showIndent(outfile, level)\n'
    outfile.write(s1)
    s1 = "        outfile.write('</%s>\\n')\n" % element.getName()
    outfile.write(s1)


#
# Generate export to YAML.
#
def generateExportYaml_1(outfile, child, name, fill):
    if child.getType() == 'xs:string' or \
            child.getType() == 'xs:integer' or \
            child.getType() == 'xs:float':
        s1 = "%s        childDict = {}\n" % fill
        outfile.write(s1)
        s1 = "%s        childDict['name'] = '%s'\n" % (fill, name)
        outfile.write(s1)
        s1 = "%s        childDict['text'] = self.%s\n" % (fill, name)
        outfile.write(s1)
        s1 = "%s        yamlChildren.append(childDict)\n" % fill
        outfile.write(s1)
    else:
        s1 = "%s        if self.%s:\n" % (fill, name)
        outfile.write(s1)
        s1 = "%s            yamlChild = self.%s.exportYaml()\n" % \
            (fill, name)
        outfile.write(s1)
        s1 = "%s            yamlChildren.append(yamlChild)\n" % fill
        outfile.write(s1)

def generateExportYaml_2(outfile, child, name, fill):
    if child.getType() == 'xs:string' or \
            child.getType() == 'xs:integer' or \
            child.getType() == 'xs:float':
        s1 = "%s        childDict = {}\n" % fill
        outfile.write(s1)
        s1 = "%s        childDict['name'] = '%s'\n" % (fill, name)
        outfile.write(s1)
        
        s1 = "%s        childDict['text'] = %s\n" % (fill, name)
        outfile.write(s1)
        s1 = "%s        yamlChildren.append(childDict)\n" % fill
        outfile.write(s1)
    else:
        s1 = "%s        yamlChild = %s.exportYaml()\n" % \
            (fill, name)
        outfile.write(s1)
        s1 = "%s        yamlChildren.append(yamlChild)\n" % fill
        outfile.write(s1)

def generateExportYaml(outfile, prefix, element):
    s1 = '    def exportYaml(self):\n'
    outfile.write(s1)
    s1 = '        objDict = {}\n'
    outfile.write(s1)
    s1 = "        objDict['name'] = '%s'\n" % element.getCleanName()
    outfile.write(s1)
    if len(element.getAttributeDefs()) > 0:
        s1 = '        attrDict = {}\n'
        outfile.write(s1)
        for attr in element.getAttributeDefs():
            s1 = "        attrDict['%s'] = self.%s\n" % \
                (attr['name'], attr['name'])
            outfile.write(s1)
        s1 = "        objDict['attributes'] = attrDict\n"
        outfile.write(s1)
    s1 = '        yamlChildren = []\n'
    outfile.write(s1)
    for child in element.getChildren():
        name = child.getCleanName()
        if child.getMaxOccurs() > 1:
            s1 = "        for %s in self.%s:\n" % (name, name)
            outfile.write(s1)
            generateExportYaml_2(outfile, child, name, '    ')
        else:
            generateExportYaml_1(outfile, child, name, '')
    s1 = "        if yamlChildren:\n"
    outfile.write(s1)
    s1 = "            objDict['children'] = yamlChildren\n"
    outfile.write(s1)
    s1 = "        return objDict\n"
    outfile.write(s1)


#
# Generate build method.
#
def generateBuildFn(outfile, prefix, element, delayed):
    outfile.write('    def build(self, node_):\n')
    outfile.write('        attrs = node_.attributes\n')
    for attr in element.getAttributeDefs():
        name = attr['name']
        if attr['type'] == 'xs:string':
            s1 = "        if attrs.get('%s'):\n" % name
            outfile.write(s1)
            s1 = "            self.%s = attrs.get('%s').value\n" % (name, name)
            outfile.write(s1)
        elif attr['type'] == 'xs:integer':
            s1 = "        if attrs.get('%s'):\n" % name
            outfile.write(s1)
            s1 = '            try:\n'
            outfile.write(s1)
            s1 = "                self.%s = int(attrs.get('%s').value)\n" % \
                (name, name)
            outfile.write(s1)
            s1 = '            except ValueError:\n'
            outfile.write(s1)
            s1 = '                pass\n'
            outfile.write(s1)
        elif attr['type'] == 'xs:float':
            s1 = "        if attrs.get('%s'):\n" % name
            outfile.write(s1)
            s1 = '            try:\n'
            outfile.write(s1)
            s1 = "                self.%s = float(attrs.get('%s').value)\n" % \
                (name, name)
            outfile.write(s1)
            s1 = '            except ValueError:\n'
            outfile.write(s1)
            s1 = '                pass\n'
            outfile.write(s1)
    outfile.write('        for child in node_.childNodes:\n')
    keyword = 'if'
    generatedOne = 0
    for child in element.getChildren():
        origName = child.getName()
        name = child.getCleanName()
        if child.getType() == 'xs:string':
            s1 = '            %s child.nodeType == Node.ELEMENT_NODE and \\\n' % \
                 keyword
            outfile.write(s1)
            s1 = "                child.nodeName == '%s':\n" % origName
            outfile.write(s1)
            s1 = "                %s = ''\n" % name
            outfile.write(s1)
            s1 = "                for text_ in child.childNodes:\n"
            outfile.write(s1)
            s1 = "                    %s += text_.nodeValue\n" % name
            outfile.write(s1)
            if 'maxOccurs' in child.attrs.keys() and \
                child.attrs['maxOccurs'] == 'unbounded':
                s1 = "                self.%s.append(%s)\n" % (name, name)
                outfile.write(s1)
            else:
                s1 = "                self.%s = %s\n" % (name, name)
                outfile.write(s1)
            keyword = 'elif'
            generatedOne = 1
        elif child.getType() == 'xs:integer':
            s1 = "            %s child.nodeType == Node.ELEMENT_NODE and \\\n" % \
                 keyword
            outfile.write(s1)
            s1 = "                child.nodeName == '%s':\n" % origName
            outfile.write(s1)
            s1 = "                if child.firstChild:\n"
            outfile.write(s1)
            s1 = "                    sval = child.firstChild.nodeValue\n"
            outfile.write(s1)
            s1 = "                    try:\n"
            outfile.write(s1)
            s1 = "                        ival = int(sval)\n"
            outfile.write(s1)
            s1 = "                    except ValueError:\n"
            outfile.write(s1)
            s1 = "                        showError('*** requires integer -- %s' % child.toxml())\n"
            outfile.write(s1)
            if 'maxOccurs' in child.attrs.keys() and \
                child.attrs['maxOccurs'] == 'unbounded':
                s1 = "                    self.%s.append(ival)\n" % name
                outfile.write(s1)
            else:
                s1 = "                    self.%s = ival\n" % name
                outfile.write(s1)
            keyword = 'elif'
            generatedOne = 1
        elif child.getType() == 'xs:float':
            s1 = "            %s child.nodeType == Node.ELEMENT_NODE and \\\n" % \
                 keyword
            outfile.write(s1)
            s1 = "                child.nodeName == '%s':\n" % origName
            outfile.write(s1)
            s1 = "                if child.firstChild:\n"
            outfile.write(s1)
            s1 = "                    sval = child.firstChild.nodeValue\n"
            outfile.write(s1)
            s1 = "                    try:\n"
            outfile.write(s1)
            s1 = "                        fval = float(sval)\n"
            outfile.write(s1)
            s1 = "                    except ValueError:\n"
            outfile.write(s1)
            s1 = "                        showError('*** requires float -- %s' % child.toxml())\n"
            outfile.write(s1)
            if 'maxOccurs' in child.attrs.keys() and \
                child.attrs['maxOccurs'] == 'unbounded':
                s1 = "                    self.%s.append(fval)\n" % name
                outfile.write(s1)
            else:
                s1 = "                    self.%s = fval\n" % name
                outfile.write(s1)
            keyword = 'elif'
            generatedOne = 1
#        elif child.getType() == 'NoneType':
        else:
            # Perhaps it's a complexType that is defined right here.
            # Generate (later) a class for the nested types.
            if not delayed and not child in DelayedElements:
                DelayedElements.append(child)
            s1 = "            %s child.nodeType == Node.ELEMENT_NODE and \\\n" % \
                 keyword
            outfile.write(s1)
#            s1 = "                child.nodeName == '%s':\n" % origName
            s1 = "                child.nodeName == '%s':\n" % child.getType()
            outfile.write(s1)
            s1 = "                obj = %s%s.factory()\n" % \
                (prefix, mapName(child.getType()))
            outfile.write(s1)
            s1 = "                obj.build(child)\n"
            outfile.write(s1)
            if 'maxOccurs' in child.attrs.keys() and \
                child.attrs['maxOccurs'] == 'unbounded':
                s1 = "                self.%s.append(obj)\n" % name
                outfile.write(s1)
            else:
                s1 = "                self.set%s(obj)\n" % \
                    name.capitalize()
                outfile.write(s1)
            keyword = 'elif'
            generatedOne = 1
    if not generatedOne:
        outfile.write('            pass\n')


def buildCtorArgs(element):
    s2 = ''
    for child in element.getChildren():
        if child.getMaxOccurs() > 1:
            s2 += ', %s=None' % child.getCleanName()
        else:
            childType = child.getType()
            if childType == 'xs:string':
                s2 += ', %s=\'\'' % child.getCleanName()
            elif childType == 'xs:integer':
                s2 += ', %s=-1' % child.getCleanName()
            elif childType == 'xs:float':
                s2 += ', %s=0.0' % child.getCleanName()
            else:
                s2 += ', %s=None' % child.getCleanName()
    for attrDef in element.getAttributeDefs():
        name = attrDef['name']
        mappedName = attrDef['name']
        mappedName = mapName(mappedName)
        #IPShell(vars(), '*** (generateCtor) attribute.')
        atype = attrDef['type']
        if 'maxOccurs' in attrDef.keys() and \
            attrDef['maxOccurs'] == 'unbounded':
            s2 += ', %s=[]' % mappedName
        elif atype == 'xs:string':
            s2 += ', %s=\'\'' % mappedName
        elif atype == 'xs:integer':
            s2 += ', %s=-1' % mappedName
        elif atype == 'xs:float':
            s2 += ', %s=0.0' % mappedName
        else:
            s2 += ', %s=None' % mappedName
    return s2


def generateCtor(outfile, element):
    s2 = buildCtorArgs(element)
    s1 = '    def __init__(self%s):\n' % s2
    outfile.write(s1)
    # Generate member initializers in ctor.
    member = 0
    for child in element.getChildren():
        if child.getMaxOccurs() > 1:
            s1 = '        if %s is None:\n' % (child.getCleanName(), )
            outfile.write(s1)
            s1 = '            self.%s = []\n' % (child.getCleanName(), )
            outfile.write(s1)
            s1 = '        else:\n'
            outfile.write(s1)
            s1 = '            self.%s = %s\n' % \
                (child.getCleanName(), child.getCleanName())
            outfile.write(s1)
        else:
            s1 = '        self.%s = %s\n' % \
                (child.getCleanName(), child.getCleanName())
            outfile.write(s1)
        member = 1
    for attrDef in element.getAttributeDefs():
        mappedName = attrDef['name']
        mappedName = mapName(mappedName)
        s1 = '        self.%s = %s\n' % (mappedName, mappedName)
        outfile.write(s1)
        member = 1
    if not member:
        outfile.write('        pass\n')



# Generate get/set/add member functions.
def generateGettersAndSetters(outfile, element):
    for child in element.getChildren():
        name = child.getCleanName()
        unmappedName = child.getUnmappedCleanName()
        s1 = '    def get%s(self): return self.%s\n' % \
            (unmappedName.capitalize(), name)
        outfile.write(s1)
        if child.getMaxOccurs() > 1:
            s1 = '    def add%s(self, %s): self.%s.append(%s)\n' % \
                (unmappedName.capitalize(), name, name, name)
        else:
            s1 = '    def set%s(self, %s): self.%s = %s\n' % \
                (unmappedName.capitalize(), name, name, name)
        outfile.write(s1)
    for attrDef in element.getAttributeDefs():
        name = attrDef['name']
        mappedName = mapName(name)
        s1 = '    def get%s(self): return self.%s\n' % \
            (name.capitalize(), mappedName)
        outfile.write(s1)
        if 'maxOccurs' in attrDef.keys() and \
            attrDef['maxOccurs'] == 'unbounded':
            s1 = '    def add%s(self, %s): self.%s.append(%s)\n' % \
                (name.capitalize(), mappedName, mappedName, mappedName)
        else:
            s1 = '    def set%s(self, %s): self.%s = %s\n' % \
                (name.capitalize(), mappedName, mappedName, mappedName)
        outfile.write(s1)


def generateClasses(outfile, prefix, element, delayed):
    global YamlGen
    wrt = outfile.write
    if not element.isComplex():
        return
    if element.getType() in AlreadyGenerated:
        return
    AlreadyGenerated.append(element.getType())
    name = element.getCleanName()
    s1 = 'class %s%s:\n' % (prefix, name)
    wrt(s1)
    wrt('    subclass = None\n')
    generateCtor(outfile, element)
    wrt('    def factory(*args):\n')
    wrt('        if %s.subclass:\n' % name)
    wrt('            return apply(%s.subclass, args)\n' % name)
    wrt('        else:\n')
    wrt('            return apply(%s, args)\n' % name)
    wrt('    factory = staticmethod(factory)\n')
    generateGettersAndSetters(outfile, element)
    generateExportFn(outfile, prefix, element)
    if YamlGen:
        generateExportYaml(outfile, prefix, element)
    generateBuildFn(outfile, prefix, element, delayed)
    wrt('# end class %s\n' % name)
    wrt('\n\n')


TEMPLATE_CHARS_INTEGER = """\
            try:
                ival = int(chrs)
            except ValueError:
                showError('*** Invalid value for integer')
                sys.exit(-1)
            self.stack[-1].set%s(ival)
"""

TEMPLATE_CHARS_FLOAT = """\
            try:
                fval = float(chrs)
            except ValueError:
                showError('*** Invalid value for float')
                sys.exit(-1)
            self.stack[-1].set%s(fval)
"""


## def collectNames(element, names):
##     if element.getName() != 'root':
##         names.append(element.getName())
##     for child in element.getChildren():
##         collectNames(child, names)

## def collectNames(element, names):
##     for el in elementWalk(element):
##         if el.getName() != 'root':
##             names.append(el.getName())

def collect(element, elements):
    if element.getName() != 'root':
        elements.append(element)
    for child in element.getChildren():
        collect(child, elements)


TEMPLATE_HEADER = """\
#!/usr/bin/env python

import sys
import getopt
from xml.dom import minidom
from xml.dom import Node
#import yaml

#
# If you have installed IPython you can uncomment and use the following.
# IPython is available from http://www-hep.colorado.edu/~fperez/ipython.
#
#from IPython.Shell import IPythonShellEmbed
#IPShell = IPythonShellEmbed('-nosep',
#    banner = 'Entering interpreter.  Ctrl-D to exit.',
#    exit_msg = 'Leaving Interpreter.')

# Use the following line where and when you want to drop into the
# IPython shell:
#    IPShell(vars(), '<a msg>')

#
# Support/utility functions.
#

def showIndent(outfile, level):
    for idx in range(level):
        outfile.write('    ')


#
# Data representation classes.
#

"""


def generateHeader(outfile, prefix):
    s1 = TEMPLATE_HEADER
    outfile.write(s1)


TEMPLATE_MAIN = """\
USAGE_TEXT = \"\"\"
Usage: python <%sParser>.py <in_xml_file>
\"\"\"

def usage():
    print USAGE_TEXT
    sys.exit(-1)


def parse(inFileName):
    doc = minidom.parse(inFileName)
    rootNode = doc.childNodes[0]
    rootObj = %s%s.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('?xml version="1.0" ?>')
    rootObj.export(sys.stdout, 0)
    #yamlObj = rootObj.exportYaml()
    #yaml.dumpToFile(sys.stdout, yamlObj)
    return rootObj


def main():
    args = sys.argv[1:]
    if len(args) != 1:
        usage()
    parse(args[0])


if __name__ == '__main__':
    main()
    #import pdb
    #pdb.run('main()')

"""

def generateMain(outfile, prefix, root):
    name = root.getChildren()[0].getName()
    s1 = TEMPLATE_MAIN % (prefix, prefix, name)
    outfile.write(s1)


def buildCtorParams(element):
    s2 = ''
    for child in element.getChildren():
        s2 += ', %s' % child.getCleanName()
    for attrDef in element.getAttributeDefs():
        s2 += ', %s' % attrDef['name']
    return s2


def generateSubclass(outfile, element, prefix):
    wrt= outfile.write
    name = element.getCleanName()
    wrt('class %s%sSub(supermod.%s):\n' % (prefix, name, name))
    s1 = buildCtorArgs(element)
    wrt('    def __init__(self%s):\n' % s1)
    s1 = buildCtorParams(element)
    wrt('        supermod.%s%s.__init__(self%s)\n' % (prefix, name, s1))
    wrt('supermod.%s.subclass = %sSub\n' % (name, name))
    wrt('# end class %s%sSub\n' % (prefix, name))
    wrt('\n\n')


TEMPLATE_SUBCLASS_HEADER = """\
#!/usr/bin/env python

import sys

import ??? as supermod

"""

TEMPLATE_SUBCLASS_FOOTER = """\

USAGE_TEXT = \"\"\"
Usage: python ???.py <infilename>
\"\"\"

def usage():
    print USAGE_TEXT
    sys.exit(-1)


def main():
    args = sys.argv[1:]
    if len(args) != 1:
        usage()
    infilename = args[0]
    root = supermod.parse(infilename)
    root.???()


if __name__ == '__main__':
    main()
    #import pdb
    #pdb.run('main()')


"""

def generateSubclasses(root, subclassFilename, prefix):
    if subclassFilename:
        subclassFile = makeFile(subclassFilename)
        if subclassFile:
            wrt = subclassFile.write
            wrt(TEMPLATE_SUBCLASS_HEADER)
            for element in root.getChildren():
                generateSubclass(subclassFile, element, prefix)
            wrt(TEMPLATE_SUBCLASS_FOOTER)
            subclassFile.close()


def generate(outfileName, subclassFilename, prefix, root):
    if outfileName:
        outfile = makeFile(outfileName)
        if outfile:
            generateHeader(outfile, prefix)
            for element in root.getChildren():
                generateClasses(outfile, prefix, element, 0)
            while 1:
                if len(DelayedElements) <= 0:
                    break
                element = DelayedElements.pop()
                generateClasses(outfile, prefix, element, 1)
            generateMain(outfile, prefix, root)
            outfile.close()
    generateSubclasses(root, subclassFilename, prefix)


def makeFile(outFileName):
    global Force
    outFile = None
    if (not Force) and os.path.exists(outFileName):
        reply = raw_input('File %s exists.  Overwrite? (y/n): ' % outFileName)
        if reply == 'y':
            outFile = file(outFileName, 'w')
    else:
        outFile = file(outFileName, 'w')
    return outFile


def mapName(oldName):
    global NameTable
    newName = oldName
    if NameTable:
        if oldName in NameTable:
            newName = NameTable[oldName]
    return newName


## def mapName(oldName):
##     return '_X_%s' % oldName


def parseAndGenerate(outfileName, subclassFilename, prefix, \
        xschemaFileName):
    #IPShell(vars(), '*** (parseAndGenerate) starting.')
    #parser = saxexts.make_parser('xml.sax.drivers2.drv_libxmlop')
    #parser = saxexts.make_parser()
    parser = saxexts.make_parser("xml.sax.drivers2.drv_pyexpat")
    #print "Parser: %s (%s, %s)" % \
    #    (parser.get_parser_name(),parser.get_parser_version(),
    #        parser.get_driver_version())
    #print 'dir(parser):', dir(parser)
    dh = XschemaHandler()
    #parser.setDocumentHandler(dh)
    parser.setContentHandler(dh)
    #IPShell(vars(), '*** (parseAndGenerate) before parse.')
    parser.parse(xschemaFileName)
    #IPShell(vars(), '*** (parseAndGenerate) after parse.')
    root = dh.getRoot()
    root.annotate()
    #response = raw_input('Press Enter')
    #root.show(sys.stdout, 0)
    #print '=' * 50
    #print ']]] root: ', root, '[[['
    generate(outfileName, subclassFilename, prefix, root)



USAGE_TEXT = """
Usage: python generateDS.py [ options ] <in_xsd_file>
Options:
    -o <outfilename>         Output file name for data representation classes
    -s <subclassfilename>    Output file name for subclasses
    -p <prefix>              Prefix string to be pre-pended to the class names
    -n <mappingfilename>     Transform names with table in mappingfilename.
    -f                       Force creation of output files.  Do not ask.
#    -y                       Generate YAML export methods.
"""

def usage():
    print USAGE_TEXT
    sys.exit(-1)


def main():
    global Force, YamlGen, NameTable
    args = sys.argv[1:]
    options, args = getopt.getopt(args, 'fyn:o:s:p:')
    prefix = ''
    outfileName = None
    subclassFilename = None
    nameFileName = None
    for option in options:
        if option[0] == '-p':
            prefix = option[1]
        elif option[0] == '-o':
            outfileName = option[1]
        elif option[0] == '-s':
            subclassFilename = option[1]
        elif option[0] == '-f':
            Force = 1
        elif option[0] == '-y':
            YamlGen = 1
        elif option[0] == '-n':
            nameFileName = option[1]
    if len(args) != 1:
        usage()
    if nameFileName:
##         import yaml
##         table1 = yaml.loadFile(nameFileName)
        table2 = [x for x in table1]
        NameTable = table2[0]
    xschemaFileName = args[0]
##     if outFilename:
##         outfile = makeFile(outFilename)
##         if not outfile:
##             return
##     else:
##         outfile = sys.stdout
    #IPShell(vars(), '*** (main) before calling parseAndGenerate.')
    parseAndGenerate(outfileName, subclassFilename, prefix, \
        xschemaFileName)
##     if outFilename:
##         outfile.close()


if __name__ == '__main__':
    main()
    #import pdb
    #pdb.run('main()')

