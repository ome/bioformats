#!/usr/bin/env python

#
# Generated Tue Aug 11 11:57:44 2009 by generateDS.py version 1.18c.
#

import sys
from string import lower as str_lower
from xml.dom import minidom
from xml.sax import handler, make_parser

import out2sup as supermod

#
# Globals
#

ExternalEncoding = 'ascii'

#
# Data representation classes
#

class peopleSub(supermod.people):
    def __init__(self, person=None):
        supermod.people.__init__(self, person)
supermod.people.subclass = peopleSub
# end class peopleSub


class personSub(supermod.person):
    def __init__(self, id=None, value=None, ratio_attr=None, name=None, ratio=None, imagesize=None, interest=None, category=None, hot_agent=None, promoter=None, hot=None):
        supermod.person.__init__(self, id, value, ratio_attr, name, ratio, imagesize, interest, category, hot_agent, promoter, hot)
supermod.person.subclass = personSub
# end class personSub


class hot_agentSub(supermod.hot_agent):
    def __init__(self, firstname=None, lastname=None, priority=None):
        supermod.hot_agent.__init__(self, firstname, lastname, priority)
supermod.hot_agent.subclass = hot_agentSub
# end class hot_agentSub


class boosterSub(supermod.booster):
    def __init__(self, firstname=None, lastname=None, client=None):
        supermod.booster.__init__(self, firstname, lastname, client)
supermod.booster.subclass = boosterSub
# end class boosterSub


class clientSub(supermod.client):
    def __init__(self, fullname=None, refid=None):
        supermod.client.__init__(self, fullname, refid)
supermod.client.subclass = clientSub
# end class clientSub



#
# SAX handler used to determine the top level element.
#
class SaxSelectorHandler(handler.ContentHandler):
    def __init__(self):
        self.topElementName = None
    def getTopElementName(self):
        return self.topElementName
    def startElement(self, name, attrs):
        self.topElementName = name
        raise StopIteration


def parseSelect(inFileName):
    infile = file(inFileName, 'r')
    topElementName = None
    parser = make_parser()
    documentHandler = SaxSelectorHandler()
    parser.setContentHandler(documentHandler)
    try:
        try:
            parser.parse(infile)
        except StopIteration:
            topElementName = documentHandler.getTopElementName()
        if topElementName is None:
            raise RuntimeError, 'no top level element'
        topElementName = topElementName.replace('-', '_').replace(':', '_')
        if topElementName not in supermod.__dict__:
            raise RuntimeError, 'no class for top element: %s' % topElementName
        topElement = supermod.__dict__[topElementName]
        infile.seek(0)
        doc = minidom.parse(infile)
    finally:
        infile.close()
    rootNode = doc.childNodes[0]
    rootObj = topElement.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0)
    return rootObj


def saxParse(inFileName):
    parser = make_parser()
    documentHandler = supermod.Sax_peopleHandler()
    parser.setDocumentHandler(documentHandler)
    parser.parse('file:%s' % inFileName)
    rootObj = documentHandler.getRoot()
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    return rootObj


def saxParseString(inString):
    parser = make_parser()
    documentHandler = supermod.SaxContentHandler()
    parser.setDocumentHandler(documentHandler)
    parser.feed(inString)
    parser.close()
    rootObj = documentHandler.getRoot()
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    return rootObj


def parse(inFilename):
    doc = minidom.parse(inFilename)
    rootNode = doc.documentElement
    rootObj = supermod.people.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0, name_="people",
        namespacedef_='')
    doc = None
    return rootObj


def parseString(inString):
    doc = minidom.parseString(inString)
    rootNode = doc.documentElement
    rootObj = supermod.people.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0, name_="people",
        namespacedef_='')
    return rootObj


def parseLiteral(inFilename):
    doc = minidom.parse(inFilename)
    rootNode = doc.documentElement
    rootObj = supermod.people.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('from out2sup import *\n\n')
    sys.stdout.write('rootObj = people(\n')
    rootObj.exportLiteral(sys.stdout, 0, name_="people")
    sys.stdout.write(')\n')
    return rootObj


USAGE_TEXT = """
Usage: python ???.py <infilename>
"""

def usage():
    print USAGE_TEXT
    sys.exit(1)


def main():
    args = sys.argv[1:]
    if len(args) != 1:
        usage()
    infilename = args[0]
    root = parse(infilename)


if __name__ == '__main__':
    main()
    #import pdb
    #pdb.run('main()')


