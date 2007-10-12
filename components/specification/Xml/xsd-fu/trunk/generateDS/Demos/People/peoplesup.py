#!/usr/bin/env python

#
# Generated Tue Jan 30 12:32:05 2007 by generateDS.py.
#

import sys
import getopt
from xml.dom import minidom
from xml.dom import Node

#
# If you have installed IPython you can uncomment and use the following.
# IPython is available from http://ipython.scipy.org/.
#

## from IPython.Shell import IPShellEmbed
## args = ''
## ipshell = IPShellEmbed(args,
##     banner = 'Dropping into IPython',
##     exit_msg = 'Leaving Interpreter, back to program.')

# Then use the following line where and when you want to drop into the
# IPython shell:
#    ipshell('<some message> -- Entering ipshell.\nHit Ctrl-D to exit')

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
    s1 = s1.replace('"', '&quot;')
    return s1

def quote_python(inStr):
    s1 = inStr
    if s1.find("'") == -1:
        if s1.find('\n') == -1:
            return "'%s'" % s1
        else:
            return "'''%s'''" % s1
    else:
        if s1.find('"') != -1:
            s1 = s1.replace('"', '\\"')
        if s1.find('\n') == -1:
            return '"%s"' % s1
        else:
            return '"""%s"""' % s1


class MixedContainer:
    # Constants for category:
    CategoryNone = 0
    CategoryText = 1
    CategorySimple = 2
    CategoryComplex = 3
    # Constants for content_type:
    TypeNone = 0
    TypeText = 1
    TypeString = 2
    TypeInteger = 3
    TypeFloat = 4
    TypeDecimal = 5
    TypeDouble = 6
    TypeBoolean = 7
    def __init__(self, category, content_type, name, value):
        self.category = category
        self.content_type = content_type
        self.name = name
        self.value = value
    def getCategory(self):
        return self.category
    def getContenttype(self, content_type):
        return self.content_type
    def getValue(self):
        return self.value
    def getName(self):
        return self.name
    def export(self, outfile, level, name):
        if self.category == MixedContainer.CategoryText:
            outfile.write(self.value)
        elif self.category == MixedContainer.CategorySimple:
            self.exportSimple(outfile, level, name)
        else:    # category == MixedContainer.CategoryComplex
            self.value.export(outfile, level, name)
    def exportSimple(self, outfile, level, name):
        if self.content_type == MixedContainer.TypeString:
            outfile.write('<%s>%s</%s>' % (self.name, self.value, self.name))
        elif self.content_type == MixedContainer.TypeInteger or \
                self.content_type == MixedContainer.TypeBoolean:
            outfile.write('<%s>%d</%s>' % (self.name, self.value, self.name))
        elif self.content_type == MixedContainer.TypeFloat or \
                self.content_type == MixedContainer.TypeDecimal:
            outfile.write('<%s>%f</%s>' % (self.name, self.value, self.name))
        elif self.content_type == MixedContainer.TypeDouble:
            outfile.write('<%s>%g</%s>' % (self.name, self.value, self.name))
    def exportLiteral(self, outfile, level, name):
        if self.category == MixedContainer.CategoryText:
            showIndent(outfile, level)
            outfile.write('MixedContainer(%d, %d, "%s", "%s"),\n' % \
                (self.category, self.content_type, self.name, self.value))
        elif self.category == MixedContainer.CategorySimple:
            showIndent(outfile, level)
            outfile.write('MixedContainer(%d, %d, "%s", "%s"),\n' % \
                (self.category, self.content_type, self.name, self.value))
        else:    # category == MixedContainer.CategoryComplex
            showIndent(outfile, level)
            outfile.write('MixedContainer(%d, %d, "%s",\n' % \
                (self.category, self.content_type, self.name,))
            self.value.exportLiteral(outfile, level + 1)
            showIndent(outfile, level)
            outfile.write(')\n')


#
# Data representation classes.
#

