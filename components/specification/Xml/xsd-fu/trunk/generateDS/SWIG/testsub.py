#!/usr/bin/env python

import sys

import ??? as supermod

class topSub(supermod.top):
    def __init__(self, attributelist=None, include=None, id='', addr=''):
        supermod.top.__init__(self, attributelist, include, id, addr)
supermod.top.subclass = topSub
# end class topSub


class attributelistSub(supermod.attributelist):
    def __init__(self, attribute=None, kwargs=None, parmlist=None, id='', addr=''):
        supermod.attributelist.__init__(self, attribute, kwargs, parmlist, id, addr)
supermod.attributelist.subclass = attributelistSub
# end class attributelistSub


class attributeSub(supermod.attribute):
    def __init__(self, name='', value='', id='', addr=''):
        supermod.attribute.__init__(self, name, value, id, addr)
supermod.attribute.subclass = attributeSub
# end class attributeSub


class includeSub(supermod.include):
    def __init__(self, attributelist=None, include=None, typemap=None, insert=None, module=None, cdecl=None, klass=None, emport=None, enum=None, id='', addr=''):
        supermod.include.__init__(self, attributelist, include, typemap, insert, module, cdecl, klass, emport, enum, id, addr)
supermod.include.subclass = includeSub
# end class includeSub


class emportSub(supermod.emport):
    def __init__(self, attributelist=None, include=None, typemap=None, insert=None, module=None, cdecl=None, klass=None, emport=None, enum=None, id='', addr=''):
        supermod.emport.__init__(self, attributelist, include, typemap, insert, module, cdecl, klass, emport, enum, id, addr)
supermod.emport.subclass = emportSub
# end class emportSub


class enumSub(supermod.enum):
    def __init__(self, attributelist=None, enumitem=None, id='', addr=''):
        supermod.enum.__init__(self, attributelist, enumitem, id, addr)
supermod.enum.subclass = enumSub
# end class enumSub


class enumitemSub(supermod.enumitem):
    def __init__(self, attributelist=None, id='', addr=''):
        supermod.enumitem.__init__(self, attributelist, id, addr)
supermod.enumitem.subclass = enumitemSub
# end class enumitemSub


class kwargsSub(supermod.kwargs):
    def __init__(self, attributelist=None, id='', addr=''):
        supermod.kwargs.__init__(self, attributelist, id, addr)
supermod.kwargs.subclass = kwargsSub
# end class kwargsSub


class typemapSub(supermod.typemap):
    def __init__(self, attributelist=None, typemapitem=None, id='', addr=''):
        supermod.typemap.__init__(self, attributelist, typemapitem, id, addr)
supermod.typemap.subclass = typemapSub
# end class typemapSub


class typemapitemSub(supermod.typemapitem):
    def __init__(self, attributelist=None, id='', addr=''):
        supermod.typemapitem.__init__(self, attributelist, id, addr)
supermod.typemapitem.subclass = typemapitemSub
# end class typemapitemSub


class klassSub(supermod.klass):
    def __init__(self, attributelist=None, cdecl=None, access=None, constructor=None, destructor=None, id='', addr=''):
        supermod.klass.__init__(self, attributelist, cdecl, access, constructor, destructor, id, addr)
supermod.klass.subclass = klassSub
# end class klassSub


class cdeclSub(supermod.cdecl):
    def __init__(self, attributelist=None, id='', addr=''):
        supermod.cdecl.__init__(self, attributelist, id, addr)
supermod.cdecl.subclass = cdeclSub
# end class cdeclSub


class accessSub(supermod.access):
    def __init__(self, attributelist=None, id='', addr=''):
        supermod.access.__init__(self, attributelist, id, addr)
supermod.access.subclass = accessSub
# end class accessSub


class parmlistSub(supermod.parmlist):
    def __init__(self, parm=None, id='', addr=''):
        supermod.parmlist.__init__(self, parm, id, addr)
supermod.parmlist.subclass = parmlistSub
# end class parmlistSub


class parmSub(supermod.parm):
    def __init__(self, attributelist=None, id='', addr=''):
        supermod.parm.__init__(self, attributelist, id, addr)
supermod.parm.subclass = parmSub
# end class parmSub


class constructorSub(supermod.constructor):
    def __init__(self, attributelist=None, id='', addr=''):
        supermod.constructor.__init__(self, attributelist, id, addr)
supermod.constructor.subclass = constructorSub
# end class constructorSub


class destructorSub(supermod.destructor):
    def __init__(self, attributelist=None, id='', addr=''):
        supermod.destructor.__init__(self, attributelist, id, addr)
supermod.destructor.subclass = destructorSub
# end class destructorSub



USAGE_TEXT = """
Usage: python ???.py <infilename>
"""

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


