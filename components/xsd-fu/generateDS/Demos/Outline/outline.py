#!/usr/bin/env python

#
# Generated Mon Aug 18 17:09:37 2003 by generateDS.py.
#

import sys
import getopt
from xml.dom import minidom
from xml.dom import Node

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

def quote_xml(inStr):
    s1 = inStr
    s1 = s1.replace('&', '&amp;')
    s1 = s1.replace('<', '&lt;')
    return s1


#
# Data representation classes.
#

class outline:
    subclass = None
    def __init__(self, name='', description='', children=None):
        self.name = name
        self.description = description
        if children is None:
            self.children = []
        else:
            self.children = children
    def factory(*args):
        if outline.subclass:
            return apply(outline.subclass, args)
        else:
            return apply(outline, args)
    factory = staticmethod(factory)
    def getName(self): return self.name
    def setName(self, name): self.name = name
    def getDescription(self): return self.description
    def setDescription(self, description): self.description = description
    def getChildren(self): return self.children
    def addChildren(self, value): self.children.append(value)
    def setChildren(self, index, value): self.children[index] = value
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<outline>\n')
        level += 1
        showIndent(outfile, level)
        outfile.write('<name>%s</name>\n' % quote_xml(self.getName()))
        showIndent(outfile, level)
        outfile.write('<description>%s</description>\n' % quote_xml(self.getDescription()))
        for children in self.children:
            children.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</outline>\n')
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'name':
                name = ''
                for text_ in child.childNodes:
                    name += text_.nodeValue
                self.name = name
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'description':
                description = ''
                for text_ in child.childNodes:
                    description += text_.nodeValue
                self.description = description
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'node':
                obj = node.factory()
                obj.build(child)
                self.children.append(obj)
# end class outline


class node:
    subclass = None
    def __init__(self, label='', text='', children=None, hidden=''):
        self.label = label
        self.text = text
        if children is None:
            self.children = []
        else:
            self.children = children
        self.hidden = hidden
    def factory(*args):
        if node.subclass:
            return apply(node.subclass, args)
        else:
            return apply(node, args)
    factory = staticmethod(factory)
    def getLabel(self): return self.label
    def setLabel(self, label): self.label = label
    def getText(self): return self.text
    def setText(self, text): self.text = text
    def getChildren(self): return self.children
    def addChildren(self, value): self.children.append(value)
    def setChildren(self, index, value): self.children[index] = value
    def getHidden(self): return self.hidden
    def setHidden(self, hidden): self.hidden = hidden
    def export(self, outfile, level):
        showIndent(outfile, level)
        if self.hidden:
            outfile.write('<node hidden="%s">\n' % (self.hidden, ))
        else:
            outfile.write('<node>\n')
        level += 1
        showIndent(outfile, level)
        outfile.write('<label>%s</label>\n' % quote_xml(self.getLabel()))
        showIndent(outfile, level)
        outfile.write('<text>%s</text>\n' % quote_xml(self.getText()))
        for children in self.children:
            children.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</node>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('hidden'):
            self.hidden = attrs.get('hidden').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'label':
                label = ''
                for text_ in child.childNodes:
                    label += text_.nodeValue
                self.label = label
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'text':
                text = ''
                for text_ in child.childNodes:
                    text += text_.nodeValue
                self.text = text
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'node':
                obj = node.factory()
                obj.build(child)
                self.children.append(obj)
# end class node


USAGE_TEXT = """
Usage: python <Parser>.py <in_xml_file>
"""

def usage():
    print USAGE_TEXT
    sys.exit(-1)


def parse(inFileName):
    doc = minidom.parse(inFileName)
    rootNode = doc.childNodes[0]
    rootObj = outline.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>')
    rootObj.export(sys.stdout, 0)
    return rootObj


def parseString(inString):
    doc = minidom.parseString(inString)
    rootNode = doc.childNodes[0]
    rootObj = outline.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0)
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

