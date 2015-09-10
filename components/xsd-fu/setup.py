#!/usr/bin/env python
"""
   setuptools entry point

   This script is used by the ant build (build.xml) to
   package and test the xsd-fu prackage. For most uses,
   see ant.

   Copyright 2011 Glencoe Software, Inc. All rights reserved.
   Use is subject to license terms supplied in LICENSE.txt

"""

import glob
import sys
import os

from ez_setup import use_setuptools
from setuptools import setup, find_packages

ov = os.environ.get("BF_VERSION", "unknown")

for tools in glob.glob("tempTOFIX/setuptools*.egg"):
    if tools.find(".".join(map(str, sys.version_info[0:2]))) > 0:
        sys.path.insert(0, tools)

use_setuptools(to_dir='tempTOFIX')

if os.path.exists("target"):
    packages = find_packages("target")+[""]
else:
    packages = [""]

setup(
    name="xsd-fu",
    version=ov,
    description="Python tools for generating code from the OME specification",
    long_description="""
Python tools for generating code from the OME specification""",
    author="The Open Microscopy Consortium",
    author_email="ome-users@lists.openmicroscopy.org.uk",
    url="http://downloads.openmicroscopy.org/bio-formats/",
    download_url="http://downloads.openmicroscopy.org/bio-formats/",
    # package_dir = {"": "target"},
    # packages=packages,
    test_suite='test.suite'
)