class people:
    subclass = None
    def __init__(self, person=None):
        if person is None:
            self.person = []
        else:
            self.person = person
    def factory(*args_, **kwargs_):
        if people.subclass:
            return people.subclass(*args_, **kwargs_)
        else:
            return people(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getPerson(self): return self.person
    def setPerson(self, person): self.person = person
    def addPerson(self, value): self.person.append(value)
    def insertPerson(self, index, value): self.person[index] = value
    def export(self, outfile, level, name_='people'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        self.exportChildren(outfile, level + 1, name_)
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportAttributes(self, outfile, level, name_='people'):
        pass
    def exportChildren(self, outfile, level, name_='people'):
        for person_ in self.getPerson():
            person_.export(outfile, level)
    def exportLiteral(self, outfile, level, name_='people'):
        level += 1
        self.exportLiteralAttributes(outfile, level, name_)
        self.exportLiteralChildren(outfile, level, name_)
    def exportLiteralAttributes(self, outfile, level, name_):
        pass
    def exportLiteralChildren(self, outfile, level, name_):
        showIndent(outfile, level)
        outfile.write('person=[\n')
        level += 1
        for person in self.person:
            showIndent(outfile, level)
            outfile.write('person(\n')
            person.exportLiteral(outfile, level)
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
        showIndent(outfile, level)
        outfile.write('],\n')
    def build(self, node_):
        attrs = node_.attributes
        self.buildAttributes(attrs)
        for child_ in node_.childNodes:
            nodeName_ = child_.nodeName.split(':')[-1]
            self.buildChildren(child_, nodeName_)
    def buildAttributes(self, attrs):
        pass
    def buildChildren(self, child_, nodeName_):
        if child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'person':
            obj_ = person.factory()
            obj_.build(child_)
            self.person.append(obj_)
# end class people


class person:
    subclass = None
    def __init__(self, id=-1, value='', ratio_attr='', name='', ratio='', imagesize=None, interest=None, category=-1, hot_agent=None, promoter=None, hot=None):
        self.id = id
        self.value = value
        self.ratio_attr = ratio_attr
        self.name = name
        self.ratio = ratio
        self.imagesize = imagesize
        if interest is None:
            self.interest = []
        else:
            self.interest = interest
        self.category = category
        self.hot_agent = hot_agent
        if promoter is None:
            self.promoter = []
        else:
            self.promoter = promoter
        self.hot = hot
        self.anyAttributes_ = {}
    def factory(*args_, **kwargs_):
        if person.subclass:
            return person.subclass(*args_, **kwargs_)
        else:
            return person(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getName(self): return self.name
    def setName(self, name): self.name = name
    def getRatio(self): return self.ratio
    def setRatio(self, ratio): self.ratio = ratio
    def validate_percent(self, value):
        # Validate type percent, a restriction on xs:integer.
        if (value < 0 or
            value > 100):
            print 'bad percent value'
    def getImagesize(self): return self.imagesize
    def setImagesize(self, imagesize): self.imagesize = imagesize
    def validate_scale(self, value):
        # validate type scale
        pass
    def getInterest(self): return self.interest
    def setInterest(self, interest): self.interest = interest
    def addInterest(self, value): self.interest.append(value)
    def insertInterest(self, index, value): self.interest[index] = value
    def getCategory(self): return self.category
    def setCategory(self, category): self.category = category
    def getHot_agent(self): return self.hot_agent
    def setHot_agent(self, hot_agent): self.hot_agent = hot_agent
    def getPromoter(self): return self.promoter
    def setPromoter(self, promoter): self.promoter = promoter
    def addPromoter(self, value): self.promoter.append(value)
    def insertPromoter(self, index, value): self.promoter[index] = value
    def getHot(self): return self.hot
    def setHot(self, hot): self.hot = hot
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getValue(self): return self.value
    def setValue(self, value): self.value = value
    def getRatio_attr(self): return self.ratio_attr
    def setRatio_attr(self, ratio_attr): self.ratio_attr = ratio_attr
    def getAnyAttributes_(self): return self.anyAttributes_
    def setAnyAttributes_(self, anyAttributes_): self.anyAttributes_ = anyAttributes_
    def export(self, outfile, level, name_='person'):
        showIndent(outfile, level)
        outfile.write('<%s' % (name_, ))
        self.exportAttributes(outfile, level, name_='person')
        outfile.write('>\n')
        self.exportChildren(outfile, level + 1, name_)
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportAttributes(self, outfile, level, name_='person'):
        if self.getId() is not None:
            outfile.write(' id="%s"' % (self.getId(), ))
        if self.getValue() is not None:
            outfile.write(' value="%s"' % (self.getValue(), ))
        if self.getRatio_attr() is not None:
            outfile.write(' ratio_attr="%s"' % (self.getRatio_attr(), ))
        for name, value in self.anyAttributes_.items():
            outfile.write(' %s="%s"' % (name, value, ))
    def exportChildren(self, outfile, level, name_='person'):
        showIndent(outfile, level)
        outfile.write('<name>%s</name>\n' % quote_xml(self.getName()))
        showIndent(outfile, level)
        outfile.write('<ratio>%s</ratio>\n' % quote_xml(self.getRatio()))
        if self.imagesize:
            self.imagesize.export(outfile, level, name_='imagesize')
        for interest_ in self.getInterest():
            showIndent(outfile, level)
            outfile.write('<interest>%s</interest>\n' % quote_xml(interest_))
        showIndent(outfile, level)
        outfile.write('<category>%d</category>\n' % self.getCategory())
        if self.hot_agent:
            self.hot_agent.export(outfile, level)
        for promoter_ in self.getPromoter():
            promoter_.export(outfile, level, name_='promoter')
        if self.hot:
            self.hot.export(outfile, level, name_='hot')
    def exportLiteral(self, outfile, level, name_='person'):
        level += 1
        self.exportLiteralAttributes(outfile, level, name_)
        self.exportLiteralChildren(outfile, level, name_)
    def exportLiteralAttributes(self, outfile, level, name_):
        showIndent(outfile, level)
        outfile.write('id = "%s",\n' % (self.getId(),))
        showIndent(outfile, level)
        outfile.write('value = "%s",\n' % (self.getValue(),))
        showIndent(outfile, level)
        outfile.write('ratio_attr = "%s",\n' % (self.getRatio_attr(),))
        for name, value in self.anyAttributes_.items():
            showIndent(outfile, level)
            outfile.write('%s = "%s",\n' % (name, value,))
    def exportLiteralChildren(self, outfile, level, name_):
        showIndent(outfile, level)
        outfile.write('name=%s,\n' % quote_python(self.getName()))
        showIndent(outfile, level)
        outfile.write('ratio=%s,\n' % quote_python(self.getRatio()))
        if self.imagesize:
            showIndent(outfile, level)
            outfile.write('imagesize=scale(\n')
            self.imagesize.exportLiteral(outfile, level, name_='imagesize')
            showIndent(outfile, level)
            outfile.write('),\n')
        showIndent(outfile, level)
        outfile.write('interest=[\n')
        level += 1
        for interest in self.interest:
            showIndent(outfile, level)
            outfile.write('%s,\n' % quote_python(interest))
        level -= 1
        showIndent(outfile, level)
        outfile.write('],\n')
        showIndent(outfile, level)
        outfile.write('category=%d,\n' % self.getCategory())
        if self.hot_agent:
            showIndent(outfile, level)
            outfile.write('hot_agent=hot_agent(\n')
            self.hot_agent.exportLiteral(outfile, level, name_='hot_agent')
            showIndent(outfile, level)
            outfile.write('),\n')
        showIndent(outfile, level)
        outfile.write('promoter=[\n')
        level += 1
        for promoter in self.promoter:
            showIndent(outfile, level)
            outfile.write('booster(\n')
            promoter.exportLiteral(outfile, level, name_='promoter')
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
        showIndent(outfile, level)
        outfile.write('],\n')
        if self.hot:
            showIndent(outfile, level)
            outfile.write('hot=BasicEmptyType(\n')
            self.hot.exportLiteral(outfile, level, name_='hot')
            showIndent(outfile, level)
            outfile.write('),\n')
    def build(self, node_):
        attrs = node_.attributes
        self.buildAttributes(attrs)
        for child_ in node_.childNodes:
            nodeName_ = child_.nodeName.split(':')[-1]
            self.buildChildren(child_, nodeName_)
    def buildAttributes(self, attrs):
        if attrs.get('id'):
            try:
                self.id = int(attrs.get('id').value)
            except ValueError:
                raise ValueError('Bad integer attribute (id)')
        if attrs.get('value'):
            self.value = attrs.get('value').value
        if attrs.get('ratio_attr'):
            self.ratio_attr = attrs.get('ratio_attr').value
        self.anyAttributes_ = {}
        for name, value in attrs.items():
            if name != "id" and name != "value" and name != "ratio_attr":
                self.anyAttributes_[name] = value
    def buildChildren(self, child_, nodeName_):
        if child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'name':
            name_ = ''
            for text__content_ in child_.childNodes:
                name_ += text__content_.nodeValue
            self.name = name_
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'ratio':
            ratio_ = ''
            for text__content_ in child_.childNodes:
                ratio_ += text__content_.nodeValue
            self.ratio = ratio_
            self.validate_percent(self.ratio)    # validate type percent
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'imagesize':
            obj_ = scale.factory()
            obj_.build(child_)
            self.setImagesize(obj_)
            self.validate_scale(self.imagesize)    # validate type scale
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'interest':
            interest_ = ''
            for text__content_ in child_.childNodes:
                interest_ += text__content_.nodeValue
            self.interest.append(interest_)
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'category':
            if child_.firstChild:
                sval_ = child_.firstChild.nodeValue
                try:
                    ival_ = int(sval_)
                except ValueError:
                    raise ValueError('requires integer -- %s' % child_.toxml())
                self.category = ival_
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'hot.agent':
            obj_ = hot_agent.factory()
            obj_.build(child_)
            self.setHot_agent(obj_)
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'promoter':
            obj_ = booster.factory()
            obj_.build(child_)
            self.promoter.append(obj_)
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'hot':
            obj_ = BasicEmptyType.factory()
            obj_.build(child_)
            self.setHot(obj_)
# end class person


class scale:
    subclass = None
    def __init__(self, valueOf_=''):
        self.valueOf_ = valueOf_
    def factory(*args_, **kwargs_):
        if scale.subclass:
            return scale.subclass(*args_, **kwargs_)
        else:
            return scale(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getValueOf_(self): return self.valueOf_
    def setValueOf_(self, valueOf_): self.valueOf_ = valueOf_
    def export(self, outfile, level, name_='scale'):
        showIndent(outfile, level)
        outfile.write('<%s>' % name_)
        self.exportChildren(outfile, level + 1, name_)
        outfile.write('</%s>\n' % name_)
    def exportAttributes(self, outfile, level, name_='scale'):
        pass
    def exportChildren(self, outfile, level, name_='scale'):
        outfile.write(self.valueOf_)
    def exportLiteral(self, outfile, level, name_='scale'):
        level += 1
        self.exportLiteralAttributes(outfile, level, name_)
        self.exportLiteralChildren(outfile, level, name_)
    def exportLiteralAttributes(self, outfile, level, name_):
        pass
    def exportLiteralChildren(self, outfile, level, name_):
        showIndent(outfile, level)
        outfile.write('valueOf_ = "%s",\n' % (self.valueOf_,))
    def build(self, node_):
        attrs = node_.attributes
        self.buildAttributes(attrs)
        self.valueOf_ = ''
        for child_ in node_.childNodes:
            nodeName_ = child_.nodeName.split(':')[-1]
            self.buildChildren(child_, nodeName_)
    def buildAttributes(self, attrs):
        pass
    def buildChildren(self, child_, nodeName_):
        if child_.nodeType == Node.TEXT_NODE:
            self.valueOf_ += child_.nodeValue
# end class scale


class BasicEmptyType:
    subclass = None
    def __init__(self, valueOf_=''):
        self.valueOf_ = valueOf_
    def factory(*args_, **kwargs_):
        if BasicEmptyType.subclass:
            return BasicEmptyType.subclass(*args_, **kwargs_)
        else:
            return BasicEmptyType(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getValueOf_(self): return self.valueOf_
    def setValueOf_(self, valueOf_): self.valueOf_ = valueOf_
    def export(self, outfile, level, name_='BasicEmptyType'):
        showIndent(outfile, level)
        outfile.write('<%s>' % name_)
        self.exportChildren(outfile, level + 1, name_)
        outfile.write('</%s>\n' % name_)
    def exportAttributes(self, outfile, level, name_='BasicEmptyType'):
        pass
    def exportChildren(self, outfile, level, name_='BasicEmptyType'):
        outfile.write(self.valueOf_)
    def exportLiteral(self, outfile, level, name_='BasicEmptyType'):
        level += 1
        self.exportLiteralAttributes(outfile, level, name_)
        self.exportLiteralChildren(outfile, level, name_)
    def exportLiteralAttributes(self, outfile, level, name_):
        pass
    def exportLiteralChildren(self, outfile, level, name_):
        showIndent(outfile, level)
        outfile.write('valueOf_ = "%s",\n' % (self.valueOf_,))
    def build(self, node_):
        attrs = node_.attributes
        self.buildAttributes(attrs)
        self.valueOf_ = ''
        for child_ in node_.childNodes:
            nodeName_ = child_.nodeName.split(':')[-1]
            self.buildChildren(child_, nodeName_)
    def buildAttributes(self, attrs):
        pass
    def buildChildren(self, child_, nodeName_):
        if child_.nodeType == Node.TEXT_NODE:
            self.valueOf_ += child_.nodeValue
# end class BasicEmptyType


class hot_agent:
    subclass = None
    def __init__(self, firstname='', lastname='', priority=0.0):
        self.firstname = firstname
        self.lastname = lastname
        self.priority = priority
        self.anyAttributes_ = {}
    def factory(*args_, **kwargs_):
        if hot_agent.subclass:
            return hot_agent.subclass(*args_, **kwargs_)
        else:
            return hot_agent(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getFirstname(self): return self.firstname
    def setFirstname(self, firstname): self.firstname = firstname
    def getLastname(self): return self.lastname
    def setLastname(self, lastname): self.lastname = lastname
    def getPriority(self): return self.priority
    def setPriority(self, priority): self.priority = priority
    def getAnyAttributes_(self): return self.anyAttributes_
    def setAnyAttributes_(self, anyAttributes_): self.anyAttributes_ = anyAttributes_
    def export(self, outfile, level, name_='hot.agent'):
        showIndent(outfile, level)
        outfile.write('<%s' % (name_, ))
        self.exportAttributes(outfile, level, name_='hot.agent')
        outfile.write('>\n')
        self.exportChildren(outfile, level + 1, name_)
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportAttributes(self, outfile, level, name_='hot.agent'):
        for name, value in self.anyAttributes_.items():
            outfile.write(' %s="%s"' % (name, value, ))
        pass
    def exportChildren(self, outfile, level, name_='hot.agent'):
        showIndent(outfile, level)
        outfile.write('<firstname>%s</firstname>\n' % quote_xml(self.getFirstname()))
        showIndent(outfile, level)
        outfile.write('<lastname>%s</lastname>\n' % quote_xml(self.getLastname()))
        showIndent(outfile, level)
        outfile.write('<priority>%f</priority>\n' % self.getPriority())
    def exportLiteral(self, outfile, level, name_='hot.agent'):
        level += 1
        self.exportLiteralAttributes(outfile, level, name_)
        self.exportLiteralChildren(outfile, level, name_)
    def exportLiteralAttributes(self, outfile, level, name_):
        for name, value in self.anyAttributes_.items():
            showIndent(outfile, level)
            outfile.write('%s = "%s",\n' % (name, value,))
    def exportLiteralChildren(self, outfile, level, name_):
        showIndent(outfile, level)
        outfile.write('firstname=%s,\n' % quote_python(self.getFirstname()))
        showIndent(outfile, level)
        outfile.write('lastname=%s,\n' % quote_python(self.getLastname()))
        showIndent(outfile, level)
        outfile.write('priority=%f,\n' % self.getPriority())
    def build(self, node_):
        attrs = node_.attributes
        self.buildAttributes(attrs)
        for child_ in node_.childNodes:
            nodeName_ = child_.nodeName.split(':')[-1]
            self.buildChildren(child_, nodeName_)
    def buildAttributes(self, attrs):
        self.anyAttributes_ = {}
        for name, value in attrs.items():
            self.anyAttributes_[name] = value
    def buildChildren(self, child_, nodeName_):
        if child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'firstname':
            firstname_ = ''
            for text__content_ in child_.childNodes:
                firstname_ += text__content_.nodeValue
            self.firstname = firstname_
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'lastname':
            lastname_ = ''
            for text__content_ in child_.childNodes:
                lastname_ += text__content_.nodeValue
            self.lastname = lastname_
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'priority':
            if child_.firstChild:
                sval_ = child_.firstChild.nodeValue
                try:
                    fval_ = float(sval_)
                except ValueError:
                    raise ValueError('requires float (or double) -- %s' % child_.toxml())
                self.priority = fval_
# end class hot_agent


class booster:
    subclass = None
    def __init__(self, firstname='', lastname='', client=None):
        self.firstname = firstname
        self.lastname = lastname
        if client is None:
            self.client = []
        else:
            self.client = client
    def factory(*args_, **kwargs_):
        if booster.subclass:
            return booster.subclass(*args_, **kwargs_)
        else:
            return booster(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getFirstname(self): return self.firstname
    def setFirstname(self, firstname): self.firstname = firstname
    def getLastname(self): return self.lastname
    def setLastname(self, lastname): self.lastname = lastname
    def getClient(self): return self.client
    def setClient(self, client): self.client = client
    def addClient(self, value): self.client.append(value)
    def insertClient(self, index, value): self.client[index] = value
    def export(self, outfile, level, name_='booster'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        self.exportChildren(outfile, level + 1, name_)
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportAttributes(self, outfile, level, name_='booster'):
        pass
    def exportChildren(self, outfile, level, name_='booster'):
        showIndent(outfile, level)
        outfile.write('<firstname>%s</firstname>\n' % quote_xml(self.getFirstname()))
        showIndent(outfile, level)
        outfile.write('<lastname>%s</lastname>\n' % quote_xml(self.getLastname()))
        for client_ in self.getClient():
            client_.export(outfile, level)
    def exportLiteral(self, outfile, level, name_='booster'):
        level += 1
        self.exportLiteralAttributes(outfile, level, name_)
        self.exportLiteralChildren(outfile, level, name_)
    def exportLiteralAttributes(self, outfile, level, name_):
        pass
    def exportLiteralChildren(self, outfile, level, name_):
        showIndent(outfile, level)
        outfile.write('firstname=%s,\n' % quote_python(self.getFirstname()))
        showIndent(outfile, level)
        outfile.write('lastname=%s,\n' % quote_python(self.getLastname()))
        showIndent(outfile, level)
        outfile.write('client=[\n')
        level += 1
        for client in self.client:
            showIndent(outfile, level)
            outfile.write('client(\n')
            client.exportLiteral(outfile, level)
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
        showIndent(outfile, level)
        outfile.write('],\n')
    def build(self, node_):
        attrs = node_.attributes
        self.buildAttributes(attrs)
        for child_ in node_.childNodes:
            nodeName_ = child_.nodeName.split(':')[-1]
            self.buildChildren(child_, nodeName_)
    def buildAttributes(self, attrs):
        pass
    def buildChildren(self, child_, nodeName_):
        if child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'firstname':
            firstname_ = ''
            for text__content_ in child_.childNodes:
                firstname_ += text__content_.nodeValue
            self.firstname = firstname_
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'lastname':
            lastname_ = ''
            for text__content_ in child_.childNodes:
                lastname_ += text__content_.nodeValue
            self.lastname = lastname_
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'client':
            obj_ = client.factory()
            obj_.build(child_)
            self.client.append(obj_)
# end class booster


class client:
    subclass = None
    def __init__(self, fullname='', refid=-1):
        self.fullname = fullname
        self.refid = refid
    def factory(*args_, **kwargs_):
        if client.subclass:
            return client.subclass(*args_, **kwargs_)
        else:
            return client(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getFullname(self): return self.fullname
    def setFullname(self, fullname): self.fullname = fullname
    def getRefid(self): return self.refid
    def setRefid(self, refid): self.refid = refid
    def export(self, outfile, level, name_='client'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        self.exportChildren(outfile, level + 1, name_)
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportAttributes(self, outfile, level, name_='client'):
        pass
    def exportChildren(self, outfile, level, name_='client'):
        showIndent(outfile, level)
        outfile.write('<fullname>%s</fullname>\n' % quote_xml(self.getFullname()))
        showIndent(outfile, level)
        outfile.write('<refid>%d</refid>\n' % self.getRefid())
    def exportLiteral(self, outfile, level, name_='client'):
        level += 1
        self.exportLiteralAttributes(outfile, level, name_)
        self.exportLiteralChildren(outfile, level, name_)
    def exportLiteralAttributes(self, outfile, level, name_):
        pass
    def exportLiteralChildren(self, outfile, level, name_):
        showIndent(outfile, level)
        outfile.write('fullname=%s,\n' % quote_python(self.getFullname()))
        showIndent(outfile, level)
        outfile.write('refid=%d,\n' % self.getRefid())
    def build(self, node_):
        attrs = node_.attributes
        self.buildAttributes(attrs)
        for child_ in node_.childNodes:
            nodeName_ = child_.nodeName.split(':')[-1]
            self.buildChildren(child_, nodeName_)
    def buildAttributes(self, attrs):
        pass
    def buildChildren(self, child_, nodeName_):
        if child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'fullname':
            fullname_ = ''
            for text__content_ in child_.childNodes:
                fullname_ += text__content_.nodeValue
            self.fullname = fullname_
        elif child_.nodeType == Node.ELEMENT_NODE and \
            nodeName_ == 'refid':
            if child_.firstChild:
                sval_ = child_.firstChild.nodeValue
                try:
                    ival_ = int(sval_)
                except ValueError:
                    raise ValueError('requires integer -- %s' % child_.toxml())
                self.refid = ival_
# end class client


from xml.sax import handler, make_parser

class SaxStackElement:
    def __init__(self, name='', obj=None):
        self.name = name
        self.obj = obj
        self.content = ''

#
# SAX handler
#
class SaxPeopleHandler(handler.ContentHandler):
    def __init__(self):
        self.stack = []
        self.root = None

    def getRoot(self):
        return self.root

    def setDocumentLocator(self, locator):
        self.locator = locator
    
    def showError(self, msg):
        print '*** (showError):', msg
        sys.exit(-1)

    def startElement(self, name, attrs):
        done = 0
        if name == 'people':
            obj = people.factory()
            stackObj = SaxStackElement('people', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'person':
            obj = person.factory()
            val = attrs.get('id', None)
            if val is not None:
                try:
                    obj.setId(int(val))
                except:
                    self.reportError('"id" attribute must be integer')
            val = attrs.get('value', None)
            if val is not None:
                obj.setValue(val)
            val = attrs.get('ratio_attr', None)
            if val is not None:
                obj.setRatio_attr(val)
            stackObj = SaxStackElement('person', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'name':
            stackObj = SaxStackElement('name', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'ratio':
            stackObj = SaxStackElement('ratio', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'imagesize':
            obj = scale.factory()
            stackObj = SaxStackElement('imagesize', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'interest':
            stackObj = SaxStackElement('interest', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'category':
            stackObj = SaxStackElement('category', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'hot.agent':
            obj = hot_agent.factory()
            stackObj = SaxStackElement('hot_agent', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'promoter':
            obj = booster.factory()
            stackObj = SaxStackElement('promoter', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'hot':
            obj = BasicEmptyType.factory()
            stackObj = SaxStackElement('hot', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'firstname':
            stackObj = SaxStackElement('firstname', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'lastname':
            stackObj = SaxStackElement('lastname', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'priority':
            stackObj = SaxStackElement('priority', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'client':
            obj = client.factory()
            stackObj = SaxStackElement('client', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'fullname':
            stackObj = SaxStackElement('fullname', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'refid':
            stackObj = SaxStackElement('refid', None)
            self.stack.append(stackObj)
            done = 1
        if not done:
            self.reportError('"%s" element not allowed here.' % name)

    def endElement(self, name):
        done = 0
        if name == 'people':
            if len(self.stack) == 1:
                self.root = self.stack[-1].obj
                self.stack.pop()
                done = 1
        elif name == 'person':
            if len(self.stack) >= 2:
                self.stack[-2].obj.addPerson(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'name':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setName(content)
                self.stack.pop()
                done = 1
        elif name == 'ratio':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setRatio(content)
                self.stack.pop()
                done = 1
        elif name == 'imagesize':
            if len(self.stack) >= 2:
                self.stack[-2].obj.setImagesize(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'interest':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.addInterest(content)
                self.stack.pop()
                done = 1
        elif name == 'category':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                if content:
                    try:
                        content = int(content)
                    except:
                        self.reportError('"category" must be integer -- content: %s' % content)
                else:
                    content = -1
                self.stack[-2].obj.setCategory(content)
                self.stack.pop()
                done = 1
        elif name == 'hot.agent':
            if len(self.stack) >= 2:
                self.stack[-2].obj.setHot_agent(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'promoter':
            if len(self.stack) >= 2:
                self.stack[-2].obj.addPromoter(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'hot':
            if len(self.stack) >= 2:
                self.stack[-2].obj.setHot(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'firstname':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setFirstname(content)
                self.stack.pop()
                done = 1
        elif name == 'lastname':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setLastname(content)
                self.stack.pop()
                done = 1
        elif name == 'priority':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                if content:
                    try:
                        content = float(content)
                    except:
                        self.reportError('"priority" must be float -- content: %s' % content)
                else:
                    content = -1
                self.stack[-2].obj.setPriority(content)
                self.stack.pop()
                done = 1
        elif name == 'client':
            if len(self.stack) >= 2:
                self.stack[-2].obj.addClient(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'fullname':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setFullname(content)
                self.stack.pop()
                done = 1
        elif name == 'refid':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                if content:
                    try:
                        content = int(content)
                    except:
                        self.reportError('"refid" must be integer -- content: %s' % content)
                else:
                    content = -1
                self.stack[-2].obj.setRefid(content)
                self.stack.pop()
                done = 1
        if not done:
            self.reportError('"%s" element not allowed here.' % name)

    def characters(self, chrs, start, end):
        if len(self.stack) > 0:
            self.stack[-1].content += chrs[start:end]

    def reportError(self, mesg):
        locator = self.locator
        sys.stderr.write('Doc: %s  Line: %d  Column: %d\n' % \
            (locator.getSystemId(), locator.getLineNumber(), 
            locator.getColumnNumber() + 1))
        sys.stderr.write(mesg)
        sys.stderr.write('\n')
        sys.exit(-1)
        #raise RuntimeError

USAGE_TEXT = """
Usage: python <Parser>.py [ -s ] <in_xml_file>
Options:
    -s        Use the SAX parser, not the minidom parser.
"""

def usage():
    print USAGE_TEXT
    sys.exit(-1)


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
        if topElementName not in globals():
            raise RuntimeError, 'no class for top element: %s' % topElementName
        topElement = globals()[topElementName]
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
    documentHandler = SaxPeopleHandler()
    parser.setDocumentHandler(documentHandler)
    parser.parse('file:%s' % inFileName)
    root = documentHandler.getRoot()
    sys.stdout.write('<?xml version="1.0" ?>\n')
    root.export(sys.stdout, 0)
    return root


def saxParseString(inString):
    parser = make_parser()
    documentHandler = SaxPeopleHandler()
    parser.setDocumentHandler(documentHandler)
    parser.feed(inString)
    parser.close()
    rootObj = documentHandler.getRoot()
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    return rootObj


def parse(inFileName):
    doc = minidom.parse(inFileName)
    rootNode = doc.documentElement
    rootObj = people.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0, name_="people")
    return rootObj


def parseString(inString):
    doc = minidom.parseString(inString)
    rootNode = doc.documentElement
    rootObj = people.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0, name_="people")
    return rootObj


def parseLiteral(inFileName):
    doc = minidom.parse(inFileName)
    rootNode = doc.documentElement
    rootObj = people.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('from peoplesup import *\n\n')
    sys.stdout.write('rootObj = people(\n')
    rootObj.exportLiteral(sys.stdout, 0, name_="people")
    sys.stdout.write(')\n')
    return rootObj


def main():
    args = sys.argv[1:]
    if len(args) == 2 and args[0] == '-s':
        saxParse(args[1])
    elif len(args) == 1:
        parse(args[0])
    else:
        usage()


if __name__ == '__main__':
    main()
    #import pdb
    #pdb.run('main()')

