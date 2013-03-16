
from distutils.core import setup

setup(name="generateDS",
#
# Do not modify the following VERSION comments.
# Used by updateversion.py.
##VERSION##
    version="1.18c",
##VERSION##
    description="Generation of Python data structures and XML parser from Xschema",
    author="Dave Kuhlman",
    author_email="dkuhlman@rexx.com",
    url="http://www.rexx.com/~dkuhlman",
    py_modules=["generateDS", "process_includes", ],
    #scripts=["generateDS.py", "process_includes.py"],
    )

